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
package net.sourceforge.plantuml.project.graphic;

import java.util.Map;
import java.util.SortedMap;

import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorSetSimple;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.project.Instant;
import net.sourceforge.plantuml.project.Item;
import net.sourceforge.plantuml.project.Jalon;
import net.sourceforge.plantuml.project.Project;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class GanttDiagram {

	private final Project project;
	private final TimeScale timeScale;
	private final ItemHeader itemHeader;

	public GanttDiagram(Project project) {
		this.project = project;
		this.timeScale = new TimeScale(project);
		this.itemHeader = new ItemHeader(project);
	}

	public void draw(UGraphic ug, double x, double y) {
		final StringBounder stringBounder = ug.getStringBounder();
		final double x0start = itemHeader.getWidth(stringBounder);

		final double timeScaleHeight = timeScale.getHeight(stringBounder);

		final SortedMap<Instant, Double> pos = timeScale.getAbscisse(stringBounder);
		for (Item it : project.getValidItems()) {
			final Instant start = it.getBegin();
			final Instant completed = it.getCompleted();
			if (pos.get(start) == null || pos.get(completed) == null) {
				Log.println("PB " + it);
				continue;
			}
			final double x1 = pos.get(start) + 3;
			final double x2 = pos.get(completed) - 3;

			final double yitem = timeScaleHeight + itemHeader.getPosition(stringBounder, it) + 3;

			final UShape rect;
			if (it instanceof Jalon) {
				rect = new UPolygon();
				((UPolygon) rect).addPoint(0, 3);
				((UPolygon) rect).addPoint(3, 0);
				((UPolygon) rect).addPoint(6, 3);
				((UPolygon) rect).addPoint(3, 6);
			} else {
				rect = new URectangle(x2 - x1, 3);
			}
			ug = ug.apply(new UChangeColor(HtmlColorUtils.GREEN));
			ug = ug.apply(new UChangeBackColor(HtmlColorUtils.GRAY));
			ug.apply(new UTranslate(x0start + x1, yitem)).draw(rect);

		}

		drawGrid(ug, x + x0start, y + timeScaleHeight, pos);

		ug = ug.apply(new UChangeColor(HtmlColorUtils.BLACK));
		ug = ug.apply(new UChangeBackColor(null));
		timeScale.draw(ug, x + x0start, y);
		itemHeader.draw(ug, x, y + timeScaleHeight);

	}

	private final HtmlColor lightGray = new HtmlColorSetSimple().getColorIfValid("#C8C8C8");

	private void drawGrid(UGraphic ug, double x, double y, SortedMap<Instant, Double> pos) {
		final ULine line = new ULine(0, itemHeader.getHeight(ug.getStringBounder()));
		Instant last = null;
		for (Map.Entry<Instant, Double> ent : pos.entrySet()) {
			final double xcur = ent.getValue();
			if (last == null || last.next(null).equals(ent.getKey())) {
				ug = ug.apply(new UChangeColor(lightGray));
			} else {
				ug = ug.apply(new UChangeColor(HtmlColorUtils.BLACK));
			}
			ug.apply(new UTranslate(x + xcur, y)).draw(line);
			last = ent.getKey();
		}
	}

	public double getWidth(StringBounder stringBounder) {
		return itemHeader.getWidth(stringBounder) + timeScale.getWidth(stringBounder) + 3;
	}

	public double getHeight(StringBounder stringBounder) {
		return itemHeader.getHeight(stringBounder) + timeScale.getHeight(stringBounder) + 3;
	}

}
