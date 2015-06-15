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
package net.sourceforge.plantuml.cucadiagram;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.creole.CreoleParser;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockLineBefore;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.TextBlockVertical2;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class BodyEnhanced extends AbstractTextBlock implements TextBlock {

	private TextBlock area2;
	private final FontConfiguration titleConfig;
	private final List<String> rawBody;
	private final FontParam fontParam;
	private final ISkinParam skinParam;
	private final boolean lineFirst;
	private final HorizontalAlignment align;
	private final boolean manageHorizontalLine;
	private final boolean manageModifier;
	private final List<Url> urls = new ArrayList<Url>();
	private final boolean manageUrl;

	public BodyEnhanced(List<String> rawBody, FontParam fontParam, ISkinParam skinParam, boolean manageModifier) {
		this.rawBody = new ArrayList<String>(rawBody);
		this.fontParam = fontParam;
		this.skinParam = skinParam;
		this.manageUrl = true;

		this.titleConfig = new FontConfiguration(skinParam.getFont(fontParam, null, false), new Rose().getFontColor(
				skinParam, fontParam), skinParam.getHyperlinkColor(), skinParam.useUnderlineForHyperlink());
		this.lineFirst = true;
		this.align = HorizontalAlignment.LEFT;
		this.manageHorizontalLine = true;
		this.manageModifier = manageModifier;
	}

	public BodyEnhanced(Display display, FontParam fontParam, ISkinParam skinParam, HorizontalAlignment align,
			Stereotype stereotype, boolean manageHorizontalLine, boolean manageModifier, boolean manageUrl) {
		this.manageUrl = manageUrl;
		this.rawBody = new ArrayList<String>();
		for (CharSequence s : display) {
			this.rawBody.add(s.toString());
		}
		this.fontParam = fontParam;
		this.skinParam = skinParam;

		this.titleConfig = new FontConfiguration(skinParam, fontParam, stereotype);
		this.lineFirst = false;
		this.align = align;
		this.manageHorizontalLine = manageHorizontalLine;
		this.manageModifier = manageModifier;

	}

	private TextBlock decorate(StringBounder stringBounder, TextBlock b, char separator, TextBlock title) {
		if (separator == 0) {
			return b;
		}
		if (title == null) {
			return new TextBlockLineBefore(TextBlockUtils.withMargin(b, 6, 4), separator);
		}
		final Dimension2D dimTitle = title.calculateDimension(stringBounder);
		final TextBlock raw = new TextBlockLineBefore(TextBlockUtils.withMargin(b, 6, 6, dimTitle.getHeight() / 2, 4),
				separator, title);
		return TextBlockUtils.withMargin(raw, 0, 0, dimTitle.getHeight() / 2, 0);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return getArea(stringBounder).calculateDimension(stringBounder);
	}

	private TextBlock getArea(StringBounder stringBounder) {
		if (area2 != null) {
			return area2;
		}
		urls.clear();
		final List<TextBlock> blocks = new ArrayList<TextBlock>();

		char separator = lineFirst ? '_' : 0;
		TextBlock title = null;
		List<Member> members = new ArrayList<Member>();
		for (ListIterator<String> it = rawBody.listIterator(); it.hasNext();) {
			final String s = it.next();
			if (manageHorizontalLine && isBlockSeparator(s)) {
				blocks.add(decorate(stringBounder, new MethodsOrFieldsArea(members, fontParam, skinParam, align),
						separator, title));
				separator = s.charAt(0);
				title = getTitle(s, skinParam);
				members = new ArrayList<Member>();
			} else if (CreoleParser.isTreeStart(s)) {
				if (members.size() > 0) {
					blocks.add(decorate(stringBounder, new MethodsOrFieldsArea(members, fontParam, skinParam, align),
							separator, title));
				}
				members = new ArrayList<Member>();
				final List<String> allTree = buildAllTree(s, it);
				final TextBlock bloc = TextBlockUtils.create(Display.create(allTree),
						fontParam.getFontConfiguration(skinParam), align, skinParam, false);
				blocks.add(bloc);
			} else {
				final Member m = new MemberImpl(s, StringUtils.isMethod(s), manageModifier, manageUrl);
				members.add(m);
				if (m.getUrl() != null) {
					urls.add(m.getUrl());
				}
			}
		}
		blocks.add(decorate(stringBounder, new MethodsOrFieldsArea(members, fontParam, skinParam, align), separator,
				title));

		if (blocks.size() == 1) {
			this.area2 = blocks.get(0);
		} else {
			this.area2 = new TextBlockVertical2(blocks, align);
		}

		return area2;
	}

	private static List<String> buildAllTree(String init, ListIterator<String> it) {
		final List<String> result = new ArrayList<String>();
		result.add(init);
		while (it.hasNext()) {
			final String s = it.next();
			if (CreoleParser.isTreeStart(StringUtils.trinNoTrace(s))) {
				result.add(s);
			} else {
				it.previous();
				return result;
			}

		}
		return result;
	}

	public static boolean isBlockSeparator(String s) {
		if (s.startsWith("--") && s.endsWith("--")) {
			return true;
		}
		if (s.startsWith("==") && s.endsWith("==")) {
			return true;
		}
		if (s.startsWith("..") && s.endsWith("..") && s.equals("...") == false) {
			return true;
		}
		if (s.startsWith("__") && s.endsWith("__")) {
			return true;
		}
		return false;
	}

	private TextBlock getTitle(String s, ISkinSimple spriteContainer) {
		if (s.length() <= 4) {
			return null;
		}
		s = StringUtils.trin(s.substring(2, s.length() - 2));
		return TextBlockUtils
				.create(Display.getWithNewlines(s), titleConfig, HorizontalAlignment.LEFT, spriteContainer);
	}

	public void drawU(UGraphic ug) {
		getArea(ug.getStringBounder()).drawU(ug);
	}

	public List<Url> getUrls() {
		return Collections.unmodifiableList(urls);
	}
}
