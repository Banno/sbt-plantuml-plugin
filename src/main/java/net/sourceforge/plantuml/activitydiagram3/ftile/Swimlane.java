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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import net.sourceforge.plantuml.SpecificBackcolorable;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Swimlane implements SpecificBackcolorable {

	private final String name;
	private HtmlColor color;
	private Display display;

	private UTranslate translate = new UTranslate();
	private double totalWidth;

	public Swimlane(String name) {
		this.name = name;
		this.display = Display.getWithNewlines(name);

	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public Display getDisplay() {
		return display;
	}

	public void setDisplay(Display label) {
		this.display = label;
	}

	public final UTranslate getTranslate() {
		return translate;
	}

	public final void setTranslateAndWidth(UTranslate translate, double totalWidth) {
		this.translate = translate;
		this.totalWidth = totalWidth;
	}

	public HtmlColor getSpecificBackColor() {
		return color;
	}

	public void setSpecificBackcolor(HtmlColor specificBackcolor) {
		this.color = specificBackcolor;
	}

	public final double getTotalWidth() {
		return totalWidth;
	}

}
