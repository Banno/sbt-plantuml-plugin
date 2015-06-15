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

import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.svek.IEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.UAntiAliasing;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GraphicStrings extends AbstractTextBlock implements IEntityImage {

	private final HtmlColor background;

	private final UFont font;

	private final HtmlColor green;

	private final HtmlColor hyperlinkColor = HtmlColorUtils.BLUE;
	
	private final boolean useUnderlineForHyperlink = true;

	private final List<String> strings;

	private final BufferedImage image;

	private final GraphicPosition position;

	private final UAntiAliasing antiAliasing;

	private final ColorMapper colorMapper = new ColorMapperIdentity();

	public static GraphicStrings createDefault(List<String> strings, boolean useRed) {
		if (useRed) {
			return new GraphicStrings(strings, new UFont("SansSerif", Font.BOLD, 14), HtmlColorUtils.BLACK,
					HtmlColorUtils.RED_LIGHT, UAntiAliasing.ANTI_ALIASING_ON);
		}
		return new GraphicStrings(strings, new UFont("SansSerif", Font.BOLD, 14), HtmlColorSet.getInstance()
				.getColorIfValid("#33FF02"), HtmlColorUtils.BLACK, UAntiAliasing.ANTI_ALIASING_ON);
	}

	public GraphicStrings(List<String> strings, UFont font, HtmlColor green, HtmlColor background,
			UAntiAliasing antiAliasing) {
		this(strings, font, green, background, antiAliasing, null, null);
	}

	public GraphicStrings(List<String> strings, UFont font, HtmlColor green, HtmlColor background,
			UAntiAliasing antiAliasing, BufferedImage image, GraphicPosition position) {
		this.strings = strings;
		this.font = font;
		this.green = green;
		this.background = background;
		this.image = image;
		this.position = position;
		this.antiAliasing = antiAliasing;
	}

	private double minWidth;

	public void setMinWidth(double minWidth) {
		this.minWidth = minWidth;
	}

	private int maxLine = 0;

	private TextBlock getTextBlock() {
		TextBlock result = null;
		if (maxLine == 0) {
			result = TextBlockUtils.create(Display.create(strings), new FontConfiguration(font, green, hyperlinkColor, useUnderlineForHyperlink),
					HorizontalAlignment.LEFT, new SpriteContainerEmpty());
		} else {
			for (int i = 0; i < strings.size(); i += maxLine) {
				final int n = Math.min(i + maxLine, strings.size());
				final TextBlock textBlock1 = TextBlockUtils.create(Display.create(strings.subList(i, n)),
						new FontConfiguration(font, green, hyperlinkColor, useUnderlineForHyperlink), HorizontalAlignment.LEFT,
						new SpriteContainerEmpty());
				if (result == null) {
					result = textBlock1;
				} else {
					result = TextBlockUtils.withMargin(result, 0, 10, 0, 0);
					result = TextBlockUtils.mergeLR(result, textBlock1, VerticalAlignment.TOP);
				}
			}
		}
		result = DateEventUtils.addEvent(result, green);
		return result;
	}

	public void drawU(UGraphic ug) {
		final Dimension2D size = calculateDimension(ug.getStringBounder());
		getTextBlock().drawU(ug.apply(new UChangeColor(green)));

		if (image != null) {
			if (position == GraphicPosition.BOTTOM) {
				ug.apply(new UTranslate((size.getWidth() - image.getWidth()) / 2, size.getHeight() - image.getHeight()))
						.draw(new UImage(image));
			} else if (position == GraphicPosition.BACKGROUND_CORNER_BOTTOM_RIGHT) {
				ug.apply(new UTranslate(size.getWidth() - image.getWidth(), size.getHeight() - image.getHeight()))
						.draw(new UImage(image));
			} else if (position == GraphicPosition.BACKGROUND_CORNER_TOP_RIGHT) {
				ug.apply(new UTranslate(size.getWidth() - image.getWidth() - 1, 1)).draw(new UImage(image));
			}
		}
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		Dimension2D dim = getTextBlock().calculateDimension(stringBounder);
		if (dim.getWidth() < minWidth) {
			dim = new Dimension2DDouble(minWidth, dim.getHeight());
		}
		if (image != null) {
			if (position == GraphicPosition.BOTTOM) {
				dim = new Dimension2DDouble(dim.getWidth(), dim.getHeight() + image.getHeight());
			} else if (position == GraphicPosition.BACKGROUND_CORNER_BOTTOM_RIGHT) {
				dim = new Dimension2DDouble(dim.getWidth() + image.getWidth(), dim.getHeight());
			} else if (position == GraphicPosition.BACKGROUND_CORNER_TOP_RIGHT) {
				dim = new Dimension2DDouble(dim.getWidth() + image.getWidth(), dim.getHeight());
			}
		}
		return dim;
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	public HtmlColor getBackcolor() {
		return background;
	}

	public int getShield() {
		return 0;
	}

	public boolean isHidden() {
		return false;
	}

	public final void setMaxLine(int maxLine) {
		this.maxLine = maxLine;
	}

}
