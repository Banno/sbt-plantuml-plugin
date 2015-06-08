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
package net.sourceforge.plantuml.posimo;

import java.awt.geom.Dimension2D;
import java.util.List;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Frame implements Component {

	private final List<? extends CharSequence> name;
	private final ISkinParam skinParam;
	private final Rose rose = new Rose();

	// private final Color textColor;
	// private final Color lineColor;
	// private final Font font;

	public Frame(List<? extends CharSequence> name, ISkinParam skinParam) {
		this.name = name;
		this.skinParam = skinParam;
		// this.textColor = textColor;
		// this.lineColor = lineColor;
		// this.font = font;
	}

	public void drawU(UGraphic ug, Area area, Context2D context) {
		final Dimension2D dimensionToUse = area.getDimensionToUse();
		final HtmlColor lineColor = rose.getHtmlColor(skinParam, ColorParam.packageBorder);
		ug = ug.apply(new UChangeColor(lineColor));
		ug = ug.apply(new UChangeBackColor(null));
		ug.apply(new UStroke(1.4)).draw(new URectangle(dimensionToUse.getWidth(), dimensionToUse.getHeight()));

		final TextBlock textBlock = createTextBloc();
		textBlock.drawU(ug.apply(new UTranslate(2, 2)));

		final Dimension2D textDim = getTextDim(ug.getStringBounder());
		final double x = textDim.getWidth() + 6;
		final double y = textDim.getHeight() + 6;
		final UPolygon poly = new UPolygon();
		poly.addPoint(x, 0);
		poly.addPoint(x, y - 6);
		poly.addPoint(x - 6, y);
		poly.addPoint(0, y);
		poly.addPoint(0, 0);
		ug.apply(new UStroke(1.4)).draw(poly);

	}

	public double getPreferredHeight(StringBounder stringBounder) {
		final Dimension2D dim = getTextDim(stringBounder);
		return dim.getHeight() + 8;
	}

	public double getPreferredWidth(StringBounder stringBounder) {
		final Dimension2D dim = getTextDim(stringBounder);
		return dim.getWidth() + 8;
	}

	public Dimension2D getTextDim(StringBounder stringBounder) {
		final TextBlock bloc = createTextBloc();
		return bloc.calculateDimension(stringBounder);
	}

	private TextBlock createTextBloc() {
		final UFont font = skinParam.getFont(FontParam.PACKAGE, null, false);
		final HtmlColor textColor = skinParam.getFontHtmlColor(FontParam.PACKAGE, null);
		final TextBlock bloc = TextBlockUtils.create(Display.create(name), new FontConfiguration(font, textColor, skinParam.getHyperlinkColor(), skinParam.useUnderlineForHyperlink()),
				HorizontalAlignment.LEFT, new SpriteContainerEmpty());
		return bloc;
	}

	public final Dimension2D getPreferredDimension(StringBounder stringBounder) {
		final double w = getPreferredWidth(stringBounder);
		final double h = getPreferredHeight(stringBounder);
		return new Dimension2DDouble(w, h);
	}

}
