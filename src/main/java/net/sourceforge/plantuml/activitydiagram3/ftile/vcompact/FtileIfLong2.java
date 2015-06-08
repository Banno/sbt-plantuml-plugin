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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractConnection;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Arrows;
import net.sourceforge.plantuml.activitydiagram3.ftile.Connection;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileAssemblySimple;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileMinWidth;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileUtils;
import net.sourceforge.plantuml.activitydiagram3.ftile.Snake;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDiamondInside2;
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

class FtileIfLong2 extends AbstractFtile {

	private final double xSeparation = 20;

	private final List<Ftile> tiles;
	private final Ftile tile2;
	private final List<Ftile> diamonds;
	private final List<Ftile> couples = new ArrayList<Ftile>();

	private final HtmlColor arrowColor;

	private FtileIfLong2(List<Ftile> diamonds, List<Ftile> tiles, Ftile tile2, HtmlColor arrowColor) {
		super(tiles.get(0).shadowing() || tile2.shadowing());
		if (diamonds.size() != tiles.size()) {
			throw new IllegalArgumentException();
		}
		for (int i = 0; i < diamonds.size(); i++) {
			couples.add(new FtileAssemblySimple(diamonds.get(i), tiles.get(i)));
		}
		this.tile2 = tile2;
		this.diamonds = new ArrayList<Ftile>(diamonds);
		this.tiles = new ArrayList<Ftile>(tiles);

		this.arrowColor = arrowColor;

	}

	private static List<Ftile> alignDiamonds(List<Ftile> diamonds, StringBounder stringBounder) {
		double maxOutY = 0;
		for (Ftile diamond : diamonds) {
			maxOutY = Math.max(maxOutY, diamond.calculateDimension(stringBounder).getOutY());
		}
		final List<Ftile> result = new ArrayList<Ftile>();
		for (int i = 0; i < diamonds.size(); i++) {
			Ftile diamond = diamonds.get(i);
			final double missing = maxOutY - diamond.calculateDimension(stringBounder).getOutY();
			assert missing >= 0;
			diamond = FtileUtils.addVerticalMargin(diamond, missing / 2, 20);
			result.add(diamond);
		}
		return result;
	}

	public Set<Swimlane> getSwimlanes() {
		final Set<Swimlane> result = new HashSet<Swimlane>();
		if (getSwimlaneIn() != null) {
			result.add(getSwimlaneIn());
		}
		for (Ftile tile : couples) {
			result.addAll(tile.getSwimlanes());
		}
		result.addAll(tile2.getSwimlanes());
		return Collections.unmodifiableSet(result);
	}

	public Swimlane getSwimlaneIn() {
		return couples.get(0).getSwimlaneIn();
	}

	public Swimlane getSwimlaneOut() {
		return getSwimlaneIn();
	}

