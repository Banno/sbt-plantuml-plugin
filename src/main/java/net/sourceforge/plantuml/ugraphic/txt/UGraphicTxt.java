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
package net.sourceforge.plantuml.ugraphic.txt;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.asciiart.TextStringBounder;
import net.sourceforge.plantuml.asciiart.TranslatedCharArea;
import net.sourceforge.plantuml.asciiart.UmlCharArea;
import net.sourceforge.plantuml.asciiart.UmlCharAreaImpl;
import net.sourceforge.plantuml.graphic.FontStyle;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.ugraphic.AbstractCommonUGraphic;
import net.sourceforge.plantuml.ugraphic.ClipContainer;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.UImage;
import net.sourceforge.plantuml.ugraphic.UShape;
import net.sourceforge.plantuml.ugraphic.UText;

public class UGraphicTxt extends AbstractCommonUGraphic implements ClipContainer {

	private final UmlCharArea charArea;

	@Override
	protected AbstractCommonUGraphic copyUGraphic() {
		return new UGraphicTxt(this);
	}

	private UGraphicTxt(UGraphicTxt other) {
		super(other);
		this.charArea = other.charArea;
	}

	public UGraphicTxt() {
		super(new ColorMapperIdentity());
		this.charArea = new UmlCharAreaImpl();
	}

	public StringBounder getStringBounder() {
		return new TextStringBounder();
	}

	public void draw(UShape shape) {
		// final UClip clip = getClip();
		if (shape instanceof UText) {
			final UText txt = (UText) shape;
			final int y = ((int) (getTranslateY() + txt.getDescent())) / 10;
			if (txt.getFontConfiguration().containsStyle(FontStyle.WAVE)) {
				charArea.drawHLine('^', y, getDx(), txt.getText().length());
				charArea.drawStringLR(txt.getText(), 0, y + 1);
			} else {
				charArea.drawStringLR(txt.getText(), 0, y);
			}
			return;
		} else if (shape instanceof UImage) {
			return;
		}
		throw new UnsupportedOperationException("cl=" + shape.getClass());
	}

	public final UmlCharArea getCharArea() {
		return new TranslatedCharArea(charArea, getDx(), getDy());
	}

	private int getDy() {
		return (int) getTranslateY();
	}

	private int getDx() {
		return (int) getTranslateX();
	}

	public void startUrl(Url url) {
	}

	public void closeAction() {
	}

	public Dimension2D getDimension() {
		return new Dimension2DDouble(0, 0);
	}

}
