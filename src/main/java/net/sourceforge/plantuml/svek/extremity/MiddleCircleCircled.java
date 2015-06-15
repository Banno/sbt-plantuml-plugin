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

import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class MiddleCircleCircled extends Extremity {

	private final double angle;
	private final MiddleCircleCircledMode mode;
	private final double radius1 = 6;
	private final UEllipse circle = new UEllipse(2 * radius1, 2 * radius1);

	private final double radius2 = 10;
	private final UEllipse bigcircle = new UEllipse(2 * radius2, 2 * radius2);

	public MiddleCircleCircled(double angle, MiddleCircleCircledMode mode) {
		this.angle = angle;
		this.mode = mode;
	}

	public void drawU(UGraphic ug) {
		ug = ug.apply(new UChangeBackColor(HtmlColorUtils.WHITE));
		if (mode == MiddleCircleCircledMode.BOTH) {
			ug.apply(new UChangeColor(HtmlColorUtils.WHITE)).apply(new UTranslate(-radius2, -radius2)).draw(bigcircle);
		}

		ug = ug.apply(new UStroke(1.5));

		final double d = 0;
		if (mode == MiddleCircleCircledMode.MODE1 || mode == MiddleCircleCircledMode.BOTH) {
			final UEllipse arc1 = new UEllipse(2 * radius2, 2 * radius2, angle, 90);
			ug.apply(new UTranslate(-radius2 + d, -radius2 + d)).draw(arc1);
		}
		if (mode == MiddleCircleCircledMode.MODE2 || mode == MiddleCircleCircledMode.BOTH) {
			final UEllipse arc2 = new UEllipse(2 * radius2, 2 * radius2, angle + 180, 90);
			ug.apply(new UTranslate(-radius2 + d, -radius2 + d)).draw(arc2);
		}
		ug.apply(new UTranslate(-radius1, -radius1)).draw(circle);
	}

}
