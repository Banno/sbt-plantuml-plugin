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
package net.sourceforge.plantuml.cucadiagram;

import net.sourceforge.plantuml.ugraphic.UStroke;

public enum LinkStyle {

	NORMAL, DASHED, DOTTED, BOLD, INVISIBLE,

	DOUBLE_tobedone, __toremove_INTERFACE_PROVIDER, __toremove_INTERFACE_USER;

	public static UStroke getStroke(LinkStyle style) {
		return getStroke(style, 1);
	}

	public static UStroke getStroke(LinkStyle style, double thickness) {
		if (style == LinkStyle.DASHED) {
			return new UStroke(6, 6, thickness);
		}
		if (style == LinkStyle.DOTTED) {
			return new UStroke(1, 3, thickness);
		}
		if (style == LinkStyle.BOLD) {
			return new UStroke(2.5);
		}
		return new UStroke();
	}

	public static LinkStyle fromString(String s) {
		if ("dashed".equalsIgnoreCase(s)) {
			return DASHED;
		}
		if ("dotted".equalsIgnoreCase(s)) {
			return DOTTED;
		}
		if ("bold".equalsIgnoreCase(s)) {
			return BOLD;
		}
		if ("hidden".equalsIgnoreCase(s)) {
			return INVISIBLE;
		}
		return LinkStyle.NORMAL;
	}

}
