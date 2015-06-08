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
package net.sourceforge.plantuml.skin.bluemodern;

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.skin.AbstractTextualComponent;
import net.sourceforge.plantuml.skin.ArrowComponent;
import net.sourceforge.plantuml.skin.ArrowConfiguration;

public abstract class AbstractComponentBlueModernArrow extends AbstractTextualComponent implements ArrowComponent {

	private final int arrowDeltaX = 12;
	private final int arrowDeltaY = 10;

	private final int arrowDeltaX2 = 10;
	private final int arrowDeltaY2 = 5;
	private final ArrowConfiguration arrowConfiguration;
	private final HtmlColor foregroundColor;

	public AbstractComponentBlueModernArrow(HtmlColor foregroundColor, FontConfiguration font, Display stringsToDisplay, ArrowConfiguration arrowConfiguration, ISkinSimple spriteContainer) {
		super(stringsToDisplay, font, HorizontalAlignment.LEFT, 17, 17, 2,
				spriteContainer, 0, false, null, null);
		this.arrowConfiguration = arrowConfiguration;
		this.foregroundColor = foregroundColor;
	}

	protected final HtmlColor getForegroundColor() {
		return foregroundColor;
	}

	final protected int getArrowDeltaX() {
		return arrowDeltaX;
	}

	final protected int getArrowDeltaY() {
		return arrowDeltaY;
	}

	final protected int getArrowDeltaY2() {
		return arrowDeltaY2;
	}

	final protected int getArrowDeltaX2() {
		return arrowDeltaX2;
	}

	@Override
	public final double getPaddingY() {
		return 6;
	}

	final protected ArrowConfiguration getArrowConfiguration() {
		return arrowConfiguration;
	}

}
