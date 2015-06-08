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

import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.skin.VisibilityModifier;

public class CommandAddMethod extends SingleLineCommand<ClassDiagram> {

	public CommandAddMethod() {
		super("(?i)^([\\p{L}0-9_.]+|[%g][^%g]+[%g])[%s]+:[%s]+(.*)$");
	}

	@Override
	protected CommandExecutionResult executeArg(ClassDiagram system, List<String> arg) {
		final IEntity entity = system.getOrCreateLeaf(Code.of(arg.get(0)), null, null);

		final String field = arg.get(1);
		if (field.length() > 0 && VisibilityModifier.isVisibilityCharacter(field.charAt(0))) {
			system.setVisibilityModifierPresent(true);
		}
		entity.getBodier().addFieldOrMethod(field);
		return CommandExecutionResult.ok();
	}
}
