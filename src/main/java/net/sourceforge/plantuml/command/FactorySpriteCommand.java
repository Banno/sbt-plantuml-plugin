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
package net.sourceforge.plantuml.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.command.note.SingleMultiFactoryCommand;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.ugraphic.sprite.Sprite;
import net.sourceforge.plantuml.ugraphic.sprite.SpriteGrayLevel;

public final class FactorySpriteCommand implements SingleMultiFactoryCommand<UmlDiagram> {

	private RegexConcat getRegexConcatMultiLine() {
		return new RegexConcat(new RegexLeaf("^"), //
				new RegexLeaf("sprite[%s]+\\$?"), //
				new RegexLeaf("NAME", "([\\p{L}0-9_]+)[%s]*"), //
				new RegexLeaf("DIM", "(?:\\[(\\d+)x(\\d+)/(\\d+)(z)?\\])?"), //
				new RegexLeaf("[%s]*\\{"), //
				new RegexLeaf("$"));
	}

	private RegexConcat getRegexConcatSingleLine() {
		return new RegexConcat(new RegexLeaf("^"), //
				new RegexLeaf("sprite[%s]+\\$?"), //
				new RegexLeaf("NAME", "([\\p{L}0-9_]+)[%s]*"), //
				new RegexLeaf("DIM", "(?:\\[(\\d+)x(\\d+)/(\\d+)(z)\\])?"), //
				new RegexLeaf("[%s]+"), //
				new RegexLeaf("DATA", "([-_A-Za-z0-9]+)"), //
				new RegexLeaf("$"));
	}

	public Command<UmlDiagram> createSingleLine() {
		return new SingleLineCommand2<UmlDiagram>(getRegexConcatSingleLine()) {

			@Override
			protected CommandExecutionResult executeArg(final UmlDiagram system, RegexResult arg) {
				return executeInternal(system, arg, Arrays.asList((CharSequence) arg.get("DATA", 0)));
			}

		};
	}

	public Command<UmlDiagram> createMultiLine(boolean withBracket) {
		return new CommandMultilines2<UmlDiagram>(getRegexConcatMultiLine(), MultilinesStrategy.REMOVE_STARTING_QUOTE) {

			@Override
			public String getPatternEnd() {
				return "(?i)^end[%s]?sprite|\\}$";
			}

			public CommandExecutionResult executeNow(final UmlDiagram system, BlocLines lines) {
				lines = lines.trim(true);
				final RegexResult line0 = getStartingPattern().matcher(StringUtils.trin(lines.getFirst499()));

				lines = lines.subExtract(1, 1);
				lines = lines.removeEmptyColumns();
				if (lines.size() == 0) {
					return CommandExecutionResult.error("No sprite defined.");
				}
				return executeInternal(system, line0, lines.getLines());
			}

		};
	}

	private CommandExecutionResult executeInternal(UmlDiagram system, RegexResult line0,
			final List<CharSequence> strings) {
		try {
			final Sprite sprite;
			if (line0.get("DIM", 0) == null) {
				sprite = SpriteGrayLevel.GRAY_16.buildSprite(-1, -1, strings);
			} else {
				final int width = Integer.parseInt(line0.get("DIM", 0));
				final int height = Integer.parseInt(line0.get("DIM", 1));
				final int nbColor = Integer.parseInt(line0.get("DIM", 2));
				if (nbColor != 4 && nbColor != 8 && nbColor != 16) {
					return CommandExecutionResult.error("Only 4, 8 or 16 graylevel are allowed.");
				}
				final SpriteGrayLevel level = SpriteGrayLevel.get(nbColor);
				if (line0.get("DIM", 3) == null) {
					sprite = level.buildSprite(width, height, strings);
				} else {
					sprite = level.buildSpriteZ(width, height, concat(strings));
				}
			}
			system.addSprite(line0.get("NAME", 0), sprite);
			return CommandExecutionResult.ok();
		} catch (IOException e) {
			return CommandExecutionResult.error("Cannot decode sprite.");
		}
	}

	private String concat(final List<? extends CharSequence> strings) {
		final StringBuilder sb = new StringBuilder();
		for (CharSequence s : strings) {
			sb.append(StringUtils.trin(s));
		}
		return sb.toString();
	}

}
