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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.Direction;
import net.sourceforge.plantuml.FileGroup;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractConnection;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Arrows;
import net.sourceforge.plantuml.activitydiagram3.ftile.Connection;
import net.sourceforge.plantuml.activitydiagram3.ftile.ConnectionTranslatable;
import net.sourceforge.plantuml.activitydiagram3.ftile.Diamond;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
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
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UEmpty;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

class FtileWhile extends AbstractFtile {

	private final Ftile whileBlock;
	private final Ftile diamond1;
	private final TextBlock supplementarySouthText;

	public Set<Swimlane> getSwimlanes() {
		final Set<Swimlane> result = new HashSet<Swimlane>(whileBlock.getSwimlanes());
		result.add(getSwimlaneIn());
		return result;
	}

	public Swimlane getSwimlaneIn() {
		return diamond1.getSwimlaneIn();
	}

	public Swimlane getSwimlaneOut() {
		return getSwimlaneIn();
	}

	private FtileWhile(Ftile whileBlock, Ftile diamond1, TextBlock supplementarySouthText) {
		super(whileBlock.shadowing());
		this.whileBlock = whileBlock;
		this.diamond1 = diamond1;
		this.supplementarySouthText = supplementarySouthText;
	}

	private static TextBlock createLabel1(Display test, Display yes, UFont font, ISkinSimple spriteContainer,
			HtmlColor hyperlinkColor, boolean useUnderlineForHyperlink) {
		final FontConfiguration fc = new FontConfiguration(font, HtmlColorUtils.BLACK, hyperlinkColor,
				useUnderlineForHyperlink);
		final TextBlock tmpb = TextBlockUtils.create(yes, fc, HorizontalAlignment.LEFT, spriteContainer);
		if (test == null) {
			return tmpb;
		}
		return TextBlockUtils.mergeTB(TextBlockUtils.create(test, fc, HorizontalAlignment.LEFT, spriteContainer), tmpb,
				HorizontalAlignment.CENTER);
	}

	public static Ftile create(Swimlane swimlane, Ftile whileBlock, Display test, HtmlColor borderColor,
			HtmlColor backColor, HtmlColor arrowColor, Display yes, Display out2, UFont fontArrow,
			HtmlColor endInlinkColor, LinkRendering afterEndwhile, FtileFactory ftileFactory,
			ConditionStyle conditionStyle, UFont fontTest, HtmlColor hyperlinkColor, boolean useUnderlineForHyperlink) {

		final FontConfiguration fcArrow = new FontConfiguration(fontArrow, HtmlColorUtils.BLACK, hyperlinkColor,
				useUnderlineForHyperlink);
		final FontConfiguration fcTest = new FontConfiguration(fontTest, HtmlColorUtils.BLACK, hyperlinkColor,
				useUnderlineForHyperlink);
		final TextBlock yesTb = TextBlockUtils.create(yes, fcArrow, HorizontalAlignment.LEFT, ftileFactory);
		final TextBlock testTb = TextBlockUtils.create(test, fcTest, HorizontalAlignment.LEFT, ftileFactory);
		final TextBlock out = TextBlockUtils.create(out2, fcArrow, HorizontalAlignment.LEFT, ftileFactory);

		final Ftile diamond1;
		final TextBlock supplementarySouthText;
		if (conditionStyle == ConditionStyle.INSIDE) {
			supplementarySouthText = TextBlockUtils.empty(0, 0);
			diamond1 = new FtileDiamondInside(whileBlock.shadowing(), backColor, borderColor, swimlane, testTb)
					.withNorth(yesTb).withWest(out);
		} else if (conditionStyle == ConditionStyle.FOO1) {
			supplementarySouthText = TextBlockUtils.empty(0, 0);
			diamond1 = new FtileDiamondFoo1(whileBlock.shadowing(), backColor, borderColor, swimlane, testTb)
					.withNorth(yesTb).withWest(out);
		} else if (conditionStyle == ConditionStyle.DIAMOND) {
			supplementarySouthText = createLabel1(test, yes, fontArrow, ftileFactory, hyperlinkColor,
					useUnderlineForHyperlink);
			diamond1 = new FtileDiamond(whileBlock.shadowing(), backColor, borderColor, swimlane).withWest(out)
					.withSouth(supplementarySouthText);
		} else {
			throw new IllegalStateException();
		}

		final FtileWhile result = new FtileWhile(whileBlock, diamond1, supplementarySouthText);
		HtmlColor afterEndwhileColor = arrowColor;
		if (afterEndwhile != null && afterEndwhile.getColor() != null) {
			afterEndwhileColor = afterEndwhile.getColor();
		}

		final List<Connection> conns = new ArrayList<Connection>();
		conns.add(result.new ConnectionIn(LinkRendering.getColor(whileBlock.getInLinkRendering(), arrowColor)));
		conns.add(result.new ConnectionBack(endInlinkColor));
		conns.add(result.new ConnectionOut(afterEndwhileColor));
		return FtileUtils.addConnection(result, conns);
	}

