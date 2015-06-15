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

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class USymbolComponent2 extends USymbol {

	@Override
	public SkinParameter getSkinParameter() {
		return SkinParameter.COMPONENT2;
	}


	private void drawNode(UGraphic ug, double widthTotal, double heightTotal, boolean shadowing) {

		final URectangle form = new URectangle(widthTotal, heightTotal);
		if (shadowing) {
			form.setDeltaShadow(4);
		}

		final UShape small = new URectangle(15, 10);
		final UShape tiny = new URectangle(4, 2);

		ug.draw(form);

		// UML 2 Component Notation
		ug.apply(new UTranslate(widthTotal - 20, 5)).draw(small);
		ug.apply(new UTranslate(widthTotal - 22, 7)).draw(tiny);
		ug.apply(new UTranslate(widthTotal - 22, 11)).draw(tiny);

	}

	private Margin getMargin() {
		return new Margin(10 + 5, 20 + 5, 15 + 5, 5 + 5);
	}

	public TextBlock asSmall(final TextBlock label, TextBlock stereotype, final SymbolContext symbolContext) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final Dimension2D dim = calculateDimension(ug.getStringBounder());
				ug = symbolContext.apply(ug);
				drawNode(ug, dim.getWidth(), dim.getHeight(), symbolContext.isShadowing());
				final Margin margin = getMargin();
				label.drawU(ug.apply(new UTranslate(margin.getX1(), margin.getY1())));
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				final Dimension2D dim = label.calculateDimension(stringBounder);
				return getMargin().addDimension(dim);
			}
		};
	}

	public TextBlock asBig(final TextBlock title, TextBlock stereotype, final double width, final double height,
			final SymbolContext symbolContext) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final Dimension2D dim = calculateDimension(ug.getStringBounder());
				ug = symbolContext.apply(ug);
				drawNode(ug, dim.getWidth(), dim.getHeight(), symbolContext.isShadowing());
				title.drawU(ug.apply(new UTranslate(3, 13)));
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				return new Dimension2DDouble(width, height);
			}

		};
	}

}
