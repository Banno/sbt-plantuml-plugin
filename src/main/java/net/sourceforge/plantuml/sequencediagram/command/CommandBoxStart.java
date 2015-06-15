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
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;

public class CommandBoxStart extends SingleLineCommand<SequenceDiagram> {

	public CommandBoxStart() {
		super("(?i)^box(?:[%s]+[%g]([^%g]+)[%g])?(?:[%s]+(#\\w+))?$");
	}

	@Override
	protected CommandExecutionResult executeArg(SequenceDiagram diagram, List<String> arg) {
		if (diagram.isBoxPending()) {
			return CommandExecutionResult.error("Box cannot be nested");
		}
		final HtmlColor color = diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(arg.get(1));
		final String title = arg.get(0) == null ? "" : arg.get(0);
		diagram.boxStart(Display.getWithNewlines(title), color);
		return CommandExecutionResult.ok();
	}

}
