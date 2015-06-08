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

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.command.regex.MyPattern;

class IfManager implements ReadLine {

	protected static final Pattern ifdefPattern = MyPattern.cmpile("^[%s]*!if(n)?def[%s]+([A-Za-z_][A-Za-z_0-9]*)$");
	protected static final Pattern elsePattern = MyPattern.cmpile("^[%s]*!else$");
	protected static final Pattern endifPattern = MyPattern.cmpile("^[%s]*!endif$");

	private final Defines defines;
	private final ReadLine source;

	private IfManager child;

	public IfManager(ReadLine source, Defines defines) {
		this.defines = defines;
		this.source = source;
	}

	final public String readLine() throws IOException {
		if (child != null) {
			final String s = child.readLine();
			if (s != null) {
				return s;
			}
			child = null;
		}

		return readLineInternal();
	}

	protected String readLineInternal() throws IOException {
		final String s = source.readLine();
		if (s == null) {
			return null;
		}

		final Matcher m = ifdefPattern.matcher(s);
		if (m.find()) {
			boolean ok = defines.isDefine(m.group(2));
			if (m.group(1) != null) {
				ok = !ok;
			}
			if (ok) {
				child = new IfManagerPositif(source, defines);
			} else {
				child = new IfManagerNegatif(source, defines);
			}
			// child = new IfManager(source, defines, ok ? IfPart.IF :
			// IfPart.SKIP);
			return this.readLine();
		}

		// m = endifPattern.matcher(s);
		// if (m.find()) {
		// return null;
		// }
		return s;
	}

	public void close() throws IOException {
		source.close();
	}

}
