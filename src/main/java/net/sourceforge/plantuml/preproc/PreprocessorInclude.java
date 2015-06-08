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
package net.sourceforge.plantuml.preproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.FileSystem;
import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.utils.StartUtils;

class PreprocessorInclude implements ReadLine {

	private static final Pattern includePattern = MyPattern.cmpile("^[%s]*!include[%s]+[%g]?([^%g]+)[%g]?$");
	private static final Pattern includeURLPattern = MyPattern.cmpile("^[%s]*!includeurl[%s]+[%g]?([^%g]+)[%g]?$");

	private final ReadLine reader2;
	private final String charset;
	private final Defines defines;

	private int numLine = 0;

	private PreprocessorInclude included = null;

	private final File oldCurrentDir;
	private final Set<File> filesUsedCurrent;
	private final Set<File> filesUsedGlobal;

	public PreprocessorInclude(ReadLine reader, Defines defines, String charset, File newCurrentDir) {
		this(reader, defines, charset, newCurrentDir, new HashSet<File>(), new HashSet<File>());
	}

	public Set<File> getFilesUsedGlobal() {
		return Collections.unmodifiableSet(filesUsedGlobal);
	}

	private PreprocessorInclude(ReadLine reader, Defines defines, String charset, File newCurrentDir,
			Set<File> filesUsedCurrent, Set<File> filesUsedGlobal) {
		this.defines = defines;
		this.charset = charset;
		this.reader2 = reader;
		this.filesUsedCurrent = filesUsedCurrent;
		this.filesUsedGlobal = filesUsedGlobal;
		if (newCurrentDir == null) {
			oldCurrentDir = null;
		} else {
			oldCurrentDir = FileSystem.getInstance().getCurrentDir();
			FileSystem.getInstance().setCurrentDir(newCurrentDir);
		}
	}

	private void restoreCurrentDir() {
		if (oldCurrentDir != null) {
			FileSystem.getInstance().setCurrentDir(oldCurrentDir);
		}
	}

	public String readLine() throws IOException {
		final String result = readLineInternal();
		if (result != null && (StartUtils.isArobaseEndDiagram(result) || StartUtils.isArobaseStartDiagram(result))) {
			// http://plantuml.sourceforge.net/qa/?qa=3389/error-generating-when-same-file-included-different-diagram
			filesUsedCurrent.clear();
		}
		return result;
	}

	private String readLineInternal() throws IOException {
		if (included != null) {
			final String s = included.readLine();
			if (s != null) {
				return s;
			}
			included.close();
			included = null;
		}

		final String s = reader2.readLine();
		numLine++;
		if (s == null) {
			return null;
		}
		if (OptionFlags.ALLOW_INCLUDE) {
			final Matcher m = includePattern.matcher(s);
			assert included == null;
			if (m.find()) {
				return manageFileInclude(m);
			}
		}
		final Matcher mUrl = includeURLPattern.matcher(s);
		if (mUrl.find()) {
			return manageUrlInclude(mUrl);
		}
		return s;
	}

	private String manageUrlInclude(Matcher m) throws IOException {
		String urlString = m.group(1);
		urlString = defines.applyDefines(urlString).get(0);
		//
		final int idx = urlString.lastIndexOf('!');
		String suf = null;
		if (idx != -1) {
			suf = urlString.substring(idx + 1);
			urlString = urlString.substring(0, idx);
		}
		try {
			final URL url = new URL(urlString);
			included = new PreprocessorInclude(getReaderInclude(url, suf), defines, charset, null, filesUsedCurrent,
					filesUsedGlobal);
		} catch (MalformedURLException e) {
			return "Cannot include url " + urlString;
		}
		return this.readLine();
	}

	private String manageFileInclude(Matcher m) throws IOException {
		String fileName = m.group(1);
		fileName = defines.applyDefines(fileName).get(0);
		final int idx = fileName.lastIndexOf('!');
		String suf = null;
		if (idx != -1) {
			suf = fileName.substring(idx + 1);
			fileName = fileName.substring(0, idx);
		}
		final File f = FileSystem.getInstance().getFile(withEnvironmentVariable(fileName));
		if (f.exists() == false) {
			return "Cannot include " + f.getAbsolutePath();
		} else if (filesUsedCurrent.contains(f)) {
			return "File already included " + f.getAbsolutePath();
		} else {
			filesUsedCurrent.add(f);
			filesUsedGlobal.add(f);
			included = new PreprocessorInclude(getReaderInclude(f, suf), defines, charset, f.getParentFile(),
					filesUsedCurrent, filesUsedGlobal);
		}
		return this.readLine();
	}

	static String withEnvironmentVariable(String s) {
		final Pattern p = Pattern.compile("%(\\w+)%");

		final Matcher m = p.matcher(s);
		final StringBuffer sb = new StringBuffer();
		while (m.find()) {
			final String var = m.group(1);
			final String value = getenv(var);
			if (value != null) {
				m.appendReplacement(sb, Matcher.quoteReplacement(value));
			}
		}
		m.appendTail(sb);
		s = sb.toString();
		return s;
	}

	private static String getenv(String var) {
		final String env = System.getProperty(var);
		if (StringUtils.isNotEmpty(env)) {
			return StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(env);
		}
		final String getenv = System.getenv(var);
		if (StringUtils.isNotEmpty(getenv)) {
			return StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(getenv);
		}
		return null;
	}

	private ReadLine getReaderInclude(final File f, String suf) throws IOException {
		if (StartDiagramExtractReader.containsStartDiagram(f, charset)) {
			int bloc = 0;
			if (suf != null && suf.matches("\\d+")) {
				bloc = Integer.parseInt(suf);
			}
			return new StartDiagramExtractReader(f, bloc, charset);
		}
		if (charset == null) {
			Log.info("Using default charset");
			return new ReadLineReader(new FileReader(f));
		}
		Log.info("Using charset " + charset);
		return new ReadLineReader(new InputStreamReader(new FileInputStream(f), charset));
	}

	private ReadLine getReaderInclude(final URL url, String suf) throws IOException {
		if (StartDiagramExtractReader.containsStartDiagram(url, charset)) {
			int bloc = 0;
			if (suf != null && suf.matches("\\d+")) {
				bloc = Integer.parseInt(suf);
			}
			return new StartDiagramExtractReader(url, bloc, charset);
		}
		final InputStream is = url.openStream();
		if (charset == null) {
			Log.info("Using default charset");
			return new ReadLineReader(new InputStreamReader(is));
		}
		Log.info("Using charset " + charset);
		return new ReadLineReader(new InputStreamReader(is, charset));
	}

	public int getLineNumber() {
		return numLine;
	}

	public void close() throws IOException {
		restoreCurrentDir();
		reader2.close();
	}

}
