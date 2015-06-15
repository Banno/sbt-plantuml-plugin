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
package net.sourceforge.plantuml.png;

import java.awt.Font;
import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UFont;

public class PngTitler {

	private final HtmlColor textColor;
	private final HtmlColor hyperlinkColor;
	private final Display text;
	private final int fontSize;
	private final String fontFamily;
	private final HorizontalAlignment horizontalAlignment;
	private final boolean useUnderlineForHyperlink;

	public PngTitler(HtmlColor textColor, Display text, int fontSize, String fontFamily,
			HorizontalAlignment horizontalAlignment, HtmlColor hyperlinkColor, boolean useUnderlineForHyperlink) {
		this.textColor = textColor;
		this.text = text;
		this.fontSize = fontSize;
		this.fontFamily = fontFamily;
		this.horizontalAlignment = horizontalAlignment;
		this.hyperlinkColor = hyperlinkColor;
		this.useUnderlineForHyperlink = useUnderlineForHyperlink;

	}

	public Dimension2D getTextDimension(StringBounder stringBounder) {
		final TextBlock textBloc = getTextBlock();
		if (textBloc == null) {
			return null;
		}
		return textBloc.calculateDimension(stringBounder);
	}

	public TextBlock getTextBlock() {
		if (text == null || text.size() == 0) {
			return null;
		}
		final UFont normalFont = new UFont(fontFamily, Font.PLAIN, fontSize);
		return TextBlockUtils.create(text, new FontConfiguration(normalFont, textColor, hyperlinkColor, useUnderlineForHyperlink), horizontalAlignment,
				new SpriteContainerEmpty());
	}

	private double getOffsetX(double imWidth, StringBounder stringBounder) {
		final TextBlock textBloc = getTextBlock();
		if (textBloc == null) {
			return 0;
		}
		final Dimension2D dimText = textBloc.calculateDimension(stringBounder);

		if (imWidth >= dimText.getWidth()) {
			return 0;
		}
		return (dimText.getWidth() - imWidth) / 2;
	}

	private double getOffsetY(StringBounder stringBounder) {
		final TextBlock textBloc = getTextBlock();
		if (textBloc == null) {
			return 0;
		}
		final Dimension2D dimText = textBloc.calculateDimension(stringBounder);
		final double height = dimText.getHeight();
		return height;
	}
}
