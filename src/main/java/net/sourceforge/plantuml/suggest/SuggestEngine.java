/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
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
package net.sourceforge.plantuml.suggest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.CharSequence2;
import net.sourceforge.plantuml.CharSequence2Impl;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.CommandControl;
import net.sourceforge.plantuml.command.UmlDiagramFactory;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.utils.StartUtils;
import net.sourceforge.plantuml.version.IteratorCounter2;
import net.sourceforge.plantuml.version.IteratorCounter2Impl;

final public class SuggestEngine {

	private static final int LIMIT = 500;

	private final UmlDiagramFactory systemFactory;

	private final IteratorCounter2 it99;

	public SuggestEngine(UmlSource source, UmlDiagramFactory systemFactory) {
		this.systemFactory = systemFactory;
		this.it99 = source.iterator2();
		final CharSequence startLine = it99.next();
		if (StartUtils.isArobaseStartDiagram(startLine) == false) {
			throw new UnsupportedOperationException();
		}
	}

	public SuggestEngineResult tryToSuggest(AbstractPSystem system) {
		return executeUmlCommand(system);
	}

	private SuggestEngineResult executeUmlCommand(AbstractPSystem system) {
		while (it99.hasNext()) {
			if (StartUtils.isArobaseEndDiagram(it99.peek())) {
				return SuggestEngineResult.SYNTAX_OK;
			}
			final SuggestEngineResult check = checkAndCorrect();
			if (check.getStatus() != SuggestEngineStatus.SYNTAX_OK) {
				return check;
			}
			final CommandControl commandControl = systemFactory.isValid2(it99);
			if (commandControl == CommandControl.OK_PARTIAL) {
				systemFactory.goForwardMultiline(it99);
				// if (ok == false) {
				// return SuggestEngineResult.CANNOT_CORRECT;
				// }
			} else if (commandControl == CommandControl.OK) {
				it99.next();
				// final Command cmd = new ProtectedCommand(systemFactory.createCommand(Arrays.asList(s)));
				// final CommandExecutionResult result = cmd.execute(system, Arrays.asList(s));
				// if (result.isOk() == false) {
				// return SuggestEngineResult.CANNOT_CORRECT;
				// }
			} else {
				return SuggestEngineResult.CANNOT_CORRECT;
			}
		}
		return SuggestEngineResult.CANNOT_CORRECT;
		// throw new IllegalStateException();
	}

	SuggestEngineResult checkAndCorrect() {
		final String incorrectLine = it99.peek().toString();
		if (incorrectLine.length() > LIMIT) {
			return SuggestEngineResult.CANNOT_CORRECT;
		}
		final CommandControl commandControl = systemFactory.isValid2(it99);
		if (commandControl != CommandControl.NOT_OK) {
			return SuggestEngineResult.SYNTAX_OK;
		}

		if (StringUtils.trin(incorrectLine).startsWith("{")
				&& systemFactory.isValid(BlocLines.single(it99.peekPrevious() + " {")) != CommandControl.NOT_OK) {
			return new SuggestEngineResult(it99.peekPrevious() + " {");
		}

		final Collection<Iterator<String>> all = new ArrayList<Iterator<String>>();
		all.add(new VariatorRemoveOneChar(incorrectLine));
		all.add(new VariatorSwapLetter(incorrectLine));
		// all.add(new VariatorAddOneCharBetweenWords(incorrectLine, ':'));
		all.add(new VariatorAddOneCharBetweenWords(incorrectLine, '-'));
		all.add(new VariatorAddOneCharBetweenWords(incorrectLine, ' '));
		// all.add(new VariatorAddTwoChar(incorrectLine, '\"'));

		for (Iterator<String> it2 : all) {
			final SuggestEngineResult result = tryThis(it2);
			if (result != null) {
				return result;
			}
		}
		return SuggestEngineResult.CANNOT_CORRECT;
	}

	private SuggestEngineResult tryThis(Iterator<String> it2) {
		while (it2.hasNext()) {
			final String newS = it2.next();
			if (StringUtils.trin(newS).length() == 0) {
				continue;
			}
			final CommandControl commandControl = systemFactory.isValid2(replaceFirstLine(newS));
			if (commandControl == CommandControl.OK) {
				return new SuggestEngineResult(newS);
			}
		}
		return null;
	}

	private IteratorCounter2 replaceFirstLine(String s) {
		final List<CharSequence2> tmp = new ArrayList<CharSequence2>();
		tmp.add(new CharSequence2Impl(s, null));
		final Iterator<? extends CharSequence> it3 = it99.cloneMe();
		if (it3.hasNext()) {
			it3.next();
		}
		while (it3.hasNext()) {
			tmp.add(new CharSequence2Impl(it3.next(), null));
		}
		return new IteratorCounter2Impl(tmp);
	}
}
