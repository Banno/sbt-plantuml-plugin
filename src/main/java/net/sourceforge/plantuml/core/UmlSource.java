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
package net.sourceforge.plantuml.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.utils.StartUtils;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.version.IteratorCounter;
import net.sourceforge.plantuml.version.IteratorCounterImpl;

/**
 * Represents the textual source of some diagram.
 * The source should start with a <code>@startfoo</code> and end with <code>@endfoo</code>.
 * <p>
 * So the diagram does not have to be a UML one.
 * 
 * @author Arnaud Roques
 *
 */
final public class UmlSource {

	final private List<String> source;

	/**
	 * Build the source from a text.
	 * 
	 * @param source	the source of the diagram
	 * @param checkEndingBackslash	<code>true</code> if an ending backslash means that a line has
	 * to be collapsed with the following one.
	 */
	public UmlSource(List<? extends CharSequence> source, boolean checkEndingBackslash) {
		final List<String> tmp = new ArrayList<String>();
		// final DiagramType type =
		// DiagramType.getTypeFromArobaseStart(source.get(0).toString());
		if (checkEndingBackslash) {
			final StringBuilder pending = new StringBuilder();
			for (CharSequence cs : source) {
				final String s = cs.toString();
				if (StringUtils.endsWithBackslash(s)) {
					pending.append(s.substring(0, s.length() - 1));
				} else {
					pending.append(s);
					tmp.add(pending.toString());
					pending.setLength(0);
				}
			}
		} else {
			for (CharSequence s : source) {
				tmp.add(s.toString());
			}
		}
		this.source = Collections.unmodifiableList(tmp);
	}


	/**
	 * Retrieve the type of the diagram.
	 * This is based on the first line <code>@startfoo</code>.
	 * 
	 * @return the type of the diagram.
	 */
	public DiagramType getDiagramType() {
		return DiagramType.getTypeFromArobaseStart(source.get(0));
	}

	/**
	 * Allows to iterator over the source.
	 * 
	 * @return a iterator that allow counting line number.
	 */
	public IteratorCounter iterator() {
		return new IteratorCounterImpl(source);
	}

	/**
	 * Return the source as a single String with <code>\n</code> as line separator.
	 * 
	 * @return the whole diagram source
	 */
	public String getPlainString() {
		final StringBuilder sb = new StringBuilder();
		for (String s : source) {
			sb.append(s);
			sb.append('\r');
			sb.append('\n');
		}
		return sb.toString();
	}

	/**
	 * Return a specific line of the diagram description.
	 * 
	 * @param n		line number, starting at 0
	 * @return
	 */
	public String getLine(int n) {
		return source.get(n);
	}

	/**
	 * Return the number of line in the diagram.
	 * 
	 * @return
	 */
	public int getTotalLineCount() {
		return source.size();
	}

	/**
	 * Check if a source diagram description is empty.
	 * Does not take comment line into account.
	 * 
	 * @return <code>true<code> if the diagram does not contain information.
	 */
	public boolean isEmpty() {
		for (String s : source) {
			if (StartUtils.isArobaseStartDiagram(s)) {
				continue;
			}
			if (StartUtils.isArobaseEndDiagram(s)) {
				continue;
			}
			if (s.matches("\\s*'.*")) {
				continue;
			}
			if (StringUtils.trin(s).length() != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Retrieve the title, if defined in the diagram source.
	 * Never return <code>null</code>.
	 * @return
	 */
	public Display getTitle() {
		final Pattern p = MyPattern.cmpile("(?i)^[%s]*title[%s]+(.+)$");
		for (String s : source) {
			final Matcher m = p.matcher(s);
			final boolean ok = m.matches();
			if (ok) {
				return Display.create(m.group(1));
			}
		}
		return Display.empty();
	}

}
