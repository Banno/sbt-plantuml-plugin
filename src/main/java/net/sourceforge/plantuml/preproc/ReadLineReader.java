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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import net.sourceforge.plantuml.CharSequence2;
import net.sourceforge.plantuml.CharSequence2Impl;
import net.sourceforge.plantuml.LineLocation;
import net.sourceforge.plantuml.LineLocationImpl;

public class ReadLineReader implements ReadLine {

	// private static final int LIMIT = 850;
	private final BufferedReader br;
	private LineLocationImpl location;

	public ReadLineReader(Reader reader, String desc, LineLocation parent) {
		br = new BufferedReader(reader);
		location = new LineLocationImpl(desc, parent);
	}

	public ReadLineReader(Reader reader, String desc) {
		this(reader, desc, null);
	}

	public CharSequence2 readLine() throws IOException {
		String s = br.readLine();
		location = location.oneLineRead();
		if (s == null) {
			return null;
		}
		// if (s.length() > LIMIT) {
		// Log.debug("Line truncated from " + s.length() + " to " + LIMIT);
		// s = s.substring(0, LIMIT);
		// }
		if (s.startsWith("\uFEFF")) {
			s = s.substring(1);
		}
		s = s.replace('\u2013', '-');
		// s = s.replace('\u00A0', ' ');
		// s = s.replace('\u201c', '\"');
		// s = s.replace('\u201d', '\"');
		// s = s.replace('\u00ab', '\"');
		// s = s.replace('\u00bb', '\"');
		// s = s.replace('\u2018', '\'');
		// s = s.replace('\u2019', '\'');
		// for (int i = 0; i < s.length(); i++) {
		// char c = s.charAt(i);
		// System.err.println("X " + Integer.toHexString((int) c) + " " + c);
		// }
		return new CharSequence2Impl(s, location);
	}

	public void close() throws IOException {
		br.close();
	}

}
