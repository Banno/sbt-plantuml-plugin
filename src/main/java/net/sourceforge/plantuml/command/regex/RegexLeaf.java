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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class RegexLeaf implements IRegex {

	private final Pattern pattern;
	private final String name;

	private int count = -1;

	public RegexLeaf(String regex) {
		this(null, regex);
	}

	public RegexLeaf(String name, String regex) {
		this.pattern = MyPattern.cmpile(regex, Pattern.CASE_INSENSITIVE);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getPattern() {
		return pattern.pattern();
	}

	public int count() {
		if (count == -1) {
			count = pattern.matcher("").groupCount();
		}
		return count;
	}

	public Map<String, RegexPartialMatch> createPartialMatch(Iterator<String> it) {
		final RegexPartialMatch m = new RegexPartialMatch(name);
		for (int i = 0; i < count(); i++) {
			final String group = it.next();
			m.add(group);
		}
		if (name == null) {
			return Collections.emptyMap();
		}
		return Collections.singletonMap(name, m);
	}

}
