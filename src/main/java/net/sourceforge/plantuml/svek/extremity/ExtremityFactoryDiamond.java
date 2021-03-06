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
package net.sourceforge.plantuml.svek.extremity;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.svek.AbstractExtremityFactory;

public class ExtremityFactoryDiamond extends AbstractExtremityFactory implements ExtremityFactory {

	private final boolean fill;
	private final HtmlColor backgroundColor;

	@Override
	public UDrawable createUDrawable(Point2D p0, double angle) {
		return new ExtremityDiamond(p0, angle - Math.PI / 2, fill, backgroundColor);
	}

	public ExtremityFactoryDiamond(boolean fill, HtmlColor backgroundColor) {
		this.fill = fill;
		this.backgroundColor = backgroundColor;
	}

	public UDrawable createUDrawable(Point2D p0, Point2D p1, Point2D p2) {
		final double ortho = atan2(p0, p2);
		return new ExtremityDiamond(p1, ortho, fill, backgroundColor);
	}

}
