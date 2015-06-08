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
package net.sourceforge.plantuml.skin;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.cucadiagram.BodyEnhanced2;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockEmpty;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UFont;

public abstract class AbstractTextualComponent extends AbstractComponent {

	private final Display strings;

	private final int marginX1;
	private final int marginX2;
	private final int marginY;

	private final TextBlock textBlock;

	private final UFont font;
	private final HtmlColor fontColor;

	public AbstractTextualComponent(CharSequence label, FontConfiguration font,
			HorizontalAlignment horizontalAlignment, int marginX1, int marginX2, int marginY,
			ISkinSimple spriteContainer, double maxMessageSize, UFont fontForStereotype,
			HtmlColor htmlColorForStereotype) {
		this(Display.getWithNewlines(label == null ? "" : label.toString()), font, horizontalAlignment, marginX1,
				marginX2, marginY, spriteContainer, maxMessageSize, false, fontForStereotype, htmlColorForStereotype);
	}

	public AbstractTextualComponent(Display strings, FontConfiguration font, HorizontalAlignment horizontalAlignment,
			int marginX1, int marginX2, int marginY, ISkinSimple spriteContainer, double maxMessageSize,
			boolean enhanced, UFont fontForStereotype, HtmlColor htmlColorForStereotype) {
		this.font = font.getFont();
		this.fontColor = font.getColor();
		this.marginX1 = marginX1;
		this.marginX2 = marginX2;
		this.marginY = marginY;
		this.strings = strings;

		if (strings.size() == 1 && strings.get(0).length() == 0) {
			textBlock = new TextBlockEmpty();
		} else if (enhanced) {
			textBlock = new BodyEnhanced2(strings, FontParam.NOTE, spriteContainer, HorizontalAlignment.LEFT, font);
		} else {
			textBlock = TextBlockUtils.create(strings, font, horizontalAlignment, spriteContainer, maxMessageSize,
					false, fontForStereotype, htmlColorForStereotype);
		}
	}

	final protected TextBlock getTextBlock() {
		return textBlock;
	}

	final protected double getPureTextWidth(StringBounder stringBounder) {
		final TextBlock textBlock = getTextBlock();
		final Dimension2D size = textBlock.calculateDimension(stringBounder);
		return size.getWidth();
	}

	final public double getTextWidth(StringBounder stringBounder) {
		return getPureTextWidth(stringBounder) + marginX1 + marginX2;
	}

	// // For cache
	// private Dimension2D size;
	//
	// private Dimension2D getSize(StringBounder stringBounder, final TextBlock textBlock) {
	// if (size == null) {
	// size = textBlock.calculateDimension(stringBounder);
	// }
	// return size;
	// }

	final protected double getTextHeight(StringBounder stringBounder) {
		final TextBlock textBlock = getTextBlock();
		final Dimension2D size = textBlock.calculateDimension(stringBounder);
		return size.getHeight() + 2 * marginY;
	}

	final protected Display getLabels() {
		return strings;
	}

	final protected int getMarginX1() {
		return marginX1;
	}

	final protected int getMarginX2() {
		return marginX2;
	}

	final protected int getMarginY() {
		return marginY;
	}

	final protected UFont getFont() {
		return font;
	}

	protected HtmlColor getFontColor() {
		return fontColor;
	}

}
