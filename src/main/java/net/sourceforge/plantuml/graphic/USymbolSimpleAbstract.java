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
package net.sourceforge.plantuml.graphic;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

abstract class USymbolSimpleAbstract extends USymbol {

	public TextBlock asSmall(TextBlock name, final TextBlock label, final TextBlock stereotype, final SymbolContext symbolContext) {
		if (stereotype == null) {
			throw new IllegalArgumentException();
		}
		final TextBlock stickman = getDrawing(symbolContext);
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final StringBounder stringBounder = ug.getStringBounder();
				final Dimension2D dimName = label.calculateDimension(stringBounder);
				final Dimension2D dimStereo = stereotype.calculateDimension(stringBounder);
				final Dimension2D dimStickMan = stickman.calculateDimension(stringBounder);
				final Dimension2D dimTotal = calculateDimension(stringBounder);
				final double stickmanX = (dimTotal.getWidth() - dimStickMan.getWidth()) / 2;
				final double stickmanY = dimStereo.getHeight();
				ug = symbolContext.apply(ug);
				stickman.drawU(ug.apply(new UTranslate(stickmanX, stickmanY)));
				final double labelX = (dimTotal.getWidth() - dimName.getWidth()) / 2;
				final double labelY = dimStickMan.getHeight() + dimStereo.getHeight();
				label.drawU(ug.apply(new UTranslate(labelX, labelY)));

				final double stereoX = (dimTotal.getWidth() - dimStereo.getWidth()) / 2;
				stereotype.drawU(ug.apply(new UTranslate(stereoX, 0)));
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				final Dimension2D dimName = label.calculateDimension(stringBounder);
				final Dimension2D dimStereo = stereotype.calculateDimension(stringBounder);
				final Dimension2D dimActor = stickman.calculateDimension(stringBounder);
				return Dimension2DDouble.mergeLayoutT12B3(dimStereo, dimActor, dimName);
			}
		};
	}

	abstract protected TextBlock getDrawing(final SymbolContext symbolContext);

	public TextBlock asBig(final TextBlock title, TextBlock stereotype, final double width, final double height,
			final SymbolContext symbolContext) {
		throw new UnsupportedOperationException();
	}

}
