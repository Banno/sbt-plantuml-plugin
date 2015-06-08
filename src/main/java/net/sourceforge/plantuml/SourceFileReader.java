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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.code.Transcoder;
import net.sourceforge.plantuml.code.TranscoderUtil;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.preproc.Defines;

public class SourceFileReader implements ISourceFileReader {

	private final File file;
	private final File outputDirectory;

	private final BlockUmlBuilder builder;
	private FileFormatOption fileFormatOption;

	public SourceFileReader(File file) throws IOException {
		this(file, file.getAbsoluteFile().getParentFile());
	}

	public SourceFileReader(File file, File outputDirectory, String charset) throws IOException {
		this(new Defines(), file, outputDirectory, Collections.<String> emptyList(), charset, new FileFormatOption(
				FileFormat.PNG));
	}

	public SourceFileReader(final File file, File outputDirectory) throws IOException {
		this(new Defines(), file, outputDirectory, Collections.<String> emptyList(), null, new FileFormatOption(
				FileFormat.PNG));
	}

	public SourceFileReader(final File file, File outputDirectory, FileFormatOption fileFormatOption)
			throws IOException {
		this(new Defines(), file, outputDirectory, Collections.<String> emptyList(), null, fileFormatOption);
	}

	public SourceFileReader(Defines defines, final File file, File outputDirectory, List<String> config,
			String charset, FileFormatOption fileFormatOption) throws IOException {
		this.file = file;
		this.fileFormatOption = fileFormatOption;
		if (file.exists() == false) {
			throw new IllegalArgumentException();
		}
		FileSystem.getInstance().setCurrentDir(file.getAbsoluteFile().getParentFile());
		if (outputDirectory == null) {
			outputDirectory = file.getAbsoluteFile().getParentFile();
		} else if (outputDirectory.isAbsolute() == false) {
			outputDirectory = FileSystem.getInstance().getFile(outputDirectory.getPath());
		}
		if (outputDirectory.exists() == false) {
			outputDirectory.mkdirs();
		}
		this.outputDirectory = outputDirectory;

		builder = new BlockUmlBuilder(config, charset, defines, getReader(charset), file.getAbsoluteFile()
				.getParentFile());
	}

	public boolean hasError() {
		for (final BlockUml b : builder.getBlockUmls()) {
			if (b.getDiagram() instanceof PSystemError) {
				return true;
			}
		}
		return false;
	}

	public List<GeneratedImage> getGeneratedImages() throws IOException {
		Log.info("Reading file: " + file);

		int cpt = 0;
		final List<GeneratedImage> result = new ArrayList<GeneratedImage>();

		for (BlockUml blockUml : builder.getBlockUmls()) {
			String newName = blockUml.getFilename();

			if (newName == null) {
				newName = fileFormatOption.getFileFormat().changeName(file.getName(), cpt++);
			}

			final File suggested = new File(outputDirectory, newName);
			suggested.getParentFile().mkdirs();

			final Diagram system = blockUml.getDiagram();
			final List<File> exportDiagrams = PSystemUtils.exportDiagrams(system, suggested, fileFormatOption);
			OptionFlags.getInstance().logData(file, system);

			for (File f : exportDiagrams) {
				final String desc = "[" + file.getName() + "] " + system.getDescription();
				if (OptionFlags.getInstance().isWord()) {
					final String warnOrError = system.getWarningOrError();
					if (warnOrError != null) {
						final String name = f.getName().substring(0, f.getName().length() - 4) + ".err";
						final File errorFile = new File(f.getParentFile(), name);
						final PrintStream ps = new PrintStream(new FileOutputStream(errorFile));
						ps.print(warnOrError);
						ps.close();
					}
				}
				final GeneratedImage generatedImage = new GeneratedImage(f, desc, blockUml);
				result.add(generatedImage);
			}

		}

		Log.info("Number of image(s): " + result.size());

		return Collections.unmodifiableList(result);
	}

	public List<String> getEncodedUrl() throws IOException {
		final List<String> result = new ArrayList<String>();
		final Transcoder transcoder = TranscoderUtil.getDefaultTranscoder();
		for (BlockUml blockUml : builder.getBlockUmls()) {
			final String source = blockUml.getDiagram().getSource().getPlainString();
			final String encoded = transcoder.encode(source);
			result.add(encoded);
		}
		return Collections.unmodifiableList(result);
	}

	private Reader getReader(String charset) throws FileNotFoundException, UnsupportedEncodingException {
		if (charset == null) {
			Log.info("Using default charset");
			return new InputStreamReader(new FileInputStream(file));
		}
		Log.info("Using charset " + charset);
		return new InputStreamReader(new FileInputStream(file), charset);
	}

	public final void setFileFormatOption(FileFormatOption fileFormatOption) {
		this.fileFormatOption = fileFormatOption;
	}

	public final Set<File> getIncludedFiles() {
		return builder.getIncludedFiles();
	}

}
