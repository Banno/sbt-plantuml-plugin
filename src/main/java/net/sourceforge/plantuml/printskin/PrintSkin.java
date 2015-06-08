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
package net.sourceforge.plantuml.printskin;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.EmptyImageBuilder;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.api.ImageDataSimple;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.DiagramDescriptionImpl;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.png.PngIO;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.SimpleContext2D;
import net.sourceforge.plantuml.skin.Skin;
import net.sourceforge.plantuml.skin.SkinUtils;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.ugraphic.g2d.UGraphicG2d;

class PrintSkin extends AbstractPSystem {

	private static final UFont FONT1 = new UFont("SansSerif", Font.PLAIN, 10);

	final private Skin skin;
	final private List<String> toPrint;
	private UGraphic ug;
	private float xpos = 10;
	private float ypos = 0;
	private float maxYpos = 0;

	// public List<File> createFiles(File suggestedFile, FileFormatOption fileFormat) throws IOException,
	// InterruptedException {
	// final List<File> result = Arrays.asList(suggestedFile);
	// final BufferedImage im = createImage();
	//
	// PngIO.write(im.getSubimage(0, 0, im.getWidth(), (int) maxYpos), suggestedFile, 96);
	// return result;
	//
	// }

	public ImageData exportDiagram(OutputStream os, int num, FileFormatOption fileFormatOption) throws IOException {
		final BufferedImage im = createImage();
		final ImageData imageData = new ImageDataSimple(im.getWidth(), (int) maxYpos);
		PngIO.write(im.getSubimage(0, 0, imageData.getWidth(), imageData.getHeight()), os, 96);
		return imageData;
	}

	private BufferedImage createImage() {
		final EmptyImageBuilder builder = new EmptyImageBuilder(2000, 830, Color.WHITE);

		final BufferedImage im = builder.getBufferedImage();
		final Graphics2D g2d = builder.getGraphics2D();

		ug = new UGraphicG2d(new ColorMapperIdentity(), g2d, 1.0);

		for (ComponentType type : ComponentType.values()) {
			printComponent(type);
			ypos += 10;
			maxYpos = Math.max(maxYpos, ypos);
			if (ypos > 620) {
				ypos = 0;
				xpos += 200;
			}
		}
		g2d.dispose();
		return im;
	}

	private void printComponent(ComponentType type) {
		println(type.name());
		final Component comp = skin.createComponent(type, ArrowConfiguration.withDirectionNormal(),
				SkinParam.noShadowing(), Display.create(toPrint));
		if (comp == null) {
			println("null");
			return;
		}
		double height = comp.getPreferredHeight(ug.getStringBounder());
		double width = comp.getPreferredWidth(ug.getStringBounder());
		println("height = " + String.format("%4.2f", height));
		println("width = " + width);

		if (height == 0) {
			height = 42;
		}
		if (width == 0) {
			width = 42;
		}
		ug.apply(new UChangeBackColor(HtmlColorUtils.LIGHT_GRAY)).apply(new UChangeColor(HtmlColorUtils.LIGHT_GRAY))
				.apply(new UTranslate((double) (xpos - 1), (double) (ypos - 1)))
				.draw(new URectangle(width + 2, height + 2));

		comp.drawU(ug.apply(new UTranslate(xpos, ypos)), new Area(new Dimension2DDouble(width, height)),
				new SimpleContext2D(false));

		ypos += height;
	}

	private void println(String s) {
		final TextBlock textBlock = TextBlockUtils.create(Display.create(s), new FontConfiguration(FONT1,
				HtmlColorUtils.BLACK, HtmlColorUtils.BLUE, true), HorizontalAlignment.LEFT, new SpriteContainerEmpty());
		textBlock.drawU(ug.apply(new UTranslate(xpos, ypos)));
		ypos += textBlock.calculateDimension(ug.getStringBounder()).getHeight();
	}

	public DiagramDescription getDescription() {
		return new DiagramDescriptionImpl("Printing of " + skin.getClass().getName(), getClass());
	}

	public PrintSkin(String className, List<String> toPrint) {
		this.skin = SkinUtils.loadSkin(className);
		this.toPrint = toPrint;
	}
}
