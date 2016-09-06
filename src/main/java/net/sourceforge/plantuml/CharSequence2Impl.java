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

public class CharSequence2Impl implements CharSequence2 {

	private final CharSequence s;
	private final LineLocation location;

	public CharSequence2Impl(CharSequence s, LineLocation location) {
		if (s == null) {
			throw new IllegalArgumentException();
		}
		this.s = s;
		this.location = location;
	}

	public static CharSequence2 errorPreprocessor(CharSequence s, LineLocation lineLocation) {
		return new CharSequence2Impl(s, lineLocation);
	}

	public int length() {
		return s.length();
	}

	public char charAt(int index) {
		return s.charAt(index);
	}

	public CharSequence2 subSequence(int start, int end) {
		return new CharSequence2Impl(s.subSequence(start, end), location);
	}

	public CharSequence toCharSequence() {
		return s;
	}

	@Override
	public String toString() {
		return s.toString();
	}

	public String toString2() {
		return s.toString();
	}

	public LineLocation getLocation() {
		return location;
	}

	public CharSequence2 trin() {
		return new CharSequence2Impl(StringUtils.trin(s.toString()), location);
	}

	public boolean startsWith(String start) {
		return s.toString().startsWith(start);
	}

}
