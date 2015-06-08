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
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class TextBlockVertical2 extends AbstractTextBlock implements TextBlock {

	private final List<TextBlock> blocks = new ArrayList<TextBlock>();
	private final HorizontalAlignment horizontalAlignment;

	public TextBlockVertical2(TextBlock b1, TextBlock b2, HorizontalAlignment horizontalAlignment) {
		this.blocks.add(b1);
		this.blocks.add(b2);
		this.horizontalAlignment = horizontalAlignment;
	}

	public TextBlockVertical2(List<TextBlock> all, HorizontalAlignment horizontalAlignment) {
		if (all.size() < 2) {
			throw new IllegalArgumentException();
		}
		this.blocks.addAll(all);
		this.horizontalAlignment = horizontalAlignment;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		Dimension2D dim = blocks.get(0).calculateDimension(stringBounder);
		for (int i = 1; i < blocks.size(); i++) {
			dim = Dimension2DDouble.mergeTB(dim, blocks.get(i).calculateDimension(stringBounder));
		}
		return dim;
	}

	public void drawU(UGraphic ug) {
		double y = 0;
		final Dimension2D dimtotal = calculateDimension(ug.getStringBounder());
		for (TextBlock block : blocks) {
			final Dimension2D dimb = block.calculateDimension(ug.getStringBounder());
			if (horizontalAlignment == HorizontalAlignment.LEFT) {
				block.drawU(ug.apply(new UTranslate(0, y)));
			} else if (horizontalAlignment == HorizontalAlignment.CENTER) {
				final double dx = (dimtotal.getWidth() - dimb.getWidth()) / 2;
				block.drawU(ug.apply(new UTranslate(dx, y)));
			} else {
				throw new UnsupportedOperationException();
			}
			y += dimb.getHeight();
		}
	}

	@Override
	public Rectangle2D getInnerPosition(String member, StringBounder stringBounder) {
		double y = 0;
		for (TextBlock block : blocks) {
			final Dimension2D dimb = block.calculateDimension(stringBounder);
			final Rectangle2D result = block.getInnerPosition(member, stringBounder);
			if (result != null) {
				return new UTranslate(0, y).apply(result);
			}
			y += dimb.getHeight();
		}
		return null;
	}

}
