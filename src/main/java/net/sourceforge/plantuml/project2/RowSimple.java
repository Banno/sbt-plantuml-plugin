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

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class RowSimple implements Row {

	private static final int HEIGHT = 12;
	private final Day first;
	private final Day last;
	private final HtmlColor backcolor;
	private final TextBlock header;

	public RowSimple(Day first, Day last) {
		this(first, last, HtmlColorUtils.BLACK);
	}

	public RowSimple(Day first, Day last, HtmlColor backcolor) {
		this(first, last, backcolor, TextBlockUtils.empty(0, HEIGHT));
	}

	public RowSimple(Day first, Day last, HtmlColor backcolor, TextBlock header) {
		this.first = first;
		this.last = last;
		this.backcolor = backcolor;
		if (last.compareTo(first) < 0) {
			throw new IllegalArgumentException();
		}
		this.header = header;
	}

	public TextBlock asTextBloc(final TimeConverter timeConverter) {
		return new AbstractTextBlock() {

			public void drawU(UGraphic ug) {
				final double x1 = getMinXwithoutHeader(timeConverter) + 1;
				final double x2 = getMaxXwithoutHeader(timeConverter) - 1;
				final double height = getHeight() - 4;
				final UShape rect = new URectangle(x2 - x1, height, 4, 4);
				ug.apply(new UChangeColor(HtmlColorUtils.RED)).apply(new UChangeBackColor(backcolor)).apply(new UTranslate(x1, 2)).draw(rect);
			}

			public Dimension2D calculateDimension(StringBounder stringBounder) {
				final double width = getMaxXwithoutHeader(timeConverter) - getMinXwithoutHeader(timeConverter);
				final double height = getHeight();
				return new Dimension2DDouble(width, height);
			}
		};
	}

	public double getMinXwithoutHeader(TimeConverter timeConverter) {
		return timeConverter.getStartPosition(first);
	}

	public double getMaxXwithoutHeader(TimeConverter timeConverter) {
		return timeConverter.getEndPosition(last);
	}

	public double getHeight() {
		return HEIGHT;
	}

	public TextBlock header() {
		return header;
	}
}
