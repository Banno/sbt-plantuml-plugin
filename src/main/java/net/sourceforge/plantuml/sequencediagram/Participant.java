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

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamBackcolored;
import net.sourceforge.plantuml.SpecificBackcolorable;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.HtmlColor;

public class Participant implements SpecificBackcolorable {

	private final String code;
	private Display display;
	private final ParticipantType type;

	private int initialLife = 0;

	private Stereotype stereotype;

	public Participant(ParticipantType type, String code, Display display) {
		if (type == null) {
			throw new IllegalArgumentException();
		}
		if (code == null || code.length() == 0) {
			throw new IllegalArgumentException();
		}
		if (display == null || display.size() == 0) {
			throw new IllegalArgumentException();
		}
		this.code = code;
		this.type = type;
		this.display = display;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return getCode();
	}

	public Display getDisplay(boolean underlined) {
		if (underlined) {
			return display.underlined();
		}
		return display;
	}

	public ParticipantType getType() {
		return type;
	}

	public final void setStereotype(Stereotype stereotype, boolean stereotypePositionTop) {
		// if (type == ParticipantType.ACTOR) {
		// return;
		// }
		if (this.stereotype != null) {
			throw new IllegalStateException();
		}
		if (stereotype == null) {
			throw new IllegalArgumentException();
		}
		this.stereotype = stereotype;
		if (stereotypePositionTop) {
			display = display.addFirst(stereotype);
		} else {
			display = display.add(stereotype);
		}
	}

	public final int getInitialLife() {
		return initialLife;
	}

	private HtmlColor liveBackcolor;

	public final void incInitialLife(HtmlColor backcolor) {
		initialLife++;
		this.liveBackcolor = backcolor;
	}

	public HtmlColor getLiveSpecificBackColor() {
		return liveBackcolor;
	}

	private HtmlColor specificBackcolor;

	public HtmlColor getSpecificBackColor() {
		return specificBackcolor;
	}

	public void setSpecificBackcolor(HtmlColor color) {
		this.specificBackcolor = color;
	}

	private Url url;

	public final Url getUrl() {
		return url;
	}

	public final void setUrl(Url url) {
		this.url = url;
	}

	public final Stereotype getStereotype() {
		return stereotype;
	}

	public ColorParam getBackgroundColorParam() {
		return type.getBackgroundColorParam();
	}

	public SkinParamBackcolored getSkinParamBackcolored(ISkinParam skinParam) {
		HtmlColor specificBackColor = getSpecificBackColor();
		final boolean clickable = getUrl() != null;
		final HtmlColor stereoBackColor = skinParam.getHtmlColor(getBackgroundColorParam(), getStereotype(), clickable);
		if (stereoBackColor != null && specificBackColor == null) {
			specificBackColor = stereoBackColor;
		}
		return new SkinParamBackcolored(skinParam, specificBackColor, clickable);
	}

}
