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
import net.sourceforge.plantuml.ugraphic.CompressionTransform;
import net.sourceforge.plantuml.ugraphic.SlotFinder;
import net.sourceforge.plantuml.ugraphic.SlotSet;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicCompress;

public class TextBlockCompressed extends AbstractTextBlock implements TextBlock {

	private final TextBlock textBlock;

	public TextBlockCompressed(TextBlock textBlock) {
		this.textBlock = textBlock;
	}

	public void drawU(final UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final CompressionTransform compressionTransform = getCompressionTransform(stringBounder);
		textBlock.drawU(new UGraphicCompress(ug, compressionTransform));
	}

	private CompressionTransform getCompressionTransform(final StringBounder stringBounder) {
		final SlotFinder slotFinder = new SlotFinder(stringBounder);
		textBlock.drawU(slotFinder);
		final SlotSet ysSlotSet = slotFinder.getYSlotSet().reverse().smaller(5.0);
		final CompressionTransform compressionTransform = new CompressionTransform(ysSlotSet);
		return compressionTransform;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final CompressionTransform compressionTransform = getCompressionTransform(stringBounder);
		final Dimension2D dim = textBlock.calculateDimension(stringBounder);
		return new Dimension2DDouble(dim.getWidth(), compressionTransform.transform(dim.getHeight()));
	}
}
