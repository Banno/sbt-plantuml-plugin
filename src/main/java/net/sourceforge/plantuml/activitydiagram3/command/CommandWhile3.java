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

import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexOptional;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorSet;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;

public class CommandWhile3 extends SingleLineCommand2<ActivityDiagram3> {

	public CommandWhile3() {
		super(getRegexConcat());
	}

	static RegexConcat getRegexConcat() {
		return new RegexConcat(//
				new RegexLeaf("^"), //
				new RegexLeaf("COLOR", "(?:(" + HtmlColorUtils.COLOR_REGEXP + "):)?"), //
				new RegexLeaf("while"), //
				new RegexLeaf("TEST", "[%s]*\\((.+?)\\)"), //
				new RegexOptional(new RegexConcat(//
						new RegexLeaf("[%s]*(is|equals?)[%s]*"), //
						new RegexLeaf("YES", "\\((.+?)\\)"))), //
				new RegexLeaf(";?$"));
	}

	@Override
	protected CommandExecutionResult executeArg(ActivityDiagram3 diagram, RegexResult arg) {
		final HtmlColor color = diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(arg.get("COLOR", 0));
		diagram.doWhile(Display.getWithNewlines(arg.get("TEST", 0)), Display.getWithNewlines(arg.get("YES", 0)), color);

		return CommandExecutionResult.ok();
	}

}