	static Ftile create(Swimlane swimlane, HtmlColor borderColor, HtmlColor backColor, UFont font,
			HtmlColor arrowColor, FtileFactory ftileFactory, ConditionStyle conditionStyle, List<Branch> thens,
			Branch branch2, HtmlColor hyperlinkColor, boolean useUnderlineForHyperlink) {

		final List<Ftile> tiles = new ArrayList<Ftile>();

		for (Branch branch : thens) {
			tiles.add(new FtileMinWidth(branch.getFtile(), 30));
		}

		final Ftile tile2 = new FtileMinWidth(branch2.getFtile(), 30);

		final FontConfiguration fc = new FontConfiguration(font, HtmlColorUtils.BLACK, hyperlinkColor,
				useUnderlineForHyperlink);

		List<Ftile> diamonds = new ArrayList<Ftile>();
		final List<Connection> conns = new ArrayList<Connection>();
		for (Branch branch : thens) {
			final TextBlock tb1 = TextBlockUtils.create(branch.getLabelPositive(), fc, HorizontalAlignment.LEFT,
					ftileFactory);
			final TextBlock tbTest = TextBlockUtils.create(branch.getLabelTest(), fc, HorizontalAlignment.LEFT,
					ftileFactory);
			FtileDiamondInside2 diamond = new FtileDiamondInside2(branch.shadowing(), backColor, borderColor, swimlane,
					tbTest);
			diamond = diamond.withNorth(tb1);
			diamonds.add(diamond);
		}

		final TextBlock tb2 = TextBlockUtils.create(branch2.getLabelPositive(), fc, HorizontalAlignment.LEFT,
				ftileFactory);
		final int last = diamonds.size() - 1;
		diamonds.set(last, ((FtileDiamondInside2) diamonds.get(last)).withEast(tb2));

		diamonds = alignDiamonds(diamonds, ftileFactory.getStringBounder());

		final FtileIfLong2 result = new FtileIfLong2(diamonds, tiles, tile2, arrowColor);

		for (int i = 0; i < thens.size(); i++) {
			final Ftile ftile = tiles.get(i);
			final Ftile diam = diamonds.get(i);

			final HtmlColor color = thens.get(i).getInlinkRenderingColor();
			conns.add(result.new ConnectionVerticalIn(diam, ftile, color == null ? arrowColor : color));
			conns.add(result.new ConnectionVerticalOut(ftile, arrowColor));
		}

		for (int i = 0; i < diamonds.size() - 1; i++) {
			final Ftile diam1 = diamonds.get(i);
			final Ftile diam2 = diamonds.get(i + 1);
			conns.add(result.new ConnectionHorizontal(diam1, diam2, arrowColor));
		}
		conns.add(result.new ConnectionIn(arrowColor));
		conns.add(result.new ConnectionLastElseIn(arrowColor));
		conns.add(result.new ConnectionLastElseOut(arrowColor));
		conns.add(result.new ConnectionHline(arrowColor));

		return FtileUtils.addConnection(result, conns);
	}

	class ConnectionHorizontal extends AbstractConnection {

		private final HtmlColor color;

		public ConnectionHorizontal(Ftile diam1, Ftile diam2, HtmlColor color) {
			super(diam1, diam2);
			this.color = color;
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();
			final Point2D p1 = getP1(stringBounder);
			final Point2D p2 = getP2(stringBounder);

			final Snake snake = new Snake(color, Arrows.asToRight());
			snake.addPoint(p1);
			snake.addPoint(p2);
			ug.draw(snake);
		}

		private Point2D getP1(StringBounder stringBounder) {
			final FtileGeometry dimDiamond1 = getFtile1().calculateDimension(stringBounder);
			final Point2D p = new Point2D.Double(dimDiamond1.getLeft() * 2, getYdiamontOutToLeft(dimDiamond1,
					stringBounder));

			return getTranslateDiamond1(getFtile1(), stringBounder).getTranslated(p);
		}

		private Point2D getP2(StringBounder stringBounder) {
			final FtileGeometry dimDiamond1 = getFtile2().calculateDimension(stringBounder);
			final Point2D p = new Point2D.Double(0, getYdiamontOutToLeft(dimDiamond1, stringBounder));
			return getTranslateDiamond1(getFtile2(), stringBounder).getTranslated(p);
		}

	}

	static private double getYdiamontOutToLeft(FtileGeometry dimDiamond1, StringBounder stringBounder) {
		return (dimDiamond1.getInY() + dimDiamond1.getOutY()) / 2;
	}

	class ConnectionIn extends AbstractConnection {

		private final HtmlColor arrowColor;

		public ConnectionIn(HtmlColor arrowColor) {
			super(null, diamonds.get(0));
			this.arrowColor = arrowColor;
		}

		public void drawU(UGraphic ug) {
			final UTranslate tr = getTranslateDiamond1(getFtile2(), ug.getStringBounder());
			final Point2D p2 = tr.getTranslated(getFtile2().calculateDimension(ug.getStringBounder()).getPointIn());
			final Snake snake = new Snake(arrowColor, Arrows.asToDown());
			final Point2D p1 = calculateDimensionInternal(ug.getStringBounder()).getPointIn();

			snake.addPoint(p1);
			snake.addPoint(p2.getX(), p1.getY());
			snake.addPoint(p2);
			ug.draw(snake);
		}

	}

	class ConnectionLastElseIn extends AbstractConnection {

		private final HtmlColor arrowColor;

		public ConnectionLastElseIn(HtmlColor arrowColor) {
			super(diamonds.get(diamonds.size() - 1), tile2);
			this.arrowColor = arrowColor;
		}

