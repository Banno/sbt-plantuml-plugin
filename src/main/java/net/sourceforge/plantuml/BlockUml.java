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
package net.sourceforge.plantuml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.utils.StartUtils;

public class BlockUml {

	private final List<CharSequence2> data;
	private final int startLine;
	private Diagram system;

	BlockUml(String... strings) {
		this(convert(strings), 0);
	}

	public String getFlashData() {
		final StringBuilder sb = new StringBuilder();
		for (CharSequence2 line : data) {
			sb.append(line);
			sb.append('\r');
			sb.append('\n');
		}
		return sb.toString();
	}

	public static List<CharSequence2> convert(String... strings) {
		return convert(Arrays.asList(strings));
	}

	public static List<CharSequence2> convert(List<String> strings) {
		final List<CharSequence2> result = new ArrayList<CharSequence2>();
		LineLocationImpl location = new LineLocationImpl("block", null);
		for (String s : strings) {
			location = location.oneLineRead();
			result.add(new CharSequence2Impl(s, location));
		}
		return result;
	}

	public BlockUml(List<CharSequence2> strings, int startLine) {
		this.startLine = startLine;
		final CharSequence2 s0 = strings.get(0).trin();
		if (StartUtils.startsWithSymbolAnd("start", s0) == false) {
			throw new IllegalArgumentException();
		}
		this.data = new ArrayList<CharSequence2>(strings);
	}

	public String getFileOrDirname() {
		if (OptionFlags.getInstance().isWord()) {
			return null;
		}
		final Matcher2 m = StartUtils.patternFilename.matcher(StringUtils.trin(data.get(0).toString()));
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
		if (result.startsWith("file://")) {
			result = result.substring("file://".length());
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

	public final List<CharSequence2> getData() {
		return data;
	}

}
