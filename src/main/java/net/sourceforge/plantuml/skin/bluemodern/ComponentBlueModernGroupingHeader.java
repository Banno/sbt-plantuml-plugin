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
package net.sourceforge.plantuml.skin.bluemodern;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.skin.AbstractTextualComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ComponentBlueModernGroupingHeader extends AbstractTextualComponent {

	private final int cornersize = 10;
	private final int commentMargin = 0; // 8;

	private final TextBlock commentTextBlock;

	private final HtmlColor headerBackgroundColor;
	private final HtmlColor generalBackgroundColor;
	private final HtmlColor borderColor;

	public ComponentBlueModernGroupingHeader(HtmlColor headerBackgroundColor, HtmlColor generalBackgroundColor,
			HtmlColor borderColor, HtmlColor fontColor2, FontConfiguration bigFont, UFont smallFont, Display strings, ISkinSimple spriteContainer) {
		super(strings.get(0), bigFont, HorizontalAlignment.LEFT, 15, 30, 1, spriteContainer, 0, null,
				null);
		this.headerBackgroundColor = headerBackgroundColor;
		this.generalBackgroundColor = generalBackgroundColor;
		this.borderColor = borderColor;
		if (strings.size() == 1 || strings.get(1) == null) {
			this.commentTextBlock = null;
		} else {
			this.commentTextBlock = TextBlockUtils.create(Display.create("[" + strings.get(1) + "]"),
					new FontConfiguration(smallFont, fontColor2, bigFont.getHyperlinkColor(), bigFont.useUnderlineForHyperlink()), HorizontalAlignment.LEFT,
					spriteContainer);
		}
	}

	// @Override
	// public double getPaddingY() {
	// return 6;
	// }

	@Override
	final public double getPreferredWidth(StringBounder stringBounder) {
		final double sup;
		if (commentTextBlock == null) {
			sup = commentMargin * 2;
		} else {
			final Dimension2D size = commentTextBlock.calculateDimension(stringBounder);
			sup = getMarginX1() + commentMargin + size.getWidth();

		}
		return getTextWidth(stringBounder) + sup;
	}

	@Override
	final public double getPreferredHeight(StringBounder stringBounder) {
		return getTextHeight(stringBounder) + 2 * getPaddingY();
	}

	@Override
	protected void drawBackgroundInternalU(UGraphic ug, Area area) {
		final Dimension2D dimensionToUse = area.getDimensionToUse();
		ug.apply(new UChangeColor(borderColor)).apply(new UStroke(2))
				.apply(new UChangeBackColor(generalBackgroundColor))
				.draw(new URectangle(dimensionToUse.getWidth(), dimensionToUse.getHeight()));
	}

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {
		final Dimension2D dimensionToUse = area.getDimensionToUse();
		ug = ug.apply(new UChangeColor(borderColor));
		ug.apply(new UStroke(2)).draw(new URectangle(dimensionToUse.getWidth(), dimensionToUse.getHeight()));

		final StringBounder stringBounder = ug.getStringBounder();
		final int textWidth = (int) getTextWidth(stringBounder);
		final int textHeight = (int) getTextHeight(stringBounder);

		final UPolygon polygon = new UPolygon();
		polygon.addPoint(0, 0);
		polygon.addPoint(textWidth, 0);

		polygon.addPoint(textWidth, textHeight - cornersize);
		polygon.addPoint(textWidth - cornersize, textHeight);

		polygon.addPoint(0, textHeight);
		polygon.addPoint(0, 0);

		ug = ug.apply(new UStroke(2));
		ug = ug.apply(new UChangeBackColor(headerBackgroundColor));
		ug.draw(polygon);
		ug.draw(new ULine(dimensionToUse.getWidth(), 0));

		final double heightWithoutPadding = dimensionToUse.getHeight() - getPaddingY();

		ug.apply(new UTranslate(dimensionToUse.getWidth(), 0)).draw(new ULine(0, heightWithoutPadding));
		ug.apply(new UTranslate(0, textHeight)).draw(new ULine(0, heightWithoutPadding - textHeight));
		ug = ug.apply(new UStroke());

		getTextBlock().drawU(ug.apply(new UTranslate(getMarginX1(), getMarginY())));

		if (commentTextBlock != null) {
			final int x1 = getMarginX1() + textWidth;
			final int y2 = getMarginY() + 1;

			commentTextBlock.drawU(ug.apply(new UChangeColor(generalBackgroundColor)).apply(
					new UTranslate(x1 + commentMargin, y2)));
		}
	}

}
