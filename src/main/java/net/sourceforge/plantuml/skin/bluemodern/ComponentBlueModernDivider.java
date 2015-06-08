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
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.skin.AbstractTextualComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ComponentBlueModernDivider extends AbstractTextualComponent {

	private final HtmlColor background1;
	private final HtmlColor background2;
	private final HtmlColor borderColor;

	public ComponentBlueModernDivider(FontConfiguration font, HtmlColor background1, HtmlColor background2,
			HtmlColor borderColor, Display stringsToDisplay, ISkinSimple spriteContainer) {
		super(stringsToDisplay, font, HorizontalAlignment.CENTER, 4, 4, 4,
				spriteContainer, 0, false, null, null);
		this.background1 = background1;
		this.background2 = background2;
		this.borderColor = borderColor;
	}

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {
		final Dimension2D dimensionToUse = area.getDimensionToUse();
		final TextBlock textBlock = getTextBlock();
		final StringBounder stringBounder = ug.getStringBounder();
		final double textWidth = getTextWidth(stringBounder);
		final double textHeight = getTextHeight(stringBounder);

		final double deltaX = 6;
		final double xpos = (dimensionToUse.getWidth() - textWidth - deltaX) / 2;
		final double ypos = (dimensionToUse.getHeight() - textHeight) / 2;

		ug = ug.apply(new UChangeColor(HtmlColorUtils.BLACK));
		ug = ug.apply(new UChangeBackColor(HtmlColorUtils.BLACK));
		ug = ug.apply(new UStroke(2));

		ug.apply(new UTranslate(0, dimensionToUse.getHeight() / 2 - 1)).draw(new ULine(dimensionToUse.getWidth(), 0));
		ug.apply(new UTranslate(0, dimensionToUse.getHeight() / 2 + 2)).draw(new ULine(dimensionToUse.getWidth(), 0));

		final FillRoundShape shape = new FillRoundShape(textWidth + deltaX, textHeight, background1, background2, 5);
		shape.drawU(ug.apply(new UTranslate(xpos, ypos)));

		ug = ug.apply(new UChangeColor(borderColor));
		ug = ug.apply(new UChangeBackColor(null));
		ug.apply(new UTranslate(xpos, ypos)).draw(new URectangle(textWidth + deltaX, textHeight, 5, 5));
		ug = ug.apply(new UStroke());

		textBlock.drawU(ug.apply(new UTranslate(xpos + deltaX, ypos + getMarginY())));
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return getTextHeight(stringBounder) + 20;
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return getTextWidth(stringBounder) + 30;
	}

}
