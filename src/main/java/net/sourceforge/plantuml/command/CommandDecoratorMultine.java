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

import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.core.Diagram;

public class CommandDecoratorMultine<D extends Diagram> implements Command<D> {

	private final SingleLineCommand2<D> cmd;

	public CommandDecoratorMultine(SingleLineCommand2<D> cmd) {
		this.cmd = cmd;
	}

	public CommandExecutionResult execute(D diagram, List<String> lines) {
		final String concat = concat(lines);
		return cmd.execute(diagram, Collections.singletonList(concat));
	}

	public CommandControl isValid(List<String> lines) {
		if (cmd.isCommandForbidden()) {
			return CommandControl.NOT_OK;
		}
		final String concat = concat(lines);
		if (cmd.isForbidden(concat)) {
			return CommandControl.NOT_OK;
		}
		final CommandControl tmp = cmd.isValid(Collections.singletonList(concat));
		if (tmp == CommandControl.OK_PARTIAL) {
			throw new IllegalStateException();
		}
		if (tmp == CommandControl.OK) {
			return tmp;
		}
		return CommandControl.OK_PARTIAL;
	}

	private String concat(List<String> lines) {
		final StringBuilder sb = new StringBuilder();
		for (String line : lines) {
			sb.append(line);
			sb.append(StringUtils.hiddenNewLine());
		}
		return sb.substring(0, sb.length() - 1);
	}

	public String[] getDescription() {
		return cmd.getDescription();
	}

}
