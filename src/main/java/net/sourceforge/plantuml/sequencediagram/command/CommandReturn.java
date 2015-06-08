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

import java.util.List;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.sequencediagram.AbstractMessage;
import net.sourceforge.plantuml.sequencediagram.EventWithDeactivate;
import net.sourceforge.plantuml.sequencediagram.LifeEventType;
import net.sourceforge.plantuml.sequencediagram.Message;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.skin.ArrowConfiguration;

public class CommandReturn extends SingleLineCommand<SequenceDiagram> {

	public CommandReturn() {
		super("(?i)^return[%s]*(.*)$");
	}

	@Override
	protected CommandExecutionResult executeArg(SequenceDiagram sequenceDiagram, List<String> arg) {

		Message message = sequenceDiagram.getActivatingMessage();
		boolean doDeactivation = true;
		if (message == null) {
			final EventWithDeactivate last = sequenceDiagram.getLastEventWithDeactivate();
			if (last instanceof Message == false) {
				return CommandExecutionResult.error("Nowhere to return to.");
			}
			message = (Message) last;
			doDeactivation = false;
		}

		final ArrowConfiguration arrow = message.getArrowConfiguration().withDotted();

		sequenceDiagram.addMessage(
				new Message(message.getParticipant2(), message.getParticipant1(), Display.getWithNewlines(arg
				.get(0)), arrow, sequenceDiagram.getNextMessageNumber()));

		if (doDeactivation) {
			final String error = sequenceDiagram.activate(message.getParticipant2(), LifeEventType.DEACTIVATE, null);
			if (error != null) {
				return CommandExecutionResult.error(error);
			}
		}
		return CommandExecutionResult.ok();

	}

}
