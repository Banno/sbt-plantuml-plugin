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

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractConnection;
import net.sourceforge.plantuml.activitydiagram3.ftile.Arrows;
import net.sourceforge.plantuml.activitydiagram3.ftile.Connection;
import net.sourceforge.plantuml.activitydiagram3.ftile.ConnectionTranslatable;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileAssemblySimple;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactoryDelegator;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileHeightFixed;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileMarged;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileUtils;
import net.sourceforge.plantuml.activitydiagram3.ftile.Snake;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileBlackBlock;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class FtileFactoryDelegatorCreateFork extends FtileFactoryDelegator {

	private final double spaceArroundBlackBar = 20;
	private final double barHeight = 6;
	private final double xMargin = 14;

	private final Rose rose = new Rose();

	public FtileFactoryDelegatorCreateFork(FtileFactory factory, ISkinParam skinParam) {
		super(factory, skinParam);
	}

	@Override
	public Ftile createFork(Swimlane swimlane, List<Ftile> all) {
		final HtmlColor colorBar = rose.getHtmlColor(getSkinParam(), ColorParam.activityBar);
		final HtmlColor arrowColor = rose.getHtmlColor(getSkinParam(), ColorParam.activityArrow);

		final Dimension2D dimSuper = super.createFork(swimlane, all).calculateDimension(getStringBounder());
		final double height1 = dimSuper.getHeight() + 2 * spaceArroundBlackBar;

		final List<Ftile> list = new ArrayList<Ftile>();
		for (Ftile tmp : all) {
			list.add(new FtileHeightFixed(new FtileMarged(tmp, xMargin), height1));
		}

		Ftile inner = super.createFork(swimlane, list);

		final List<Connection> conns = new ArrayList<Connection>();

		final Ftile black = new FtileBlackBlock(shadowing(), colorBar, list.get(0).getSwimlaneIn());
		double x = 0;
		for (Ftile tmp : list) {
			final Dimension2D dim = tmp.calculateDimension(getStringBounder());
			conns.add(new ConnectionIn(black, tmp, x, arrowColor));
			conns.add(new ConnectionOut(tmp, black, x, arrowColor, height1));
			x += dim.getWidth();
		}

		inner = FtileUtils.addConnection(inner, conns);
		((FtileBlackBlock) black).setDimenstion(inner.calculateDimension(getStringBounder()).getWidth(), barHeight);
		final Ftile tmp1 = new FtileAssemblySimple(black, inner);
		return new FtileAssemblySimple(tmp1, black);
	}

	class ConnectionIn extends AbstractConnection implements ConnectionTranslatable {

		private final double x;
		private final HtmlColor arrowColor;

		public ConnectionIn(Ftile ftile1, Ftile ftile2, double x, HtmlColor arrowColor) {
			super(ftile1, ftile2);
			this.x = x;
			this.arrowColor = arrowColor;
		}

		public void drawU(UGraphic ug) {
			ug = ug.apply(new UTranslate(x, 0));
			final FtileGeometry geo = getFtile2().calculateDimension(getStringBounder());
			final Snake s = new Snake(arrowColor, Arrows.asToDown());
			s.addPoint(geo.getLeft(), 0);
			s.addPoint(geo.getLeft(), geo.getInY());
			ug.draw(s);
		}

		public void drawTranslate(UGraphic ug, UTranslate translate1, UTranslate translate2) {
			ug = ug.apply(new UTranslate(x, 0));
			final FtileGeometry geo = getFtile2().calculateDimension(getStringBounder());
			final Point2D p1 = new Point2D.Double(geo.getLeft(), 0);
			final Point2D p2 = new Point2D.Double(geo.getLeft(), geo.getInY());

			final Snake snake = new Snake(arrowColor, Arrows.asToDown());
			final Point2D mp1a = translate1.getTranslated(p1);
			final Point2D mp2b = translate2.getTranslated(p2);
			final double middle = mp1a.getY() + 4;
			snake.addPoint(mp1a);
			snake.addPoint(mp1a.getX(), middle);
			snake.addPoint(mp2b.getX(), middle);
			snake.addPoint(mp2b);
			ug.draw(snake);
		}
	}

	class ConnectionOut extends AbstractConnection implements ConnectionTranslatable {

		private final double x;
		private final HtmlColor arrowColor;
		private final double height;

		public ConnectionOut(Ftile ftile1, Ftile ftile2, double x, HtmlColor arrowColor, double height) {
			super(ftile1, ftile2);
			this.x = x;
			this.arrowColor = arrowColor;
			this.height = height;
		}

		public void drawU(UGraphic ug) {
			ug = ug.apply(new UTranslate(x, 0));
			final FtileGeometry geo = getFtile1().calculateDimension(getStringBounder());
			if (geo.hasPointOut() == false) {
				return;
			}
			final Snake s = new Snake(arrowColor, Arrows.asToDown());
			s.addPoint(geo.getLeft(), geo.getOutY());
			s.addPoint(geo.getLeft(), height);
			ug.draw(s);
		}

		public void drawTranslate(UGraphic ug, UTranslate translate1, UTranslate translate2) {
			ug = ug.apply(new UTranslate(x, 0));
			final FtileGeometry geo = getFtile1().calculateDimension(getStringBounder());
			if (geo.hasPointOut() == false) {
				return;
			}
			final Point2D p1 = new Point2D.Double(geo.getLeft(), geo.getOutY());
			final Point2D p2 = new Point2D.Double(geo.getLeft(), height);

			final Snake snake = new Snake(arrowColor, Arrows.asToDown());
			final Point2D mp1a = translate1.getTranslated(p1);
			final Point2D mp2b = translate2.getTranslated(p2);
			final double middle = mp2b.getY() - 14;
			snake.addPoint(mp1a);
			snake.addPoint(mp1a.getX(), middle);
			snake.addPoint(mp2b.getX(), middle);
			snake.addPoint(mp2b);
			ug.draw(snake);
		}

	}

}
