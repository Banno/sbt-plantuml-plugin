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
package net.sourceforge.plantuml.png;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileUtils;
import net.sourceforge.plantuml.Log;

public class PngSplitter {

	private final List<File> files = new ArrayList<File>();

	public static void main(String[] args) throws IOException {
		final File f = new File(args[0]);
		final int x = Integer.parseInt(args[1]);
		final int y = Integer.parseInt(args[2]);
		final File cp = new File(f.getParent(), f.getName().replaceAll("\\.png$", "_000.png"));
		FileUtils.copyToFile(f, cp);
		new PngSplitter(cp, x, y, "", 96, false);

	}

	public PngSplitter(File pngFile, int horizontalPages, int verticalPages, String source, int dpi,
			boolean isWithMetadata) throws IOException {
		if (horizontalPages == 1 && verticalPages == 1) {
			this.files.add(pngFile);
			return;
		}

		Log.info("Splitting " + horizontalPages + " x " + verticalPages);
		final File full = new File(pngFile.getParentFile(), pngFile.getName() + ".tmp");
		Thread.yield();
		full.delete();
		Thread.yield();
		final boolean ok = pngFile.renameTo(full);
		Thread.yield();
		if (ok == false) {
			throw new IOException("Cannot rename");
		}

		Thread.yield();
		final BufferedImage im = ImageIO.read(full);
		Thread.yield();
		final PngSegment horizontalSegment = new PngSegment(im.getWidth(), horizontalPages);
		final PngSegment verticalSegment = new PngSegment(im.getHeight(), verticalPages);

		int x = 0;
		for (int i = 0; i < horizontalPages; i++) {
			for (int j = 0; j < verticalPages; j++) {
				final File f = FileFormat.PNG.computeFilename(pngFile, x++);
				this.files.add(f);
				final BufferedImage imPiece = im.getSubimage(horizontalSegment.getStart(i),
						verticalSegment.getStart(j), horizontalSegment.getLen(i), verticalSegment.getLen(j));
				Thread.yield();
				PngIO.write(imPiece, f, isWithMetadata ? source : null, dpi);
				Thread.yield();
			}
		}

		full.delete();
		Log.info("End of splitting");
	}

	public List<File> getFiles() {
		return Collections.unmodifiableList(files);
	}

}
