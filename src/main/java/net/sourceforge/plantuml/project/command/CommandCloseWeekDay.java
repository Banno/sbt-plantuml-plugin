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
package net.sourceforge.plantuml.project.command;

import java.util.List;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand;
import net.sourceforge.plantuml.project.PSystemProject;
import net.sourceforge.plantuml.project.WeekDay;

public class CommandCloseWeekDay extends SingleLineCommand<PSystemProject> {

	public CommandCloseWeekDay() {
		super("(?i)^\\s*close\\s+(\\w{3,}day)\\s*$");
	}

	@Override
	protected CommandExecutionResult executeArg(PSystemProject diagram, List<String> arg) {
		final WeekDay weekDay = WeekDay.valueOf(StringUtils.goUpperCase(arg.get(0).substring(0, 3)));
		diagram.getProject().closeWeekDay(weekDay);
		return CommandExecutionResult.ok();
	}
}
