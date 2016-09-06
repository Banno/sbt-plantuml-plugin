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
package net.sourceforge.plantuml.descdiagram.command;

import java.util.List;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.BlocLines;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.descdiagram.DescriptionDiagram;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.graphic.color.ColorParser;
import net.sourceforge.plantuml.graphic.color.ColorType;

public class CommandCreateElementMultilines extends CommandMultilines2<DescriptionDiagram> {

	private final int type;

	enum Mode {
		EXTENDS, IMPLEMENTS
	};

	public CommandCreateElementMultilines(int type) {
		super(getRegexConcat(type), MultilinesStrategy.REMOVE_STARTING_QUOTE);
		this.type = type;
	}

	@Override
	public String getPatternEnd() {
		if (type == 0) {
			return "(?i)^(.*)[%g]$";
		}
		if (type == 1) {
			return "(?i)^(.*)\\]$";
		}
		throw new IllegalArgumentException();
	}

	private static RegexConcat getRegexConcat(int type) {
		if (type == 0) {
			return new RegexConcat(new RegexLeaf("^"), //
					new RegexLeaf("TYPE", "(" + CommandCreateElementFull.ALL_TYPES + ")[%s]+"), //
					new RegexLeaf("CODE", "([\\p{L}0-9_.]+)"), //
					new RegexLeaf("[%s]*"), //
					new RegexLeaf("STEREO", "(\\<\\<.+\\>\\>)?"), //
					new RegexLeaf("[%s]*"), //
					ColorParser.exp1(), //
					new RegexLeaf("[%s]*"), //
					new RegexLeaf("DESC", "as[%s]*[%g](.*)$"));
		}
		if (type == 1) {
			return new RegexConcat(new RegexLeaf("^"), //
					new RegexLeaf("TYPE", "(" + CommandCreateElementFull.ALL_TYPES + ")[%s]+"), //
					new RegexLeaf("CODE", "([\\p{L}0-9_.]+)"), //
					new RegexLeaf("[%s]*"), //
					new RegexLeaf("STEREO", "(\\<\\<.+\\>\\>)?"), //
					new RegexLeaf("[%s]*"), //
					ColorParser.exp1(), //
					new RegexLeaf("[%s]*"), //
					new RegexLeaf("DESC", "\\[(.*)$"));
		}
		throw new IllegalArgumentException();
	}

	public CommandExecutionResult executeNow(DescriptionDiagram diagram, BlocLines lines) {
		lines = lines.trim(false);
		final RegexResult line0 = getStartingPattern().matcher(StringUtils.trin(lines.getFirst499()));
		final String symbol = StringUtils.goUpperCase(line0.get("TYPE", 0));
		final LeafType type;
		USymbol usymbol;

		if (symbol.equalsIgnoreCase("usecase")) {
			type = LeafType.USECASE;
			usymbol = null;
		} else {
			usymbol = USymbol.getFromString(symbol);
			if (usymbol == null) {
				throw new IllegalStateException();
			}
			type = LeafType.DESCRIPTION;
		}

		final Code code = Code.of(line0.get("CODE", 0));
		final List<String> lineLast = StringUtils.getSplit(MyPattern.cmpile(getPatternEnd()), lines.getLast499()
				.toString());
		lines = lines.subExtract(1, 1);
		Display display = lines.toDisplay();
		final String descStart = line0.get("DESC", 0);
		if (StringUtils.isNotEmpty(descStart)) {
			display = display.addFirst(descStart);
		}

		if (StringUtils.isNotEmpty(lineLast.get(0))) {
			display = display.add(lineLast.get(0));
		}

		final String stereotype = line0.get("STEREO", 0);

		final ILeaf result = diagram.createLeaf(code, display, type, usymbol);
		result.setUSymbol(usymbol);
		if (stereotype != null) {
			result.setStereotype(new Stereotype(stereotype, diagram.getSkinParam().getCircledCharacterRadius(), diagram
					.getSkinParam().getFont(null, false, FontParam.CIRCLED_CHARACTER), diagram.getSkinParam()
					.getIHtmlColorSet()));
		}

		result.setSpecificColorTOBEREMOVED(ColorType.BACK,
				diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(line0.get("COLOR", 0)));

		return CommandExecutionResult.ok();
	}
}
