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
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.sequencediagram.InGroupableList;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class GroupingGraphicalElementHeader extends GroupingGraphicalElement {

	private final Component comp;
	private double endY;
	private final boolean isParallel;

	public GroupingGraphicalElementHeader(double currentY, Component comp, InGroupableList inGroupableList, boolean isParallel) {
		super(currentY, inGroupableList);
		this.comp = comp;
		this.isParallel = isParallel;
	}

	@Override
	public String toString() {
		return super.toString() + " " + (getInGroupableList() == null ? "no" : getInGroupableList().toString());
	}

	@Override
	final public double getPreferredWidth(StringBounder stringBounder) {
		return comp.getPreferredWidth(stringBounder) + 5;
	}

	@Override
	final public double getPreferredHeight(StringBounder stringBounder) {
		return comp.getPreferredHeight(stringBounder);
	}

	@Override
	protected void drawInternalU(UGraphic ug, double maxX, Context2D context) {
		if (isParallel) {
			return;
		}
		final StringBounder stringBounder = ug.getStringBounder();
		final double x1 = getInGroupableList().getMinX(stringBounder);
		final double x2 = getInGroupableList().getMaxX(stringBounder);
		ug = ug.apply(new UTranslate(x1, getStartingY()));
		double height = comp.getPreferredHeight(stringBounder);
		if (endY > 0) {
			height = endY - getStartingY();
		} else {
			// assert false;
			return;
		}
		final Dimension2D dim = new Dimension2DDouble(x2 - x1, height);
		comp.drawU(ug, new Area(dim), context);
	}

	public void setEndY(double y) {
		this.endY = y;
	}

}
