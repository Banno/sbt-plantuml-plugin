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
import net.sourceforge.plantuml.skin.AbstractTextualComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ComponentBlueModernEnglober extends AbstractTextualComponent {

	private final HtmlColor borderColor;
	private final HtmlColor backColor;

	public ComponentBlueModernEnglober(HtmlColor borderColor, HtmlColor backColor, Display strings,
			FontConfiguration font, ISkinSimple spriteContainer) {
		super(strings, font, HorizontalAlignment.CENTER, 4, 4, 1, spriteContainer, 0, false,
				null, null);
		this.borderColor = borderColor;
		this.backColor = backColor;
	}

	@Override
	protected void drawBackgroundInternalU(UGraphic ug, Area area) {
		final Dimension2D dimensionToUse = area.getDimensionToUse();
		ug = ug.apply(new UChangeColor(borderColor));
		ug = ug.apply(new UChangeBackColor(backColor));
		ug.apply(new UStroke(2)).draw(new URectangle(dimensionToUse.getWidth(), dimensionToUse.getHeight(), 9, 9));
		final double xpos = (dimensionToUse.getWidth() - getPureTextWidth(ug.getStringBounder())) / 2;

		getTextBlock().drawU(ug.apply(new UTranslate(xpos, 0)));
	}

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {
		// ug.getParam().setColor(Color.RED);
		// ug.getParam().setBackcolor(Color.YELLOW);
		// ug.draw(0, 0, new URectangle(dimensionToUse.getWidth(),
		// dimensionToUse.getHeight()));
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return getTextHeight(stringBounder) + 3;
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return getTextWidth(stringBounder) + 10;
	}
}
