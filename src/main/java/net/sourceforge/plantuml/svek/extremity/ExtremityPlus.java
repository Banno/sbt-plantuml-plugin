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

import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class ExtremityPlus extends Extremity {

	private final UEllipse circle;
	private final double px;
	private final double py;
	private final double radius = 8;
	private final double angle;

	public ExtremityPlus(Point2D p1, double angle) {
		this.angle = angle;
		this.circle = new UEllipse(2 * radius, 2 * radius);
		this.px = p1.getX() - radius + radius * Math.sin(angle);
		this.py = p1.getY() - radius - radius * Math.cos(angle);
	}

	public void drawU(UGraphic ug) {
		ug.apply(new UChangeBackColor(HtmlColorUtils.WHITE)).apply(new UTranslate(px + 0, py + 0)).draw(circle);
		drawLine(ug, 0, 0, getPointOnCircle(angle - Math.PI / 2), getPointOnCircle(angle + Math.PI / 2));
		drawLine(ug, 0, 0, getPointOnCircle(angle), getPointOnCircle(angle + Math.PI));
	}

	private Point2D getPointOnCircle(double angle) {
		final double x = px + radius + radius * Math.cos(angle);
		final double y = py + radius + radius * Math.sin(angle);
		return new Point2D.Double(x, y);
	}

	static private void drawLine(UGraphic ug, double x, double y, Point2D p1, Point2D p2) {
		final double dx = p2.getX() - p1.getX();
		final double dy = p2.getY() - p1.getY();
		ug.apply(new UTranslate(x + p1.getX(), y + p1.getY())).draw(new ULine(dx, dy));

	}

}
