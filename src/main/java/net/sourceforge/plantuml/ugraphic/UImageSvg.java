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
package net.sourceforge.plantuml.ugraphic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UImageSvg implements UShape {

	private final String svg;

	public UImageSvg(String svg) {
		if (svg.startsWith("<?xml")) {
			final int idx = svg.indexOf("<svg");
			svg = svg.substring(idx);
		}
		this.svg = svg;
	}

	public final String getSvg() {
		if (svg.startsWith("<svg")) {
			final int idx = svg.indexOf(">");
			return "<svg>" + svg.substring(idx + 1);
		}
		return svg;
	}

	private int getData(String name) {
		final Pattern p = Pattern.compile("(?i)" + name + "\\W+(\\d+)");
		final Matcher m = p.matcher(svg);
		if (m.find()) {
			final String s = m.group(1);
			return Integer.parseInt(s);
		}
		throw new IllegalStateException("Cannot find " + name);
	}

	public int getHeight() {
		return getData("height");
	}

	public int getWidth() {
		return getData("width");
	}

}
