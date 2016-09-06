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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.graphic.HtmlColorAndStyle;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.CompressionTransform;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Snake implements UShape {

	private final Worm worm = new Worm();
	private final UPolygon endDecoration;
	private final Rainbow color;
	private TextBlock textBlock;
	private MergeStrategy mergeable = MergeStrategy.FULL;
	private Direction emphasizeDirection;

	public Snake transformX(CompressionTransform compressionTransform) {
		final Snake result = new Snake(color, endDecoration);
		result.textBlock = this.textBlock;
		result.mergeable = this.mergeable;
		result.emphasizeDirection = this.emphasizeDirection;
		for (Point2D.Double pt : worm) {
			final double x = compressionTransform.transform(pt.x);
			final double y = pt.y;
			result.addPoint(x, y);
		}
		return result;
	}

	public Snake(Rainbow color, UPolygon endDecoration) {
		if (color == null) {
			throw new IllegalArgumentException();
		}
		if (color.size() == 0) {
			throw new IllegalArgumentException();
		}
		this.endDecoration = endDecoration;
		this.color = color;
	}

	public Snake(Rainbow color) {
		this(color, null);
	}

	public void setLabel(TextBlock label) {
		this.textBlock = label;
	}

	public Snake move(double dx, double dy) {
		final Snake result = new Snake(color, endDecoration);
		for (Point2D pt : worm) {
			result.addPoint(pt.getX() + dx, pt.getY() + dy);
		}
		result.textBlock = this.textBlock;
		result.mergeable = this.mergeable;
		result.emphasizeDirection = this.emphasizeDirection;
		return result;
	}

	public Snake translate(UTranslate translate) {
		return move(translate.getDx(), translate.getDy());
	}

	@Override
	public String toString() {
		return worm.toString();
	}

	public void addPoint(double x, double y) {
		worm.addPoint(x, y);
	}

	public void addPoint(Point2D p) {
		addPoint(p.getX(), p.getY());
	}

	public void drawInternal(UGraphic ug) {
		if (color.size() > 1) {
			drawRainbow(ug);
		} else {
			worm.drawInternalOneColor(ug, color.getColors().get(0), 1.5, emphasizeDirection, endDecoration);
			drawInternalLabel(ug);
		}

	}

	private void drawRainbow(UGraphic ug) {
		final List<HtmlColorAndStyle> colors = color.getColors();
		final int colorArrowSeparationSpace = color.getColorArrowSeparationSpace();
		final double move = 2 + colorArrowSeparationSpace;
		final WormMutation mutation = WormMutation.create(worm, move);
		final double globalMove = -1.0 * (colors.size() - 1) / 2.0;
		Worm current = worm.moveFirstPoint(mutation.getFirst().multiplyBy(globalMove));
		if (mutation.size() > 2) {
			current = current.moveLastPoint(mutation.getLast().multiplyBy(globalMove));
		}
		for (int i = 0; i < colors.size(); i++) {
			double stroke = 1.5;
			if (colorArrowSeparationSpace == 0) {
				stroke = i == colors.size() - 1 ? 2.0 : 3.0;
			}
			current.drawInternalOneColor(ug, colors.get(i), stroke, emphasizeDirection, endDecoration);
			current = mutation.mute(current);
		}
		final UTranslate textTranslate = mutation.getTextTranslate(colors.size());
		drawInternalLabel(ug.apply(textTranslate));
	}

	private void drawInternalLabel(UGraphic ug) {
		if (textBlock != null) {
			final Point2D position = getTextBlockPosition(ug.getStringBounder());
			textBlock.drawU(ug.apply(new UTranslate(position)));
		}
	}

	public double getMaxX(StringBounder stringBounder) {
		double result = -Double.MAX_VALUE;
		for (Point2D pt : worm) {
			result = Math.max(result, pt.getX());
		}
		if (textBlock != null) {
			final Point2D position = getTextBlockPosition(stringBounder);
			final Dimension2D dim = textBlock.calculateDimension(stringBounder);
			result = Math.max(result, position.getX() + dim.getWidth());
		}
		return result;
	}

	private Point2D getTextBlockPosition(StringBounder stringBounder) {
		final Point2D pt1 = worm.get(0);
		final Point2D pt2 = worm.get(1);
		final Dimension2D dim = textBlock.calculateDimension(stringBounder);
		final double y = (pt1.getY() + pt2.getY()) / 2 - dim.getHeight() / 2;
		return new Point2D.Double(Math.max(pt1.getX(), pt2.getX()) + 4, y);
	}

	public List<Line2D> getHorizontalLines() {
		final List<Line2D> result = new ArrayList<Line2D>();
		for (int i = 0; i < worm.size() - 1; i++) {
			final Point2D pt1 = worm.get(i);
			final Point2D pt2 = worm.get(i + 1);
			if (pt1.getY() == pt2.getY()) {
				final Line2D line = new Line2D.Double(pt1, pt2);
				result.add(line);
			}
		}
		return result;

	}

	private Point2D getFirst() {
		return worm.get(0);
	}

	public Point2D getLast() {
		return worm.get(worm.size() - 1);
	}

	static boolean same(Point2D pt1, Point2D pt2) {
		return pt1.distance(pt2) < 0.001;
	}

	public Snake merge(Snake other, StringBounder stringBounder) {
		final MergeStrategy strategy = this.mergeable.max(other.mergeable);
		if (strategy == MergeStrategy.NONE) {
			return null;
		}
		if (TextBlockUtils.isEmpty(other.textBlock, stringBounder) == false) {
			return null;
			// System.err.println("merge other.textBlock="+other.textBlock+" "+other.textBlock.calculateDimension(TextBlockUtils.getDummyStringBounder()));
		}
		// if (other.textBlock != null) {
		// return null;
		// }
		if (same(this.getLast(), other.getFirst())) {
			final UPolygon oneOf = other.endDecoration == null ? endDecoration : other.endDecoration;
			final Snake result = new Snake(color, oneOf);
			result.emphasizeDirection = emphasizeDirection == null ? other.emphasizeDirection : emphasizeDirection;
			result.worm.addAll(this.worm.merge(other.worm, strategy));
			result.mergeable = strategy;
			return result;
		}
		if (same(this.getFirst(), other.getLast())) {
			return other.merge(this, stringBounder);
		}
		return null;
	}

	public void goUnmergeable(MergeStrategy strategy) {
		this.mergeable = strategy;
	}

	public void emphasizeDirection(Direction direction) {
		this.emphasizeDirection = direction;
	}

}
