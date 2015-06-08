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

import java.util.List;
import java.util.regex.Matcher;

import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.StringUtils;

public class CommandMultilinesHeader extends CommandMultilines<UmlDiagram> {

	public CommandMultilinesHeader() {
		super("(?i)^(?:(left|right|center)?[%s]*)header$");
	}
	
	@Override
	public String getPatternEnd() {
		return "(?i)^end[%s]?header$";
	}


	public CommandExecutionResult execute(final UmlDiagram diagram, List<String> lines) {
		StringUtils.trim(lines, false);
		final Matcher m = getStartingPattern().matcher(StringUtils.trin(lines.get(0)));
		if (m.find() == false) {
			throw new IllegalStateException();
		}
		final String align = m.group(1);
		if (align != null) {
			diagram.setHeaderAlignment(HorizontalAlignment.valueOf(StringUtils.goUpperCase(align)));
		}
		final Display strings = Display.create(lines.subList(1, lines.size() - 1));
		if (strings.size() > 0) {
			diagram.setHeader(strings);
			return CommandExecutionResult.ok();
		}
		return CommandExecutionResult.error("Empty header");
	}

}
