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
package net.sourceforge.plantuml.activitydiagram;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.activitydiagram.command.CommandElse;
import net.sourceforge.plantuml.activitydiagram.command.CommandEndPartition;
import net.sourceforge.plantuml.activitydiagram.command.CommandEndif;
import net.sourceforge.plantuml.activitydiagram.command.CommandIf;
import net.sourceforge.plantuml.activitydiagram.command.CommandLinkActivity;
import net.sourceforge.plantuml.activitydiagram.command.CommandLinkLongActivity2;
import net.sourceforge.plantuml.activitydiagram.command.CommandPartition;
import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandFootboxIgnored;
import net.sourceforge.plantuml.command.CommandRankDir;
import net.sourceforge.plantuml.command.UmlDiagramFactory;
import net.sourceforge.plantuml.command.note.FactoryNoteActivityCommand;
import net.sourceforge.plantuml.command.note.FactoryNoteOnLinkCommand;

public class ActivityDiagramFactory extends UmlDiagramFactory {

	@Override
	public ActivityDiagram createEmptyDiagram() {
		return new ActivityDiagram();
	}

	@Override
	protected List<Command> createCommands() {
		final List<Command> cmds = new ArrayList<Command>();
		cmds.add(new CommandFootboxIgnored());
		addCommonCommands(cmds);
		cmds.add(new CommandRankDir());

		cmds.add(new CommandPartition());
		cmds.add(new CommandEndPartition());
		cmds.add(new CommandLinkLongActivity2());

		final FactoryNoteActivityCommand factoryNoteActivityCommand = new FactoryNoteActivityCommand();
		cmds.add(factoryNoteActivityCommand.createSingleLine());
		cmds.add(factoryNoteActivityCommand.createMultiLine());

		final FactoryNoteOnLinkCommand factoryNoteOnLinkCommand = new FactoryNoteOnLinkCommand();
		cmds.add(factoryNoteOnLinkCommand.createSingleLine());
		cmds.add(factoryNoteOnLinkCommand.createMultiLine());

		cmds.add(new CommandIf());
		cmds.add(new CommandElse());
		cmds.add(new CommandEndif());

		cmds.add(new CommandLinkActivity());
		// addCommand(new CommandInnerConcurrent(system));

		return cmds;

	}

}
