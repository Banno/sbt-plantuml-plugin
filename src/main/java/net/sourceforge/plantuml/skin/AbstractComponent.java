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
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public abstract class AbstractComponent implements Component {

//	final protected void stroke(Graphics2D g2d, float dash, float thickness) {
//		final float[] style = { dash, dash };
//		g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, style, 0));
//	}
//
//	final protected UGraphic stroke(UGraphic ug, double dashVisible, double dashSpace, double thickness) {
//		return ug.apply(new UStroke(dashVisible, dashSpace, thickness));
//	}
//
//	final protected void stroke(Graphics2D g2d, float dash) {
//		stroke(g2d, dash, 1);
//	}
//
//	final protected UGraphic stroke(UGraphic ug, double dashVisible, double dashSpace) {
//		return stroke(ug, dashVisible, dashSpace, 1);
//	}

	abstract protected void drawInternalU(UGraphic ug, Area area);

	protected void drawBackgroundInternalU(UGraphic ug, Area area) {
	}

	public final void drawU(UGraphic ug, Area area, Context2D context) {
		ug = ug.apply(new UTranslate(getPaddingX(), getPaddingY()));
		if (context.isBackground()) {
			drawBackgroundInternalU(ug, area);
		} else {
			drawInternalU(ug, area);
		}
	}

	public double getPaddingX() {
		return 0;
	}

	public double getPaddingY() {
		return 0;
	}

	public abstract double getPreferredWidth(StringBounder stringBounder);

	public abstract double getPreferredHeight(StringBounder stringBounder);

	public final Dimension2D getPreferredDimension(StringBounder stringBounder) {
		final double w = getPreferredWidth(stringBounder);
		final double h = getPreferredHeight(stringBounder);
		return new Dimension2DDouble(w, h);
	}

}
