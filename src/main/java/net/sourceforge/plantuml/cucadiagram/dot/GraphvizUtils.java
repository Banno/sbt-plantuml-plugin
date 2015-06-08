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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.StringUtils;

public class GraphvizUtils {

	private static int DOT_VERSION_LIMIT = 226;

	private static boolean isWindows() {
		return File.separatorChar == '\\';
	}

	@Deprecated
	public static Graphviz create(String dotString, String... type) {
		final AbstractGraphviz result;
		if (isWindows()) {
			result = new GraphvizWindows(dotString, type);
		} else {
			result = new GraphvizLinux(dotString, type);
		}
		// if (OptionFlags.GRAPHVIZCACHE) {
		// return new GraphvizCached(result);
		// }
		return result;
	}

	// public static Graphviz create2(GraphvizLayoutStrategy strategy, String
	// dotString, String... type) {
	// return new AbstractGraphviz2(getOS(), strategy, dotString, type);
	// }

	static public File getDotExe() {
		return create(null, "png").getDotExe();
	}

	public static String getenvGraphvizDot() {
		final String env = System.getProperty("GRAPHVIZ_DOT");
		if (StringUtils.isNotEmpty(env)) {
			return StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(env);
		}
		final String getenv = System.getenv("GRAPHVIZ_DOT");
		if (StringUtils.isNotEmpty(getenv)) {
			return StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(getenv);
		}
		return null;
	}

	public static String getenvLogData() {
		final String env = System.getProperty("PLANTUML_LOGDATA");
		if (StringUtils.isNotEmpty(env)) {
			return env;
		}
		return System.getenv("PLANTUML_LOGDATA");
	}

	private static String dotVersion = null;

	public static String dotVersion() throws IOException, InterruptedException {
		if (dotVersion == null) {
			if (GraphvizUtils.getDotExe() == null) {
				dotVersion = "Error: Dot not installed";
			} else if (GraphvizUtils.getDotExe().exists() == false) {
				dotVersion = "Error: " + GraphvizUtils.getDotExe().getAbsolutePath() + " does not exist";
			} else if (GraphvizUtils.getDotExe().isFile() == false) {
				dotVersion = "Error: " + GraphvizUtils.getDotExe().getAbsolutePath() + " is not a file";
			} else if (GraphvizUtils.getDotExe().canRead() == false) {
				dotVersion = "Error: " + GraphvizUtils.getDotExe().getAbsolutePath() + " cannot be read";
			} else {
				dotVersion = create(null, "png").dotVersion();
			}
		}
		return dotVersion;
	}

	public static int retrieveVersion(String s) {
		if (s == null) {
			return -1;
		}
		final Pattern p = Pattern.compile("\\s([12].\\d\\d)\\D");
		final Matcher m = p.matcher(s);
		if (m.find() == false) {
			return -1;
		}
		return Integer.parseInt(m.group(1).replaceAll("\\.", ""));
	}

	public static int getDotVersion() throws IOException, InterruptedException {
		return retrieveVersion(dotVersion());
	}

	static public List<String> getTestDotStrings(boolean withRichText) {
		String red = "";
		String bold = "";
		if (withRichText) {
			red = "<b><color:red>";
			bold = "<b>";
		}
		final List<String> result = new ArrayList<String>();
		final String ent = GraphvizUtils.getenvGraphvizDot();
		if (ent == null) {
			result.add("The environment variable GRAPHVIZ_DOT has not been set");
		} else {
			result.add("The environment variable GRAPHVIZ_DOT has been set to " + ent);
		}
		final File dotExe = GraphvizUtils.getDotExe();
		result.add("Dot executable is " + dotExe);

		boolean ok = true;
		if (dotExe == null) {
			result.add(red + "Error: No dot executable found");
			ok = false;
		} else if (dotExe.exists() == false) {
			result.add(red + "Error: file does not exist");
			ok = false;
		} else if (dotExe.isFile() == false) {
			result.add(red + "Error: not a valid file");
			ok = false;
		} else if (dotExe.canRead() == false) {
			result.add(red + "Error: cannot be read");
			ok = false;
		}

		if (ok) {
			try {
				final String version = GraphvizUtils.dotVersion();
				result.add("Dot version: " + version);
				final int v = GraphvizUtils.getDotVersion();
				if (v == -1) {
					result.add("Warning : cannot determine dot version");
				} else if (v < DOT_VERSION_LIMIT) {
					result.add(bold + "Warning : Your dot installation seems old");
					result.add(bold + "Some diagrams may have issues");
				} else {
					final String err = getTestCreateSimpleFile();
					if (err == null) {
						result.add(bold + "Installation seems OK. File generation OK");
					} else {
						result.add(red + err);
					}
				}
			} catch (Exception e) {
				result.add(red + e.toString());
				e.printStackTrace();
			}
		} else {
			result.add("Error: only sequence diagrams will be generated");
		}

		return Collections.unmodifiableList(result);
	}

	static String getTestCreateSimpleFile() throws IOException {
		final Graphviz graphviz2 = GraphvizUtils.create("digraph foo { test; }", "svg");
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ProcessState state = graphviz2.createFile3(baos);
		if (state.differs(ProcessState.TERMINATED_OK())) {
			return "Error: timeout " + state;
		}

		final byte data[] = baos.toByteArray();

		if (data.length == 0) {
			return "Error: dot generates empty file. Check you dot installation.";
		}
		final String s = new String(data);
		if (s.indexOf("<svg") == -1) {
			return "Error: dot generates unreadable SVG file. Check you dot installation.";
		}
		return null;
	}

	// public static OS getOS() {
	// if (isWindows()) {
	// return new OSWindows();
	// }
	// return new OSLinux();
	// }

}
