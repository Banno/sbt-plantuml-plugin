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
package net.sourceforge.plantuml.creole;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlBuilder.ModeUrl;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.command.regex.Pattern2;

public class CommandCreoleUrl implements Command {

	private final Pattern2 pattern;
	private final ISkinSimple skinParam;

	public static Command create(ISkinSimple skinParam) {
		return new CommandCreoleUrl(skinParam, "^(" + UrlBuilder.getRegexp() + ")");
	}

	private CommandCreoleUrl(ISkinSimple skinParam, String p) {
		this.pattern = MyPattern.cmpile(p);
		this.skinParam = skinParam;

	}

	public int matchingSize(String line) {
		final Matcher2 m = pattern.matcher(line);
		if (m.find() == false) {
			return 0;
		}
		return m.group(1).length();
	}

	public String executeAndGetRemaining(String line, StripeSimple stripe) {
		final Matcher2 m = pattern.matcher(line);
		if (m.find() == false) {
			throw new IllegalStateException();
		}
		final UrlBuilder urlBuilder = new UrlBuilder(skinParam.getValue("topurl"), ModeUrl.STRICT);
		final Url url = urlBuilder.getUrl(m.group(1));
		stripe.addUrl(url);

//		final int size = Integer.parseInt(m.group(2));
//		final FontConfiguration fc1 = stripe.getActualFontConfiguration();
//		final FontConfiguration fc2 = fc1.changeSize(size);
//		// final FontConfiguration fc2 = new AddStyle(style, null).apply(fc1);
//		stripe.setActualFontConfiguration(fc2);
		// stripe.analyzeAndAdd("AZERTY");
//		stripe.setActualFontConfiguration(fc1);
		return line.substring(m.group(1).length());
	}
}
