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
package net.sourceforge.plantuml.project2;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class RowMerge implements Row {

	private final Row r1;
	private final Row r2;

	public RowMerge(Row r1, Row r2) {
		this.r1 = r1;
		this.r2 = r2;
	}

	public TextBlock asTextBloc(final TimeConverter timeConverter) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				r1.asTextBloc(timeConverter).drawU(ug);
				r2.asTextBloc(timeConverter).drawU(ug.apply(new UTranslate(0, r1.getHeight())));
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				final double width = getMaxXwithoutHeader(timeConverter) - getMinXwithoutHeader(timeConverter);
				final double height = getHeight();
				return new Dimension2DDouble(width, height);
			}
		};
	}

	public double getMinXwithoutHeader(TimeConverter timeConverter) {
		return Math.min(r1.getMinXwithoutHeader(timeConverter), r2.getMinXwithoutHeader(timeConverter));
	}

	public double getMaxXwithoutHeader(TimeConverter timeConverter) {
		return Math.max(r1.getMaxXwithoutHeader(timeConverter), r2.getMaxXwithoutHeader(timeConverter));
	}

	public double getHeight() {
		return r1.getHeight() + r2.getHeight();
	}

	public TextBlock header() {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				r1.header().drawU(ug);
				r2.header().drawU(ug.apply(new UTranslate(0, r1.getHeight())));
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				final double width = Math.max(r1.header().calculateDimension(stringBounder).getWidth(), r2.header()
						.calculateDimension(stringBounder).getWidth());
				final double height = getHeight();
				return new Dimension2DDouble(width, height);
			}
		};
	}

}
