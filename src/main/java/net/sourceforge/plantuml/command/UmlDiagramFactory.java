/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2014, Arnaud Roques
 *
 * Project Info:  http://plantuml.sourceforge.net
 * 
 * This file is part of PlantUML.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 * Original Author:  Arnaud Roques
 */
package net.sourceforge.plantuml.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.ErrorUmlType;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.PSystemError;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.classdiagram.command.CommandHideShow;
import net.sourceforge.plantuml.classdiagram.command.CommandHideShow3;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.suggest.SuggestEngine;
import net.sourceforge.plantuml.suggest.SuggestEngineResult;
import net.sourceforge.plantuml.suggest.SuggestEngineStatus;
import net.sourceforge.plantuml.utils.StartUtils;
import net.sourceforge.plantuml.version.IteratorCounter;

public abstract class UmlDiagramFactory extends PSystemAbstractFactory {

	private final List<Command> cmds;

	protected UmlDiagramFactory() {
		this(DiagramType.UML);
	}

	protected UmlDiagramFactory(DiagramType type) {
		super(type);
		cmds = createCommands();
	}

	final public Diagram createSystem(UmlSource source) {
		final IteratorCounter it = source.iterator();
		final String startLine = it.next();
		if (StartUtils.isArobaseStartDiagram(startLine) == false) {
			throw new UnsupportedOperationException();
		}

		if (source.isEmpty()) {
			return buildEmptyError(source);
		}
		AbstractPSystem sys = createEmptyDiagram();

		while (it.hasNext()) {
			if (StartUtils.isArobaseEndDiagram(it.peek())) {
				final String err = checkFinalError(sys);
				if (err != null) {
					return buildEmptyError(source, err);
				}
				if (source.getTotalLineCount() == 2) {
					return buildEmptyError(source);
				}
				if (sys == null) {
					return null;
				}
				sys.makeDiagramReady();
				if (sys.isOk() == false) {
					return null;
				}
				sys.setSource(source);
				return sys;
			}
			sys = executeOneLine(sys, source, it);
			if (sys instanceof PSystemError) {
				return sys;
			}
		}
		sys.setSource(source);
		return sys;

	}

	private AbstractPSystem executeOneLine(AbstractPSystem sys, UmlSource source, final IteratorCounter it) {
		final CommandControl commandControl = isValid2(it);
		if (commandControl == CommandControl.NOT_OK) {
			final ErrorUml err = new ErrorUml(ErrorUmlType.SYNTAX_ERROR, "Syntax Error?", it.currentNum());
			if (OptionFlags.getInstance().isUseSuggestEngine()) {
				final SuggestEngine engine = new SuggestEngine(source, this);
				final SuggestEngineResult result = engine.tryToSuggest(sys);
				if (result.getStatus() == SuggestEngineStatus.ONE_SUGGESTION) {
					err.setSuggest(result);
				}
			}
			sys = new PSystemError(source, err, null);
		} else if (commandControl == CommandControl.OK_PARTIAL) {
			final boolean ok = manageMultiline(it, sys);
			if (ok == false) {
				sys = new PSystemError(source, new ErrorUml(ErrorUmlType.EXECUTION_ERROR, "Strange Syntax Error?",
						it.currentNum() - 1), null);

			}
		} else if (commandControl == CommandControl.OK) {
			final String line = it.next();
			Command cmd = getFirstCommandOkForLines(Arrays.asList(line));
			final CommandExecutionResult result = sys.executeCommand(cmd, Arrays.asList(line));
			if (result.isOk() == false) {
				sys = new PSystemError(source, new ErrorUml(ErrorUmlType.EXECUTION_ERROR, result.getError(),
						it.currentNum() - 1), result.getDebugLines());
			}
			if (result.getNewDiagram() != null) {
				sys = result.getNewDiagram();
			}
		} else {
			assert false;
		}
		return sys;
	}

	public CommandControl isValid2(final IteratorCounter it) {
		final List<String> asList = Arrays.asList(it.peek());
		for (Command cmd : cmds) {
			final CommandControl result = cmd.isValid(asList);
			if (result == CommandControl.OK) {
				return result;
			}
			if (result == CommandControl.OK_PARTIAL && isMultilineCommandOk(it.cloneMe(), cmd) != null) {
				return result;
			}
		}
		return CommandControl.NOT_OK;
	}

