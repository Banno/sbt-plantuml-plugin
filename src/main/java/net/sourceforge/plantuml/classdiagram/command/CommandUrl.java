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
package net.sourceforge.plantuml.classdiagram.command;

import java.util.List;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlBuilder.ModeUrl;
import net.sourceforge.plantuml.classdiagram.AbstractEntityDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.IEntity;

public class CommandUrl extends SingleLineCommand<AbstractEntityDiagram> {

	public CommandUrl() {
		super("(?i)^url[%s]*(?:of|for)?[%s]+([\\p{L}0-9_.]+|[%g][^%g]+[%g])[%s]+(?:is)?[%s]*(" + UrlBuilder.getRegexp() + ")$");
	}

	@Override
	protected CommandExecutionResult executeArg(AbstractEntityDiagram diagram, List<String> arg) {
		final Code code = Code.of(arg.get(0));
		final String urlString = arg.get(1);
		final IEntity entity;
		if (diagram.leafExist(code)) {
			entity = diagram.getOrCreateLeaf(code, null, null);
		} else if (diagram.isGroup(code)) {
			entity = diagram.getGroup(code);
		} else {
			return CommandExecutionResult.error(code + " does not exist");
		}
		// final IEntity entity = diagram.getOrCreateLeaf(code, null);
		final UrlBuilder urlBuilder = new UrlBuilder(diagram.getSkinParam().getValue("topurl"), ModeUrl.STRICT);
		final Url url = urlBuilder.getUrl(urlString);
		entity.addUrl(url);
		return CommandExecutionResult.ok();
	}

}
