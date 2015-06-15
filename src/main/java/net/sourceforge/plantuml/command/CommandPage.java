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

import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;

public class CommandPage extends SingleLineCommand<AbstractEntityDiagram> {

	public CommandPage() {
		super("(?i)^page[%s]+(\\d+)[%s]*x[%s]*(\\d+)$");
	}

	@Override
	protected CommandExecutionResult executeArg(AbstractEntityDiagram classDiagram, List<String> arg) {

		final int horizontal = Integer.parseInt(arg.get(0));
		final int vertical = Integer.parseInt(arg.get(1));
		if (horizontal <= 0 || vertical <= 0) {
			return CommandExecutionResult.error("Argument must be positive");
		}
		classDiagram.setHorizontalPages(horizontal);
		classDiagram.setVerticalPages(vertical);
		return CommandExecutionResult.ok();
	}

}
