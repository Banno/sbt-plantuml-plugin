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
package net.sourceforge.plantuml.sequencediagram.graphic;

import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.plantuml.AnnotatedWorker;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.EntityImageLegend;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.DisplayPositionned;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.png.PngTitler;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.sequencediagram.Newpage;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.SimpleContext2D;
import net.sourceforge.plantuml.skin.Skin;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class SequenceDiagramFileMakerPuma2 implements FileMaker {

	private final SequenceDiagram diagram;
	private final DrawableSet drawableSet;
	private final Dimension2D fullDimension;
	private final List<Page> pages;
	private final FileFormatOption fileFormatOption;
	private final StringBounder stringBounder;

	private double scale;

	public SequenceDiagramFileMakerPuma2(SequenceDiagram sequenceDiagram, Skin skin, FileFormatOption fileFormatOption) {
		this.diagram = sequenceDiagram;
		this.stringBounder = fileFormatOption.getDefaultStringBounder();
		this.fileFormatOption = fileFormatOption;
		final DrawableSetInitializer initializer = new DrawableSetInitializer(skin, sequenceDiagram.getSkinParam(),
				sequenceDiagram.isShowFootbox(), sequenceDiagram.getAutonewpage());

		for (Participant p : sequenceDiagram.participants().values()) {
			initializer.addParticipant(p, sequenceDiagram.getEnglober(p));
		}

		for (Event ev : sequenceDiagram.events()) {
			initializer.addEvent(ev);
			// if (ev instanceof Message) {
			// // TODO mieux faire
			// final Message m = (Message) ev;
			// for (LifeEvent lifeEvent : m.getLiveEvents()) {
			// if (lifeEvent.getType() == LifeEventType.DESTROY
			// /*
			// * || lifeEvent.getType() == LifeEventType.CREATE
			// */) {
			// initializer.addEvent(lifeEvent);
			// }
			// }
			// }
		}
		drawableSet = initializer.createDrawableSet(stringBounder);
		final List<Newpage> newpages = new ArrayList<Newpage>();
		for (Event ev : drawableSet.getAllEvents()) {
			if (ev instanceof Newpage) {
				newpages.add((Newpage) ev);
			}
		}
		fullDimension = drawableSet.getDimension();
		final Map<Newpage, Double> positions = new LinkedHashMap<Newpage, Double>();
		for (Newpage n : newpages) {
			positions.put(n, initializer.getYposition(stringBounder, n));
		}
		pages = create(drawableSet, positions, sequenceDiagram.isShowFootbox(), sequenceDiagram.getTitle().getDisplay())
				.getPages();
	}

	public int getNbPages() {
		return pages.size();
	}

	private PageSplitter create(DrawableSet drawableSet, Map<Newpage, Double> positions, boolean showFootbox,
			Display title) {

		final double headerHeight = drawableSet.getHeadHeight(stringBounder);
		final double tailHeight = drawableSet.getTailHeight(stringBounder, showFootbox);
		final double signatureHeight = 0;
		final double newpageHeight = drawableSet.getSkin()
				.createComponent(ComponentType.NEWPAGE, null, drawableSet.getSkinParam(), Display.create(""))
				.getPreferredHeight(stringBounder);

		return new PageSplitter(fullDimension.getHeight(), headerHeight, positions, tailHeight, signatureHeight,
				newpageHeight, title);
	}

	public ImageData createOne(OutputStream os, final int index, boolean isWithMetadata) throws IOException {

		final Page page = pages.get(index);
		final SequenceDiagramArea area = new SequenceDiagramArea(fullDimension.getWidth(), page.getHeight());

		final Component compTitle;
		final TextBlock caption = new AnnotatedWorker(diagram, diagram.getSkinParam()).getCaption();
		area.setCaptionArea(caption.calculateDimension(stringBounder));

		if (Display.isNull(page.getTitle())) {
			compTitle = null;
		} else {
			compTitle = drawableSet.getSkin().createComponent(ComponentType.TITLE, null, drawableSet.getSkinParam(),
					page.getTitle());
			area.setTitleArea(compTitle.getPreferredWidth(stringBounder), compTitle.getPreferredHeight(stringBounder));
		}
		area.initFooter(getPngTitler(FontParam.FOOTER), stringBounder);
		area.initHeader(getPngTitler(FontParam.HEADER), stringBounder);

		final DisplayPositionned legend = diagram.getLegend();
		final TextBlock legendBlock;
		if (DisplayPositionned.isNull(legend)) {
			legendBlock = TextBlockUtils.empty(0, 0);
		} else {
			legendBlock = EntityImageLegend.create(legend.getDisplay(), diagram.getSkinParam());
		}
		final Dimension2D dimLegend = legendBlock.calculateDimension(stringBounder);

		scale = getScale(area.getWidth(), area.getHeight());

		final double dpiFactor = diagram.getDpiFactor(fileFormatOption);
		// System.err.println("dpiFactor=" + dpiFactor);
		// System.err.println("scale=" + scale);

		final String metadata = fileFormatOption.isWithMetadata() ? diagram.getMetadata() : null;

		final ImageBuilder imageBuilder = new ImageBuilder(diagram.getSkinParam().getColorMapper(), oneOf(scale,
				dpiFactor), diagram.getSkinParam().getBackgroundColor(), metadata, null, 3, 10, diagram.getAnimation(),
				diagram.getSkinParam().handwritten());

		imageBuilder.setUDrawable(new UDrawable() {
			public void drawU(UGraphic ug) {

				double delta = 0;
				if (index > 0) {
					delta = page.getNewpage1() - page.getHeaderHeight();
				}
				if (delta < 0) {
					delta = 0;
				}

				double legendYdelta = 0;
				if (compTitle != null) {
					final StringBounder stringBounder = ug.getStringBounder();
					final double h = compTitle.getPreferredHeight(stringBounder);
					legendYdelta += h;
					final double w = compTitle.getPreferredWidth(stringBounder);
					compTitle.drawU(ug.apply(new UTranslate(area.getTitleX(), area.getTitleY())), new Area(
							new Dimension2DDouble(w, h)), new SimpleContext2D(false));
				}
				caption.drawU(ug.apply(new UTranslate(area.getCaptionX(), area.getCaptionY())));

				final double delta1 = Math.max(0, dimLegend.getWidth() - area.getWidth());

				final boolean legendTop = DisplayPositionned.isNull(legend) == false
						&& legend.getVerticalAlignment() == VerticalAlignment.TOP;

				double sequenceAreaY = area.getSequenceAreaY();
				if (legendTop) {
					sequenceAreaY += legendBlock.calculateDimension(ug.getStringBounder()).getHeight();
				}
				drawableSet.drawU22(ug.apply(new UTranslate(area.getSequenceAreaX() + delta1 / 2, sequenceAreaY)),
						delta, fullDimension.getWidth(), page, diagram.isShowFootbox());

				drawHeader(area, ug);
				drawFooter(area, ug);

				if (DisplayPositionned.isNull(legend) == false) {
					final double delta2;
					if (legend.getHorizontalAlignment() == HorizontalAlignment.LEFT) {
						delta2 = 0;
					} else if (legend.getHorizontalAlignment() == HorizontalAlignment.RIGHT) {
						delta2 = Math.max(0, area.getWidth() - dimLegend.getWidth());
					} else {
						delta2 = Math.max(0, area.getWidth() - dimLegend.getWidth()) / 2;
					}
					legendBlock.drawU(ug.apply(new UTranslate(delta2, legendTop ? legendYdelta : legendYdelta
							+ area.getHeight())));
				}

			}
		});
		return imageBuilder.writeImageTOBEMOVED(fileFormatOption, os);
	}

	private void drawFooter(SequenceDiagramArea area, UGraphic ug) {
		final PngTitler pngTitler = getPngTitler(FontParam.FOOTER);
		final TextBlock text = pngTitler.getTextBlock();
		if (text == null) {
			return;
		}
		text.drawU(ug.apply(new UTranslate(area.getFooterX(diagram.getFooter().getHorizontalAlignment()), area
				.getFooterY())));
	}

	private void drawHeader(SequenceDiagramArea area, UGraphic ug) {
		final PngTitler pngTitler = getPngTitler(FontParam.HEADER);
		final TextBlock text = pngTitler.getTextBlock();
		if (text == null) {
			return;
		}
		text.drawU(ug.apply(new UTranslate(area.getHeaderX(diagram.getHeader().getHorizontalAlignment()), area
				.getHeaderY())));
	}

	private double oneOf(double a, double b) {
		if (a == 1) {
			return b;
		}
		return a;
	}

	private double getImageWidth(SequenceDiagramArea area, double dpiFactor, double legendWidth) {
		final int minsize = diagram.getMinwidth();
		final double w = Math.max(area.getWidth() * getScale(area.getWidth(), area.getHeight()) * dpiFactor,
				legendWidth);
		if (minsize == Integer.MAX_VALUE) {
			return w;
		}
		if (w >= minsize) {
			return w;
		}
		return minsize;
	}

	private double getScale(double width, double height) {
		if (diagram.getScale() == null) {
			return 1;
		}
		return diagram.getScale().getScale(width, height);
	}

	private PngTitler getPngTitler(final FontParam fontParam) {
		final HtmlColor hyperlinkColor = diagram.getSkinParam().getHyperlinkColor();
		final HtmlColor titleColor = diagram.getSkinParam().getFontHtmlColor(null, fontParam);
		final String fontFamily = diagram.getSkinParam().getFont(null, false, fontParam).getFamily(null);
		final int fontSize = diagram.getSkinParam().getFont(null, false, fontParam).getSize();
		return new PngTitler(titleColor, diagram.getFooterOrHeaderTeoz(fontParam).getDisplay(), fontSize, fontFamily,
				diagram.getFooterOrHeaderTeoz(fontParam).getHorizontalAlignment(), hyperlinkColor, diagram
						.getSkinParam().useUnderlineForHyperlink());
	}

}
