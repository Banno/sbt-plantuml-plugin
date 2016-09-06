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
package net.sourceforge.plantuml.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.cucadiagram.Display;

public class BlocLines implements Iterable<CharSequence> {

	private List<CharSequence> lines;

	@Override
	public String toString() {
		return lines.toString();
	}

	private BlocLines(List<? extends CharSequence> lines) {
		this.lines = (List<CharSequence>) Collections.unmodifiableList(lines);
	}

	public Display toDisplay() {
		return Display.create(lines);
	}

	public static BlocLines single(CharSequence single) {
		return new BlocLines(Arrays.asList(single));
	}

	public static BlocLines getWithNewlines(CharSequence s) {
		return new BlocLines(StringUtils.getWithNewlines(s));
	}

	public BlocLines() {
		this(new ArrayList<CharSequence>());
	}

	public BlocLines add2(CharSequence s) {
		final List<CharSequence> copy = new ArrayList<CharSequence>(lines);
		copy.add(s);
		return new BlocLines(copy);
	}

	public List<CharSequence> getLines() {
		return lines;
	}

	public int size() {
		return lines.size();
	}

	public CharSequence get499(int i) {
		return lines.get(i);
	}

	public CharSequence getFirst499() {
		return lines.get(0);
	}

	public CharSequence getLast499() {
		return lines.get(lines.size() - 1);
	}

	public BlocLines cleanList2(MultilinesStrategy strategy) {
		final List<CharSequence> copy = new ArrayList<CharSequence>(lines);
		strategy.cleanList(copy);
		return new BlocLines(copy);
	}

	public BlocLines trim(boolean removeEmptyLines) {
		final List<CharSequence> copy = new ArrayList<CharSequence>(lines);
		for (int i = 0; i < copy.size(); i++) {
			final CharSequence s = copy.get(i);
			copy.set(i, StringUtils.trin(s));
		}
		if (removeEmptyLines) {
			for (final Iterator<CharSequence> it = copy.iterator(); it.hasNext();) {
				if (it.next().length() == 0) {
					it.remove();
				}
			}
		}
		return new BlocLines(copy);
	}

	public BlocLines removeEmptyColumns() {
		if (firstColumnRemovable(lines) == false) {
			return this;
		}
		final List<CharSequence> copy = new ArrayList<CharSequence>(lines);
		do {
			for (int i = 0; i < copy.size(); i++) {
				final CharSequence s = copy.get(i);
				if (s.length() > 0) {
					copy.set(i, s.subSequence(1, s.length()));
				}
			}
		} while (firstColumnRemovable(copy));
		return new BlocLines(copy);
	}

	private static boolean firstColumnRemovable(List<CharSequence> data) {
		boolean allEmpty = true;
		for (CharSequence s : data) {
			if (s.length() == 0) {
				continue;
			}
			allEmpty = false;
			final char c = s.charAt(0);
			if (c != ' ' && c != '\t') {
				return false;
			}
		}
		return allEmpty == false;
	}

	public char getLastChar() {
		final CharSequence s = lines.get(lines.size() - 1);
		return s.charAt(s.length() - 1);
	}

	public BlocLines removeStartingAndEnding2(String data) {
		if (lines.size() == 0) {
			return this;
		}
		final List<CharSequence> copy = new ArrayList<CharSequence>(lines);
		copy.set(0, data);
		final int n = copy.size() - 1;
		final CharSequence s = copy.get(n);
		copy.set(n, s.subSequence(0, s.length() - 1));
		return new BlocLines(copy);
	}

	public BlocLines concat2() {
		final StringBuilder sb = new StringBuilder();
		for (CharSequence line : lines) {
			sb.append(line);
			sb.append(StringUtils.hiddenNewLine());
		}
		return single(sb.substring(0, sb.length() - 1));
	}

	public BlocLines trimSmart(int referenceLine) {
		if (lines.size() <= referenceLine) {
			return this;
		}
		final List<CharSequence> copy = new ArrayList<CharSequence>(lines);
		final int nbStartingSpace = nbStartingSpace(copy.get(referenceLine));
		for (int i = referenceLine; i < copy.size(); i++) {
			final CharSequence s = copy.get(i);
			copy.set(i, removeStartingSpaces(s, nbStartingSpace));
		}
		return new BlocLines(copy);
	}

	private static int nbStartingSpace(CharSequence s) {
		int nb = 0;
		while (nb < s.length() && isSpaceOrTab(s.charAt(nb))) {
			nb++;
		}
		return nb;
	}

	private static boolean isSpaceOrTab(char c) {
		return c == ' ' || c == '\t';
	}

	private static CharSequence removeStartingSpaces(CharSequence arg, int nbStartingSpace) {
		if (arg.length() == 0) {
			return arg;
		}
		int i = 0;
		while (i < nbStartingSpace && i < arg.length() && isSpaceOrTab(arg.charAt(i))) {
			i++;
		}
		if (i == 0) {
			return arg;
		}
		return arg.subSequence(i, arg.length());
	}

	public BlocLines subExtract(int start, int end) {
		List<CharSequence> copy = new ArrayList<CharSequence>(lines);
		copy = copy.subList(start, copy.size() - end);
		return new BlocLines(copy);
	}

	public Iterator<CharSequence> iterator() {
		return lines.iterator();
	}

	public BlocLines removeComments() {
		final List<CharSequence> copy = new ArrayList<CharSequence>();
		boolean inComment = false;
		for (CharSequence cs : lines) {
			if (inComment == false && MyPattern.mtches(cs, CommandMultilinesComment.COMMENT_SINGLE_LINE)) {
				continue;
			}
			if (inComment == false && MyPattern.mtches(cs, CommandMultilinesComment.COMMENT_MULTILINE_START)) {
				inComment = true;
				continue;
			}
			if (inComment && MyPattern.mtches(cs, CommandMultilinesComment.COMMENT_MULTILINE_END)) {
				inComment = false;
				continue;
			}
			if (inComment == false) {
				copy.add(cs);
			}
		}
		return new BlocLines(copy);
	}

	public BlocLines removeInnerComments() {
		final List<CharSequence> copy = new ArrayList<CharSequence>();
		for (CharSequence cs : lines) {
			copy.add(MyPattern.removeAll(cs, CommandMultilinesComment.INNER_COMMENT));
		}
		return new BlocLines(copy);
	}

}
