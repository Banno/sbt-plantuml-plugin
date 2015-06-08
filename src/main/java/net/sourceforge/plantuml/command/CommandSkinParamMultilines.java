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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.StringUtils;

public class CommandSkinParamMultilines extends CommandMultilinesBracket<UmlDiagram> {

	static class Context {
		private List<String> strings = new ArrayList<String>();

		public void push(String s) {
			strings.add(s);
		}

		public void pop() {
			strings.remove(strings.size() - 1);
		}

		public String getFullParam() {
			final StringBuilder sb = new StringBuilder();
			for (String s : strings) {
				sb.append(s);
			}
			return sb.toString();
		}
	}

	private final static Pattern p1 = MyPattern
			.cmpile("^([\\w.]*(?:\\<\\<.*\\>\\>)?[\\w.]*)[%s]+(?:(\\{)|(.*))$|^\\}?$");

	public CommandSkinParamMultilines() {
		super("(?i)^skinparam[%s]*(?:[%s]+([\\w.]*(?:\\<\\<.*\\>\\>)?[\\w.]*))?[%s]*\\{$");
	}

	@Override
	protected boolean isLineConsistent(String line, int level) {
		line = StringUtils.trin(line);
		if (hasStartingQuote(line)) {
			return true;
		}
		return p1.matcher(line).matches();
	}

	private boolean hasStartingQuote(String line) {
		return MyPattern.mtches(line, "[%s]*[%q].*");
	}

	public CommandExecutionResult execute(UmlDiagram diagram, List<String> lines) {
		final Context context = new Context();
		final Matcher mStart = getStartingPattern().matcher(StringUtils.trin(lines.get(0)));
		if (mStart.find() == false) {
			throw new IllegalStateException();
		}
		if (mStart.group(1) != null) {
			context.push(mStart.group(1));
		}

		lines = new ArrayList<String>(lines.subList(1, lines.size() - 1));
		StringUtils.trim(lines, true);

		for (String s : lines) {
			assert s.length() > 0;
			if (hasStartingQuote(s)) {
				continue;
			}
			if (s.equals("}")) {
				context.pop();
				continue;
			}
			final Matcher m = p1.matcher(s);
			if (m.find() == false) {
				throw new IllegalStateException();
			}
			if (m.group(2) != null) {
				context.push(m.group(1));
			} else if (m.group(3) != null) {
				final String key = context.getFullParam() + m.group(1);
				diagram.setParam(key, m.group(3));
			} else {
				throw new IllegalStateException();
			}
		}

		return CommandExecutionResult.ok();
	}

}
