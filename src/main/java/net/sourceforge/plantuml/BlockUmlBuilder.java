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
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.preproc.Defines;
import net.sourceforge.plantuml.preproc.Preprocessor;
import net.sourceforge.plantuml.preproc.ReadLineReader;
import net.sourceforge.plantuml.preproc.UncommentReadLine;
import net.sourceforge.plantuml.utils.StartUtils;

final public class BlockUmlBuilder {

	private final List<BlockUml> blocks = new ArrayList<BlockUml>();
	private Set<File> usedFiles = new HashSet<File>();
	private final UncommentReadLine reader2;

	public BlockUmlBuilder(List<String> config, String charset, Defines defines, Reader reader, File newCurrentDir)
			throws IOException {
		Preprocessor includer = null;
		try {
			reader2 = new UncommentReadLine(new ReadLineReader(reader));
			includer = new Preprocessor(reader2, charset, defines, newCurrentDir);
			init(includer, config);
		} finally {
			if (includer != null) {
				includer.close();
				usedFiles = includer.getFilesUsed();
			}
		}
	}

	private void init(Preprocessor includer, List<String> config) throws IOException {
		String s = null;
		List<String> current = null;
		boolean paused = false;
		int startLine = 0;
		while ((s = includer.readLine()) != null) {
			if (StartUtils.isArobaseStartDiagram(s)) {
				current = new ArrayList<String>();
				paused = false;
				startLine = includer.getLineNumber();
			}
			if (StartUtils.isArobasePauseDiagram(s)) {
				paused = true;
				reader2.setPaused(true);
			}
			if (current != null && paused == false) {
				current.add(s);
			} else if (paused) {
				final String append = StartUtils.getPossibleAppend(s);
				if (append != null) {
					current.add(append);
				}
			}

			if (StartUtils.isArobaseUnpauseDiagram(s)) {
				paused = false;
				reader2.setPaused(false);
			}
			if (StartUtils.isArobaseEndDiagram(s) && current != null) {
				current.addAll(1, config);
				blocks.add(new BlockUml(current, startLine));
				current = null;
				reader2.setPaused(false);
			}
		}
	}

	public List<BlockUml> getBlockUmls() {
		return Collections.unmodifiableList(blocks);
	}

	public final Set<File> getIncludedFiles() {
		return Collections.unmodifiableSet(usedFiles);
	}

	/*
	 * private List<String> getStrings(Reader reader) throws IOException { final List<String> result = new
	 * ArrayList<String>(); Preprocessor includer = null; try { includer = new Preprocessor(reader, defines); String s =
	 * null; while ((s = includer.readLine()) != null) { result.add(s); } } finally { if (includer != null) {
	 * includer.close(); } } return Collections.unmodifiableList(result); }
	 */
}
