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
package net.sourceforge.plantuml.graph;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.command.regex.MyPattern;

public class LinkString {

	final private static Pattern p = MyPattern.cmpile("(.*)->(.*)");

	final private String node1;
	final private String node2;

	public LinkString(String desc) {
		final Matcher m = p.matcher(desc);
		if (m.find() == false) {
			throw new IllegalArgumentException();
		}
		node1 = m.group(1);
		node2 = m.group(2);

	}

	public final String getNode1() {
		return node1;
	}

	public final String getNode2() {
		return node2;
	}
}
