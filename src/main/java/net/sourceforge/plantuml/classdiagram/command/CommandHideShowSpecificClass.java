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

import net.sourceforge.plantuml.classdiagram.ClassDiagram;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.SingleLineCommand2;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexLeaf;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.LeafType;

public class CommandHideShowSpecificClass extends SingleLineCommand2<ClassDiagram> {

	public CommandHideShowSpecificClass() {
		super(getRegexConcat());
	}

	static RegexConcat getRegexConcat() {
		return new RegexConcat(new RegexLeaf("^"), //
				new RegexLeaf("COMMAND", "(hide|show)"), //
				new RegexLeaf("[%s]+"), //
				new RegexLeaf("CODE", "(" + CommandCreateClass.CODE + ")"), //
				new RegexLeaf("$"));
	}

	@Override
	protected CommandExecutionResult executeArg(ClassDiagram classDiagram, RegexResult arg) {

		final String codeString = arg.get("CODE", 0);
		if (codeString.equals("class")) {
			classDiagram.hideOrShow(LeafType.CLASS, arg.get("COMMAND", 0).equalsIgnoreCase("show"));
		} else if (codeString.equals("interface")) {
			classDiagram.hideOrShow(LeafType.INTERFACE, arg.get("COMMAND", 0).equalsIgnoreCase("show"));
		} else {
			final Code code = Code.of(codeString);
			final ILeaf leaf = classDiagram.getEntityFactory().getLeafs().get(code);
			if (leaf == null) {
				return CommandExecutionResult.error("Class does not exist : " + code.getFullName());
			}
			classDiagram.hideOrShow(leaf, arg.get("COMMAND", 0).equalsIgnoreCase("show"));
		}
		return CommandExecutionResult.ok();
	}
}
