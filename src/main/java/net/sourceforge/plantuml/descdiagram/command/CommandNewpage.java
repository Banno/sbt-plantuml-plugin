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
package net.sourceforge.plantuml.descdiagram.command;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.NewpagedDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.UmlDiagramFactory;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;

public class CommandNewpage extends SingleLineCommand2<AbstractPSystem> {

	private final UmlDiagramFactory factory;

	public CommandNewpage(UmlDiagramFactory factory) {
		super(getRegexConcat());
		this.factory = factory;
	}

	static RegexConcat getRegexConcat() {
		return new RegexConcat(new RegexLeaf("^"), //
				new RegexLeaf("newpage"), //
				new RegexLeaf("$"));
	}

	@Override
	protected CommandExecutionResult executeArg(AbstractPSystem diagram, RegexResult arg) {
		// NewpagedDiagram result = NewpagedDiagram.newpage(diagram, factory.createEmptyDiagram());
		NewpagedDiagram result = new NewpagedDiagram(diagram, factory.createEmptyDiagram());
		return CommandExecutionResult.newDiagram(result);
	}
}
