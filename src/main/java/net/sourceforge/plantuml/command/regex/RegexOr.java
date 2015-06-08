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
package net.sourceforge.plantuml.command.regex;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class RegexOr extends RegexComposed implements IRegex {

	private final Pattern full;
	private final String name;

	public RegexOr(IRegex... partial) {
		this(null, partial);
	}

	public RegexOr(String name, IRegex... partial) {
		super(partial);
		this.name = name;
		final StringBuilder sb = new StringBuilder("(");
		if (name == null) {
			sb.append("?:");
		}
		for (IRegex p : partial) {
			sb.append(p.getPattern());
			sb.append("|");
		}
		sb.setLength(sb.length() - 1);
		sb.append(')');
		this.full = MyPattern.cmpileNockeck(sb.toString());
	}

	@Override
	protected Pattern getFull() {
		return full;
	}

	protected int getStartCount() {
		return 1;
	}

	final public Map<String, RegexPartialMatch> createPartialMatch(Iterator<String> it) {
		final Map<String, RegexPartialMatch> result = new HashMap<String, RegexPartialMatch>();
		final String fullGroup = name == null ? null : it.next();
		result.putAll(super.createPartialMatch(it));
		if (name != null) {
			final RegexPartialMatch m = new RegexPartialMatch(name);
			m.add(fullGroup);
			result.put(name, m);
		}
		return result;
	}

}
