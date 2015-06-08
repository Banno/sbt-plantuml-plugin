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
package net.sourceforge.plantuml.salt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.EmptyImageBuilder;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.api.ImageDataSimple;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.DiagramDescriptionImpl;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.salt.element.Element;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.UAntiAliasing;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.g2d.UGraphicG2d;

public class PSystemSalt extends AbstractPSystem {

	private final List<String> data;

	@Deprecated
	public PSystemSalt(List<String> data) {
		this.data = data;
	}

	public PSystemSalt() {
		this(new ArrayList<String>());
	}

	public void add(String s) {
		data.add(s);
	}

	public ImageData exportDiagram(OutputStream os, int num, FileFormatOption fileFormat) throws IOException {
		final Element salt = SaltUtils.createElement(data);

		final Dimension2D size = salt.getPreferredDimension(TextBlockUtils.getDummyStringBounder(), 0, 0);
		final ImageBuilder builder = new ImageBuilder(new ColorMapperIdentity(), 1.0, HtmlColorUtils.WHITE, null,
				null, 5, 5, null, false);
		builder.addUDrawable(new UDrawable() {

			public void drawU(UGraphic ug) {
				ug = ug.apply(new UChangeColor(HtmlColorUtils.BLACK));
				salt.drawU(ug, 0, new Dimension2DDouble(size.getWidth(), size.getHeight()));
				salt.drawU(ug, 1, new Dimension2DDouble(size.getWidth(), size.getHeight()));
			}
		});
		return builder.writeImageTOBEMOVED(fileFormat, os);
	}

	private ImageData exportDiagramOld(OutputStream os, int num, FileFormatOption fileFormat) throws IOException {
		final Element salt = SaltUtils.createElement(data);

		EmptyImageBuilder builder = new EmptyImageBuilder(10, 10, Color.WHITE);
		Graphics2D g2d = builder.getGraphics2D();

		final Dimension2D size = salt.getPreferredDimension(
				new UGraphicG2d(new ColorMapperIdentity(), g2d, 1.0).getStringBounder(), 0, 0);
		g2d.dispose();

		builder = new EmptyImageBuilder(size.getWidth() + 6, size.getHeight() + 6, Color.WHITE);
		final BufferedImage im = builder.getBufferedImage();
		g2d = builder.getGraphics2D();
		g2d.translate(3, 3);
		UAntiAliasing.ANTI_ALIASING_ON.apply(g2d);
		UGraphic ug = new UGraphicG2d(new ColorMapperIdentity(), g2d, 1.0);
		ug = ug.apply(new UChangeColor(HtmlColorUtils.BLACK));
		salt.drawU(ug, 0, new Dimension2DDouble(size.getWidth(), size.getHeight()));
		salt.drawU(ug, 1, new Dimension2DDouble(size.getWidth(), size.getHeight()));
		g2d.dispose();

		// Writes the off-screen image into a PNG file
		ImageIO.write(im, "png", os);
		return new ImageDataSimple(im.getWidth(), im.getHeight());
	}

	public DiagramDescription getDescription() {
		return new DiagramDescriptionImpl("(Salt)", getClass());
	}

}
