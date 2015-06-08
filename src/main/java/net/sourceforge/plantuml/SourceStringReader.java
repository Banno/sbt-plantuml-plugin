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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.DiagramDescriptionImpl;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;

public class SourceStringReader {

	final private List<BlockUml> blocks;

	public SourceStringReader(String source) {
		this(new Defines(), source, Collections.<String> emptyList());
	}

	public SourceStringReader(String source, String charset) {
		this(new Defines(), source, "UTF-8", Collections.<String> emptyList());
	}

	public SourceStringReader(Defines defines, String source, List<String> config) {
		this(defines, source, "UTF-8", config);
	}

	public SourceStringReader(Defines defines, String source, String charset, List<String> config) {
		// WARNING GLOBAL LOCK HERE
		synchronized (SourceStringReader.class) {
			try {
				final BlockUmlBuilder builder = new BlockUmlBuilder(config, charset, defines, new StringReader(source),
						null);
				this.blocks = builder.getBlockUmls();
			} catch (IOException e) {
				Log.error("error " + e);
				throw new IllegalStateException(e);
			}
		}
	}

	public String generateImage(OutputStream os) throws IOException {
		return generateImage(os, 0);
	}

	public String generateImage(File f) throws IOException {
		final OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
		final String result = generateImage(os, 0);
		os.close();
		return result;
	}

	public String generateImage(OutputStream os, FileFormatOption fileFormatOption) throws IOException {
		return generateImage(os, 0, fileFormatOption);
	}

	public String generateImage(OutputStream os, int numImage) throws IOException {
		return generateImage(os, numImage, new FileFormatOption(FileFormat.PNG));
	}

	public String generateImage(OutputStream os, int numImage, FileFormatOption fileFormatOption) throws IOException {
		if (blocks.size() == 0) {
			noStartumlFound(os, fileFormatOption);
			return null;
		}
		for (BlockUml b : blocks) {
			final Diagram system = b.getDiagram();
			final int nbInSystem = system.getNbImages();
			if (numImage < nbInSystem) {
				// final CMapData cmap = new CMapData();
				final ImageData imageData = system.exportDiagram(os, numImage, fileFormatOption);
				if (imageData.containsCMapData()) {
					return system.getDescription().getDescription() + "\n" + imageData.getCMapData("plantuml");
				}
				return system.getDescription().getDescription();
			}
			numImage -= nbInSystem;
		}
		Log.error("numImage is too big = " + numImage);
		return null;

	}

	private void noStartumlFound(OutputStream os, FileFormatOption fileFormatOption) throws IOException {
		final GraphicStrings error = GraphicStrings.createDefault(Arrays.asList("No @startuml found"),
				fileFormatOption.isUseRedForError());
		final ImageBuilder imageBuilder = new ImageBuilder(new ColorMapperIdentity(), 1.0, error.getBackcolor(), null,
				null, 0, 0, null, false);
		imageBuilder.addUDrawable(error);
		imageBuilder.writeImageTOBEMOVED(fileFormatOption, os);
	}

	public DiagramDescription generateDiagramDescription(OutputStream os) throws IOException {
		return generateDiagramDescription(os, 0);
	}

	public DiagramDescription generateDiagramDescription(File f) throws IOException {
		final OutputStream os = new BufferedOutputStream(new FileOutputStream(f));
		final DiagramDescription result = generateDiagramDescription(os, 0);
		os.close();
		return result;
	}

	public DiagramDescription generateDiagramDescription(OutputStream os, FileFormatOption fileFormatOption)
			throws IOException {
		return generateDiagramDescription(os, 0, fileFormatOption);
	}

	public DiagramDescription generateDiagramDescription(OutputStream os, int numImage) throws IOException {
		return generateDiagramDescription(os, numImage, new FileFormatOption(FileFormat.PNG));
	}

	public DiagramDescription generateDiagramDescription(OutputStream os, int numImage,
			FileFormatOption fileFormatOption) throws IOException {
		if (blocks.size() == 0) {
			noStartumlFound(os, fileFormatOption);
			return null;
		}
		for (BlockUml b : blocks) {
			final Diagram system = b.getDiagram();
			final int nbInSystem = system.getNbImages();
			if (numImage < nbInSystem) {
				// final CMapData cmap = new CMapData();
				final ImageData imageData = system.exportDiagram(os, numImage, fileFormatOption);
				if (imageData.containsCMapData()) {
					return ((DiagramDescriptionImpl) system.getDescription()).withCMapData(imageData
							.getCMapData("plantuml"));
				}
				return system.getDescription();
			}
			numImage -= nbInSystem;
		}
		Log.error("numImage is too big = " + numImage);
		return null;

	}

	public final List<BlockUml> getBlocks() {
		return Collections.unmodifiableList(blocks);
	}

}
