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
package net.sourceforge.plantuml.sequencediagram.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.sequencediagram.GroupingType;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;

public class CommandGrouping extends SingleLineCommand2<SequenceDiagram> {

	public CommandGrouping() {
		super(getRegexConcat());
	}

	static RegexConcat getRegexConcat() {
		return new RegexConcat(//
				new RegexLeaf("^"), //
				new RegexLeaf("TYPE", "(opt|alt|loop|par|par2|break|critical|else|end|also|group)"), //
				new RegexLeaf("COLORS", "((?<!else)(?<!also)(?<!end)#\\w+)?(?:[%s]+(#\\w+))?"), //
				new RegexLeaf("COMMENT", "(?:[%s]+(.*?))?"), //
				new RegexLeaf("$"));
	}

	@Override
	protected CommandExecutionResult executeArg(SequenceDiagram diagram, RegexResult arg) {
		String type = StringUtils.goLowerCase(arg.get("TYPE", 0));
		final HtmlColor backColorElement = diagram.getSkinParam().getIHtmlColorSet()
				.getColorIfValid(arg.get("COLORS", 0));
		final HtmlColor backColorGeneral = diagram.getSkinParam().getIHtmlColorSet()
				.getColorIfValid(arg.get("COLORS", 1));
		String comment = arg.get("COMMENT", 0);
		final GroupingType groupingType = GroupingType.getType(type);
		if ("group".equals(type)) {
			if (StringUtils.isEmpty(comment)) {
				comment = "group";
			} else {
				final Pattern p = Pattern.compile("^(.*?)\\[(.*)\\]$");
				final Matcher m = p.matcher(comment);
				if (m.find()) {
					type = m.group(1);
					comment = m.group(2);
				}
			}
		}
		final boolean result = diagram.grouping(type, comment, groupingType, backColorGeneral, backColorElement);
		if (result == false) {
			return CommandExecutionResult.error("Cannot create group");
		}
		return CommandExecutionResult.ok();
	}
}
