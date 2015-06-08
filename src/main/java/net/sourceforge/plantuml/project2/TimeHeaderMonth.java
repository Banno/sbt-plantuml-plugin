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
package net.sourceforge.plantuml.project2;

import java.awt.Font;
import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
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
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class TimeHeaderMonth extends AbstractTextBlock implements TextBlock {

	private final Day start;
	private final Day end;
	private final TimeLine timeline;
	private final double dayWidth;

	private final UFont font = new UFont("Serif", Font.PLAIN, 9);
	private final FontConfiguration fontConfig = new FontConfiguration(font, HtmlColorUtils.BLACK, HtmlColorUtils.BLUE, true);

	public TimeHeaderMonth(Day start, Day end, TimeLine timeline, double dayWidth) {
		this.start = start;
		this.end = end;
		this.timeline = timeline;
		this.dayWidth = dayWidth;
	}

	public void drawU(UGraphic ug) {
		int n = 0;
		String last = null;
		
		double pendingX = -1;
		for (Day d = start; d.compareTo(end) <= 0; d = (Day) timeline.next(d)) {
			final String text = "" + d.getMonth().name();
			if (pendingX == -1) {
				pendingX = n * dayWidth;
				last = text;
			}
			ug = ug.apply(new UChangeColor(HtmlColorUtils.BLACK));
			ug = ug.apply(new UChangeBackColor(HtmlColorUtils.WHITE));
			if (text.equals(last) == false) {
				manage(ug, 0, 0, n, last, pendingX);
				pendingX = n * dayWidth;
			}
			last = text;
			n++;
		}
		manage(ug, 0, 0, n, last, pendingX);
	}

	private void manage(UGraphic ug, double x, double y, int n, String last, double pendingX) {
		final double width = n * dayWidth - pendingX;
		ug.apply(new UTranslate(x + pendingX, y)).draw(new URectangle(width, getHeight()));
		final TextBlock b = TextBlockUtils.create(Display.create(last), fontConfig, HorizontalAlignment.LEFT,
				new SpriteContainerEmpty());
		final Dimension2D dimText = b.calculateDimension(ug.getStringBounder());
		final double diffX = width - dimText.getWidth();
		final double diffY = getHeight() - dimText.getHeight();
		b.drawU(ug.apply(new UTranslate((x + pendingX + diffX / 2), (y + diffY / 2))));
	}

	private double getHeight() {
		return 20;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		int n = 0;
		for (Day d = start; d.compareTo(end) <= 0; d = (Day) timeline.next(d)) {
			n++;
		}
		return new Dimension2DDouble(n * dayWidth, getHeight());
	}
}
