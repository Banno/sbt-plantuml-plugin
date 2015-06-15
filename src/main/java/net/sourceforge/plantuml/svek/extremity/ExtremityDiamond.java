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
package net.sourceforge.plantuml.svek.extremity;

import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPolygon;

class ExtremityDiamond extends Extremity {

	private UPolygon polygon = new UPolygon();
	private final boolean fill;

	public ExtremityDiamond(Point2D p1, double angle, boolean fill) {
		this.fill = fill;
		angle = manageround(angle);
		polygon.addPoint(0, 0);
		final int xAile = 6;
		final int yOuverture = 4;
		polygon.addPoint(-xAile, -yOuverture);
		polygon.addPoint(-xAile * 2, 0);
		polygon.addPoint(-xAile, yOuverture);
		polygon.addPoint(0, 0);
		polygon.rotate(angle + Math.PI / 2);
		polygon = polygon.translate(p1.getX(), p1.getY());
	}

	public void drawU(UGraphic ug) {
		if (fill) {
			ug = ug.apply(new UChangeBackColor(ug.getParam().getColor()));
		}
		ug.draw(polygon);
	}

}
