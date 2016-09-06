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
package net.sourceforge.plantuml.cucadiagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.plantuml.CharSequence2;
import net.sourceforge.plantuml.CharSequence2Impl;
import net.sourceforge.plantuml.EmbededDiagram;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.LineLocationImpl;
import net.sourceforge.plantuml.SpriteContainer;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlBuilder.ModeUrl;
import net.sourceforge.plantuml.command.regex.Matcher2;
import net.sourceforge.plantuml.command.regex.Pattern2;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.CreoleParser;
import net.sourceforge.plantuml.creole.Sheet;
import net.sourceforge.plantuml.creole.SheetBlock1;
import net.sourceforge.plantuml.creole.SheetBlock2;
import net.sourceforge.plantuml.graphic.CircledCharacter;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockSimple;
import net.sourceforge.plantuml.graphic.TextBlockSprited;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.VerticalAlignment;
import net.sourceforge.plantuml.sequencediagram.MessageNumber;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.sprite.Sprite;

public class Display implements Iterable<CharSequence> {

	private final List<CharSequence> display;
	private final HorizontalAlignment naturalHorizontalAlignment;
	private final boolean isNull;
	private final CreoleMode defaultCreoleMode;

	// public void setDefaultCreoleMode(CreoleMode defaultCreoleMode) {
	// this.defaultCreoleMode = defaultCreoleMode;
	// }

	public Display removeUrlHiddenNewLineUrl() {
		final String full = UrlBuilder.purgeUrl(asStringWithHiddenNewLine());
		return new Display(StringUtils.splitHiddenNewLine(full), this.naturalHorizontalAlignment, this.isNull,
				this.defaultCreoleMode);
	}

	public final static Display NULL = new Display(null, null, true, CreoleMode.FULL);

	public boolean isWhite() {
		return display.size() == 0 || (display.size() == 1 && display.get(0).toString().matches("\\s*"));
	}

	public static Display empty() {
		return new Display((HorizontalAlignment) null, false, CreoleMode.FULL);
	}

	public static Display create(CharSequence... s) {
		return create(Arrays.asList(s));
	}

	public static Display create(Collection<? extends CharSequence> other) {
		return new Display(other, null, false, CreoleMode.FULL);
	}

	public static Display getWithNewlines(Code s) {
		return getWithNewlines(s.getFullName());
	}

	public static Display getWithNewlines(String s) {
		if (s == null) {
			// Thread.dumpStack();
			return NULL;
		}
		final List<String> result = new ArrayList<String>();
		final StringBuilder current = new StringBuilder();
		HorizontalAlignment naturalHorizontalAlignment = null;
		for (int i = 0; i < s.length(); i++) {
			final char c = s.charAt(i);
			if (c == '\\' && i < s.length() - 1) {
				final char c2 = s.charAt(i + 1);
				i++;
				if (c2 == 'n' || c2 == 'r' || c2 == 'l') {
					if (c2 == 'r') {
						naturalHorizontalAlignment = HorizontalAlignment.RIGHT;
					} else if (c2 == 'l') {
						naturalHorizontalAlignment = HorizontalAlignment.LEFT;
					}
					result.add(current.toString());
					current.setLength(0);
				} else if (c2 == 't') {
					current.append('\t');
				} else if (c2 == '\\') {
					current.append(c2);
				} else {
					current.append(c);
					current.append(c2);
				}
			} else if (c == StringUtils.hiddenNewLine()) {
				result.add(current.toString());
				current.setLength(0);
			} else {
				current.append(c);
			}
		}
		result.add(current.toString());
		return new Display(result, naturalHorizontalAlignment, false, CreoleMode.FULL);
	}

	private Display(Display other, CreoleMode mode) {
		this(other.naturalHorizontalAlignment, other.isNull, mode);
		this.display.addAll(other.display);
	}

	private Display(HorizontalAlignment naturalHorizontalAlignment, boolean isNull, CreoleMode defaultCreoleMode) {
		this.defaultCreoleMode = defaultCreoleMode;
		this.isNull = isNull;
		this.display = isNull ? null : new ArrayList<CharSequence>();
		this.naturalHorizontalAlignment = isNull ? null : naturalHorizontalAlignment;
	}

	private Display(Collection<? extends CharSequence> other, HorizontalAlignment naturalHorizontalAlignment,
			boolean isNull, CreoleMode defaultCreoleMode) {
		this(naturalHorizontalAlignment, isNull, defaultCreoleMode);
		if (isNull == false) {
			this.display.addAll(manageEmbededDiagrams2(other));
		}
	}

