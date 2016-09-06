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
package net.sourceforge.plantuml.activitydiagram.command;

import java.util.List;

import net.sourceforge.plantuml.activitydiagram.ActivityDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand;

public class CommandEndif extends SingleLineCommand<ActivityDiagram> {

	public CommandEndif() {
		super("(?i)^end[%s]?if$");
	}

	@Override
	protected CommandExecutionResult executeArg(ActivityDiagram diagram, List<String> arg) {
		if (diagram.getLastEntityConsulted() == null) {
			return CommandExecutionResult.error("No if for this endif");
		}
		if (diagram.getCurrentContext() == null) {
			return CommandExecutionResult.error("No if for this endif");
		}
		diagram.endif();

		return CommandExecutionResult.ok();
	}

}
