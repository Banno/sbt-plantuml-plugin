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
package net.sourceforge.plantuml.command.note.sequence;

import java.util.List;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlBuilder.ModeUrl;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.note.SingleMultiFactoryCommand;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.sequencediagram.AbstractMessage;
import net.sourceforge.plantuml.sequencediagram.EventWithDeactivate;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.StringUtils;

public final class FactorySequenceNoteOnArrowCommand implements SingleMultiFactoryCommand<SequenceDiagram> {

	private RegexConcat getRegexConcatMultiLine() {
		return new RegexConcat(new RegexLeaf("^note[%s]+"), //
				new RegexLeaf("POSITION", "(right|left)[%s]*"), //
				new RegexLeaf("COLOR", "(" + HtmlColorUtils.COLOR_REGEXP + ")?"), //
				new RegexLeaf("$"));
	}

	private RegexConcat getRegexConcatSingleLine() {
		return new RegexConcat(new RegexLeaf("^note[%s]+"), //
				new RegexLeaf("POSITION", "(right|left)[%s]*"), //
				new RegexLeaf("COLOR", "(" + HtmlColorUtils.COLOR_REGEXP + ")?"), //
				new RegexLeaf("[%s]*:[%s]*"), //
				new RegexLeaf("NOTE", "(.*)"), //
				new RegexLeaf("$"));
	}

	public Command<SequenceDiagram> createSingleLine() {
		return new SingleLineCommand2<SequenceDiagram>(getRegexConcatSingleLine()) {

			@Override
			protected CommandExecutionResult executeArg(final SequenceDiagram system, RegexResult arg) {
				final List<String> strings = StringUtils.getWithNewlines2(arg.get("NOTE", 0));
				return executeInternal(system, arg, strings);
			}

		};
	}

	public Command<SequenceDiagram> createMultiLine() {
		return new CommandMultilines2<SequenceDiagram>(getRegexConcatMultiLine(),
				MultilinesStrategy.KEEP_STARTING_QUOTE) {

			@Override
			public String getPatternEnd() {
				return "(?i)^end[%s]?note$";
			}

			public CommandExecutionResult executeNow(final SequenceDiagram system, List<String> lines) {
				final RegexResult line0 = getStartingPattern().matcher(StringUtils.trin(lines.get(0)));
				final List<String> in = StringUtils.removeEmptyColumns(lines.subList(1, lines.size() - 1));

				return executeInternal(system, line0, in);
			}

		};
	}

	private CommandExecutionResult executeInternal(SequenceDiagram system, final RegexResult line0, List<String> in) {
		final EventWithDeactivate m = system.getLastEventWithDeactivate();
		if (m instanceof AbstractMessage) {
			final NotePosition position = NotePosition.valueOf(StringUtils.goUpperCase(line0.get("POSITION", 0)));
			final Url url;
			if (in.size() > 0) {
				final UrlBuilder urlBuilder = new UrlBuilder(system.getSkinParam().getValue("topurl"), ModeUrl.STRICT);
				url = urlBuilder.getUrl(in.get(0).toString());
			} else {
				url = null;
			}
			if (url != null) {
				in = in.subList(1, in.size());
			}

			((AbstractMessage) m).setNote(Display.create(in), position, line0.get("COLOR", 0), url);
		}

		return CommandExecutionResult.ok();
	}

}
