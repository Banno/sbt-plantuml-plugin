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
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.utils.UniqueSequence;

public class CommandPackageEmpty extends SingleLineCommand<AbstractEntityDiagram> {

	public CommandPackageEmpty() {
		super(
				"(?i)^package[%s]+([%g][^%g]+[%g]|[^#%s{}]*)(?:[%s]+as[%s]+([\\p{L}0-9_.]+))?[%s]*(#[0-9a-fA-F]{6}|#?\\w+)?[%s]*\\{[%s]*\\}$");
	}

	@Override
	protected CommandExecutionResult executeArg(AbstractEntityDiagram diagram, List<String> arg) {
		final Code code;
		final String display;
		if (arg.get(1) == null) {
			if (StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(arg.get(0)).length() == 0) {
				code = Code.of("##" + UniqueSequence.getValue());
				display = null;
			} else {
				code = Code.of(StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(arg.get(0)));
				display = code.getFullName();
			}
		} else {
			display = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(arg.get(0));
			code = Code.of(arg.get(1));
		}
		final IGroup currentPackage = diagram.getCurrentGroup();
		final IEntity p = diagram.getOrCreateGroup(code, Display.getWithNewlines(display), GroupType.PACKAGE,
				currentPackage);
		final String color = arg.get(2);
		if (color != null) {
			p.setSpecificBackcolor(diagram.getSkinParam().getIHtmlColorSet().getColorIfValid(color));
		}
		diagram.endGroup();
		return CommandExecutionResult.ok();
	}

}
