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
package net.sourceforge.plantuml.ugraphic;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class UPolygon extends AbstractShadowable {

	private final List<Point2D.Double> all = new ArrayList<Point2D.Double>();

	private MinMax minmax = MinMax.getEmpty(false);

	public UPolygon() {
	}

	public UPolygon(List<Point2D.Double> points) {
		all.addAll(points);
		for (Point2D.Double pt : all) {
			manageMinMax(pt.getX(), pt.getY());
		}
	}

	public void addPoint(double x, double y) {
		all.add(new Point2D.Double(x, y));
		manageMinMax(x, y);
	}

	private void manageMinMax(double x, double y) {
		minmax = minmax.addPoint(x, y);
	}

	public List<Point2D.Double> getPoints() {
		return all;
	}

	public UPolygon translate(double dx, double dy) {
		final UPolygon result = new UPolygon();
		for (Point2D.Double pt : all) {
			result.addPoint(pt.x + dx, pt.y + dy);
		}
		return result;
	}

	public void rotate(double theta) {
		final AffineTransform rotate = AffineTransform.getRotateInstance(theta);
		for (Point2D.Double pt : all) {
			rotate.transform(pt, pt);
		}
	}

	@Override
	public String toString() {
		return super.toString() + " " + all;
	}

	public double getHeight() {
		return minmax.getHeight();
	}

	public double getWidth() {
		return minmax.getWidth();
	}

	public double getMinX() {
		return minmax.getMinX();
	}

	public double getMinY() {
		return minmax.getMinY();
	}

	public double getMaxX() {
		return minmax.getMaxX();

	}

	public double getMaxY() {
		return minmax.getMaxY();
	}

	public MinMax getMinMax() {
		return minmax;
	}

	public double[] getPointArray(double x, double y) {
		final double points[] = new double[getPoints().size() * 2];
		int i = 0;

		for (Point2D pt : getPoints()) {
			points[i++] = pt.getX() + x;
			points[i++] = pt.getY() + y;
		}
		return points;
	}
}
