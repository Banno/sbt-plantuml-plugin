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
package net.sourceforge.plantuml.sequencediagram.teoz;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.SimpleContext2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class MainTileAdapter extends AbstractTextBlock implements TextBlock {

	private final MainTile mainTile;
	private Dimension2D cacheDimension;

	public MainTileAdapter(MainTile mainTile) {
		if (mainTile == null) {
			throw new IllegalArgumentException();
		}
		this.mainTile = mainTile;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		if (cacheDimension == null) {
			final double width = mainTile.getMaxX(stringBounder).getCurrentValue()
					- mainTile.getMinX(stringBounder).getCurrentValue();

			final int factor = mainTile.isShowFootbox() ? 2 : 1;
			final double height = mainTile.getPreferredHeight(stringBounder) + factor
					* mainTile.getLivingSpaces().getHeadHeight(stringBounder);

			cacheDimension = new Dimension2DDouble(width, height);
		}
		return cacheDimension;
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();

		final Context2D context = new SimpleContext2D(false);
		final double height = mainTile.getPreferredHeight(stringBounder);
		final LivingSpaces livingSpaces = mainTile.getLivingSpaces();

		final double headHeight = livingSpaces.getHeadHeight(stringBounder);

		mainTile.drawU(ug.apply(new UTranslate(0, headHeight)));
		livingSpaces.drawLifeLines(ug.apply(new UTranslate(0, headHeight)), height, context);
		livingSpaces.drawHeads(ug, context, VerticalAlignment.BOTTOM);
		if (mainTile.isShowFootbox()) {
			livingSpaces.drawHeads(ug.apply(new UTranslate(0, height + headHeight)), context, VerticalAlignment.TOP);
		}
		mainTile.drawForeground(ug.apply(new UTranslate(0, headHeight)));
	}

	public Real getMinX(StringBounder stringBounder) {
		return mainTile.getMinX(stringBounder);
	}

}
