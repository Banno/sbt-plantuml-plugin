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
package net.sourceforge.plantuml.skin.rose;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.ArrowDecoration;
import net.sourceforge.plantuml.skin.ArrowDirection;
import net.sourceforge.plantuml.skin.ArrowDressing;
import net.sourceforge.plantuml.skin.ArrowHead;
import net.sourceforge.plantuml.skin.ArrowPart;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UPolygon;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class ComponentRoseArrow extends AbstractComponentRoseArrow {

	private final HorizontalAlignment messagePosition;
	private final boolean niceArrow;

	public ComponentRoseArrow(HtmlColor foregroundColor, FontConfiguration font, Display stringsToDisplay, ArrowConfiguration arrowConfiguration, HorizontalAlignment messagePosition, ISkinSimple spriteContainer,
			HorizontalAlignment textHorizontalAlignment, double maxMessageSize, boolean niceArrow) {
		super(foregroundColor, font, stringsToDisplay, arrowConfiguration, spriteContainer, textHorizontalAlignment, maxMessageSize);
		this.messagePosition = messagePosition;
		this.niceArrow = niceArrow;
	}

	public static final double spaceCrossX = 6;
	public static final double diamCircle = 8;
	public static final double thinCircle = 1.5;

	@Override
	public void drawInternalU(UGraphic ug, Area area) {
		final Dimension2D dimensionToUse = area.getDimensionToUse();
		final StringBounder stringBounder = ug.getStringBounder();
		final int textHeight = (int) getTextHeight(stringBounder);
		ug = ug.apply(new UChangeColor(getForegroundColor()));

		final ArrowDressing dressing1 = getArrowConfiguration().getDressing1();
		final ArrowDressing dressing2 = getArrowConfiguration().getDressing2();

		double start = 0;
		double len = dimensionToUse.getWidth() - 1;

		final double pos1 = start + 1;
		final double pos2 = len - 1;

		if (getArrowConfiguration().getDecoration2() == ArrowDecoration.CIRCLE && dressing2.getHead() == ArrowHead.NONE) {
			len -= diamCircle / 2;
		}
		if (getArrowConfiguration().getDecoration2() == ArrowDecoration.CIRCLE && dressing2.getHead() != ArrowHead.NONE) {
			len -= diamCircle / 2 + thinCircle;
		}

		if (getArrowConfiguration().getDecoration1() == ArrowDecoration.CIRCLE && dressing1.getHead() == ArrowHead.NONE) {
			start += diamCircle / 2;
			len -= diamCircle / 2;
		}
		if (getArrowConfiguration().getDecoration1() == ArrowDecoration.CIRCLE
				&& dressing1.getHead() == ArrowHead.NORMAL) {
			start += diamCircle + thinCircle;
			len -= diamCircle + thinCircle;
		}

		drawDressing1(ug, pos1, dressing1, getArrowConfiguration().getDecoration1());
		drawDressing2(ug, pos2, dressing2, getArrowConfiguration().getDecoration2());

		if (dressing2.getPart() == ArrowPart.FULL && dressing2.getHead() == ArrowHead.NORMAL) {
			len -= getArrowDeltaX() / 2;
		}
		if (dressing1.getPart() == ArrowPart.FULL && dressing1.getHead() == ArrowHead.NORMAL) {
			start += getArrowDeltaX() / 2;
			len -= getArrowDeltaX() / 2;
		}

		if (dressing2.getHead() == ArrowHead.CROSSX) {
			len -= 2 * spaceCrossX;
		}
		if (dressing1.getHead() == ArrowHead.CROSSX) {
			start += 2 * spaceCrossX;
			len -= 2 * spaceCrossX;
		}

		if (getArrowConfiguration().isDotted()) {
			ug = stroke(ug, 2, 2);
		}
		ug.apply(new UTranslate(start, textHeight)).draw(new ULine(len, 0));
		if (getArrowConfiguration().isDotted()) {
			ug = ug.apply(new UStroke());
		}

		final ArrowDirection direction2 = getDirection2();
		final double textPos;
		if (messagePosition == HorizontalAlignment.CENTER) {
			final double textWidth = getTextBlock().calculateDimension(stringBounder).getWidth();
			textPos = (dimensionToUse.getWidth() - textWidth) / 2;
		} else if (messagePosition == HorizontalAlignment.RIGHT) {
			final double textWidth = getTextBlock().calculateDimension(stringBounder).getWidth();
			textPos = dimensionToUse.getWidth() - textWidth - getMarginX2()
					- (direction2 == ArrowDirection.LEFT_TO_RIGHT_NORMAL ? getArrowDeltaX() : 0);
		} else {
			textPos = getMarginX1()
					+ (direction2 == ArrowDirection.RIGHT_TO_LEFT_REVERSE
							|| direction2 == ArrowDirection.BOTH_DIRECTION ? getArrowDeltaX() : 0);
		}
		getTextBlock().drawU(ug.apply(new UTranslate(textPos, 0)));
	}

	private void drawDressing1(UGraphic ug, double x, ArrowDressing dressing, ArrowDecoration decoration) {
		final StringBounder stringBounder = ug.getStringBounder();
		final int textHeight = (int) getTextHeight(stringBounder);

		if (decoration == ArrowDecoration.CIRCLE) {
			final UEllipse circle = new UEllipse(diamCircle, diamCircle);
			ug.apply(new UStroke(thinCircle))
					.apply(new UChangeColor(getForegroundColor()))
					.apply(new UTranslate(x - diamCircle / 2 - thinCircle, textHeight - diamCircle / 2 - thinCircle / 2))
					.draw(circle);
			x += diamCircle / 2 + thinCircle;
		}

		if (dressing.getHead() == ArrowHead.ASYNC) {
			if (dressing.getPart() != ArrowPart.BOTTOM_PART) {
				ug.apply(new UTranslate(x - 1, textHeight)).draw(new ULine(getArrowDeltaX(), -getArrowDeltaY()));
			}
			if (dressing.getPart() != ArrowPart.TOP_PART) {
				ug.apply(new UTranslate(x - 1, textHeight)).draw(new ULine(getArrowDeltaX(), getArrowDeltaY()));
			}
		} else if (dressing.getHead() == ArrowHead.CROSSX) {
			ug = ug.apply(new UStroke(2));
			ug.apply(new UTranslate(spaceCrossX, textHeight - getArrowDeltaX() / 2)).draw(
					new ULine(getArrowDeltaX(), getArrowDeltaX()));
			ug.apply(new UTranslate(spaceCrossX, textHeight + getArrowDeltaX() / 2)).draw(
					new ULine(getArrowDeltaX(), -getArrowDeltaX()));
		} else if (dressing.getHead() == ArrowHead.NORMAL) {
			final UPolygon polygon = getPolygonReverse(dressing.getPart(), textHeight);
			ug.apply(new UChangeBackColor(getForegroundColor())).apply(new UTranslate(x, 0)).draw(polygon);
		}

	}

	private void drawDressing2(UGraphic ug, double x, ArrowDressing dressing, ArrowDecoration decoration) {
		final StringBounder stringBounder = ug.getStringBounder();
		final int textHeight = (int) getTextHeight(stringBounder);

		if (decoration == ArrowDecoration.CIRCLE) {
			ug = ug.apply(new UStroke(thinCircle)).apply(new UChangeColor(getForegroundColor()));
			final UEllipse circle = new UEllipse(diamCircle, diamCircle);
			ug.apply(new UTranslate(x - diamCircle / 2 + thinCircle, textHeight - diamCircle / 2 - thinCircle / 2))
					.draw(circle);
			ug = ug.apply(new UStroke());
			x -= diamCircle / 2 + thinCircle;
		}

		if (dressing.getHead() == ArrowHead.ASYNC) {
			if (dressing.getPart() != ArrowPart.BOTTOM_PART) {
				ug.apply(new UTranslate(x, textHeight)).draw(new ULine(-getArrowDeltaX(), -getArrowDeltaY()));
			}
			if (dressing.getPart() != ArrowPart.TOP_PART) {
				ug.apply(new UTranslate(x, textHeight)).draw(new ULine(-getArrowDeltaX(), getArrowDeltaY()));
			}
		} else if (dressing.getHead() == ArrowHead.CROSSX) {
			ug = ug.apply(new UStroke(2));
			ug.apply(new UTranslate(x - spaceCrossX - getArrowDeltaX(), textHeight - getArrowDeltaX() / 2)).draw(
					new ULine(getArrowDeltaX(), getArrowDeltaX()));
			ug.apply(new UTranslate(x - spaceCrossX - getArrowDeltaX(), textHeight + getArrowDeltaX() / 2)).draw(
					new ULine(getArrowDeltaX(), -getArrowDeltaX()));
			ug = ug.apply(new UStroke());
		} else if (dressing.getHead() == ArrowHead.NORMAL) {
			final UPolygon polygon = getPolygonNormal(dressing.getPart(), textHeight, x);
			ug.apply(new UChangeBackColor(getForegroundColor())).draw(polygon);
		}

	}

	private UPolygon getPolygonNormal(ArrowPart part, final int textHeight, final double x2) {
		final UPolygon polygon = new UPolygon();
		if (part == ArrowPart.TOP_PART) {
			polygon.addPoint(x2 - getArrowDeltaX(), textHeight - getArrowDeltaY());
			polygon.addPoint(x2, textHeight);
			polygon.addPoint(x2 - getArrowDeltaX(), textHeight);
		} else if (part == ArrowPart.BOTTOM_PART) {
			polygon.addPoint(x2 - getArrowDeltaX(), textHeight + 1);
			polygon.addPoint(x2, textHeight + 1);
			polygon.addPoint(x2 - getArrowDeltaX(), textHeight + getArrowDeltaY() + 1);
		} else {
			polygon.addPoint(x2 - getArrowDeltaX(), textHeight - getArrowDeltaY());
			polygon.addPoint(x2, textHeight);
			polygon.addPoint(x2 - getArrowDeltaX(), textHeight + getArrowDeltaY());
			if (niceArrow) {
				polygon.addPoint(x2 - getArrowDeltaX() + 4, textHeight);
			}
		}
		return polygon;
	}

	private UPolygon getPolygonReverse(ArrowPart part, final int textHeight) {
		final UPolygon polygon = new UPolygon();
		if (part == ArrowPart.TOP_PART) {
			polygon.addPoint(getArrowDeltaX(), textHeight - getArrowDeltaY());
			polygon.addPoint(0, textHeight);
			polygon.addPoint(getArrowDeltaX(), textHeight);
		} else if (part == ArrowPart.BOTTOM_PART) {
			polygon.addPoint(getArrowDeltaX(), textHeight + 1);
			polygon.addPoint(0, textHeight + 1);
			polygon.addPoint(getArrowDeltaX(), textHeight + getArrowDeltaY() + 1);
		} else {
			polygon.addPoint(getArrowDeltaX(), textHeight - getArrowDeltaY());
			polygon.addPoint(0, textHeight);
			polygon.addPoint(getArrowDeltaX(), textHeight + getArrowDeltaY());
			if (niceArrow) {
				polygon.addPoint(getArrowDeltaX() - 4, textHeight);
			}
		}
		return polygon;
	}

	public Point2D getStartPoint(StringBounder stringBounder, Dimension2D dimensionToUse) {
		final int textHeight = (int) getTextHeight(stringBounder);
		if (getDirection2() == ArrowDirection.LEFT_TO_RIGHT_NORMAL) {
			return new Point2D.Double(getPaddingX(), textHeight + getPaddingY());
		}
		return new Point2D.Double(dimensionToUse.getWidth() + getPaddingX(), textHeight + getPaddingY());
	}

	public Point2D getEndPoint(StringBounder stringBounder, Dimension2D dimensionToUse) {
		final int textHeight = (int) getTextHeight(stringBounder);
		if (getDirection2() == ArrowDirection.LEFT_TO_RIGHT_NORMAL) {
			return new Point2D.Double(dimensionToUse.getWidth() + getPaddingX(), textHeight + getPaddingY());
		}
		return new Point2D.Double(getPaddingX(), textHeight + getPaddingY());
	}

	final private ArrowDirection getDirection2() {
		return getArrowConfiguration().getArrowDirection();
	}

	@Override
	public double getPreferredHeight(StringBounder stringBounder) {
		return getTextHeight(stringBounder) + getArrowDeltaY() + 2 * getPaddingY();
	}

	@Override
	public double getPreferredWidth(StringBounder stringBounder) {
		return getTextWidth(stringBounder) + getArrowDeltaX();
	}

}
