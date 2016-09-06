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
package net.sourceforge.plantuml.ugraphic;

import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.EmptyImageBuilder;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.eps.EpsStrategy;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.png.PngIO;
import net.sourceforge.plantuml.ugraphic.eps.UGraphicEps;
import net.sourceforge.plantuml.ugraphic.g2d.UGraphicG2d;
import net.sourceforge.plantuml.ugraphic.svg.UGraphicSvg;

public abstract class UGraphicUtils {

	public static UDrawable translate(final UDrawable d, final double dx, final double dy) {
		return new UDrawable() {
			public void drawU(UGraphic ug) {
				d.drawU(ug.apply(new UTranslate(dx, dy)));
			}
		};

	}

	public static void writeImage(OutputStream os, String metadata, FileFormatOption fileFormatOption,
			ColorMapper colorMapper, HtmlColor background, TextBlock image) throws IOException {
		final FileFormat fileFormat = fileFormatOption.getFileFormat();
		if (fileFormat == FileFormat.PNG) {
			final BufferedImage im = createImage(colorMapper, background, image);
			PngIO.write(im, os, fileFormatOption.isWithMetadata() ? metadata : null, 96);
		} else if (fileFormat == FileFormat.SVG) {
			final UGraphicSvg svg = new UGraphicSvg(colorMapper, StringUtils.getAsHtml(colorMapper
					.getMappedColor(background)), false, 1.0, fileFormatOption.getSvgLinkTarget());
			image.drawU(svg);
			svg.createXml(os);
		} else if (fileFormat == FileFormat.EPS) {
			final UGraphicEps ug = new UGraphicEps(colorMapper, EpsStrategy.getDefault2());
			image.drawU(ug);
			os.write(ug.getEPSCode().getBytes());
		} else if (fileFormat == FileFormat.EPS_TEXT) {
			final UGraphicEps ug = new UGraphicEps(colorMapper, EpsStrategy.WITH_MACRO_AND_TEXT);
			image.drawU(ug);
			os.write(ug.getEPSCode().getBytes());
		} else {
			throw new UnsupportedOperationException();
		}
	}

	private static BufferedImage createImage(ColorMapper colorMapper, HtmlColor background, TextBlock image) {
		EmptyImageBuilder builder = new EmptyImageBuilder(10, 10, colorMapper.getMappedColor(background));
		Graphics2D g2d = builder.getGraphics2D();

		final UGraphicG2d tmp = new UGraphicG2d(colorMapper, g2d, 1.0);
		final Dimension2D size = image.calculateDimension(tmp.getStringBounder());
		g2d.dispose();

		builder = new EmptyImageBuilder(size.getWidth(), size.getHeight(), colorMapper.getMappedColor(background));
		final BufferedImage im = builder.getBufferedImage();
		g2d = builder.getGraphics2D();

		final UGraphicG2d ug = new UGraphicG2d(colorMapper, g2d, 1.0);
		image.drawU(ug);
		g2d.dispose();
		return im;
	}

	// public static void writeImage(OutputStream os, UGraphic ug, String metadata, int dpi) throws IOException {
	// if (ug instanceof UGraphicG2d) {
	// final BufferedImage im = ((UGraphicG2d) ug).getBufferedImage();
	// PngIO.write(im, os, metadata, dpi);
	// } else if (ug instanceof UGraphicSvg) {
	// final UGraphicSvg svg = (UGraphicSvg) ug;
	// svg.createXml(os);
	// } else if (ug instanceof UGraphicEps) {
	// final UGraphicEps eps = (UGraphicEps) ug;
	// os.write(eps.getEPSCode().getBytes());
	// } else if (ug instanceof UGraphicHtml5) {
	// final UGraphicHtml5 html5 = (UGraphicHtml5) ug;
	// os.write(html5.generateHtmlCode().getBytes());
	// } else {
	// throw new UnsupportedOperationException();
	// }
	// }

}
