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

import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.HtmlColor;

public class SkinParamBackcolored extends SkinParamDelegator {

	final private HtmlColor backColorElement;
	final private HtmlColor backColorGeneral;
	final private boolean forceClickage;

	public SkinParamBackcolored(ISkinParam skinParam, HtmlColor backColorElement) {
		this(skinParam, backColorElement, null, false);
	}

	public SkinParamBackcolored(ISkinParam skinParam, HtmlColor backColorElement, boolean forceClickage) {
		this(skinParam, backColorElement, null, forceClickage);
	}

	public SkinParamBackcolored(ISkinParam skinParam, HtmlColor backColorElement, HtmlColor backColorGeneral) {
		this(skinParam, backColorElement, backColorGeneral, false);
	}

	public SkinParamBackcolored(ISkinParam skinParam, HtmlColor backColorElement, HtmlColor backColorGeneral,
			boolean forceClickage) {
		super(skinParam);
		this.forceClickage = forceClickage;
		this.backColorElement = backColorElement;
		this.backColorGeneral = backColorGeneral;
	}

	public HtmlColor getBackgroundColor() {
		if (backColorGeneral != null) {
			return backColorGeneral;
		}
		return super.getBackgroundColor();
	}

	public HtmlColor getHtmlColor(ColorParam param, Stereotype stereotype, boolean clickable) {
		if (param.isBackground() && backColorElement != null) {
			return backColorElement;
		}
		if (forceClickage) {
			final HtmlColor c1 = super.getHtmlColor(param, stereotype, true);
			if (c1 != null) {
				return c1;
			}
			// clickable = true;
		}
		return super.getHtmlColor(param, stereotype, clickable);
	}

}
