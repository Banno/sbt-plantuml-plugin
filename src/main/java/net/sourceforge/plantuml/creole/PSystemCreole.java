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
package net.sourceforge.plantuml.creole;

import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.DiagramDescriptionImpl;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.UFont;

public class PSystemCreole extends AbstractPSystem {

	private final List<String> lines = new ArrayList<String>();

	public PSystemCreole() {
	}

	public DiagramDescription getDescription() {
		return new DiagramDescriptionImpl("(Creole)", getClass());
	}

	public void doCommandLine(String line) {
		lines.add(line);
	}

	public ImageData exportDiagram(OutputStream os, int num, FileFormatOption fileFormat) throws IOException {
		final Display display = Display.create(lines);
		final UFont font = new UFont("Serif", Font.PLAIN, 14);
		final FontConfiguration fontConfiguration = new FontConfiguration(font, HtmlColorUtils.BLACK,
				HtmlColorUtils.BLUE, true);
		final Sheet sheet = new CreoleParser(fontConfiguration, HorizontalAlignment.LEFT, null, false)
				.createSheet(display);
		final SheetBlock1 sheetBlock = new SheetBlock1(sheet, 0, 0);

		final ImageBuilder builder = new ImageBuilder(new ColorMapperIdentity(), 1.0, null, null, null, 0, 0, null, false);
		builder.addUDrawable(sheetBlock);
		return builder.writeImageTOBEMOVED(fileFormat, os);

		// final Dimension2D dim = TextBlockUtils.getDimension(sheetBlock);
		// final UGraphic2 ug = fileFormat.createUGraphic(new ColorMapperIdentity(), 1, dim, null, false);
		// // sheetBlock.drawU(ug.apply(new UTranslate(0, 10)));
		// sheetBlock.drawU(ug);
		// ug.writeImageTOBEMOVED(os, null, 96);
		// return new ImageDataSimple(dim);
	}
}
