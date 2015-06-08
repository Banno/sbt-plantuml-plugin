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
import net.sourceforge.plantuml.graphic.HtmlColorSet;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.sequencediagram.Note;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.NoteStyle;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.StringUtils;

public final class FactorySequenceNoteCommand implements SingleMultiFactoryCommand<SequenceDiagram> {

	private RegexConcat getRegexConcatMultiLine() {
		return new RegexConcat(//
				new RegexLeaf("^"), //
				new RegexLeaf("VMERGE", "(/)?[%s]*"), //
				new RegexLeaf("STYLE", "(note|hnote|rnote)[%s]+"), //
				new RegexLeaf("POSITION", "(right|left|over)[%s]+"), //
				new RegexLeaf("PARTICIPANT", "(?:of[%s]+)?([\\p{L}0-9_.@]+|[%g][^%g]+[%g])[%s]*"), //
				new RegexLeaf("COLOR", "(" + HtmlColorUtils.COLOR_REGEXP + ")?"), //
				new RegexLeaf("$"));
	}

	private RegexConcat getRegexConcatSingleLine() {
		return new RegexConcat(//
				new RegexLeaf("^"), //
				new RegexLeaf("VMERGE", "(/)?[%s]*"), //
				new RegexLeaf("STYLE", "(note|hnote|rnote)[%s]+"), //
				new RegexLeaf("POSITION", "(right|left|over)[%s]+"), //
				new RegexLeaf("PARTICIPANT", "(?:of[%s])?([\\p{L}0-9_.@]+|[%g][^%g]+[%g])[%s]*"), //
				new RegexLeaf("COLOR", "(" + HtmlColorUtils.COLOR_REGEXP + ")?"), //
				new RegexLeaf("[%s]*:[%s]*"), //
				new RegexLeaf("NOTE", "(.*)"), //
				new RegexLeaf("$"));
	}

	public Command<SequenceDiagram> createMultiLine() {
		return new CommandMultilines2<SequenceDiagram>(getRegexConcatMultiLine(),
				MultilinesStrategy.KEEP_STARTING_QUOTE) {

			@Override
			public String getPatternEnd() {
				return "(?i)^end[%s]?(note|hnote|rnote)$";
			}

			public CommandExecutionResult executeNow(final SequenceDiagram system, List<String> lines) {
				final RegexResult line0 = getStartingPattern().matcher(StringUtils.trin(lines.get(0)));
				final List<String> strings = StringUtils.removeEmptyColumns(lines.subList(1, lines.size() - 1));
				return executeInternal(system, line0, strings);
			}
		};
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

	private CommandExecutionResult executeInternal(SequenceDiagram diagram, RegexResult arg, final List<String> strings) {
		final Participant p = diagram.getOrCreateParticipant(StringUtils
				.eventuallyRemoveStartingAndEndingDoubleQuote(arg.get("PARTICIPANT", 0)));

		final NotePosition position = NotePosition.valueOf(StringUtils.goUpperCase(arg.get("POSITION", 0)));

		if (strings.size() > 0) {
			final boolean tryMerge = arg.get("VMERGE", 0) != null;
			final Note note = new Note(p, position, Display.create(strings));
			note.setSpecificBackcolor(diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(arg.get("COLOR", 0)));
			note.setStyle(NoteStyle.getNoteStyle(arg.get("STYLE", 0)));
			diagram.addNote(note, tryMerge);
		}
		return CommandExecutionResult.ok();
	}

}
