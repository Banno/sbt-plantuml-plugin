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
package net.sourceforge.plantuml.project2;

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
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GanttDiagram2 {

	private final Project2 project;
	private final double dayWith = 20;

	public GanttDiagram2(Project2 project) {
		this.project = project;
	}

	private final UFont font = new UFont("Serif", Font.PLAIN, 9);
	private final FontConfiguration fontConfig = FontConfiguration.blackBlueTrue(font);

	public void draw(UGraphic ug, double x, double y) {

		final TextBlock timeHeader = project.getTimeHeader(dayWith);
		final Row row = getMainRow();
		final TextBlock headers = row.header();

		final double deltaX = headers.calculateDimension(ug.getStringBounder()).getWidth();
		final double deltaY = timeHeader.calculateDimension(ug.getStringBounder()).getHeight();

		headers.drawU(ug.apply(new UTranslate(x, (y + deltaY))));
		final TextBlock tbRow = row.asTextBloc(project.getTimeConverter(dayWith));
		tbRow.drawU(ug.apply(new UTranslate((x + deltaX), (y + deltaY))));

		timeHeader.drawU(ug.apply(new UTranslate((x + deltaX), y)));
	}

	private Row getMainRow() {
		final List<Task> tasks = project.getTasks();
		final List<Row> rows = new ArrayList<Row>();
		for (Task t : tasks) {
			final String text = t.getCode();
			final TextBlock label = Display.create(text).create(fontConfig, HorizontalAlignment.LEFT,
					new SpriteContainerEmpty());
			rows.add(new RowSimple((Day) t.getStart(), (Day) t.getEnd(), HtmlColorUtils.BLACK, TextBlockUtils
					.withMargin(label, 3, 3)));
		}
		final Row row = RowUtils.merge(rows);
		return row;
	}

	public double getWidth(StringBounder stringBounder) {
		final TextBlock timeHeader = project.getTimeHeader(dayWith);
		final Row row = getMainRow();
		final TextBlock headers = row.header();
		return headers.calculateDimension(stringBounder).getWidth()
				+ timeHeader.calculateDimension(stringBounder).getWidth() + 1;
	}

	public double getHeight(StringBounder stringBounder) {
		final TextBlock timeHeader = project.getTimeHeader(dayWith);
		final Row row = getMainRow();
		final TextBlock headers = row.header();
		return headers.calculateDimension(stringBounder).getHeight()
				+ timeHeader.calculateDimension(stringBounder).getHeight();
	}

}
