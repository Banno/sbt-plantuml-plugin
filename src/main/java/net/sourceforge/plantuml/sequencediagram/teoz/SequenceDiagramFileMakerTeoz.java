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
package net.sourceforge.plantuml.sequencediagram.teoz;

import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.EntityImageLegend;
import net.sourceforge.plantuml.api.ImageDataSimple;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.png.PngTitler;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.real.RealOrigin;
import net.sourceforge.plantuml.real.RealUtils;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;
import net.sourceforge.plantuml.sequencediagram.graphic.FileMaker;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.SimpleContext2D;
import net.sourceforge.plantuml.skin.Skin;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphic2;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.hand.UGraphicHandwritten;
import net.sourceforge.plantuml.utils.MathUtils;

public class SequenceDiagramFileMakerTeoz implements FileMaker {

	private final SequenceDiagram diagram;
	private final FileFormatOption fileFormatOption;
	private final Skin skin;

	public SequenceDiagramFileMakerTeoz(SequenceDiagram sequenceDiagram, Skin skin, FileFormatOption fileFormatOption) {
		this.diagram = sequenceDiagram;
		this.fileFormatOption = fileFormatOption;
		this.skin = skin;
		this.footer = getFooterOrHeader(FontParam.FOOTER);
		this.header = getFooterOrHeader(FontParam.HEADER);

		this.main = new MainTileAdapter(createMainTile());
		this.min1 = ((MainTileAdapter) main).getMinX(stringBounder);

		this.title = getTitle();
		this.legend = getLegend();

		this.heightEnglober1 = englobers.getOffsetForEnglobers(stringBounder);
		this.heightEnglober2 = heightEnglober1 == 0 ? 0 : 10;

		final double totalWidth = MathUtils.max(main.calculateDimension(stringBounder).getWidth(), title
				.calculateDimension(stringBounder).getWidth(), footer.calculateDimension(stringBounder).getWidth(),
				header.calculateDimension(stringBounder).getWidth(), legend.calculateDimension(stringBounder)
						.getWidth());
		final double totalHeight = main.calculateDimension(stringBounder).getHeight() + heightEnglober1
				+ heightEnglober2 + title.calculateDimension(stringBounder).getHeight()
				+ header.calculateDimension(stringBounder).getHeight()
				+ legend.calculateDimension(stringBounder).getHeight()
				+ footer.calculateDimension(stringBounder).getHeight();
		this.dimTotal = new Dimension2DDouble(totalWidth, totalHeight);

	}

	private Englobers englobers;
	private final StringBounder stringBounder = TextBlockUtils.getDummyStringBounder();

	private final TextBlock footer;
	private final TextBlock header;

	private final TextBlock main;

	private final TextBlock title;
	private final TextBlock legend;
	private final Dimension2D dimTotal;
	private final Real min1;

	private final LivingSpaces livingSpaces = new LivingSpaces();
	private final double heightEnglober1;
	private final double heightEnglober2;

	public ImageData createOne(OutputStream os, int index, boolean isWithMetadata) throws IOException {
		final UTranslate min1translate = new UTranslate(-min1.getCurrentValue(), 0);
		final UGraphic2 ug2 = (UGraphic2) fileFormatOption.createUGraphic(getSkinParam().getColorMapper(),
				diagram.getDpiFactor(fileFormatOption), dimTotal, getSkinParam().getBackgroundColor(), false).apply(
				min1translate);

		UGraphic ug = getSkinParam().handwritten() ? new UGraphicHandwritten(ug2) : ug2;
		englobers.drawEnglobers(goDownForEnglobers(ug), main.calculateDimension(stringBounder).getHeight()
				+ this.heightEnglober1 + this.heightEnglober2 / 2, new SimpleContext2D(true));

		printAligned(ug, diagram.getAlignmentTeoz(FontParam.HEADER), header);
		ug = goDown(ug, header);

		printAligned(ug, HorizontalAlignment.CENTER, title);
		ug = goDown(ug, title);

		if (diagram.getLegendVerticalAlignment() == VerticalAlignment.TOP) {
			printAligned(ug, diagram.getLegendAlignment(), legend);
			ug = goDown(ug, legend);
		}

		ug = ug.apply(new UTranslate(0, this.heightEnglober1));
		printAligned(ug, HorizontalAlignment.CENTER, main);
		ug = goDown(ug, main);
		ug = ug.apply(new UTranslate(0, this.heightEnglober2));

		if (diagram.getLegendVerticalAlignment() == VerticalAlignment.BOTTOM) {
			printAligned(ug, diagram.getLegendAlignment(), legend);
			ug = goDown(ug, legend);
		}

		printAligned(ug, diagram.getAlignmentTeoz(FontParam.FOOTER), footer);

		ug2.writeImageTOBEMOVED(os, isWithMetadata ? diagram.getMetadata() : null, diagram.getDpi(fileFormatOption));

		return new ImageDataSimple(dimTotal);
	}

