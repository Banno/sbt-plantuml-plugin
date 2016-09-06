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
package net.sourceforge.plantuml.preproc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;

public class Defines {

	private final Map<String, String> values = new LinkedHashMap<String, String>();
	private final Map<String, String> savedState = new LinkedHashMap<String, String>();

	public void define(String name, List<String> value) {
		values.put(name, addLineReturn(value));
	}

	private String addLineReturn(List<String> value) {
		if (value == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		for (final Iterator<String> it = value.iterator(); it.hasNext();) {
			sb.append(it.next());
			if (it.hasNext()) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}

	public boolean isDefine(String name) {
		for (String key : values.keySet()) {
			if (key.equals(name) || key.startsWith(name + "(")) {
				return true;
			}
		}
		return false;
	}

	public void undefine(String name) {
		values.remove(name);
	}

	public List<String> applyDefines(String line) {
		for (Map.Entry<String, String> ent : values.entrySet()) {
			final String key = ent.getKey();
			if (ent.getValue() == null) {
				continue;
			}
			final String value = Matcher.quoteReplacement(ent.getValue());
			if (key.contains("(")) {
				line = applyMethod(line, key, value);
			} else {
				final String regex = "\\b" + key + "\\b";
				line = line.replaceAll(regex, value);
			}
		}
		return Arrays.asList(line.split("\n"));
	}

	private String applyMethod(String line, final String key, final String value) {
		final StringTokenizer st = new StringTokenizer(key, "(),");
		final String fctName = st.nextToken();
		String newValue = value;
		final StringBuilder regex = new StringBuilder("\\b" + fctName + "\\(");
		int i = 1;

		while (st.hasMoreTokens()) {
			regex.append("(?:(?:\\s*\"([^\"]*)\"\\s*)|(?:\\s*'([^']*)'\\s*)|\\s*" + "((?:\\([^()]*\\)|[^,])*?)" + ")");
			final String var1 = st.nextToken();
			final String var2 = "(##" + var1 + "\\b)|(\\b" + var1 + "##)|(\\b" + var1 + "\\b)";
			newValue = newValue.replaceAll(var2, "\\$" + i + "\\$" + (i + 1) + "\\$" + (i + 2));
			i += 3;
			if (st.hasMoreTokens()) {
				regex.append(",");
			}
		}
		regex.append("\\)");
		line = line.replaceAll(regex.toString(), newValue);
		return line;
	}

	public void saveState() {
		if (savedState.size() > 0) {
			throw new IllegalStateException();
		}
		this.savedState.putAll(values);

	}

	public void restoreState() {
		this.values.clear();
		this.values.putAll(savedState);

	}

}
