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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.StringUtils;

public abstract class SingleLineCommand<S extends Diagram> implements Command<S> {

	private final Pattern pattern;

	public SingleLineCommand(String pattern) {
		if (pattern == null) {
			throw new IllegalArgumentException();
		}
		if (pattern.startsWith("(?i)^") == false || pattern.endsWith("$") == false) {
			throw new IllegalArgumentException("Bad pattern " + pattern);
		}

		this.pattern = MyPattern.cmpile(pattern);
	}

	public String[] getDescription() {
		return new String[] { pattern.pattern() };
	}

	final public CommandControl isValid(List<String> lines) {
		if (lines.size() != 1) {
			return CommandControl.NOT_OK;
		}
		if (isCommandForbidden()) {
			return CommandControl.NOT_OK;
		}
		final String line = StringUtils.trin(lines.get(0));
		final Matcher m = pattern.matcher(line);
		final boolean result = m.find();
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

	public final CommandExecutionResult execute(S system, List<String> lines) {
		if (lines.size() != 1) {
			throw new IllegalArgumentException();
		}
		final String line = StringUtils.trin(lines.get(0));
		if (isForbidden(line)) {
			return CommandExecutionResult.error("Forbidden line " + line);
		}
		final List<String> arg = getSplit(line);
		if (arg == null) {
			return CommandExecutionResult.error("Cannot parse line " + line);
		}
		return executeArg(system, arg);
	}

	protected boolean isForbidden(String line) {
		return false;
	}

	protected abstract CommandExecutionResult executeArg(S system, List<String> arg);

	final public List<String> getSplit(String line) {
		return StringUtils.getSplit(pattern, line);
	}

}
