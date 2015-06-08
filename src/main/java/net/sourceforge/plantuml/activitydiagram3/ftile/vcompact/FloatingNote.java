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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.creole.CreoleParser;
import net.sourceforge.plantuml.creole.Sheet;
import net.sourceforge.plantuml.creole.SheetBlock1;
import net.sourceforge.plantuml.creole.SheetBlock2;
import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.svek.image.Opale;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;

public class FloatingNote extends AbstractTextBlock implements Stencil, TextBlock {

	private final Opale opale;

	public FloatingNote(Display note, ISkinParam skinParam) {

		final Rose rose = new Rose();
		final HtmlColor fontColor = rose.getFontColor(skinParam, FontParam.NOTE);
		final UFont fontNote = skinParam.getFont(FontParam.NOTE, null, false);

		final HtmlColor noteBackgroundColor = rose.getHtmlColor(skinParam, ColorParam.noteBackground);
		final HtmlColor borderColor = rose.getHtmlColor(skinParam, ColorParam.noteBorder);

		final FontConfiguration fc = new FontConfiguration(fontNote, fontColor, skinParam.getHyperlinkColor(), skinParam.useUnderlineForHyperlink());

		final Sheet sheet = new CreoleParser(fc, HorizontalAlignment.LEFT, skinParam, false).createSheet(note);
		final SheetBlock2 sheetBlock2 = new SheetBlock2(new SheetBlock1(sheet, 0, skinParam.getPadding()), this, new UStroke(1));
		this.opale =  new Opale(borderColor, noteBackgroundColor, sheetBlock2, skinParam.shadowing(), false);

		// this.text = sheetBlock2;

	}

	public void drawU(UGraphic ug) {
		opale.drawU(ug);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return opale.calculateDimension(stringBounder);
	}

	public double getStartingX(StringBounder stringBounder, double y) {
		return -opale.getMarginX1();
	}

	public double getEndingX(StringBounder stringBounder, double y) {
		return opale.calculateDimension(stringBounder).getWidth() - opale.getMarginX1();
	}

}
