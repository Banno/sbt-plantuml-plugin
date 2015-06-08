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

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;

class GrayComponent extends AbstractComponent {

	private static final UFont NORMAL = new UFont("SansSerif", Font.PLAIN, 7);

	private final ComponentType type;

	public GrayComponent(ComponentType type) {
		this.type = type;
	}

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {
		final StringBounder stringBounder = ug.getStringBounder();
		ug = ug.apply(new UChangeBackColor(HtmlColorUtils.LIGHT_GRAY)).apply(new UChangeColor(HtmlColorUtils.BLACK));
		ug.draw(new URectangle(getPreferredWidth(stringBounder), getPreferredHeight(stringBounder)));

		final String n = type.name();
		final int split = 9;
		final List<String> strings = new ArrayList<String>();
		for (int i = 0; i < n.length(); i += split) {
			strings.add(n.substring(i, Math.min(i + split, n.length())));
		}

		final TextBlock textBlock = TextBlockUtils.create(Display.create(strings), new FontConfiguration(NORMAL, HtmlColorUtils.BLACK,
				HtmlColorUtils.BLUE, true), HorizontalAlignment.LEFT, new SpriteContainerEmpty());
		textBlock.drawU(ug);
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return 42;
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return 42;
	}

}
