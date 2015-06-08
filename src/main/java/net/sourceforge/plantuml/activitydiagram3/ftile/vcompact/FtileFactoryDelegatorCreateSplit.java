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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractConnection;
import net.sourceforge.plantuml.activitydiagram3.ftile.Arrows;
import net.sourceforge.plantuml.activitydiagram3.ftile.Connection;
import net.sourceforge.plantuml.activitydiagram3.ftile.ConnectionTranslatable;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactoryDelegator;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileHeightFixed;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileKilled;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileMarged;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileUtils;
import net.sourceforge.plantuml.activitydiagram3.ftile.Snake;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class FtileFactoryDelegatorCreateSplit extends FtileFactoryDelegator {

	private final double spaceArroundBlackBar = 20;
	private final double barHeight = 6;
	private final double xMargin = 14;

	private final Rose rose = new Rose();

	public FtileFactoryDelegatorCreateSplit(FtileFactory factory, ISkinParam skinParam) {
		super(factory, skinParam);
	}

	static private boolean isSimpleSwimlanes(List<Ftile> all) {
		final Set<Swimlane> already = new HashSet<Swimlane>();
		for (Ftile ftile : all) {
			final Set<Swimlane> currents = ftile.getSwimlanes();
			if (currents.size() != 1) {
				return false;
			}
			assert currents.size() == 1;
			final Swimlane current = currents.iterator().next();
			if (already.contains(current)) {
				return false;
			}
			already.add(current);
		}
		return already.size() > 1;
	}

	static private boolean isSeveralSwimlanes(List<Ftile> all) {
		final Set<Swimlane> already = new HashSet<Swimlane>();
		for (Ftile ftile : all) {
			final Set<Swimlane> currents = ftile.getSwimlanes();
			if (currents.size() > 1) {
				return true;
			}
			assert currents.size() == 0 || currents.size() == 1;
			if (currents.size() == 1) {
				final Swimlane current = currents.iterator().next();
				already.add(current);
			}
		}
		return already.size() > 1;
	}

	@Override
	public Ftile createSplit(List<Ftile> all) {
		// OptionFlags.SWI2
		// if (all != null)
		// return severalSwimlanes(all);
		// // if (isSimpleSwimlanes(all)) {
		// return simpleSwimlanes(all);
		// // return severalSwimlanes(all);
		// // } else if (isSeveralSwimlanes(all)) {
		// // return severalSwimlanes(all);
		// }
		final HtmlColor arrowColor = rose.getHtmlColor(getSkinParam(), ColorParam.activityArrow);

		final Dimension2D dimSuper = super.createSplit(all).calculateDimension(getStringBounder());
		final double height1 = dimSuper.getHeight() + 2 * spaceArroundBlackBar;

		final List<Ftile> list = new ArrayList<Ftile>();
		for (Ftile tmp : all) {
			list.add(new FtileHeightFixed(new FtileMarged(tmp, xMargin), height1));
		}

		Ftile inner = super.createSplit(list);

		final List<Connection> conns = new ArrayList<Connection>();

		double x = 0;
		boolean hasOut = false;
		for (Ftile tmp : list) {
			final Dimension2D dim = tmp.calculateDimension(getStringBounder());
			conns.add(new ConnectionIn(tmp, x, arrowColor, getTextBlock(tmp.getInLinkRendering())));
			final boolean hasOutTmp = tmp.calculateDimension(getStringBounder()).hasPointOut();
			if (hasOutTmp) {
				conns.add(new ConnectionOut(tmp, x, arrowColor, height1, getTextBlock(tmp.getOutLinkRendering())));
				hasOut = true;
			}
			x += dim.getWidth();
		}
		final double totalWidth = inner.calculateDimension(getStringBounder()).getWidth();
		conns.add(new ConnectionHline2(inner, OptionFlags.SWI2 ? HtmlColorUtils.BLUE : arrowColor, 0, list, totalWidth));
		if (hasOut) {
			conns.add(new ConnectionHline2(inner, OptionFlags.SWI2 ? HtmlColorUtils.GREEN : arrowColor, height1, list,
					totalWidth));
		}

		inner = FtileUtils.addConnection(inner, conns);
		if (hasOut == false) {
			inner = new FtileKilled(inner);
		}
		return inner;
	}

	// private Ftile severalSwimlanes(List<Ftile> all) {
	// final HtmlColor arrowColor = rose.getHtmlColor(getSkinParam(), ColorParam.activityArrow);
	// final Dimension2D dimSuper = new FtileForkInner1(all).calculateDimension(getStringBounder());
	// final double height1 = dimSuper.getHeight() + 2 * spaceArroundBlackBar;
	// final List<Ftile> list = new ArrayList<Ftile>();
	// for (Ftile tmp : all) {
	// list.add(new FtileHeightFixed(new FtileMarged(tmp, xMargin), height1));
	// // list.add(new FtileMarged(tmp, xMargin));
	// // list.add(tmp);
	// }
	//
	// Ftile inner = new FtileForkInner1(list);
	// final List<Connection> conns = new ArrayList<Connection>();
	// boolean hasOut = false;
	// for (Ftile tmp : list) {
	// // final Dimension2D dim = tmp.calculateDimension(getStringBounder());
	// final UTranslate translateFor = inner.getTranslateFor(tmp, getStringBounder());
	// if (translateFor == null) {
	// continue;
	// }
	// final double x = translateFor.getDx();
	// conns.add(new ConnectionIn(tmp, x, arrowColor));
	// final boolean hasOutTmp = tmp.calculateDimension(getStringBounder()).hasPointOut();
	// if (hasOutTmp) {
	// conns.add(new ConnectionOut(tmp, x, arrowColor, height1));
	// hasOut = true;
	// }
	// // x += dim.getWidth();
	// }
	// final double totalWidth = inner.calculateDimension(getStringBounder()).getWidth();
	// conns.add(new ConnectionHline2(inner, arrowColor, 0, list, totalWidth));
	// if (hasOut) {
	// conns.add(new ConnectionHline2(inner, arrowColor, height1, list, totalWidth));
	// }
	// inner = FtileUtils.addConnection(inner, conns);
	//
	// return inner;
	// }

	private TextBlock getTextBlock(LinkRendering linkRendering) {
		// DUP1433
		final Display display = LinkRendering.getDisplay(linkRendering);
		if (display == null) {
			return null;
		}
		final ISkinParam skinParam = getSkinParam();
		final UFont font = skinParam.getFont(FontParam.ACTIVITY_ARROW, null, false);
		final HtmlColor color = rose.getFontColor(skinParam, FontParam.ACTIVITY_ARROW);
		final FontConfiguration fontConfiguration = new FontConfiguration(font, color, skinParam.getHyperlinkColor(),
				skinParam.useUnderlineForHyperlink());
		return TextBlockUtils.create(display, fontConfiguration, HorizontalAlignment.LEFT, null, true);
	}

	private Ftile simpleSwimlanes(List<Ftile> all) {
		final HtmlColor arrowColor = rose.getHtmlColor(getSkinParam(), ColorParam.activityArrow);

		final Dimension2D dimSuper = new FtileSplit1(all).calculateDimension(getStringBounder());
		final double height1 = dimSuper.getHeight() + 2 * spaceArroundBlackBar;

		final List<Ftile> list = new ArrayList<Ftile>();
		for (Ftile tmp : all) {
			list.add(new FtileHeightFixed(new FtileMarged(tmp, xMargin), height1));
		}

		Ftile inner = new FtileSplit1(list);

		final List<Connection> conns = new ArrayList<Connection>();

		boolean hasOut = false;
		for (Ftile tmp : list) {
			// final Dimension2D dim = tmp.calculateDimension(getStringBounder());
			final double x = inner.getTranslateFor(tmp, getStringBounder()).getDx();
			conns.add(new ConnectionIn(tmp, x, arrowColor, null));
			final boolean hasOutTmp = tmp.calculateDimension(getStringBounder()).hasPointOut();
			if (hasOutTmp) {
				conns.add(new ConnectionOut(tmp, x, arrowColor, height1, null));
				hasOut = true;
			}
			// x += dim.getWidth();
		}
		final double totalWidth = inner.calculateDimension(getStringBounder()).getWidth();
		conns.add(new ConnectionHline3(inner, arrowColor, 0, list, totalWidth));
		if (hasOut) {
			conns.add(new ConnectionHline3(inner, arrowColor, height1, list, totalWidth));
		}
		//
		inner = FtileUtils.addConnection(inner, conns);
		if (hasOut == false) {
			inner = new FtileKilled(inner);
		}
		return inner;

	}

	static class ConnectionHline2 extends AbstractConnection {

		private final Ftile inner;
		private final double y;
		private final HtmlColor arrowColor;
		private final List<Ftile> list;
		private final double totalWidth;

		public ConnectionHline2(Ftile inner, HtmlColor arrowColor, double y, List<Ftile> list, double totalWidth) {
			super(null, null);
			this.inner = inner;
			this.y = y;
			this.arrowColor = arrowColor;
			this.list = list;
			this.totalWidth = totalWidth;
		}

		public void drawU(UGraphic ug) {
			double minX = Double.MAX_VALUE;
			double maxX = 0;
			if (y == 0 && ug instanceof UGraphicInterceptorOneSwimlane) {
				final Swimlane intoSw = ((UGraphicInterceptorOneSwimlane) ug).getSwimlane();
				boolean found = false;
				for (Ftile tmp : list) {
					if (tmp.getSwimlaneIn() == intoSw) {
						found = true;
					}
				}
				if (found == false) {
					return;
				}
			}
			final StringBounder stringBounder = ug.getStringBounder();
			for (Ftile tmp : list) {
				if (y > 0 && tmp.calculateDimension(stringBounder).hasPointOut() == false) {
					continue;
				}
				final UTranslate ut = inner.getTranslateFor(tmp, stringBounder);
				if (ut == null) {
					continue;
				}
				final double middle = tmp.calculateDimension(stringBounder).translate(ut).getLeft();
				minX = Math.min(minX, middle);
				maxX = Math.max(maxX, middle);
			}
			if (minX > totalWidth / 2) {
				minX = totalWidth / 2;
			}
			if (maxX < totalWidth / 2) {
				maxX = totalWidth / 2;
			}

			final Snake s = new Snake(arrowColor);
			s.goUnmergeable();
			s.addPoint(minX, y);
			s.addPoint(maxX, y);
			ug.draw(s);
		}
	}

	static class ConnectionHline3 extends AbstractConnection implements ConnectionTranslatable {

		private final Ftile inner;
		private final double y;
		private final HtmlColor arrowColor;
		private final List<Ftile> list;
		private final double totalWidth;

		public ConnectionHline3(Ftile inner, HtmlColor arrowColor, double y, List<Ftile> list, double totalWidth) {
			super(null, null);
			this.inner = inner;
			this.y = y;
			this.arrowColor = arrowColor;
			this.list = list;
			this.totalWidth = totalWidth;
		}

		public void drawU(UGraphic ug) {
			throw new UnsupportedOperationException();
		}

		public void drawTranslate(UGraphic ug, UTranslate translate1, UTranslate translate2) {
			final StringBounder stringBounder = ug.getStringBounder();
			final UTranslate left = inner.getSwimlaneIn().getTranslate();
			double minX = inner.calculateDimension(stringBounder).getLeft() + left.getDx();
			double maxX = minX;

			for (Ftile tmp : list) {
				final FtileGeometry tmpGeom = tmp.calculateDimension(stringBounder);
				final UTranslate tpos = inner.getTranslateFor(tmp, stringBounder);
				for (Swimlane sw : tmp.getSwimlanes()) {
					final double x = tmpGeom.translate(sw.getTranslate().compose(tpos)).getLeft();
					minX = Math.min(minX, x);
					maxX = Math.max(maxX, x);
				}
			}
			final Dimension2D dimInner = inner.calculateDimension(stringBounder);

			final Snake s = new Snake(arrowColor);
			// final Snake s = new Snake(HtmlColorUtils.GREEN);
			s.goUnmergeable();
			s.addPoint(minX, y);
			s.addPoint(maxX, y);
			ug.draw(s);
		}
	}

	static class ConnectionIn extends AbstractConnection {

		private final double x;
		private final HtmlColor arrowColor;
		private final TextBlock text;

		public ConnectionIn(Ftile tmp, double x, HtmlColor arrowColor, TextBlock text) {
			super(null, tmp);
			this.x = x;
			this.arrowColor = arrowColor;
			this.text = text;
		}

		public void drawU(UGraphic ug) {
			ug = ug.apply(new UTranslate(x, 0));
			final FtileGeometry geo = getFtile2().calculateDimension(ug.getStringBounder());
			final double left = geo.getLeft();
			final Snake s = new Snake(arrowColor, Arrows.asToDown());
			s.setLabel(text);
			s.addPoint(left, 0);
			s.addPoint(left, geo.getInY());
			ug.draw(s);
		}
	}

	static class ConnectionOut extends AbstractConnection {

		private final double x;
		private final HtmlColor arrowColor;
		private final double height;
		private final TextBlock text;

		public ConnectionOut(Ftile tmp, double x, HtmlColor arrowColor, double height, TextBlock text) {
			super(tmp, null);
			this.x = x;
			this.arrowColor = arrowColor;
			this.height = height;
			this.text = text;
		}

		public void drawU(UGraphic ug) {
			ug = ug.apply(new UTranslate(x, 0));
			final FtileGeometry geo = getFtile1().calculateDimension(ug.getStringBounder());
			if (geo.hasPointOut() == false) {
				assert false;
				return;
			}
			final Snake s = new Snake(arrowColor, Arrows.asToDown());
			s.setLabel(text);
			s.goUnmergeable();
			s.addPoint(geo.getLeft(), geo.getOutY());
			s.addPoint(geo.getLeft(), height);
			ug.draw(s);
		}
	}

}