	public CommandControl goForwardMultiline(final IteratorCounter it) {
		final List<String> asList = Arrays.asList(it.peek());
		for (Command cmd : cmds) {
			final CommandControl result = cmd.isValid(asList);
			if (result == CommandControl.OK) {
				throw new IllegalStateException();
			}
			if (result == CommandControl.OK_PARTIAL && isMultilineCommandOk(it, cmd) != null) {
				return result;
			}
		}
		throw new IllegalStateException();
	}

	private boolean manageMultiline(IteratorCounter it, AbstractPSystem system) {
		for (Command cmd : cmds) {
			if (isMultilineCommandOk(it.cloneMe(), cmd) != null) {
				final List<String> lines = isMultilineCommandOk(it, cmd);
				return cmd.execute(system, lines).isOk();
			}
		}
		return false;
	}

	private List<String> isMultilineCommandOk(IteratorCounter it, Command cmd) {
		final List<String> lines = new ArrayList<String>();
		while (it.hasNext()) {
			addOneSingleLineManageEmbedded(it, lines);
			final CommandControl result = cmd.isValid(lines);
			if (result == CommandControl.NOT_OK) {
				return null;
			}
			if (result == CommandControl.OK) {
				return lines;
			}
		}
		return null;
	}

	private void addOneSingleLineManageEmbedded(IteratorCounter it, final List<String> lines) {
		final String linetoBeAdded = it.next();
		lines.add(linetoBeAdded);
		if (StringUtils.trinNoTrace(linetoBeAdded).equals("{{")) {
			while (it.hasNext()) {
				final String s = it.next();
				lines.add(s);
				if (StringUtils.trinNoTrace(s).equals("}}")) {
					return;
				}
			}
		}
	}

	// -----------------------------------

	public String checkFinalError(AbstractPSystem system) {
		return null;
	}
	

	final public CommandControl isValid(List<String> lines) {
		for (Command cmd : cmds) {
			final CommandControl result = cmd.isValid(lines);
			if (result == CommandControl.OK) {
				return result;
			}
			if (result == CommandControl.OK_PARTIAL) {
				return result;
			}
		}
		return CommandControl.NOT_OK;

	}

	private Command getFirstCommandOkForLines(List<String> lines) {
		for (Command cmd : cmds) {
			final CommandControl result = cmd.isValid(lines);
			if (result == CommandControl.OK) {
				return cmd;
			}
		}
		throw new IllegalArgumentException();
	}

	protected abstract List<Command> createCommands();

	public abstract AbstractPSystem createEmptyDiagram();

	final protected void addCommonCommands(List<Command> cmds) {
		cmds.add(new CommandNope());
		cmds.add(new CommandComment());
		cmds.add(new CommandMultilinesComment());
		cmds.add(new CommandPragma());
		cmds.add(new CommandTitle());
		cmds.add(new CommandMultilinesTitle());
		cmds.add(new CommandMultilinesLegend());

		cmds.add(new CommandFooter());
		cmds.add(new CommandMultilinesFooter());

		cmds.add(new CommandHeader());
		cmds.add(new CommandMultilinesHeader());

		cmds.add(new CommandSkinParam());
		cmds.add(new CommandSkinParamMultilines());
		cmds.add(new CommandMinwidth());
		cmds.add(new CommandRotate());
		cmds.add(new CommandScale());
		cmds.add(new CommandScaleWidthAndHeight());
		cmds.add(new CommandScaleWidthOrHeight());
		cmds.add(new CommandAffineTransform());
		cmds.add(new CommandAffineTransformMultiline());
		cmds.add(new CommandHideUnlinked());
		final FactorySpriteCommand factorySpriteCommand = new FactorySpriteCommand();
		cmds.add(factorySpriteCommand.createMultiLine());
		cmds.add(factorySpriteCommand.createSingleLine());
		cmds.add(new CommandSpriteFile());

		cmds.add(new CommandHideShow3());
		cmds.add(new CommandHideShow());

	}

	final public List<String> getDescription() {
		final List<String> result = new ArrayList<String>();
		for (Command cmd : createCommands()) {
			result.addAll(Arrays.asList(cmd.getDescription()));
		}
		return Collections.unmodifiableList(result);

	}

}
