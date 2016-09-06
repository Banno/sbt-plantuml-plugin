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
package net.sourceforge.plantuml.project2;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandComment;
import net.sourceforge.plantuml.command.CommandMultilinesComment;
import net.sourceforge.plantuml.command.CommandNope;
import net.sourceforge.plantuml.command.UmlDiagramFactory;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.project2.command.CommandAffectation;
import net.sourceforge.plantuml.project2.command.CommandCloseWeekDay;

public class PSystemProjectFactory2 extends UmlDiagramFactory {

	public PSystemProjectFactory2() {
		super(DiagramType.PROJECT);
	}

	@Override
	protected List<Command> createCommands() {
		final List<Command> cmds = new ArrayList<Command>();
		cmds.add(new CommandNope());
		cmds.add(new CommandComment());
		cmds.add(new CommandMultilinesComment());
		cmds.add(new CommandAffectation());
		cmds.add(new CommandCloseWeekDay());
		return cmds;
	}

	@Override
	public PSystemProject2 createEmptyDiagram() {
		return new PSystemProject2();
	}

}
