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
package net.sourceforge.plantuml.skin;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;

public class Area {

	private final Dimension2D dimensionToUse;
	private double deltaX1;

	@Override
	public String toString() {
		return dimensionToUse.toString() + " (" + deltaX1 + ")";
	}

	public Area(Dimension2D dimensionToUse) {
		this.dimensionToUse = dimensionToUse;
	}

	public Area(double with, double height) {
		this(new Dimension2DDouble(with, height));
	}

	public Dimension2D getDimensionToUse() {
		return dimensionToUse;
	}

	public void setDeltaX1(double deltaX1) {
		this.deltaX1 = deltaX1;
	}

	public final double getDeltaX1() {
		return deltaX1;
	}

}
