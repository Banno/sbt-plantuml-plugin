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
package net.sourceforge.plantuml.cucadiagram.dot;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.StringUtils;

class OSWindows extends OS {

	@Override
	File getExecutable(GraphvizLayoutStrategy strategy) {
		File result = strategy.getSystemForcedExecutable();
		if (result != null) {
			return result;
		}
		result = searchInDir(new File("c:/Program Files"), strategy);
		if (result != null) {
			return result;
		}
		result = searchInDir(new File("c:/Program Files (x86)"), strategy);
		return result;
	}

	private File searchInDir(final File programFile, GraphvizLayoutStrategy strategy) {
		if (programFile.exists() == false || programFile.isDirectory() == false) {
			return null;
		}
		final List<File> dots = new ArrayList<File>();
		for (File f : programFile.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory() && pathname.getName().startsWith("Graphviz");
			}
		})) {
			final File result = new File(new File(f, "bin"), getFileName(strategy));
			if (result.exists() && result.canRead()) {
				dots.add(result.getAbsoluteFile());
			}
		}
		return higherVersion(dots);
	}

	static File higherVersion(List<File> dots) {
		if (dots.size() == 0) {
			return null;
		}
		Collections.sort(dots, Collections.reverseOrder());
		return dots.get(0);
	}

	@Override
	String getFileName(GraphvizLayoutStrategy strategy) {
		return StringUtils.goLowerCase(strategy.name()) + ".exe";
	}

	@Override
	public String getCommand(GraphvizLayoutStrategy strategy) {
		return "\"" + getExecutable(strategy).getAbsolutePath() + "\"";
	}

}
