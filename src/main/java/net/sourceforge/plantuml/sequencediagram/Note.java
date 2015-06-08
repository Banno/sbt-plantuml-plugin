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
package net.sourceforge.plantuml.sequencediagram;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamBackcolored;
import net.sourceforge.plantuml.SpecificBackcolorable;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.UrlBuilder;
import net.sourceforge.plantuml.UrlBuilder.ModeUrl;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;

public class Note extends AbstractEvent implements Event, SpecificBackcolorable {

	private final Participant p;
	private final Participant p2;

	private final Display strings;

	private final NotePosition position;
	private NoteStyle style = NoteStyle.NORMAL;

	private final Url url;

	public Note(Participant p, NotePosition position, Display strings) {
		this(p, null, position, strings);
	}

	public Note(Participant p, Participant p2, Display strings) {
		this(p, p2, NotePosition.OVER_SEVERAL, strings);
	}

	private Note(Participant p, Participant p2, NotePosition position, Display strings) {
		this.p = p;
		this.p2 = p2;
		this.position = position;
		if (strings != null && strings.size() > 0) {
			final UrlBuilder urlBuilder = new UrlBuilder(null, ModeUrl.STRICT);
			this.url = urlBuilder.getUrl(strings.get(0).toString());
		} else {
			this.url = null;
		}

		if (this.url == null) {
			this.strings = strings;
		} else {
			this.strings = strings.subList(1, strings.size());
		}
	}

	public Participant getParticipant() {
		return p;
	}

	public Participant getParticipant2() {
		return p2;
	}

	public Display getStrings() {
		return strings;
	}

	public NotePosition getPosition() {
		return position;
	}

	private HtmlColor specificBackcolor;

	public HtmlColor getSpecificBackColor() {
		return specificBackcolor;
	}

	public void setSpecificBackcolor(HtmlColor color) {
		this.specificBackcolor = color;
	}

	public boolean dealWith(Participant someone) {
		return p == someone || p2 == someone;
	}

	public Url getUrl() {
		return url;
	}

	public boolean hasUrl() {
		return url != null;
	}

	public final NoteStyle getStyle() {
		return style;
	}

	public final void setStyle(NoteStyle style) {
		this.style = style;
	}

	public SkinParamBackcolored getSkinParamBackcolored(ISkinParam skinParam) {
		return new SkinParamBackcolored(skinParam, getSpecificBackColor());
	}

}
