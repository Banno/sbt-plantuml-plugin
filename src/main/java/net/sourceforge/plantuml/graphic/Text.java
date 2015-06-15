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

public class Text implements HtmlCommand {

	private final String text;

	public static final Text NEWLINE = new Text("\\n");

	Text(String text) {
		this.text = text.replaceAll("\\\\\\[", "[").replaceAll("\\\\\\]", "]");
		if (text.indexOf('\n') != -1) {
			throw new IllegalArgumentException();
		}
		if (text.length() == 0) {
			throw new IllegalArgumentException();
		}
	}

	public String getText() {
		assert text.length() > 0;
		return text;
	}

	public boolean isNewline() {
		return text.equals("\\n");
	}
}
