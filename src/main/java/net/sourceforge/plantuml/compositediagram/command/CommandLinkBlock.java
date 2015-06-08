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
package net.sourceforge.plantuml.compositediagram.command;

import java.util.List;

import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand;
import net.sourceforge.plantuml.compositediagram.CompositeDiagram;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LinkDecor;
import net.sourceforge.plantuml.cucadiagram.LinkType;

public class CommandLinkBlock extends SingleLineCommand<CompositeDiagram> {

	public CommandLinkBlock() {
		super("(?i)^([\\p{L}0-9_.]+)[%s]*(\\[\\]|\\*\\))?([=-]+|\\.+)(\\[\\]|\\(\\*)?[%s]*([\\p{L}0-9_.]+)[%s]*(?::[%s]*(\\S*+))?$");
	}

	@Override
	protected CommandExecutionResult executeArg(CompositeDiagram diagram, List<String> arg) {
		final IEntity cl1 = diagram.getOrCreateLeaf(Code.of(arg.get(0)), null, null);
		final IEntity cl2 = diagram.getOrCreateLeaf(Code.of(arg.get(4)), null, null);

		final String deco1 = arg.get(1);
		final String deco2 = arg.get(3);
		LinkType linkType = new LinkType(getLinkDecor(deco1), getLinkDecor(deco2));
		
		if ("*)".equals(deco1)) {
			linkType = linkType.getInterfaceProvider();
		} else if ("(*".equals(deco2)) {
			linkType = linkType.getInterfaceUser();
		}

		final String queue = arg.get(2);

		final Link link = new Link(cl1, cl2, linkType, Display.getWithNewlines(arg.get(5)), queue.length());
		diagram.addLink(link);
		return CommandExecutionResult.ok();
	}

	private LinkDecor getLinkDecor(String s) {
		if ("[]".equals(s)) {
			return LinkDecor.SQUARRE_toberemoved;
		}
		return LinkDecor.NONE;
	}

}
