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
package net.sourceforge.plantuml.graphic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.plantuml.command.regex.MyPattern;
import net.sourceforge.plantuml.StringUtils;

public class Splitter {

	static final String endFontPattern = "\\</font\\>|\\</color\\>|\\</size\\>|\\</text\\>";
	static final String endSupSub = "\\</sup\\>|\\</sub\\>";
	public static final String fontPattern = "\\<font(\\s+size[%s]*=[%s]*[%g]?\\d+[%g]?|[%s]+color[%s]*=\\s*[%g]?(#[0-9a-fA-F]{6}|\\w+)[%g]?)+[%s]*\\>";
	public static final String fontColorPattern2 = "\\<color[\\s:]+(#[0-9a-fA-F]{6}|#?\\w+)[%s]*\\>";
	public static final String fontSizePattern2 = "\\<size[\\s:]+(\\d+)[%s]*\\>";
	static final String fontSup = "\\<sup\\>";
	static final String fontSub = "\\<sub\\>";
	static final String imgPattern = "\\<img\\s+(src[%s]*=[%s]*[%q%g]?[^\\s%g>]+[%q%g]?[%s]*|vspace\\s*=\\s*[%q%g]?\\d+[%q%g]?\\s*|valign[%s]*=[%s]*[%q%g]?(top|middle|bottom)[%q%g]?[%s]*)+\\>";
	public static final String imgPatternNoSrcColon = "\\<img[\\s:]+([^>]+)/?\\>";
	public static final String fontFamilyPattern = "\\<font[\\s:]+([^>]+)/?\\>";
	public static final String svgAttributePattern = "\\<text[\\s:]+([^>]+)/?\\>";
	public static final String openiconPattern = "\\<&([-\\w]+)\\>";
	public static final String spritePattern = "\\<\\$[\\p{L}0-9_]+\\>";
	public static final String spritePattern2 = "\\<\\$([\\p{L}0-9_]+)\\>";
	static final String htmlTag;

	static final String linkPattern = "\\[\\[([^\\[\\]]+)\\]\\]";

	private static final Pattern tagOrText;

	static {
		final StringBuilder sb = new StringBuilder("(?i)");

		for (FontStyle style : EnumSet.allOf(FontStyle.class)) {
			if (style == FontStyle.PLAIN) {
				continue;
			}
			sb.append(style.getActivationPattern());
			sb.append('|');
			sb.append(style.getDeactivationPattern());
			sb.append('|');
		}
		sb.append(fontPattern);
		sb.append('|');
		sb.append(fontColorPattern2);
		sb.append('|');
		sb.append(fontSizePattern2);
		sb.append('|');
		sb.append(fontSup);
		sb.append('|');
		sb.append(fontSub);
		sb.append('|');
		sb.append(endFontPattern);
		sb.append('|');
		sb.append(endSupSub);
		sb.append('|');
		sb.append(imgPattern);
		sb.append('|');
		sb.append(imgPatternNoSrcColon);
		sb.append('|');
		sb.append(fontFamilyPattern);
		sb.append('|');
		sb.append(spritePattern);
		sb.append('|');
		sb.append(linkPattern);
		sb.append('|');
		sb.append(svgAttributePattern);

		htmlTag = sb.toString();
		tagOrText = MyPattern.cmpile(htmlTag + "|.+?(?=" + htmlTag + ")|.+$", Pattern.CASE_INSENSITIVE);
	}

	private final List<String> splitted = new ArrayList<String>();

	public Splitter(String s) {
		final Matcher matcher = tagOrText.matcher(s);
		while (matcher.find()) {
			String part = matcher.group(0);
			part = StringUtils.showComparatorCharacters(part);
			splitted.add(part);
		}
	}

	List<String> getSplittedInternal() {
		return splitted;
	}

	public List<HtmlCommand> getHtmlCommands(boolean newLineAlone) {
		final HtmlCommandFactory factory = new HtmlCommandFactory();
		final List<HtmlCommand> result = new ArrayList<HtmlCommand>();
		for (String s : getSplittedInternal()) {
			final HtmlCommand cmd = factory.getHtmlCommand(s);
			if (newLineAlone && cmd instanceof Text) {
				result.addAll(splitText((Text) cmd));
			} else {
				result.add(cmd);
			}
		}
		return Collections.unmodifiableList(result);
	}

	private Collection<Text> splitText(Text cmd) {
		String s = cmd.getText();
		final Collection<Text> result = new ArrayList<Text>();
		while (true) {
			final int x = s.indexOf(Text.NEWLINE.getText());
			if (x == -1) {
				result.add(new Text(s));
				return result;
			}
			if (x > 0) {
				result.add(new Text(s.substring(0, x)));
			}
			result.add(Text.NEWLINE);
			s = s.substring(x + 2);
		}
	}
}