	class ConnectionIn extends AbstractConnection implements ConnectionTranslatable {
		private final HtmlColor arrowColor;

		public ConnectionIn(HtmlColor arrowColor) {
			super(diamond1, whileBlock);
			this.arrowColor = arrowColor;
		}

		private Point2D getP1(final StringBounder stringBounder) {
			return getTranslateDiamond1(stringBounder).getTranslated(
					getFtile1().calculateDimension(stringBounder).getPointOut());
		}

		private Point2D getP2(final StringBounder stringBounder) {
			return getTranslateForWhile(stringBounder).getTranslated(
					getFtile2().calculateDimension(stringBounder).getPointIn());
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();

			final Snake snake = new Snake(arrowColor, Arrows.asToDown());
			snake.addPoint(getP1(stringBounder));
			snake.addPoint(getP2(stringBounder));

			ug.draw(snake);
		}

		public void drawTranslate(UGraphic ug, UTranslate translate1, UTranslate translate2) {
			final StringBounder stringBounder = ug.getStringBounder();
			final Point2D p1 = getP1(stringBounder);
			final Point2D p2 = getP2(stringBounder);
			final Snake snake = new Snake(arrowColor, Arrows.asToDown());
			final Point2D mp1a = translate1.getTranslated(p1);
			final Point2D mp2b = translate2.getTranslated(p2);
			final double middle = (mp1a.getY() + mp2b.getY()) / 2.0;
			snake.addPoint(mp1a);
			snake.addPoint(mp1a.getX(), middle);
			snake.addPoint(mp2b.getX(), middle);
			snake.addPoint(mp2b);
			ug.draw(snake);
		}
	}

	class ConnectionBack extends AbstractConnection implements ConnectionTranslatable {
		private final HtmlColor endInlinkColor;

		public ConnectionBack(HtmlColor endInlinkColor) {
			super(whileBlock, diamond1);
			this.endInlinkColor = endInlinkColor;
		}

		private Point2D getP1(final StringBounder stringBounder) {
			final FtileGeometry geo = whileBlock.calculateDimension(stringBounder);
			if (geo.hasPointOut() == false) {
				return null;
			}
			return getTranslateForWhile(stringBounder).getTranslated(geo.getPointOut());
		}

		private Point2D getP2(final StringBounder stringBounder) {
			return getTranslateDiamond1(stringBounder).getTranslated(new Point2D.Double(0, 0));
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();

			final Snake snake = new Snake(endInlinkColor, Arrows.asToLeft());
			final Dimension2D dimTotal = calculateDimension(stringBounder);
			final Point2D p1 = getP1(stringBounder);
			if (p1 == null) {
				return;
			}
			final Point2D p2 = getP2(stringBounder);
			final FtileGeometry dimDiamond1 = diamond1.calculateDimension(stringBounder);

			final double x1 = p1.getX();
			final double y1 = p1.getY();
			final double x2 = p2.getX() + dimDiamond1.getWidth();
			final double y2 = p2.getY() + dimDiamond1.getOutY() / 2;

			snake.addPoint(x1, y1);
			snake.addPoint(x1, y1 + Diamond.diamondHalfSize);
			final double xx = dimTotal.getWidth();
			snake.addPoint(xx, y1 + Diamond.diamondHalfSize);
			snake.addPoint(xx, y2);
			snake.addPoint(x2, y2);
			snake.emphasizeDirection(Direction.UP);

			ug.draw(snake);

			ug.apply(new UTranslate(x1, y1 + Diamond.diamondHalfSize)).draw(new UEmpty(5, Diamond.diamondHalfSize));

			// ug = ug.apply(new UChangeColor(endInlinkColor)).apply(new UChangeBackColor(endInlinkColor));
			// ug.apply(new UTranslate(xx, (y1 + y2) / 2)).draw(Arrows.asToUp());
		}

		public void drawTranslate(UGraphic ug, UTranslate translate1, UTranslate translate2) {
			final StringBounder stringBounder = ug.getStringBounder();
			final Snake snake = new Snake(endInlinkColor, Arrows.asToLeft());
			final Dimension2D dimTotal = calculateDimension(stringBounder);
			final Point2D ap1 = getP1(stringBounder);
			final Point2D ap2 = getP2(stringBounder);
			final Point2D p1 = translate1.getTranslated(ap1);
			final Point2D p2 = translate2.getTranslated(ap2);

			final Dimension2D dimDiamond1 = diamond1.calculateDimension(stringBounder);

			final double x1 = p1.getX();
			final double y1 = p1.getY();
			final double x2 = p2.getX() + dimDiamond1.getWidth();
			final double y2 = p2.getY() + dimDiamond1.getHeight() / 2;

			snake.addPoint(x1, y1);
			snake.addPoint(x1, y1 + Diamond.diamondHalfSize);
			final double xx = Math.max(translate1.getDx(), translate2.getDx()) + dimTotal.getWidth();
			snake.addPoint(xx, y1 + Diamond.diamondHalfSize);
			snake.addPoint(xx, y2);
			snake.addPoint(x2, y2);

			ug.draw(snake);

			ug.apply(new UTranslate(x1, y1 + Diamond.diamondHalfSize)).draw(new UEmpty(5, Diamond.diamondHalfSize));

			ug = ug.apply(new UChangeColor(endInlinkColor)).apply(new UChangeBackColor(endInlinkColor));
			ug.apply(new UTranslate(xx, (y1 + y2) / 2)).draw(Arrows.asToUp());

		}

	}

