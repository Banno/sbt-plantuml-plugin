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
package net.sourceforge.plantuml.preproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.sourceforge.plantuml.CharSequence2;
import net.sourceforge.plantuml.CharSequence2Impl;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.utils.StartUtils;

public class Preprocessor implements ReadLine {

	private static final String ID = "[A-Za-z_][A-Za-z_0-9]*";
	private static final String ARG = "(?:\\(" + ID + "(?:," + ID + ")*?\\))?";
	private static final Pattern2 definePattern = MyPattern.cmpile("^[%s]*!define[%s]+(" + ID + ARG + ")"
			+ "(?:[%s]+(.*))?$");
	private static final Pattern2 undefPattern = MyPattern.cmpile("^[%s]*!undef[%s]+(" + ID + ")$");
	private static final Pattern2 definelongPattern = MyPattern.cmpile("^[%s]*!definelong[%s]+(" + ID + ARG + ")");
	private static final Pattern2 enddefinelongPattern = MyPattern.cmpile("^[%s]*!enddefinelong[%s]*$");

	private final Defines defines;
	private final PreprocessorInclude rawSource;
	private final ReadLineInsertable source;

	public Preprocessor(ReadLine reader, String charset, Defines defines, File newCurrentDir) {
		this.defines = defines;
		this.defines.saveState();
		this.rawSource = new PreprocessorInclude(reader, defines, charset, newCurrentDir);
		this.source = new ReadLineInsertable(new IfManager(rawSource, defines));
	}

	public CharSequence2 readLine() throws IOException {
		final CharSequence2 s = source.readLine();
		if (s == null) {
			return null;
		}
		if (StartUtils.isArobaseStartDiagram(s)) {
			this.defines.restoreState();
		}

		Matcher2 m = definePattern.matcher(s);
		if (m.find()) {
			return manageDefine(m);
		}

		m = definelongPattern.matcher(s);
		if (m.find()) {
			return manageDefineLong(m);
		}

		m = undefPattern.matcher(s);
		if (m.find()) {
			return manageUndef(m);
		}

		if (ignoreDefineDuringSeveralLines > 0) {
			ignoreDefineDuringSeveralLines--;
			return s;
		}

		final List<String> result = defines.applyDefines(s.toString2());
		if (result.size() > 1) {
			ignoreDefineDuringSeveralLines = result.size() - 2;
			source.insert(result.subList(1, result.size() - 1), s.getLocation());
		}
		return new CharSequence2Impl(result.get(0), s.getLocation());
	}

	private int ignoreDefineDuringSeveralLines = 0;

	private CharSequence2 manageUndef(Matcher2 m) throws IOException {
		defines.undefine(m.group(1));
		return this.readLine();
	}

	private CharSequence2 manageDefineLong(Matcher2 m) throws IOException {
		final String group1 = m.group(1);
		final List<String> def = new ArrayList<String>();
		while (true) {
			final CharSequence2 read = this.readLine();
			if (read == null) {
				return null;
			}
			def.add(read.toString2());
			if (enddefinelongPattern.matcher(read).find()) {
				defines.define(group1, def);
				return this.readLine();
			}
		}
	}

	private CharSequence2 manageDefine(Matcher2 m) throws IOException {
		final String group1 = m.group(1);
		final String group2 = m.group(2);
		if (group2 == null) {
			defines.define(group1, null);
		} else {
			final List<String> strings = defines.applyDefines(group2);
			if (strings.size() > 1) {
				defines.define(group1, strings);
			} else {
				final StringBuilder value = new StringBuilder(strings.get(0));
				while (StringUtils.endsWithBackslash(value.toString())) {
					value.setLength(value.length() - 1);
					final CharSequence2 read = this.readLine();
					value.append(read.toString2());
				}
				final List<String> li = new ArrayList<String>();
				li.add(value.toString());
				defines.define(group1, li);
			}
		}
		return this.readLine();
	}

	public int getLineNumber() {
		return rawSource.getLineNumber();
	}

	public void close() throws IOException {
		rawSource.close();
	}

	public Set<FileWithSuffix> getFilesUsed() {
		return Collections.unmodifiableSet(rawSource.getFilesUsedGlobal());
	}

}
