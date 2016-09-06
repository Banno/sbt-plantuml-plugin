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
package net.sourceforge.plantuml.project.graphic;

import java.awt.Font;
import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.project.Item;
import net.sourceforge.plantuml.project.Project;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class ItemHeader {

	private final UFont font = new UFont("Serif", Font.PLAIN, 9);
	private final Project project;
	private final FontConfiguration fontConfig = FontConfiguration.blackBlueTrue(font);

	public ItemHeader(Project project) {
		this.project = project;
	}

	public void draw(UGraphic ug, double x, double y) {

		final StringBounder stringBounder = ug.getStringBounder();

		ug = ug.apply(new UChangeColor(HtmlColorUtils.BLACK));
		ug.apply(new UTranslate(x, y)).draw(new URectangle(getWidth(stringBounder), getHeight(stringBounder)));

		for (Item it : project.getValidItems()) {
			final TextBlock b = Display.create("" + it.getCode()).create(fontConfig, HorizontalAlignment.LEFT,
					new SpriteContainerEmpty());
			final Dimension2D dim = b.calculateDimension(stringBounder);
			b.drawU(ug.apply(new UTranslate(x, y)));
			y += dim.getHeight();
			ug.apply(new UTranslate(x, y)).draw(new ULine(getWidth(stringBounder), 0));
		}
	}

	public double getWidth(StringBounder stringBounder) {
		double width = 0;
		for (Item it : project.getValidItems()) {
			final Dimension2D dim = stringBounder.calculateDimension(font, it.getCode());
			width = Math.max(width, dim.getWidth());
		}
		return width;
	}

	public double getHeight(StringBounder stringBounder) {
		double height = 0;
		for (Item it : project.getValidItems()) {
			final Dimension2D dim = stringBounder.calculateDimension(font, it.getCode());
			height += dim.getHeight();

		}
		return height;
	}

	public double getPosition(StringBounder stringBounder, Item item) {
		double pos = 0;
		for (Item it : project.getValidItems()) {
			if (it == item) {
				return pos;
			}
			final Dimension2D dim = stringBounder.calculateDimension(font, it.getCode());
			pos += dim.getHeight();

		}
		throw new IllegalArgumentException();
	}

}
