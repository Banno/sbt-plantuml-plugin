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
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineParam;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.Pragma;
import net.sourceforge.plantuml.activitydiagram3.Instruction;
import net.sourceforge.plantuml.activitydiagram3.InstructionList;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorAddNote;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorAddUrl;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorAssembly;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorCreateFork;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorCreateGroup;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorCreateSplit;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorIf;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorRepeat;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileFactoryDelegatorWhile;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.UGraphicInterceptorOneSwimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.VCompactFactory;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.UGraphicDelegator;
import net.sourceforge.plantuml.graphic.color.ColorType;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.svek.UGraphicForSnake;
import net.sourceforge.plantuml.ugraphic.CompressionTransform;
import net.sourceforge.plantuml.ugraphic.LimitFinder;
import net.sourceforge.plantuml.ugraphic.MinMax;
import net.sourceforge.plantuml.ugraphic.SlotFinderX;
import net.sourceforge.plantuml.ugraphic.SlotSet;
import net.sourceforge.plantuml.ugraphic.UChange;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class Swimlanes extends AbstractTextBlock implements TextBlock {

	private final ISkinParam skinParam;;
	private final Pragma pragma;

	private final List<Swimlane> swimlanes = new ArrayList<Swimlane>();
	private Swimlane currentSwimlane = null;

	private final Instruction root = new InstructionList();
	private Instruction currentInstruction = root;

	private LinkRendering nextLinkRenderer = LinkRendering.none();

	public Swimlanes(ISkinParam skinParam, Pragma pragma) {
		this.skinParam = skinParam;
		this.pragma = pragma;
	}

	private FontConfiguration getFontConfiguration() {
		return new FontConfiguration(skinParam, FontParam.SWIMLANE_TITLE, null);
	}

	private FtileFactory getFtileFactory(StringBounder stringBounder) {
		FtileFactory factory = new VCompactFactory(skinParam, stringBounder);
		factory = new FtileFactoryDelegatorAddUrl(factory);
		factory = new FtileFactoryDelegatorAssembly(factory);
		factory = new FtileFactoryDelegatorIf(factory, pragma);
		factory = new FtileFactoryDelegatorWhile(factory);
		factory = new FtileFactoryDelegatorRepeat(factory);
		factory = new FtileFactoryDelegatorCreateFork(factory);
		factory = new FtileFactoryDelegatorCreateSplit(factory);
		factory = new FtileFactoryDelegatorAddNote(factory);
		factory = new FtileFactoryDelegatorCreateGroup(factory);
		return factory;
	}

	public void swimlane(String name, HtmlColor color, Display label) {
		currentSwimlane = getOrCreate(name);
		if (color != null) {
			currentSwimlane.setSpecificColorTOBEREMOVED(ColorType.BACK, color);
		}
		if (Display.isNull(label) == false) {
			currentSwimlane.setDisplay(label);
		}
	}

	private Swimlane getOrCreate(String name) {
		for (Swimlane s : swimlanes) {
			if (s.getName().equals(name)) {
				return s;
			}
		}
		final Swimlane result = new Swimlane(name);
		swimlanes.add(result);
		return result;
	}

	class Cross extends UGraphicDelegator {

		private Cross(UGraphic ug) {
			super(ug);
		}

		@Override
		public void draw(UShape shape) {
			if (shape instanceof Ftile) {
				final Ftile tile = (Ftile) shape;
				tile.drawU(this);
			} else if (shape instanceof Connection) {
				final Connection connection = (Connection) shape;
				final Ftile tile1 = connection.getFtile1();
				final Ftile tile2 = connection.getFtile2();

				if (tile1 == null || tile2 == null) {
					return;
				}
				if (tile1.getSwimlaneOut() != tile2.getSwimlaneIn()) {
					final ConnectionCross connectionCross = new ConnectionCross(connection);
					connectionCross.drawU(getUg());
				}
			}
		}

		public UGraphic apply(UChange change) {
			return new Cross(getUg().apply(change));
		}

	}

	static private final double separationMargin = 10;

	public void drawU(UGraphic ug) {
		final FtileFactory factory = getFtileFactory(ug.getStringBounder());
		TextBlock full = root.createFtile(factory);

		ug = new UGraphicForSnake(ug);
		if (swimlanes.size() <= 1) {
			full = new TextBlockInterceptorUDrawable(full);
			// BUG42
			// full.drawU(ug);
			full.drawU(ug);
			ug.flushUg();
			return;
		}

		if (OptionFlags.SWI2) {

			final SlotFinderX slotFinder = new SlotFinderX(ug.getStringBounder());
			drawWhenSwimlanes(slotFinder, full);
			final SlotSet slotX = slotFinder.getXSlotSet().reverse();
			//
			// // final SlotSet ysSlotSet = slotFinder.getYSlotSet().reverse().smaller(5.0);
			//
			System.err.println("slotX=" + slotX);

			printDebug(ug, slotX, HtmlColorUtils.GRAY, full);

			double x2 = 0;
			double y2 = 0;
			final double stepy = 40;
			int i = 0;
			final SlotSet deconnectedSwimlanes = new SlotSet();
			for (Swimlane swimlane : swimlanes) {
				final UGraphic ug2 = ug.apply(new UChangeColor(HtmlColorUtils.GREEN)).apply(
						new UChangeBackColor(HtmlColorUtils.GREEN));
				final double totalWidth = swimlane.getTotalWidth();
				final SlotSet slot2 = slotX.filter(x2 + separationMargin, x2 + totalWidth - separationMargin);
				deconnectedSwimlanes.addAll(slot2);
				// ug2.apply(new UTranslate(x2, y2)).draw(new URectangle(totalWidth, stepy));
				x2 += totalWidth;
				y2 += stepy;
				i++;
			}
			// printDebug(ug, deconnectedSwimlanes, HtmlColorUtils.GRAY, full);

			//
			final CompressionTransform compressionTransform = new CompressionTransform(deconnectedSwimlanes);
			// ug = new UGraphicCompress2(ug, compressionTransform);
			drawWhenSwimlanes(ug, full);
		} else {
			drawWhenSwimlanes(ug, full);
		}
		// getCollisionDetector(ug, titleHeightTranslate).drawDebug(ug);
	}

	static private void printDebug(UGraphic ug, SlotSet slot, HtmlColor col, TextBlock full) {
		slot.drawDebugX(ug.apply(new UChangeColor(col)).apply(new UChangeBackColor(col)),
				full.calculateDimension(ug.getStringBounder()).getHeight());

	}

	private void drawWhenSwimlanes(UGraphic ug, TextBlock full) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimensionFull = full.calculateDimension(stringBounder);

		final UTranslate titleHeightTranslate = getTitleHeightTranslate(stringBounder);

		computeSize(ug, full);

		double x2 = 0;
		for (Swimlane swimlane : swimlanes) {
			final HtmlColor back = swimlane.getColors(skinParam).getColor(ColorType.BACK);
			if (back != null) {
				final UGraphic background = ug.apply(new UChangeBackColor(back)).apply(new UChangeColor(back))
						.apply(new UTranslate(x2, 0));
				background.draw(new URectangle(swimlane.getTotalWidth(), dimensionFull.getHeight()
						+ titleHeightTranslate.getDy()));
			}

			if (OptionFlags.SWI2 == false) {
				final TextBlock swTitle = swimlane.getDisplay().create(getFontConfiguration(),
						HorizontalAlignment.LEFT, skinParam);
				final double titleWidth = swTitle.calculateDimension(stringBounder).getWidth();
				final double posTitle = x2 + (swimlane.getTotalWidth() - titleWidth) / 2;
				swTitle.drawU(ug.apply(new UTranslate(posTitle, 0)));
			}

			drawSeparation(ug.apply(new UTranslate(x2, 0)), dimensionFull.getHeight() + titleHeightTranslate.getDy());

			full.drawU(new UGraphicInterceptorOneSwimlane(ug, swimlane).apply(swimlane.getTranslate()).apply(
					titleHeightTranslate));
			x2 += swimlane.getTotalWidth();

		}
		drawSeparation(ug.apply(new UTranslate(x2, 0)), dimensionFull.getHeight() + titleHeightTranslate.getDy());
		final Cross cross = new Cross(ug.apply(titleHeightTranslate));
		full.drawU(cross);
		cross.flushUg();
	}

	private void computeSize(UGraphic ug, TextBlock full) {
		double x1 = 0;
		final StringBounder stringBounder = ug.getStringBounder();
		for (Swimlane swimlane : swimlanes) {

			final LimitFinder limitFinder = new LimitFinder(stringBounder, false);
			final UGraphicInterceptorOneSwimlane interceptor = new UGraphicInterceptorOneSwimlane(new UGraphicForSnake(
					limitFinder), swimlane);
			full.drawU(interceptor);
			interceptor.flushUg();
			final MinMax minMax = limitFinder.getMinMax();

			final double drawingWidth = minMax.getWidth() + 2 * separationMargin;
			final TextBlock swTitle = swimlane.getDisplay().create(getFontConfiguration(), HorizontalAlignment.LEFT,
					skinParam);
			final double titleWidth = swTitle.calculateDimension(stringBounder).getWidth();
			final double totalWidth = Math.max(drawingWidth, titleWidth + 2 * separationMargin);

			final UTranslate translate = new UTranslate(x1 - minMax.getMinX() + separationMargin
					+ (totalWidth - drawingWidth) / 2.0, 0);
			swimlane.setTranslateAndWidth(translate, totalWidth);

			x1 += totalWidth;
		}
	}

	private UTranslate getTitleHeightTranslate(final StringBounder stringBounder) {
		double titlesHeight = 0;
		for (Swimlane swimlane : swimlanes) {
			final TextBlock swTitle = swimlane.getDisplay().create(getFontConfiguration(), HorizontalAlignment.LEFT,
					skinParam);

			titlesHeight = Math.max(titlesHeight, swTitle.calculateDimension(stringBounder).getHeight());
		}
		final UTranslate titleHeightTranslate = new UTranslate(0, titlesHeight);
		return titleHeightTranslate;
	}

	private CollisionDetector getCollisionDetector(UGraphic ug, final UTranslate titleHeightTranslate) {
		final FtileFactory factory = getFtileFactory(ug.getStringBounder());
		final TextBlock full = root.createFtile(factory);
		ug = new UGraphicForSnake(ug);

		final CollisionDetector collisionDetector = new CollisionDetector(ug.getStringBounder());

		for (Swimlane swimlane : swimlanes) {
			full.drawU(new UGraphicInterceptorOneSwimlane(collisionDetector, swimlane).apply(swimlane.getTranslate())
					.apply(titleHeightTranslate));
		}

		collisionDetector.setManageSnakes(true);
		final Cross cross = new Cross(collisionDetector.apply(titleHeightTranslate));
		full.drawU(cross);
		cross.flushUg();

		return collisionDetector;
	}

	private void drawSeparation(UGraphic ug, double height) {
		HtmlColor color = skinParam.getHtmlColor(ColorParam.swimlaneBorder, null, false);
		if (color == null) {
			color = ColorParam.swimlaneBorder.getDefaultValue();
		}
		final UStroke thickness = Rose.getStroke(skinParam, LineParam.swimlaneBorder, 2);
		ug.apply(thickness).apply(new UChangeColor(color)).draw(new ULine(0, height));
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return TextBlockUtils.getMinMax(this, stringBounder).getDimension();
	}

	public Instruction getCurrent() {
		return currentInstruction;
	}

	public void setCurrent(Instruction current) {
		this.currentInstruction = current;
	}

	public LinkRendering nextLinkRenderer() {
		return nextLinkRenderer;
	}

	public void setNextLinkRenderer(LinkRendering link) {
		if (link == null) {
			throw new IllegalArgumentException();
		}
		this.nextLinkRenderer = link;
	}

	public Swimlane getCurrentSwimlane() {
		return currentSwimlane;
	}

}