	private UGraphic goDownForEnglobers(UGraphic ug) {
		ug = goDown(ug, title);
		ug = goDown(ug, header);
		if (diagram.getLegendVerticalAlignment() == VerticalAlignment.TOP) {
			ug = goDown(ug, legend);
		}
		return ug;
	}

	private UGraphic goDown(UGraphic ug, TextBlock size) {
		return ug.apply(new UTranslate(0, size.calculateDimension(stringBounder).getHeight()));
	}

	public void printAligned(UGraphic ug, HorizontalAlignment align, final TextBlock layer) {
		double dx = 0;
		if (align == HorizontalAlignment.RIGHT) {
			dx = dimTotal.getWidth() - layer.calculateDimension(stringBounder).getWidth();
		} else if (align == HorizontalAlignment.CENTER) {
			dx = (dimTotal.getWidth() - layer.calculateDimension(stringBounder).getWidth()) / 2;
		}
		layer.drawU(ug.apply(new UTranslate(dx, 0)));
	}

	private MainTile createMainTile() {
		final RealOrigin origin = RealUtils.createOrigin();
		Real currentPos = origin.addAtLeast(0);
		for (Participant p : diagram.participants().values()) {
			final LivingSpace livingSpace = new LivingSpace(p, diagram.getEnglober(p), skin, getSkinParam(),
					currentPos, diagram.events());
			livingSpaces.put(p, livingSpace);
			currentPos = livingSpace.getPosD(stringBounder).addAtLeast(0);
		}

		final TileArguments tileArguments = new TileArguments(stringBounder, currentPos, livingSpaces, skin,
				diagram.getSkinParam(), origin);

		this.englobers = new Englobers(tileArguments);
		final MainTile mainTile = new MainTile(diagram, tileArguments);
		mainTile.addConstraints(stringBounder);
		this.englobers.addConstraints(stringBounder);
		origin.compileNow();
		return mainTile;
	}

	public ISkinParam getSkinParam() {
		return diagram.getSkinParam();
	}

	private TextBlock getTitle() {
		final Display title = diagram.getTitle();
		if (title == null) {
			return new ComponentAdapter(null);
		}
		final Component compTitle = skin.createComponent(ComponentType.TITLE, null, getSkinParam(), title);
		return new ComponentAdapter(compTitle);
	}

	private TextBlock getLegend() {
		final Display legend = diagram.getLegend();
		if (legend == null) {
			return TextBlockUtils.empty(0, 0);
		}
		return EntityImageLegend.create(legend, diagram.getSkinParam());
	}

	public TextBlock getFooterOrHeader(final FontParam param) {
		final Display display = diagram.getFooterOrHeaderTeoz(param);
		if (display == null) {
			return new TeozLayer(null, stringBounder, param);
		}
		final HtmlColor hyperlinkColor = getSkinParam().getHyperlinkColor();
		final HtmlColor titleColor = getSkinParam().getFontHtmlColor(param, null);
		final String fontFamily = getSkinParam().getFont(param, null, false).getFamily(null);
		final int fontSize = getSkinParam().getFont(param, null, false).getSize();
		final PngTitler pngTitler = new PngTitler(titleColor, display, fontSize, fontFamily,
				diagram.getAlignmentTeoz(param), hyperlinkColor, getSkinParam().useUnderlineForHyperlink());
		return new TeozLayer(pngTitler, stringBounder, param);
	}

	public int getNbPages() {
		return 1;
	}

}
