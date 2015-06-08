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
package net.sourceforge.plantuml.graphic;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.StringUtils;

public class SvgAttributes {

	private final Map<String, String> attributes = new TreeMap<String, String>();

	public SvgAttributes() {
	}

	private SvgAttributes(SvgAttributes other) {
		this.attributes.putAll(other.attributes);
	}

	public SvgAttributes(String args) {
		final Pattern p = MyPattern.cmpile("(\\w+)\\s*=\\s*([%g][^%g]*[%g]|(?:\\w+))");
		final Matcher m = p.matcher(args);
		while (m.find()) {
			attributes.put(m.group(1), StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(m.group(2)));
		}
	}

	public Map<String, String> attributes() {
		return Collections.unmodifiableMap(attributes);
	}

	public SvgAttributes add(String key, String value) {
		final SvgAttributes result = new SvgAttributes(this);
		result.attributes.put(key, value);
		return result;
	}

	public SvgAttributes add(SvgAttributes toBeAdded) {
		final SvgAttributes result = new SvgAttributes(this);
		result.attributes.putAll(toBeAdded.attributes);
		return result;
	}

}
