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
package net.sourceforge.plantuml.activitydiagram3;

import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;

public class LinkRendering {

	private final HtmlColor color;
	private Display display;

	public LinkRendering(HtmlColor color) {
		this.color = color;
	}

	public void setDisplay(Display display) {
		this.display = display;
	}

	public Display getDisplay() {
		return display;
	}

	public HtmlColor getColor() {
		return color;
	}

	@Override
	public String toString() {
		return super.toString() + " " + display + " " + color;
	}

	public static HtmlColor getColor(LinkRendering inLinkRendering, HtmlColor defaultColor) {
		if (inLinkRendering == null || inLinkRendering.getColor() == null) {
			return defaultColor;
		}
		return inLinkRendering.getColor();
	}

	public static HtmlColor getColor(HtmlColor col, HtmlColor defaultColor) {
		if (col == null) {
			return defaultColor;
		}
		return col;
	}

	public static Display getDisplay(LinkRendering linkRendering) {
		if (linkRendering == null) {
			return null;
		}
		return linkRendering.getDisplay();
	}

}
