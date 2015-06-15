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
package net.sourceforge.plantuml.sequencediagram.command;

import java.text.DecimalFormat;
import java.util.List;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;

public class CommandAutonumber extends SingleLineCommand<SequenceDiagram> {

	public CommandAutonumber() {
		super("(?i)^autonumber[%s]*(\\d+)?(?:[%s]+(\\d+))?(?:[%s]+[%g]([^%g]+)[%g])?[%s]*$");
	}

	@Override
	protected CommandExecutionResult executeArg(SequenceDiagram sequenceDiagram, List<String> arg) {
		int start = 1;
		if (arg.get(0) != null) {
			start = Integer.parseInt(arg.get(0));
		}
		int inc = 1;
		if (arg.get(1) != null) {
			inc = Integer.parseInt(arg.get(1));
		}

		final String df = arg.get(2) == null ? "<b>0</b>" : arg.get(2);
		final DecimalFormat decimalFormat;
		try {
			decimalFormat = new DecimalFormat(df);
		} catch (IllegalArgumentException e) {
			return CommandExecutionResult.error("Error in pattern : " + df);
		}

		sequenceDiagram.goAutonumber(start, inc, decimalFormat);
		return CommandExecutionResult.ok();
	}
}