	private static List<CharSequence> manageEmbededDiagrams2(final Collection<? extends CharSequence> strings) {
		final List<CharSequence> result = new ArrayList<CharSequence>();
		final Iterator<? extends CharSequence> it = strings.iterator();
		while (it.hasNext()) {
			CharSequence s = it.next();
			if (s != null && StringUtils.trin(s.toString()).equals("{{")) {
				final List<CharSequence> other = new ArrayList<CharSequence>();
				other.add("@startuml");
				while (it.hasNext()) {
					final CharSequence s2 = it.next();
					if (s2 != null && StringUtils.trin(s2.toString()).equals("}}")) {
						break;
					}
					other.add(s2);
				}
				other.add("@enduml");
				s = new EmbededDiagram(Display.create(other));
			}
			result.add(s);
		}
		return result;
	}

	public Display underlined() {
		final List<CharSequence> result = new ArrayList<CharSequence>();
		for (CharSequence line : display) {
			result.add("<u>" + line);
		}
		return new Display(result, this.naturalHorizontalAlignment, this.isNull, this.defaultCreoleMode);
	}

	public Display withCreoleMode(CreoleMode mode) {
		if (isNull) {
			throw new IllegalArgumentException();
		}
		return new Display(this, mode);
	}

	public String asStringWithHiddenNewLine() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < display.size(); i++) {
			sb.append(display.get(i));
			if (i < display.size() - 1) {
				sb.append(StringUtils.hiddenNewLine());
			}
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		if (isNull) {
			return "NULL";
		}
		return display.toString();
	}

	@Override
	public int hashCode() {
		return display.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		return this.display.equals(((Display) other).display);
	}

	public Display addAll(Display other) {
		final Display result = new Display(this, this.defaultCreoleMode);
		result.display.addAll(other.display);
		return result;
	}

	public Display addFirst(CharSequence s) {
		final Display result = new Display(this, this.defaultCreoleMode);
		result.display.add(0, s);
		return result;
	}

	public Display add(CharSequence s) {
		final Display result = new Display(this, this.defaultCreoleMode);
		result.display.add(s);
		return result;
	}

	public int size() {
		if (isNull) {
			return 0;
		}
		return display.size();
	}

	public CharSequence get(int i) {
		return display.get(i);
	}

	public Iterator<CharSequence> iterator() {
		return Collections.unmodifiableList(display).iterator();
	}

	public Display subList(int i, int size) {
		return new Display(display.subList(i, size), this.naturalHorizontalAlignment, this.isNull,
				this.defaultCreoleMode);
	}

	public List<? extends CharSequence> as() {
		return Collections.unmodifiableList(display);
	}

	public List<CharSequence2> as2() {
		final List<CharSequence2> result = new ArrayList<CharSequence2>();
		LineLocationImpl location = new LineLocationImpl("inner", null);
		for (CharSequence cs : display) {
			location = location.oneLineRead();
			result.add(new CharSequence2Impl(cs, location));
		}
		return Collections.unmodifiableList(result);
	}

	public Url initUrl() {
		if (this.size() == 0) {
			return null;
		}
		final UrlBuilder urlBuilder = new UrlBuilder(null, ModeUrl.AT_START);
		return urlBuilder.getUrl(StringUtils.trin(this.get(0).toString()));
	}

	public Display removeHeadingUrl(Url url) {
		if (url == null) {
			return this;
		}
		final Display result = new Display(this.naturalHorizontalAlignment, this.isNull, this.defaultCreoleMode);
		result.display.add(UrlBuilder.purgeUrl(this.get(0).toString()));
		result.display.addAll(this.subList(1, this.size()).display);
		if (result.isWhite() && url.getLabel() != null) {
			return Display.getWithNewlines(url.getLabel());
		}
		return result;
	}

	public boolean hasUrl() {
		final UrlBuilder urlBuilder = new UrlBuilder(null, ModeUrl.ANYWHERE);
		for (CharSequence s : this) {
			if (urlBuilder.getUrl(s.toString()) != null) {
				return true;
			}
		}
		return false;
	}

	public HorizontalAlignment getNaturalHorizontalAlignment() {
		return naturalHorizontalAlignment;
	}

	public List<Display> splitMultiline(Pattern2 separator) {
		final List<Display> result = new ArrayList<Display>();
		Display pending = new Display(this.naturalHorizontalAlignment, this.isNull, this.defaultCreoleMode);
		result.add(pending);
		for (CharSequence line : display) {
			final Matcher2 m = separator.matcher(line);
			if (m.find()) {
				final CharSequence s1 = line.subSequence(0, m.start());
				pending.display.add(s1);
				final CharSequence s2 = line.subSequence(m.end(), line.length());
				pending = new Display(this.naturalHorizontalAlignment, this.isNull, this.defaultCreoleMode);
				result.add(pending);
				pending.display.add(s2);
			} else {
				pending.display.add(line);
			}
		}
		return Collections.unmodifiableList(result);
	}

	// ------

	public static boolean isNull(Display display) {
		// if (display == null) {
		// throw new IllegalArgumentException();
		// }
		return display == null || display.isNull;
	}

	public TextBlock create(FontConfiguration fontConfiguration, HorizontalAlignment horizontalAlignment,
			ISkinSimple spriteContainer) {
		return create(fontConfiguration, horizontalAlignment, spriteContainer, CreoleMode.FULL);
	}

	public TextBlock createWithNiceCreoleMode(FontConfiguration fontConfiguration,
			HorizontalAlignment horizontalAlignment, ISkinSimple spriteContainer) {
		return create(fontConfiguration, horizontalAlignment, spriteContainer, defaultCreoleMode);
	}

	public TextBlock create(FontConfiguration fontConfiguration, HorizontalAlignment horizontalAlignment,
			ISkinSimple spriteContainer, CreoleMode modeSimpleLine) {
		return create(fontConfiguration, horizontalAlignment, spriteContainer, 0, modeSimpleLine, null, null);
	}

	public TextBlock create(FontConfiguration fontConfiguration, HorizontalAlignment horizontalAlignment,
			ISkinSimple spriteContainer, double maxMessageSize, CreoleMode modeSimpleLine, UFont fontForStereotype,
			HtmlColor htmlColorForStereotype) {
		if (getNaturalHorizontalAlignment() != null) {
			horizontalAlignment = getNaturalHorizontalAlignment();
		}
		if (size() > 0) {
			if (get(0) instanceof Stereotype) {
				return createStereotype(fontConfiguration, horizontalAlignment, spriteContainer, 0, fontForStereotype,
						htmlColorForStereotype);
			}
			if (get(size() - 1) instanceof Stereotype) {
				return createStereotype(fontConfiguration, horizontalAlignment, spriteContainer, size() - 1,
						fontForStereotype, htmlColorForStereotype);
			}
			if (get(0) instanceof MessageNumber) {
				return createMessageNumber(fontConfiguration, horizontalAlignment, spriteContainer, maxMessageSize);
			}
		}

		return getCreole(fontConfiguration, horizontalAlignment, spriteContainer, maxMessageSize, modeSimpleLine);
	}

	private TextBlock getCreole(FontConfiguration fontConfiguration, HorizontalAlignment horizontalAlignment,
			ISkinSimple spriteContainer, double maxMessageSize, CreoleMode modeSimpleLine) {
		final Sheet sheet = new CreoleParser(fontConfiguration, horizontalAlignment, spriteContainer, modeSimpleLine)
				.createSheet(this);
		final SheetBlock1 sheetBlock1 = new SheetBlock1(sheet, maxMessageSize, spriteContainer == null ? 0
				: spriteContainer.getPadding());
		return new SheetBlock2(sheetBlock1, sheetBlock1, new UStroke(1.5));
	}

	private TextBlock createMessageNumber(FontConfiguration fontConfiguration, HorizontalAlignment horizontalAlignment,
			ISkinSimple spriteContainer, double maxMessageSize) {
		TextBlock tb1 = subList(0, 1).getCreole(fontConfiguration, horizontalAlignment, spriteContainer,
				maxMessageSize, CreoleMode.FULL);
		tb1 = TextBlockUtils.withMargin(tb1, 0, 4, 0, 0);
		final TextBlock tb2 = subList(1, size()).getCreole(fontConfiguration, horizontalAlignment, spriteContainer,
				maxMessageSize, CreoleMode.FULL);
		return TextBlockUtils.mergeLR(tb1, tb2, VerticalAlignment.CENTER);

	}

	private TextBlock createStereotype(FontConfiguration fontConfiguration, HorizontalAlignment horizontalAlignment,
			SpriteContainer spriteContainer, int position, UFont fontForStereotype, HtmlColor htmlColorForStereotype) {
		final Stereotype stereotype = (Stereotype) get(position);
		TextBlock circledCharacter = null;
		if (stereotype.isSpotted()) {
			circledCharacter = new CircledCharacter(stereotype.getCharacter(), stereotype.getRadius(),
					stereotype.getCircledFont(), stereotype.getHtmlColor(), null, fontConfiguration.getColor());
		} else if (stereotype.getSprite() != null) {
			final Sprite tmp = spriteContainer.getSprite(stereotype.getSprite());
			if (tmp != null) {
				circledCharacter = tmp.asTextBlock(stereotype.getHtmlColor(), 1);
			}
		}
		if (circledCharacter != null) {
			if (stereotype.getLabel(false) == null) {
				return new TextBlockSprited(circledCharacter, this.subList(1, this.size()), fontConfiguration,
						horizontalAlignment, spriteContainer);
			}
			return new TextBlockSprited(circledCharacter, this, fontConfiguration, horizontalAlignment, spriteContainer);
		}
		return new TextBlockSimple(this, fontConfiguration, horizontalAlignment, spriteContainer, 0, fontForStereotype,
				htmlColorForStereotype);
	}

}
