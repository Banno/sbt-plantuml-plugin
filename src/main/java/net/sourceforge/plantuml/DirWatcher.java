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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.preproc.Defines;

@Deprecated
public class DirWatcher {

	final private File dir;
	final private Option option;
	final private String pattern;

	final private Map<File, FileWatcher> modifieds = new HashMap<File, FileWatcher>();

	public DirWatcher(File dir, Option option, String pattern) {
		this.dir = dir;
		this.option = option;
		this.pattern = pattern;
	}

	public List<GeneratedImage> buildCreatedFiles() throws IOException, InterruptedException {
		boolean error = false;
		final List<GeneratedImage> result = new ArrayList<GeneratedImage>();
		for (File f : dir.listFiles()) {
			if (error) {
				continue;
			}
			if (f.isFile() == false) {
				continue;
			}
			if (fileToProcess(f.getName()) == false) {
				continue;
			}
			final FileWatcher watcher = modifieds.get(f);

			if (watcher == null || watcher.hasChanged()) {
				final SourceFileReader sourceFileReader = new SourceFileReader(new Defines(), f, option.getOutputDir(),
						option.getConfig(), option.getCharset(), option.getFileFormatOption());
				final Set<File> files = new HashSet<File>(sourceFileReader.getIncludedFiles());
				files.add(f);
				for (GeneratedImage g : sourceFileReader.getGeneratedImages()) {
					result.add(g);
					if (option.isFailfastOrFailfast2() && g.lineErrorRaw() != -1) {
						error = true;
					}
				}
				modifieds.put(f, new FileWatcher(files));
			}
		}
		Collections.sort(result);
		return Collections.unmodifiableList(result);
	}

	public File getErrorFile() throws IOException, InterruptedException {
		for (File f : dir.listFiles()) {
			if (f.isFile() == false) {
				continue;
			}
			if (fileToProcess(f.getName()) == false) {
				continue;
			}
			final FileWatcher watcher = modifieds.get(f);

			if (watcher == null || watcher.hasChanged()) {
				final SourceFileReader sourceFileReader = new SourceFileReader(new Defines(), f, option.getOutputDir(),
						option.getConfig(), option.getCharset(), option.getFileFormatOption());
				if (sourceFileReader.hasError()) {
					return f;
				}
			}
		}
		return null;
	}

	private boolean fileToProcess(String name) {
		return name.matches(pattern);
	}

	public final File getDir() {
		return dir;
	}

	// public void setPattern(String pattern) {
	// this.pattern = pattern;
	// }
}
