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

class TextBlockHorizontal extends AbstractTextBlock implements TextBlock {

	private final TextBlock b1;
	private final TextBlock b2;
	private final VerticalAlignment alignment;

	public TextBlockHorizontal(TextBlock b1, TextBlock b2, VerticalAlignment alignment) {
		this.b1 = b1;
		this.b2 = b2;
		this.alignment = alignment;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dim1 = b1.calculateDimension(stringBounder);
		final Dimension2D dim2 = b2.calculateDimension(stringBounder);
		return Dimension2DDouble.mergeLR(dim1, dim2);
	}

	public void drawU(UGraphic ug) {
		final Dimension2D dim = calculateDimension(ug.getStringBounder());
		final Dimension2D dimb1 = b1.calculateDimension(ug.getStringBounder());
		final Dimension2D dimb2 = b2.calculateDimension(ug.getStringBounder());
		final Dimension2D dim1 = b1.calculateDimension(ug.getStringBounder());
		if (alignment == VerticalAlignment.CENTER) {
			b1.drawU(ug.apply(new UTranslate(0, ((dim.getHeight() - dimb1.getHeight()) / 2))));
			b2.drawU(ug.apply(new UTranslate(dim1.getWidth(), ((dim.getHeight() - dimb2.getHeight()) / 2))));
		} else {
			b1.drawU(ug);
			b2.drawU(ug.apply(new UTranslate(dim1.getWidth(), 0)));
		}
	}

}
