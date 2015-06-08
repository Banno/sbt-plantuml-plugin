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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.command.regex.MyPattern;

public class UrlBuilder {

	public static enum ModeUrl {
		STRICT, AT_START, ANYWHERE, AT_END
	}

	private static final String URL_PATTERN = "\\[\\[([%g][^%g]+[%g]|[^{}%s\\]\\[]*)(?:[%s]*\\{([^{}]+)\\})?(?:[%s]*([^\\]\\[]+))?\\]\\]";

	private final String topurl;
	private ModeUrl mode;

	public UrlBuilder(String topurl, ModeUrl mode) {
		this.topurl = topurl;
		this.mode = mode;
	}

	public Url getUrl(String s) {
		final Pattern p;
		if (mode == ModeUrl.STRICT) {
			p = MyPattern.cmpile("(?i)^" + URL_PATTERN + "$");
		} else if (mode == ModeUrl.AT_START) {
			p = MyPattern.cmpile("(?i)^" + URL_PATTERN + ".*");
		} else if (mode == ModeUrl.AT_END) {
			p = MyPattern.cmpile("(?i).*" + URL_PATTERN + "$");
		} else if (mode == ModeUrl.ANYWHERE) {
			p = MyPattern.cmpile("(?i).*" + URL_PATTERN + ".*");
		} else {
			throw new IllegalStateException();
		}
		final Matcher m = p.matcher(StringUtils.trinNoTrace(s));
		if (m.matches() == false) {
			return null;
		}
		String url = StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(m.group(1));
		if (url.startsWith("http:") == false && url.startsWith("https:") == false) {
			// final String top = getSystem().getSkinParam().getValue("topurl");
			if (topurl != null) {
				url = topurl + url;
			}
		}
		return new Url(url, m.group(2), m.group(3));
	}

	public static String getRegexp() {
		return URL_PATTERN;
	}

	public static String purgeUrl(final String label) {
		final Pattern p = MyPattern.cmpile("[%s]*" + URL_PATTERN + "[%s]*");
		final Matcher m = p.matcher(label);
		if (m.find() == false) {
			throw new IllegalStateException();
		}
		final String url = m.group(0);
		final int x = label.indexOf(url);
		return label.substring(0, x) + label.substring(x + url.length());
	}

}
