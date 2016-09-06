/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
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

import net.sourceforge.plantuml.PSystemError;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.regex.RegexConcat;
import net.sourceforge.plantuml.command.regex.RegexResult;
import net.sourceforge.plantuml.core.Diagram;

public abstract class SingleLineCommand2<S extends Diagram> implements Command<S> {

	private final RegexConcat pattern;

	public SingleLineCommand2(RegexConcat pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException();
		}
		if (pattern.getPattern().startsWith("^") == false || pattern.getPattern().endsWith("$") == false) {
			throw new IllegalArgumentException("Bad pattern " + pattern.getPattern());
		}

		this.pattern = pattern;
	}

	public String[] getDescription() {
		return new String[] { pattern.getPattern() };
	}

	final public CommandControl isValid(BlocLines lines) {
		if (lines.size() != 1) {
			return CommandControl.NOT_OK;
		}
		lines = lines.removeInnerComments();
		if (isCommandForbidden()) {
			return CommandControl.NOT_OK;
		}
		final String line = StringUtils.trin(lines.getFirst499());
		final boolean result = pattern.match(line);
		if (result) {
			actionIfCommandValid();
		}
		return result ? CommandControl.OK : CommandControl.NOT_OK;
	}

	protected boolean isCommandForbidden() {
		return false;
	}

	protected void actionIfCommandValid() {
	}

	public final CommandExecutionResult execute(S system, BlocLines lines) {
		if (lines.size() != 1) {
			throw new IllegalArgumentException();
		}
		lines = lines.removeInnerComments();
		final String line = StringUtils.trin(lines.getFirst499());
		if (isForbidden(line)) {
			return CommandExecutionResult.error("Forbidden line " + line);
		}

		final RegexResult arg = pattern.matcher(line);
		if (arg == null) {
			return CommandExecutionResult.error("Cannot parse line " + line);
		}
		if (system instanceof PSystemError) {
			return CommandExecutionResult.error("PSystemError cannot be cast");
		}
		// System.err.println("lines="+lines);
		// System.err.println("pattern="+pattern.getPattern());
		return executeArg(system, arg);
	}

	protected boolean isForbidden(CharSequence line) {
		return false;
	}

	protected abstract CommandExecutionResult executeArg(S system, RegexResult arg);

}
