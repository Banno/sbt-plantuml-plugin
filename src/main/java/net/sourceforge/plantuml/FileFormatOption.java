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
package net.sourceforge.plantuml;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;

import net.sourceforge.plantuml.eps.EpsStrategy;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorGradient;
import net.sourceforge.plantuml.graphic.HtmlColorSimple;
import net.sourceforge.plantuml.graphic.HtmlColorTransparent;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UGraphic2;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.eps.UGraphicEps;
import net.sourceforge.plantuml.ugraphic.g2d.UGraphicG2d;
import net.sourceforge.plantuml.ugraphic.html5.UGraphicHtml5;
import net.sourceforge.plantuml.ugraphic.svg.UGraphicSvg;
import net.sourceforge.plantuml.ugraphic.tikz.UGraphicTikz;
import net.sourceforge.plantuml.ugraphic.visio.UGraphicVdx;
import net.sourceforge.plantuml.StringUtils;

/**
 * A FileFormat with some parameters.
 * 
 * 
 * @author Arnaud Roques
 * 
 */
public class FileFormatOption {

	private final FileFormat fileFormat;
	private final AffineTransform affineTransform;
	private final boolean withMetadata;
	private final boolean useRedForError;
	private final String svgLinkTarget;

	public FileFormatOption(FileFormat fileFormat) {
		this(fileFormat, null, true, false, "_top");
	}
	
	public String getSvgLinkTarget() {
		return svgLinkTarget;
	}

	public final boolean isWithMetadata() {
		return withMetadata;
	}

	public FileFormatOption(FileFormat fileFormat, boolean withMetadata) {
		this(fileFormat, null, false, false, "_top");
	}

	private FileFormatOption(FileFormat fileFormat, AffineTransform at, boolean withMetadata, boolean useRedForError, String svgLinkTarget) {
		this.fileFormat = fileFormat;
		this.affineTransform = at;
		this.withMetadata = withMetadata;
		this.useRedForError = useRedForError;
		this.svgLinkTarget = svgLinkTarget;
	}

	public FileFormatOption withUseRedForError() {
		return new FileFormatOption(fileFormat, affineTransform, withMetadata, true, svgLinkTarget);
	}

	public FileFormatOption withSvgLinkTarget(String target) {
		return new FileFormatOption(fileFormat, affineTransform, withMetadata, useRedForError, target);
	}

	@Override
	public String toString() {
		return fileFormat.toString() + " " + affineTransform;
	}

	public final FileFormat getFileFormat() {
		return fileFormat;
	}

	public AffineTransform getAffineTransform() {
		return affineTransform;
	}

	/**
	 * Create a UGraphic corresponding to this FileFormatOption
	 * 
	 * @param colorMapper
	 * @param dpiFactor
	 *            1.0 for a standard dot per inch
	 * @param dim
	 * @param mybackcolor
	 * @param rotation
	 * @return
	 */
	public UGraphic2 createUGraphic(ColorMapper colorMapper, double dpiFactor, final Dimension2D dim,
			HtmlColor mybackcolor, boolean rotation) {
		switch (fileFormat) {
		case PNG:
			return createUGraphicPNG(colorMapper, dpiFactor, dim, mybackcolor, rotation);
		case SVG:
			return createUGraphicSVG(colorMapper, dpiFactor, dim, mybackcolor, rotation);
		case EPS:
			return new UGraphicEps(colorMapper, EpsStrategy.getDefault2());
		case EPS_TEXT:
			return new UGraphicEps(colorMapper, EpsStrategy.WITH_MACRO_AND_TEXT);
		case HTML5:
			return new UGraphicHtml5(colorMapper);
		case VDX:
			return new UGraphicVdx(colorMapper);
		case LATEX:
			return new UGraphicTikz(colorMapper);
		default:
			throw new UnsupportedOperationException(fileFormat.toString());
		}
	}

	public UGraphic2 createUGraphic(final Dimension2D dim) {
		return createUGraphic(new ColorMapperIdentity(), 1.0, dim, null, false);
	}

	private UGraphic2 createUGraphicSVG(ColorMapper colorMapper, double scale, Dimension2D dim, HtmlColor mybackcolor,
			boolean rotation) {
		Color backColor = Color.WHITE;
		if (mybackcolor instanceof HtmlColorSimple) {
			backColor = colorMapper.getMappedColor(mybackcolor);
		}
		final UGraphicSvg ug;
		if (mybackcolor instanceof HtmlColorGradient) {
			ug = new UGraphicSvg(colorMapper, (HtmlColorGradient) mybackcolor, false, scale, getSvgLinkTarget());
		} else if (backColor == null || backColor.equals(Color.WHITE)) {
			ug = new UGraphicSvg(colorMapper, false, scale, getSvgLinkTarget());
		} else {
			ug = new UGraphicSvg(colorMapper, StringUtils.getAsHtml(backColor), false, scale, getSvgLinkTarget());
		}
		return ug;

	}

	private UGraphic2 createUGraphicPNG(ColorMapper colorMapper, double dpiFactor, final Dimension2D dim,
			HtmlColor mybackcolor, boolean rotation) {
		Color backColor = Color.WHITE;
		if (mybackcolor instanceof HtmlColorSimple) {
			backColor = colorMapper.getMappedColor(mybackcolor);
		} else if (mybackcolor instanceof HtmlColorTransparent) {
			backColor = null;
		}

		final EmptyImageBuilder builder;
		final Graphics2D graphics2D;
		if (rotation) {
			builder = new EmptyImageBuilder((int) (dim.getHeight() * dpiFactor), (int) (dim.getWidth() * dpiFactor),
					backColor);
			graphics2D = builder.getGraphics2D();
			graphics2D.rotate(-Math.PI / 2);
			graphics2D.translate(-builder.getBufferedImage().getHeight(), 0);
		} else {
			builder = new EmptyImageBuilder((int) (dim.getWidth() * dpiFactor), (int) (dim.getHeight() * dpiFactor),
					backColor);
			graphics2D = builder.getGraphics2D();

		}
		final UGraphicG2d ug = new UGraphicG2d(colorMapper, graphics2D, dpiFactor);
		ug.setBufferedImage(builder.getBufferedImage());
		final BufferedImage im = ((UGraphicG2d) ug).getBufferedImage();
		if (mybackcolor instanceof HtmlColorGradient) {
			ug.apply(new UChangeBackColor(mybackcolor)).draw(new URectangle(im.getWidth(), im.getHeight()));
		}

		return ug;
	}

	public final boolean isUseRedForError() {
		return useRedForError;
	}

}