	class ConnectionOut extends AbstractConnection {
		private final HtmlColor afterEndwhileColor;

		public ConnectionOut(HtmlColor afterEndwhileColor) {
			super(diamond1, null);
			this.afterEndwhileColor = afterEndwhileColor;
		}

		private Point2D getP1(final StringBounder stringBounder) {
			return getTranslateDiamond1(stringBounder).getTranslated(new Point2D.Double(0, 0));
		}

		private Point2D getP2(final StringBounder stringBounder) {
			final FtileGeometry dimTotal = calculateDimension(stringBounder);
			return new Point2D.Double(dimTotal.getLeft(), dimTotal.getHeight());
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();

			final Snake snake = new Snake(afterEndwhileColor);

			final FtileGeometry dimDiamond1 = diamond1.calculateDimension(stringBounder);
			final Point2D p1 = getP1(stringBounder);
			final Point2D p2 = getP2(stringBounder);

			final double x1 = p1.getX();
			final double y1 = p1.getY() + dimDiamond1.getOutY() / 2;
			final double x2 = p2.getX();
			final double y2 = p2.getY();

			snake.addPoint(x1, y1);
			snake.addPoint(Diamond.diamondHalfSize, y1);
			snake.addPoint(Diamond.diamondHalfSize, y2);
			snake.emphasizeDirection(Direction.DOWN);

			ug.draw(snake);
			// ug = ug.apply(new UChangeColor(afterEndwhileColor)).apply(new UChangeBackColor(afterEndwhileColor));
			// ug.apply(new UTranslate(Diamond.diamondHalfSize, (y1 + y2) / 2)).draw(Arrows.asToDown());

			final Snake snake2 = new Snake(afterEndwhileColor);
			snake2.addPoint(Diamond.diamondHalfSize, y2);
			snake2.addPoint(x2, y2);
			ug.draw(snake2);

		}
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		ug.apply(getTranslateForWhile(stringBounder)).draw(whileBlock);
		ug.apply(getTranslateDiamond1(stringBounder)).draw(diamond1);
	}

	public FtileGeometry calculateDimension(StringBounder stringBounder) {
		final FtileGeometry geoDiamond1 = diamond1.calculateDimension(stringBounder);
		final Dimension2D dimSupplementarySouth = supplementarySouthText.calculateDimension(stringBounder);
		FtileGeometry geoWhile = whileBlock.calculateDimension(stringBounder);
		final double diff = dimSupplementarySouth.getWidth() - geoWhile.getWidth();
		if (diff > 0) {
			geoWhile = geoWhile.addMarginX(diff / 2);
		}
		final FtileGeometry geo = geoDiamond1.appendBottom(geoWhile);
		final double height = geo.getHeight() + 4 * Diamond.diamondHalfSize + dimSupplementarySouth.getHeight();
		final double dx = 2 * Diamond.diamondHalfSize;
		return new FtileGeometry(geo.getWidth() + dx + Diamond.diamondHalfSize, height, geo.getLeft() + dx, 0, height);

	}

	@Override
	public UTranslate getTranslateFor(Ftile child, StringBounder stringBounder) {
		if (child == whileBlock) {
			return getTranslateForWhile(stringBounder);
		}
		if (child == diamond1) {
			return getTranslateDiamond1(stringBounder);
		}
		throw new UnsupportedOperationException();
	}

	private UTranslate getTranslateForWhile(StringBounder stringBounder) {
		final FtileGeometry dimDiamond1 = diamond1.calculateDimension(stringBounder);

		final FtileGeometry dimTotal = calculateDimension(stringBounder);
		final FtileGeometry dimWhile = whileBlock.calculateDimension(stringBounder);

		final double y = dimDiamond1.getHeight()
				+ (dimTotal.getHeight() - dimDiamond1.getHeight() - dimWhile.getHeight()) / 2;

		final double x = dimTotal.getLeft() - dimWhile.getLeft();
		return new UTranslate(x, y);

	}

	private UTranslate getTranslateDiamond1(StringBounder stringBounder) {
		final FtileGeometry dimTotal = calculateDimension(stringBounder);
		final FtileGeometry dimDiamond1 = diamond1.calculateDimension(stringBounder);

		final double y1 = 0;
		final double x1 = dimTotal.getLeft() - dimDiamond1.getLeft();
		return new UTranslate(x1, y1);
	}

}
