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
package net.sourceforge.plantuml.command.regex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RegexComposed implements IRegex {

	private final List<IRegex> partials;

	abstract protected Pattern getFull();

	public RegexComposed(IRegex... partial) {
		this.partials = Arrays.asList(partial);
	}

	public Map<String, RegexPartialMatch> createPartialMatch(Iterator<String> it) {
		final Map<String, RegexPartialMatch> result = new HashMap<String, RegexPartialMatch>();
		for (IRegex r : partials) {
			result.putAll(r.createPartialMatch(it));
		}
		return result;
	}

	final public int count() {
		int cpt = getStartCount();
		for (IRegex r : partials) {
			cpt += r.count();
		}
		return cpt;
	}

	protected int getStartCount() {
		return 0;
	}

	public RegexResult matcher(String s) {
		final Matcher matcher = getFull().matcher(s);
		if (matcher.find() == false) {
			throw new IllegalArgumentException(getClass()+" "+s);
		}

		final Iterator<String> it = new MatcherIterator(matcher);
		return new RegexResult(createPartialMatch(it));
	}

	final public boolean match(String s) {
		return getFull().matcher(s).find();
	}

	final public String getPattern() {
		return getFull().pattern();
	}

	final protected List<IRegex> getPartials() {
		return partials;
	}

}
