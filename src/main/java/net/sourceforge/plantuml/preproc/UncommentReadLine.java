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

public class UncommentReadLine implements ReadLine {

	private final ReadLine raw;
	private final Pattern start;
	private final Pattern unpause;
	private String headerToRemove;
	private boolean paused;

	public UncommentReadLine(ReadLine source) {
		this.raw = source;
		this.start = MyPattern.cmpile("(?i)((?:\\W|\\<[^<>]*\\>)*)@start");
		this.unpause = MyPattern.cmpile("(?i)((?:\\W|\\<[^<>]*\\>)*)@unpause");
	}

	public String readLine() throws IOException {
		final String result = raw.readLine();

		if (result == null) {
			return null;
		}

		final Matcher m = start.matcher(result);
		if (m.find()) {
			headerToRemove = m.group(1);
		}
		if (paused) {
			final Matcher m2 = unpause.matcher(result);
			if (m2.find()) {
				headerToRemove = m2.group(1);
			}
		}
		if (headerToRemove != null && headerToRemove.startsWith(result)) {
			return "";
		}
		if (headerToRemove != null && result.startsWith(headerToRemove)) {
			return result.substring(headerToRemove.length());
		}
		return result;
	}

	public void close() throws IOException {
		this.raw.close();
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

}
