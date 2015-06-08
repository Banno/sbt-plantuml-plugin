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
package net.sourceforge.plantuml.activitydiagram3.command;

import java.util.List;

import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.CommandMultilines2;
import net.sourceforge.plantuml.command.MultilinesStrategy;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.StringUtils;

public class CommandActivityLong3 extends CommandMultilines2<ActivityDiagram3> {

	public CommandActivityLong3() {
		super(getRegexConcat(), MultilinesStrategy.REMOVE_STARTING_QUOTE);
	}

	@Override
	public String getPatternEnd() {
		return "^(.*)" + CommandActivity3.ENDING_GROUP + "$";
	}

	static RegexConcat getRegexConcat() {
		return new RegexConcat(new RegexLeaf("^"), //
				new RegexLeaf("COLOR", "(?::?(" + HtmlColorUtils.COLOR_REGEXP + "))?"), //
				new RegexLeaf(":"), //
				new RegexLeaf("DATA", "(.*)"), //
				new RegexLeaf("$"));
	}

	public CommandExecutionResult executeNow(ActivityDiagram3 diagram, List<String> lines) {
		lines = StringUtils.removeEmptyColumns(lines);
		final RegexResult line0 = getStartingPattern().matcher(StringUtils.trin(lines.get(0)));
		final HtmlColor color = diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(line0.get("COLOR", 0));
		final BoxStyle style = BoxStyle.fromChar(getLastChar(lines));
		removeStarting(lines, line0.get("DATA", 0));
		removeEnding(lines);
		diagram.addActivity(Display.create(lines), color, style, null);
		return CommandExecutionResult.ok();
	}

	private char getLastChar(List<String> lines) {
		final String s = lines.get(lines.size() - 1);
		return s.charAt(s.length() - 1);
	}

	private void removeStarting(List<String> lines, String data) {
		if (lines.size() == 0) {
			return;
		}
		lines.set(0, data);
	}

	private void removeEnding(List<String> lines) {
		if (lines.size() == 0) {
			return;
		}
		final int n = lines.size() - 1;
		final String s = lines.get(n);
		lines.set(n, s.substring(0, s.length() - 1));
	}

}
