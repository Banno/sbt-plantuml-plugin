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
package net.sourceforge.plantuml.skin.bluemodern;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.skin.AbstractTextualComponent;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.StickMan;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ComponentBlueModernActor extends AbstractTextualComponent {

	private final StickMan stickman;
	private final boolean head;

	public ComponentBlueModernActor(HtmlColor backgroundColor, HtmlColor foregroundColor, FontConfiguration font,
			Display stringsToDisplay, boolean head, ISkinSimple spriteContainer) {
		super(stringsToDisplay, font, HorizontalAlignment.CENTER, 3, 3, 0,
				spriteContainer, 0, false, null, null);
		this.head = head;
		stickman = new StickMan(backgroundColor, foregroundColor);
	}

	@Override
	protected void drawInternalU(UGraphic ug, Area area) {
		ug = ug.apply(new UChangeColor(getFontColor()));
		final TextBlock textBlock = getTextBlock();
		final StringBounder stringBounder = ug.getStringBounder();
		final double delta = (getPreferredWidth(stringBounder) - stickman.getPreferredWidth()) / 2;

		if (head) {
			textBlock
					.drawU(ug.apply(new UTranslate(getTextMiddlePostion(stringBounder), stickman.getPreferredHeight())));
			ug = ug.apply(new UTranslate(delta, 0));
		} else {
			textBlock.drawU(ug.apply(new UTranslate(getTextMiddlePostion(stringBounder), 0)));
			ug = ug.apply(new UTranslate(delta, getTextHeight(stringBounder)));
		}
		stickman.drawU(ug);

	}

	private double getTextMiddlePostion(StringBounder stringBounder) {
		return (getPreferredWidth(stringBounder) - getTextWidth(stringBounder)) / 2.0;
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return stickman.getPreferredHeight() + getTextHeight(stringBounder);
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return Math.max(stickman.getPreferredWidth(), getTextWidth(stringBounder));
	}

}