		public void drawU(UGraphic ug) {
			final Point2D p1 = getP1(ug.getStringBounder());
			final UTranslate tr2 = getTranslate2(ug.getStringBounder());
			final Point2D p2 = tr2.getTranslated(getFtile2().calculateDimension(ug.getStringBounder()).getPointIn());
			final Snake snake = new Snake(arrowColor, Arrows.asToDown());
			snake.addPoint(p1);
			snake.addPoint(p2.getX(), p1.getY());
			snake.addPoint(p2);
			ug.draw(snake);
		}

		private Point2D getP1(StringBounder stringBounder) {
			final FtileGeometry dimDiamond1 = getFtile1().calculateDimension(stringBounder);
			final Point2D p = new Point2D.Double(dimDiamond1.getLeft() * 2, getYdiamontOutToLeft(dimDiamond1,
					stringBounder));
			return getTranslateDiamond1(getFtile1(), stringBounder).getTranslated(p);
		}

	}

	class ConnectionLastElseOut extends AbstractConnection {

		private final HtmlColor arrowColor;

		public ConnectionLastElseOut(HtmlColor arrowColor) {
			super(tile2, null);
			this.arrowColor = arrowColor;
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();
			final UTranslate tr1 = getTranslate2(stringBounder);
			final FtileGeometry dim = getFtile1().calculateDimension(stringBounder);
			if (dim.hasPointOut() == false) {
				return;
			}
			final Point2D p1 = tr1.getTranslated(dim.getPointOut());
			final double totalHeight = calculateDimensionInternal(stringBounder).getHeight();
			final Point2D p2 = new Point2D.Double(p1.getX(), totalHeight);

			final Snake snake = new Snake(arrowColor, Arrows.asToDown());
			snake.addPoint(p1);
			snake.addPoint(p2);
			ug.draw(snake);
		}

	}

	class ConnectionVerticalIn extends AbstractConnection {

		private final HtmlColor color;

		public ConnectionVerticalIn(Ftile diamond, Ftile tile, HtmlColor color) {
			super(diamond, tile);
			this.color = color;
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();
			final Point2D p1 = getP1(stringBounder);
			final Point2D p2 = getP2(stringBounder);

			final Snake snake = new Snake(color, Arrows.asToDown());
			snake.addPoint(p1);
			snake.addPoint(p2);
			ug.draw(snake);
		}

		private Point2D getP1(StringBounder stringBounder) {
			final Point2D p = getFtile1().calculateDimension(stringBounder).getPointOut();
			return getTranslateDiamond1(getFtile1(), stringBounder).getTranslated(p);
		}

		private Point2D getP2(StringBounder stringBounder) {
			final Point2D p = getFtile2().calculateDimension(stringBounder).getPointIn();
			return getTranslate1(getFtile2(), stringBounder).getTranslated(p);
		}

	}

	class ConnectionVerticalOut extends AbstractConnection {

		private final HtmlColor color;

		public ConnectionVerticalOut(Ftile tile, HtmlColor color) {
			super(tile, null);
			this.color = color;
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();
			final double totalHeight = calculateDimensionInternal(stringBounder).getHeight();
			final Point2D p1 = getP1(stringBounder);
			if (p1 == null) {
				return;
			}
			final Point2D p2 = new Point2D.Double(p1.getX(), totalHeight);

			final Snake snake = new Snake(color, Arrows.asToDown());
			snake.addPoint(p1);
			snake.addPoint(p2);
			ug.draw(snake);
		}

		private Point2D getP1(StringBounder stringBounder) {
			final FtileGeometry geo = getFtile1().calculateDimension(stringBounder);
			if (geo.hasPointOut() == false) {
				return null;
			}
			final Point2D p = geo.getPointOut();
			return getTranslate1(getFtile1(), stringBounder).getTranslated(p);
		}

	}

	class ConnectionHline extends AbstractConnection {

		private final HtmlColor arrowColor;

		public ConnectionHline(HtmlColor arrowColor) {
			super(null, null);
			this.arrowColor = arrowColor;
		}

