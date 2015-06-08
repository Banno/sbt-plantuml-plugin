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
import net.sourceforge.plantuml.cucadiagram.EntityUtils;
import net.sourceforge.plantuml.cucadiagram.IGroup;

public class CommandEndPackage extends SingleLineCommand<AbstractEntityDiagram> {

	public CommandEndPackage() {
		super("(?i)^\\}$");
	}

	@Override
	protected CommandExecutionResult executeArg(AbstractEntityDiagram diagram, List<String> arg) {
		final IGroup currentPackage = diagram.getCurrentGroup();
		if (EntityUtils.groupRoot(currentPackage)) {
			return CommandExecutionResult.error("No package or namespace defined");
		}
		diagram.endGroup();
		return CommandExecutionResult.ok();
	}

}
