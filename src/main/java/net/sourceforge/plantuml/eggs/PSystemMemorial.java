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
package net.sourceforge.plantuml.eggs;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.DiagramDescriptionImpl;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.DateEventUtils;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.UDrawable;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.webp.Portrait;

public class PSystemMemorial extends AbstractPSystem {

	private Portrait portrait;

	PSystemMemorial(Portrait portrait) {
		this.portrait = portrait;
	}

	public ImageData exportDiagram(OutputStream os, int num, FileFormatOption fileFormat) throws IOException {
		final ImageBuilder imageBuilder = new ImageBuilder(new ColorMapperIdentity(), 1.0, HtmlColorUtils.WHITE,
				getMetadata(), null, 0, 0, null, false);
		imageBuilder.setUDrawable(new UDrawable() {

			public void drawU(UGraphic ug) {
				final String name = portrait.getName();
				final String quote = portrait.getQuote();
				final String age = "" + portrait.getAge() + " years old";
				final UFont font = new UFont("SansSerif", Font.BOLD, 14);
				final BufferedImage im = portrait.getBufferedImage();
				final FontConfiguration fc = new FontConfiguration(font, HtmlColorUtils.BLACK, HtmlColorUtils.BLACK,
						true);

				final TextBlock top = DateEventUtils.getComment(
						Arrays.asList("A thought for those who died in Paris the 13th November 2015."),
						HtmlColorUtils.BLACK);

				final TextBlock tb = Display.create(name, age, quote).create(fc, HorizontalAlignment.LEFT,
						new SpriteContainerEmpty());

				top.drawU(ug);
				ug = ug.apply(new UTranslate(0, top.calculateDimension(ug.getStringBounder()).getHeight() + 10));

				ug.draw(new UImage(im));
				ug = ug.apply(new UTranslate(im.getWidth() + 10, 0));
				tb.drawU(ug);
			}
		});
		return imageBuilder.writeImageTOBEMOVED(fileFormat, os);
	}

	public DiagramDescription getDescription() {
		return new DiagramDescriptionImpl("(Memorial)", getClass());
	}

}
