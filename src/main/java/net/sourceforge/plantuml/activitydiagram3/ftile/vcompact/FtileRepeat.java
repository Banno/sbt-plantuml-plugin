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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractConnection;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Arrows;
import net.sourceforge.plantuml.activitydiagram3.ftile.Connection;
import net.sourceforge.plantuml.activitydiagram3.ftile.ConnectionTranslatable;
import net.sourceforge.plantuml.activitydiagram3.ftile.Diamond;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileUtils;
import net.sourceforge.plantuml.activitydiagram3.ftile.Snake;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDiamond;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDiamondFoo1;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDiamondInside;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.svek.ConditionStyle;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class FtileRepeat extends AbstractFtile {

	private final Ftile repeat;
	private final Ftile diamond1;
	private final Ftile diamond2;
	private final TextBlock tbTest;

	private FtileRepeat(Ftile repeat, Ftile diamond1, Ftile diamond2, TextBlock tbTest) {
		super(repeat.shadowing());
		this.repeat = repeat;
		this.diamond1 = diamond1;
		this.diamond2 = diamond2;
		this.tbTest = tbTest;
	}

	public Swimlane getSwimlaneIn() {
		return repeat.getSwimlaneIn();
	}

	public Swimlane getSwimlaneOut() {
		return getSwimlaneIn();
	}

	public Set<Swimlane> getSwimlanes() {
		return repeat.getSwimlanes();
	}

	public static Ftile create(LinkRendering backRepeatLinkRendering, Swimlane swimlane, Ftile repeat, Display test,
			Display yes, Display out, HtmlColor borderColor, HtmlColor backColor, UFont fontTest, HtmlColor arrowColor,
			HtmlColor endRepeatLinkColor, ConditionStyle conditionStyle, ISkinSimple spriteContainer,
			HtmlColor hyperlinkColor, boolean useUnderlineForHyperlink) {

		final FontConfiguration fc = new FontConfiguration(fontTest, HtmlColorUtils.BLACK, hyperlinkColor,
				useUnderlineForHyperlink);
		final TextBlock tbTest = (test == null || test.isWhite()) ? TextBlockUtils.empty(0, 0) : TextBlockUtils.create(
				test, fc, HorizontalAlignment.LEFT, spriteContainer);
		final TextBlock yesTb = TextBlockUtils.create(yes, fc, HorizontalAlignment.LEFT, spriteContainer);
		final TextBlock outTb = TextBlockUtils.create(out, fc, HorizontalAlignment.LEFT, spriteContainer);

		final Ftile diamond1 = new FtileDiamond(repeat.shadowing(), backColor, borderColor, swimlane);
		final FtileRepeat result;
		if (conditionStyle == ConditionStyle.INSIDE) {
			final Ftile diamond2 = new FtileDiamondInside(repeat.shadowing(), backColor, borderColor, swimlane, tbTest)
					.withEast(yesTb).withSouth(outTb);
			result = new FtileRepeat(repeat, diamond1, diamond2, TextBlockUtils.empty(0, 0));
		} else if (conditionStyle == ConditionStyle.DIAMOND) {
			final Ftile diamond2 = new FtileDiamond(repeat.shadowing(), backColor, borderColor, swimlane)
					.withEast(tbTest);
			result = new FtileRepeat(repeat, diamond1, diamond2, tbTest);
		} else if (conditionStyle == ConditionStyle.FOO1) {
			final Ftile diamond2 = new FtileDiamondFoo1(repeat.shadowing(), backColor, borderColor, swimlane, tbTest);
			result = new FtileRepeat(repeat, diamond1, diamond2, TextBlockUtils.empty(0, 0));
		} else {
			throw new IllegalStateException();
		}

		final List<Connection> conns = new ArrayList<Connection>();
		final Display in1 = LinkRendering.getDisplay(repeat.getInLinkRendering());
		final TextBlock tbin1 = in1 == null ? null : TextBlockUtils.create(in1, fc, HorizontalAlignment.LEFT,
				spriteContainer, true);
		conns.add(result.new ConnectionIn(LinkRendering.getColor(repeat.getInLinkRendering(), arrowColor), tbin1));

		final Display backLink1 = LinkRendering.getDisplay(backRepeatLinkRendering);
		final TextBlock tbbackLink1 = backLink1 == null ? null : TextBlockUtils.create(backLink1, fc,
				HorizontalAlignment.LEFT, spriteContainer, true);
		conns.add(result.new ConnectionBack(LinkRendering.getColor(backRepeatLinkRendering, arrowColor), tbbackLink1));

		final Display out1 = LinkRendering.getDisplay(repeat.getOutLinkRendering());
		final TextBlock tbout1 = out1 == null ? null : TextBlockUtils.create(out1, fc, HorizontalAlignment.LEFT,
				spriteContainer, true);
		conns.add(result.new ConnectionOut(LinkRendering.getColor(endRepeatLinkColor, arrowColor), tbout1));
		return FtileUtils.addConnection(result, conns);
	}

	class ConnectionIn extends AbstractConnection {
		private final HtmlColor arrowColor;
		private final TextBlock tbin;

		public ConnectionIn(HtmlColor arrowColor, TextBlock tbin) {
			super(diamond1, repeat);
			this.arrowColor = arrowColor;
			this.tbin = tbin;
		}

		private Point2D getP1(final StringBounder stringBounder) {
			return getFtile1().calculateDimension(stringBounder).translate(getTranslateDiamond1(stringBounder))
					.getPointOut();
		}

		private Point2D getP2(final StringBounder stringBounder) {
			return getFtile2().calculateDimension(stringBounder).translate(getTranslateForRepeat(stringBounder))
					.getPointIn();
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();

			final Snake snake = new Snake(arrowColor, Arrows.asToDown());
			snake.setLabel(tbin);
			snake.addPoint(getP1(stringBounder));
			snake.addPoint(getP2(stringBounder));

			ug.draw(snake);
		}
	}

	class ConnectionOut extends AbstractConnection implements ConnectionTranslatable {
		private final HtmlColor arrowColor;
		private final TextBlock tbout;

		public ConnectionOut(HtmlColor arrowColor, TextBlock tbout) {
			super(repeat, diamond2);
			this.arrowColor = arrowColor;
			this.tbout = tbout;
		}

		private Point2D getP1(final StringBounder stringBounder) {
			return getTranslateForRepeat(stringBounder).getTranslated(
					getFtile1().calculateDimension(stringBounder).getPointOut());
		}

		private Point2D getP2(final StringBounder stringBounder) {
			return getTranslateDiamond2(stringBounder).getTranslated(
					getFtile2().calculateDimension(stringBounder).getPointIn());
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();

			final Snake snake = new Snake(arrowColor, Arrows.asToDown());
			snake.setLabel(tbout);
			snake.addPoint(getP1(stringBounder));
			snake.addPoint(getP2(stringBounder));

			ug.draw(snake);
		}

		public void drawTranslate(UGraphic ug, UTranslate translate1, UTranslate translate2) {
			final StringBounder stringBounder = ug.getStringBounder();
			final Snake snake = new Snake(arrowColor);
			snake.setLabel(tbout);
			final Point2D mp1a = translate1.getTranslated(getP1(stringBounder));
			final Point2D mp2b = translate2.getTranslated(getP2(stringBounder));
			final double middle = (mp1a.getY() + mp2b.getY()) / 2.0;
			snake.addPoint(mp1a);
			snake.addPoint(mp1a.getX(), middle);
			snake.addPoint(mp2b.getX(), middle);
			// snake.addPoint(mp2b);
			ug.draw(snake);

			final Snake small = new Snake(arrowColor, Arrows.asToDown());
			small.addPoint(mp2b.getX(), middle);
			small.addPoint(mp2b);
			ug.draw(small);

		}

	}

	class ConnectionBack extends AbstractConnection {
		private final HtmlColor arrowColor;
		private final TextBlock tbback;

		public ConnectionBack(HtmlColor arrowColor, TextBlock tbback) {
			super(diamond2, repeat);
			this.arrowColor = arrowColor;
			this.tbback = tbback;
		}

		private Point2D getP1(final StringBounder stringBounder) {
			return getTranslateDiamond2(stringBounder).getTranslated(new Point2D.Double(0, 0));
		}

		private Point2D getP2(final StringBounder stringBounder) {
			return getTranslateDiamond1(stringBounder).getTranslated(new Point2D.Double(0, 0));
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();

			final Snake snake = new Snake(arrowColor, Arrows.asToLeft());
			snake.setLabel(tbback);
			snake.emphasizeDirection(Direction.UP);
			final Dimension2D dimTotal = calculateDimensionInternal(stringBounder);
			final Point2D p1 = getP1(stringBounder);
			final Point2D p2 = getP2(stringBounder);
			final Dimension2D dimDiamond1 = diamond1.calculateDimension(stringBounder);
			final Dimension2D dimDiamond2 = diamond2.calculateDimension(stringBounder);
			final double x1 = p1.getX() + dimDiamond2.getWidth();
			final double y1 = p1.getY() + dimDiamond2.getHeight() / 2;
			final double x2 = p2.getX() + dimDiamond1.getWidth();
			final double y2 = p2.getY() + dimDiamond1.getHeight() / 2;

			snake.addPoint(x1, y1);
			final double yy = dimTotal.getWidth() - Diamond.diamondHalfSize;
			snake.addPoint(yy, y1);
			snake.addPoint(yy, y2);
			snake.addPoint(x2, y2);

			ug.draw(snake);
			// ug = ug.apply(new UChangeColor(arrowColor)).apply(new UChangeBackColor(arrowColor));
			// ug.apply(new UTranslate(yy, dimTotal.getHeight() / 2)).draw(Arrows.asToUp());
		}

	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		ug.apply(getTranslateForRepeat(stringBounder)).draw(repeat);
		ug.apply(getTranslateDiamond1(stringBounder)).draw(diamond1);
		ug.apply(getTranslateDiamond2(stringBounder)).draw(diamond2);

	}

	public FtileGeometry calculateDimension(StringBounder stringBounder) {
		final Dimension2D dimTotal = calculateDimensionInternal(stringBounder);
		return new FtileGeometry(dimTotal, getLeft(stringBounder), 0, dimTotal.getHeight());
	}

	private Dimension2D calculateDimensionInternal(StringBounder stringBounder) {
		final Dimension2D dimDiamond1 = diamond1.calculateDimension(stringBounder);
		final Dimension2D dimDiamond2 = diamond2.calculateDimension(stringBounder);
		final Dimension2D dimRepeat = repeat.calculateDimension(stringBounder);

		final double w = tbTest.calculateDimension(stringBounder).getWidth();

		double width = getLeft(stringBounder) + getRight(stringBounder);
		width = Math.max(width, w + 2 * Diamond.diamondHalfSize);
		final double height = dimDiamond1.getHeight() + dimRepeat.getHeight() + dimDiamond2.getHeight() + 8
				* Diamond.diamondHalfSize;
		return new Dimension2DDouble(width + 2 * Diamond.diamondHalfSize, height);

	}

	@Override
	public UTranslate getTranslateFor(Ftile child, StringBounder stringBounder) {
		if (child == repeat) {
			return getTranslateForRepeat(stringBounder);
		}
		if (child == diamond1) {
			return getTranslateDiamond1(stringBounder);
		}
		throw new UnsupportedOperationException();
	}

	private UTranslate getTranslateForRepeat(StringBounder stringBounder) {

		final Dimension2D dimDiamond1 = diamond1.calculateDimension(stringBounder);
		final Dimension2D dimDiamond2 = diamond2.calculateDimension(stringBounder);
		final Dimension2D dimTotal = calculateDimensionInternal(stringBounder);
		final Dimension2D dimRepeat = repeat.calculateDimension(stringBounder);
		final double y = (dimTotal.getHeight() - dimDiamond1.getHeight() - dimDiamond2.getHeight() - dimRepeat
				.getHeight()) / 2;

		final double left = getLeft(stringBounder);
		return new UTranslate(left - repeat.calculateDimension(stringBounder).getLeft(), y);

	}

	private UTranslate getTranslateDiamond1(StringBounder stringBounder) {
		final Dimension2D dimDiamond1 = diamond1.calculateDimension(stringBounder);
		final double left = getLeft(stringBounder);
		return new UTranslate(left - dimDiamond1.getWidth() / 2, 0);
	}

	private UTranslate getTranslateDiamond2(StringBounder stringBounder) {
		final Dimension2D dimTotal = calculateDimensionInternal(stringBounder);
		final Dimension2D dimDiamond2 = diamond2.calculateDimension(stringBounder);
		final double y2 = dimTotal.getHeight() - dimDiamond2.getHeight();
		final double left = getLeft(stringBounder);
		return new UTranslate(left - dimDiamond2.getWidth() / 2, y2);
	}

	private double getLeft(StringBounder stringBounder) {
		final Dimension2D dimDiamond1 = diamond1.calculateDimension(stringBounder);
		final Dimension2D dimDiamond2 = diamond2.calculateDimension(stringBounder);
		double left1 = repeat.calculateDimension(stringBounder).getLeft();
		left1 = Math.max(left1, dimDiamond1.getWidth() / 2);
		double left2 = repeat.calculateDimension(stringBounder).getLeft();
		left2 = Math.max(left2, dimDiamond2.getWidth() / 2);
		return Math.max(left1, left2);
	}

	private double getRight(StringBounder stringBounder) {
		final Dimension2D dimDiamond1 = diamond1.calculateDimension(stringBounder);
		final Dimension2D dimDiamond2 = diamond2.calculateDimension(stringBounder);
		final Dimension2D dimRepeat = repeat.calculateDimension(stringBounder);
		double right1 = dimRepeat.getWidth() - repeat.calculateDimension(stringBounder).getLeft();
		right1 = Math.max(right1, dimDiamond1.getWidth() / 2);
		double right2 = dimRepeat.getWidth() - repeat.calculateDimension(stringBounder).getLeft();
		right2 = Math.max(right2, dimDiamond2.getWidth() / 2);
		return Math.max(right1, right2);
	}

}
