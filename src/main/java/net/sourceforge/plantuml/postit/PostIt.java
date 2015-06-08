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
package net.sourceforge.plantuml.postit;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorSetSimple;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.SimpleContext2D;
import net.sourceforge.plantuml.skin.rose.ComponentRoseNote;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;

public class PostIt {

	private final String id;
	private final Display text;

	private Dimension2D minimunDimension;

	public PostIt(String id, Display text) {
		this.id = id;
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public Display getText() {
		return text;
	}

	public Dimension2D getMinimunDimension() {
		return minimunDimension;
	}

	public void setMinimunDimension(Dimension2D minimunDimension) {
		this.minimunDimension = minimunDimension;
	}

	public Dimension2D getDimension(StringBounder stringBounder) {
		double width = getComponent().getPreferredWidth(stringBounder);
		double height = getComponent().getPreferredHeight(stringBounder);

		if (minimunDimension != null && width < minimunDimension.getWidth()) {
			width = minimunDimension.getWidth();
		}
		if (minimunDimension != null && height < minimunDimension.getHeight()) {
			height = minimunDimension.getHeight();
		}

		return new Dimension2DDouble(width, height);
	}

	public void drawU(UGraphic ug) {

		final Component note = getComponent();
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimensionToUse = getDimension(stringBounder);

		note.drawU(ug, new Area(dimensionToUse), new SimpleContext2D(false));

	}

	private Component getComponent() {
		final HtmlColor noteBackgroundColor = new HtmlColorSetSimple().getColorIfValid("#FBFB77");
		final HtmlColor borderColor = HtmlColorUtils.MY_RED;

		final SkinParam param = SkinParam.noShadowing();
		final UFont fontNote = param.getFont(FontParam.NOTE, null, false);
		final ComponentRoseNote note = new ComponentRoseNote(
				new SymbolContext(noteBackgroundColor, borderColor).withStroke(new UStroke()), fontNote.toFont2(
						HtmlColorUtils.BLACK, true, HtmlColorUtils.BLUE), text, 0, 0, new SpriteContainerEmpty());
		return note;
	}
}
