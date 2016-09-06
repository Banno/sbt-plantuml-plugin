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
package net.sourceforge.plantuml;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.activitydiagram3.ActivityDiagram3;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.CucaDiagram;
import net.sourceforge.plantuml.html.CucaDiagramHtmlMaker;
import net.sourceforge.plantuml.png.PngSplitter;
import net.sourceforge.plantuml.sequencediagram.SequenceDiagram;

public class PSystemUtils {

	public static List<File> exportDiagrams(Diagram system, File suggestedFile, FileFormatOption fileFormatOption)
			throws IOException {
		if (system instanceof UmlDiagram) {
			final ISkinParam skinParam = ((UmlDiagram) system).getSkinParam();
			fileFormatOption = fileFormatOption.withSvgLinkTarget(skinParam.getSvgLinkTarget());
		}
		if (system instanceof NewpagedDiagram) {
			return exportDiagramsNewpaged((NewpagedDiagram) system, suggestedFile, fileFormatOption);
		}
		if (system instanceof SequenceDiagram) {
			return exportDiagramsSequence((SequenceDiagram) system, suggestedFile, fileFormatOption);
		}
		if (system instanceof CucaDiagram) {
			return exportDiagramsCuca((CucaDiagram) system, suggestedFile, fileFormatOption);
		}
		if (system instanceof ActivityDiagram3) {
			return exportDiagramsActivityDiagram3((ActivityDiagram3) system, suggestedFile, fileFormatOption);
		}
		return exportDiagramsDefault(system, suggestedFile, fileFormatOption);
	}

	private static List<File> exportDiagramsNewpaged(NewpagedDiagram system, File suggestedFile,
			FileFormatOption fileFormat) throws IOException {
		final List<File> result = new ArrayList<File>();
		final int nbImages = system.getNbImages();
		for (int i = 0; i < nbImages; i++) {

			final File f = fileFormat.getFileFormat().computeFilename(suggestedFile, i);
			if (canFileBeWritten(f) == false) {
				return result;
			}
			final OutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
			// ImageData cmap = null;
			try {
				/* cmap = */system.exportDiagram(fos, i, fileFormat);
			} finally {
				fos.close();
			}
			// if (system.hasUrl() && cmap != null && cmap.containsCMapData()) {
			// system.exportCmap(suggestedFile, cmap);
			// }
			Log.info("File size : " + f.length());
			result.add(f);
		}
		return result;
	}

	public static boolean canFileBeWritten(final File f) {
		Log.info("Creating file: " + f);
		if (f.exists() && f.canWrite() == false) {
			if (OptionFlags.getInstance().isOverwrite()) {
				Log.info("Overwrite " + f);
				f.setWritable(true);
				f.delete();
				return true;
			}
			Log.error("Cannot write to file " + f);
			return false;
		}
		return true;
	}

	static private List<File> exportDiagramsDefault(Diagram system, File suggestedFile, FileFormatOption fileFormat)
			throws IOException {
		if (suggestedFile.exists() && suggestedFile.isDirectory()) {
			throw new IllegalArgumentException("File is a directory " + suggestedFile);
		}
		OutputStream os = null;
		try {
			if (canFileBeWritten(suggestedFile) == false) {
				return Collections.emptyList();
			}
			os = new BufferedOutputStream(new FileOutputStream(suggestedFile));
			// system.exportDiagram(os, null, 0, fileFormat);
			system.exportDiagram(os, 0, fileFormat);
		} finally {
			if (os != null) {
				os.close();
			}
		}
		return Arrays.asList(suggestedFile);
	}

	static private List<File> exportDiagramsActivityDiagram3(ActivityDiagram3 system, File suggestedFile,
			FileFormatOption fileFormat) throws IOException {
		if (suggestedFile.exists() && suggestedFile.isDirectory()) {
			throw new IllegalArgumentException("File is a directory " + suggestedFile);
		}
		OutputStream os = null;
		ImageData cmap = null;
		try {
			if (canFileBeWritten(suggestedFile) == false) {
				return Collections.emptyList();
			}
			os = new BufferedOutputStream(new FileOutputStream(suggestedFile));
			cmap = system.exportDiagram(os, 0, fileFormat);
		} finally {
			if (os != null) {
				os.close();
			}
		}
		if (cmap != null && cmap.containsCMapData()) {
			system.exportCmap(suggestedFile, cmap);
		}
		return Arrays.asList(suggestedFile);
	}

	private static List<File> exportDiagramsSequence(SequenceDiagram system, File suggestedFile,
			FileFormatOption fileFormat) throws IOException {
		final List<File> result = new ArrayList<File>();
		final int nbImages = system.getNbImages();
		for (int i = 0; i < nbImages; i++) {

			final File f = fileFormat.getFileFormat().computeFilename(suggestedFile, i);
			if (canFileBeWritten(suggestedFile) == false) {
				return result;
			}
			final OutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
			ImageData cmap = null;
			try {
				cmap = system.exportDiagram(fos, i, fileFormat);
			} finally {
				fos.close();
			}
			if (cmap != null && cmap.containsCMapData()) {
				system.exportCmap(suggestedFile, cmap);
			}
			Log.info("File size : " + f.length());
			result.add(f);
		}
		return result;
	}

	static private List<File> exportDiagramsCuca(CucaDiagram system, File suggestedFile, FileFormatOption fileFormat)
			throws IOException {
		if (suggestedFile.exists() && suggestedFile.isDirectory()) {
			throw new IllegalArgumentException("File is a directory " + suggestedFile);
		}

		if (fileFormat.getFileFormat() == FileFormat.HTML) {
			return createFilesHtml(system, suggestedFile);
		}

		ImageData cmap = null;
		OutputStream os = null;
		try {
			if (canFileBeWritten(suggestedFile) == false) {
				return Collections.emptyList();
			}
			// System.err.println("FOO11=" + suggestedFile);
			// os = new BufferedOutputStream(new FileOutputStream(suggestedFile));
			os = new NamedOutputStream(suggestedFile);
			cmap = system.exportDiagram(os, 0, fileFormat);
		} finally {
			if (os != null) {
				os.close();
			}
		}
		List<File> result = Arrays.asList(suggestedFile);

		if (cmap != null && cmap.containsCMapData()) {
			system.exportCmap(suggestedFile, cmap);
		}

		if (fileFormat.getFileFormat() == FileFormat.PNG) {
			result = new PngSplitter(suggestedFile, system.getHorizontalPages(), system.getVerticalPages(),
					system.getMetadata(), system.getDpi(fileFormat), fileFormat.isWithMetadata()).getFiles();
		}
		return result;

	}

	private static List<File> createFilesHtml(CucaDiagram system, File suggestedFile) throws IOException {
		final String name = suggestedFile.getName();
		final int idx = name.lastIndexOf('.');
		final File dir = new File(suggestedFile.getParentFile(), name.substring(0, idx));
		final CucaDiagramHtmlMaker maker = new CucaDiagramHtmlMaker(system, dir);
		return maker.create();
	}

}
