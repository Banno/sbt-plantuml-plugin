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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.command.regex.MyPattern;

class SizeChange implements FontChange {

	static private final Pattern sizePattern = MyPattern.cmpile("(?i)" + Splitter.fontSizePattern2);

	private final Integer size;

	SizeChange(String s) {
		final Matcher matcherSize = sizePattern.matcher(s);
		if (matcherSize.find() == false) {
			throw new IllegalArgumentException();
		}
		size = new Integer(matcherSize.group(1));
	}

	Integer getSize() {
		return size;
	}

	public FontConfiguration apply(FontConfiguration initial) {
		return initial.changeSize(size);
	}

}