		public void drawU(UGraphic ug) {
			final StringBounder stringBounder = ug.getStringBounder();
			final Dimension2D totalDim = calculateDimensionInternal(stringBounder);

			final List<Ftile> all = new ArrayList<Ftile>(couples);
			all.add(tile2);
			double minX = totalDim.getWidth() / 2;
			double maxX = totalDim.getWidth() / 2;
			for (Ftile tmp : all) {
				if (tmp.calculateDimension(stringBounder).hasPointOut() == false) {
					continue;
				}
				final UTranslate ut = getTranslateFor(tmp, stringBounder);
				final double out = tmp.calculateDimension(stringBounder).translate(ut).getLeft();
				minX = Math.min(minX, out);
				maxX = Math.max(maxX, out);
			}

			final Snake s = new Snake(arrowColor);
			s.goUnmergeable();
			final double height = totalDim.getHeight();
			s.addPoint(minX, height);
			s.addPoint(maxX, height);
			ug.draw(s);
		}
	}

	@Override
	public UTranslate getTranslateFor(Ftile child, StringBounder stringBounder) {
		if (child == tile2) {
			return getTranslate2(stringBounder);
		}
		if (couples.contains(child)) {
			return getTranslateCouple1(child, stringBounder);
		}
		throw new UnsupportedOperationException();
	}

	private UTranslate getTranslate2(StringBounder stringBounder) {
		final Dimension2D dimTotal = calculateDimensionInternal(stringBounder);
		final Dimension2D dim2 = tile2.calculateDimension(stringBounder);

		final double x2 = dimTotal.getWidth() - dim2.getWidth();

		final double h = 0; // getAllDiamondsHeight(stringBounder);
		final double y2 = (dimTotal.getHeight() - h * 2 - dim2.getHeight()) / 2 + h;

		return new UTranslate(x2, y2);

	}

	private UTranslate getTranslateDiamond1(Ftile diamond, StringBounder stringBounder) {
		final int idx = diamonds.indexOf(diamond);
		if (idx == -1) {
			throw new IllegalArgumentException();
		}
		final UTranslate trCouple = getTranslateCouple1(couples.get(idx), stringBounder);
		final UTranslate in = couples.get(idx).getTranslateFor(diamond, stringBounder);
		return trCouple.compose(in);
	}

	public UTranslate getTranslate1(Ftile tile, StringBounder stringBounder) {
		final int idx = tiles.indexOf(tile);
		if (idx == -1) {
			throw new IllegalArgumentException();
		}
		final UTranslate trCouple = getTranslateCouple1(couples.get(idx), stringBounder);
		final UTranslate in = couples.get(idx).getTranslateFor(tile, stringBounder);
		return trCouple.compose(in);
	}

	private UTranslate getTranslateCouple1(Ftile candidat, StringBounder stringBounder) {
		double x1 = 0;

		for (Ftile couple : couples) {
			final FtileGeometry dim1 = couple.calculateDimension(stringBounder);
			if (couple == candidat) {
				return new UTranslate(x1, 25);
			}
			x1 += dim1.getWidth() + xSeparation;
		}
		throw new IllegalArgumentException();

	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		for (Ftile couple : couples) {
			ug.apply(getTranslateCouple1(couple, stringBounder)).draw(couple);
		}

		ug.apply(getTranslate2(stringBounder)).draw(tile2);
	}

	private FtileGeometry calculateDimensionInternal(StringBounder stringBounder) {
		Dimension2D result = new Dimension2DDouble(0, 0);
		for (Ftile couple : couples) {
			result = Dimension2DDouble.mergeLR(result, couple.calculateDimension(stringBounder));
		}
		final FtileGeometry dimTile2 = tile2.calculateDimension(stringBounder);
		result = Dimension2DDouble.mergeLR(result, dimTile2);
		result = Dimension2DDouble.delta(result, xSeparation * couples.size(), 100);

		return new FtileGeometry(result, result.getWidth() / 2, 0);
	}

	public FtileGeometry calculateDimension(StringBounder stringBounder) {
		final Dimension2D dimTotal = calculateDimensionInternal(stringBounder);

		final List<Ftile> all = new ArrayList<Ftile>(tiles);
		all.add(tile2);
		for (Ftile tmp : all) {
			if (tmp.calculateDimension(stringBounder).hasPointOut()) {
				return new FtileGeometry(dimTotal, dimTotal.getWidth() / 2, 0, dimTotal.getHeight());
			}
		}
		return new FtileGeometry(dimTotal, dimTotal.getWidth() / 2, 0);

	}


}
