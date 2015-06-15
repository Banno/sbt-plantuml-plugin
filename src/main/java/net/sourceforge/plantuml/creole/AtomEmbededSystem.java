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

import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.EmbededDiagram;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UShape;

class AtomEmbededSystem implements Atom {

	final private List<? extends CharSequence> lines;

	public AtomEmbededSystem(EmbededDiagram sys) {
		this.lines = sys.getLines().as();
	}

	public double getStartingAltitude(StringBounder stringBounder) {
		return 0;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		try {
			final BufferedImage im = getImage();
			return new Dimension2DDouble(im.getWidth(), im.getHeight());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new Dimension2DDouble(42, 42);
	}

	public void drawU(UGraphic ug) {
		try {
			final BufferedImage im = getImage();
			final UShape image = new UImage(im);
			ug.draw(image);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private BufferedImage getImage() throws IOException, InterruptedException {
		final Diagram system = getSystem();
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		system.exportDiagram(os, 0, new FileFormatOption(FileFormat.PNG));
		os.close();
		final ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
		final BufferedImage im = ImageIO.read(is);
		is.close();
		return im;
	}

	// public HorizontalAlignment getHorizontalAlignment() {
	// return HorizontalAlignment.LEFT;
	// }
	//
	private Diagram getSystem() throws IOException, InterruptedException {
		final BlockUml blockUml = new BlockUml(lines, 0);
		return blockUml.getDiagram();

	}
}
