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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.version.PSystemVersion;

public class DateEventUtils {

	public static TextBlock addEvent(TextBlock textBlock, HtmlColor color) {
		final DateFormat dateFormat = new SimpleDateFormat("MM-dd");
		final String today = dateFormat.format(new Date());

		if ("11-05".equals(today)) {
			final List<String> asList = Arrays.asList("<u>November 5th, 1955",
					"Doc Brown's discovery of the Flux Capacitor, that makes time-travel possible.");
			return TextBlockUtils.mergeTB(textBlock, getComment(asList, color), HorizontalAlignment.LEFT);
		} else if ("08-29".equals(today)) {
			final List<String> asList = Arrays.asList("<u>August 29th, 1997",
					"Skynet becomes self-aware at 02:14 AM Eastern Time.");
			return TextBlockUtils.mergeTB(textBlock, getComment(asList, color), HorizontalAlignment.LEFT);
		} else if ("06-29".equals(today)) {
			final List<String> asList = Arrays.asList("<u>June 29th, 1975",
					"\"It was the first time in history that anyone had typed",
					"a character on a keyboard and seen it show up on their",
					"own computer's screen right in front of them.\"", "\t\t\t\t\t\t\t\t\t\t<i>Steve Wozniak");
			return TextBlockUtils.mergeTB(textBlock, getComment(asList, color), HorizontalAlignment.LEFT);
		} else if ("01-07".equals(today) || "01-08".equals(today)) {
			return addCharlie(textBlock);

		}

		return textBlock;
	}

	private static TextBlock addCharlie(TextBlock textBlock) {
		final TextBlock charlie = new AbstractTextBlock() {
			private final BufferedImage charlie = PSystemVersion.getCharlieImage();

			public void drawU(UGraphic ug) {
				ug.draw(new UImage(charlie));
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				return new Dimension2DDouble(charlie.getWidth(), charlie.getHeight());
			}
		};
		return TextBlockUtils.mergeTB(charlie, textBlock, HorizontalAlignment.LEFT);

	}

	private static TextBlock getComment(final List<String> asList, HtmlColor color) {
		final UFont font = new UFont("SansSerif", Font.BOLD, 14);
		TextBlock comment = TextBlockUtils.create(Display.create(asList), new FontConfiguration(font, color,
				HtmlColorUtils.BLUE, true), HorizontalAlignment.LEFT, new SpriteContainerEmpty());
		comment = TextBlockUtils.withMargin(comment, 4, 4);
		comment = new TextBlockBordered(comment, color);
		comment = TextBlockUtils.withMargin(comment, 10, 10);
		return comment;
	}
}
