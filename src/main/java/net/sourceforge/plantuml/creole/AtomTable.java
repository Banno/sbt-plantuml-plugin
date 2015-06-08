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
package net.sourceforge.plantuml.creole;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class AtomTable implements Atom {

	class Line {
		private final List<Atom> cells = new ArrayList<Atom>();

		public void add(Atom cell) {
			cells.add(cell);
		}

		public int size() {
			return cells.size();
		}

		@Override
		public String toString() {
			return super.toString() + " " + cells.size();
		}
	}

	private final List<Line> lines = new ArrayList<Line>();
	private final Map<Atom, Position> positions = new HashMap<Atom, Position>();
	private final HtmlColor lineColor;

	public AtomTable(HtmlColor lineColor) {
		this.lineColor = lineColor;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		initMap(stringBounder);
		final double width = getEndingX(getNbCols() - 1);
		final double height = getEndingY(getNbLines() - 1);
		return new Dimension2DDouble(width, height);
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return 0;
	}

	public void drawU(UGraphic ug) {
		initMap(ug.getStringBounder());
		for (Line line : lines) {
			for (Atom cell : line.cells) {
				final Position pos = positions.get(cell);
				cell.drawU(ug.apply(pos.getTranslate()));
			}
		}
		ug = ug.apply(new UChangeColor(lineColor));
		final ULine hline = new ULine(getEndingX(getNbCols() - 1), 0);
		for (int i = 0; i <= getNbLines(); i++) {
			ug.apply(new UTranslate(0, getStartingY(i))).draw(hline);
		}
		final ULine vline = new ULine(0, getEndingY(getNbLines() - 1));
		for (int i = 0; i <= getNbCols(); i++) {
			ug.apply(new UTranslate(getStartingX(i), 0)).draw(vline);
		}

	}

	private void initMap(StringBounder stringBounder) {
		if (positions.size() > 0) {
			return;
		}
		for (Line line : lines) {
			for (Atom cell : line.cells) {
				final Dimension2D dim = cell.calculateDimension(stringBounder);
				final Position pos = new Position(0, 0, dim);
				positions.put(cell, pos);
			}
		}
		for (int i = 0; i < lines.size(); i++) {
			for (int j = 0; j < lines.get(i).size(); j++) {
				final Atom cell = lines.get(i).cells.get(j);
				final Dimension2D dim = cell.calculateDimension(stringBounder);
				final double x = getStartingX(j);
				final double y = getStartingY(i);
				final Position pos = new Position(x, y, dim);
				positions.put(cell, pos);
			}
		}
	}

	private double getStartingX(int col) {
		double result = 0;
		for (int i = 0; i < col; i++) {
			result += getColWidth(i);
		}
		return result;
	}

	private double getEndingX(int col) {
		double result = 0;
		for (int i = 0; i <= col; i++) {
			result += getColWidth(i);
		}
		return result;
	}

	private double getStartingY(int line) {
		double result = 0;
		for (int i = 0; i < line; i++) {
			result += getLineHeight(i);
		}
		return result;
	}

	private double getEndingY(int line) {
		double result = 0;
		for (int i = 0; i <= line; i++) {
			result += getLineHeight(i);
		}
		return result;
	}

	private double getColWidth(int col) {
		double result = 0;
		for (int i = 0; i < getNbLines(); i++) {
			final double width = getPosition(i, col).getWidth();
			result = Math.max(result, width);
		}
		return result;
	}

	private double getLineHeight(int line) {
		double result = 0;
		for (int i = 0; i < getNbCols(); i++) {
			final double height = getPosition(line, i).getHeight();
			result = Math.max(result, height);
		}
		return result;
	}

	private Position getPosition(int line, int col) {
		final Line l = lines.get(line);
		final Atom atom = l.cells.get(col);
		return positions.get(atom);
	}

	private int getNbCols() {
		return lines.get(0).size();
	}

	private int getNbLines() {
		return lines.size();
	}

	private Line lastLine() {
		return lines.get(lines.size() - 1);
	}

	public void addCell(Atom cell) {
		lastLine().add(cell);
		positions.clear();
	}

	public void newLine() {
		lines.add(new Line());
	}

}
