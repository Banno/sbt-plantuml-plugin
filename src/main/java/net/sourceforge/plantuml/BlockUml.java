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
package net.sourceforge.plantuml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.core.Diagram;

public class BlockUml {

	private final List<? extends CharSequence> data;
	private final int startLine;
	private Diagram system;

	private static final Pattern patternFilename = MyPattern.cmpile("^@start[^%s{}%g]+[%s{][%s%g]*([^%g]*?)[%s}%g]*$");

	BlockUml(String... strings) {
		this(Arrays.asList(strings), 0);
	}

	public BlockUml(List<? extends CharSequence> strings, int startLine) {
		this.startLine = startLine;
		final String s0 = StringUtils.trin(strings.get(0).toString());
		if (s0.startsWith("@start") == false) {
			throw new IllegalArgumentException();
		}
		this.data = new ArrayList<CharSequence>(strings);
	}

	public String getFilename() {
		if (OptionFlags.getInstance().isWord()) {
			return null;
		}
		final Matcher m = patternFilename.matcher(StringUtils.trin(data.get(0).toString()));
		final boolean ok = m.find();
		if (ok == false) {
			return null;
		}
		String result = m.group(1);
		final int x = result.indexOf(',');
		if (x != -1) {
			result = result.substring(0, x);
		}
		for (int i = 0; i < result.length(); i++) {
			final char c = result.charAt(i);
			if ("<>|".indexOf(c) != -1) {
				return null;
			}
		}
		return result;
	}

	public Diagram getDiagram() {
		if (system == null) {
			system = new PSystemBuilder().createPSystem(data);
		}
		return system;
	}

	public final int getStartLine() {
		return startLine;
	}

}
