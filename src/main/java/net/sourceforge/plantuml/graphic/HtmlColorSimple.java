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

import java.awt.Color;

import net.sourceforge.plantuml.ugraphic.ColorChangerMonochrome;

public class HtmlColorSimple implements HtmlColor {


	private final Color color;
	private final boolean monochrome;

	@Override
	public int hashCode() {
		return color.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof HtmlColorSimple == false) {
			return false;
		}
		return this.color.equals(((HtmlColorSimple) other).color);
	}


	HtmlColorSimple(Color c, boolean monochrome) {
		this.color = c;
		this.monochrome = monochrome;
	}


	public Color getColor999() {
		return color;
	}

	public HtmlColorSimple asMonochrome() {
		if (monochrome) {
			throw new IllegalStateException();
		}
		return new HtmlColorSimple(new ColorChangerMonochrome().getChangedColor(color), true);
	}

}
