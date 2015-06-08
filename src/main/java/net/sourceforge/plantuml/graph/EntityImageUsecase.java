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
package net.sourceforge.plantuml.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.QuadCurve2D;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.SpriteContainerEmpty;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.StringBounderUtils;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.ugraphic.ColorMapper;

class EntityImageUsecase extends AbstractEntityImage {

	final private TextBlock name;

	public EntityImageUsecase(IEntity entity) {
		super(entity);
		this.name = TextBlockUtils.create(entity.getDisplay(), new FontConfiguration(getFont14(), 
				HtmlColorUtils.BLACK, HtmlColorUtils.BLUE, true), HorizontalAlignment.CENTER, new SpriteContainerEmpty());
	}

	@Override
	public Dimension2D getDimension(StringBounder stringBounder) {
		final Dimension2D nameDim = name.calculateDimension(stringBounder);
		// final double eps = Math.sqrt(nameDim.getWidth() /
		// nameDim.getHeight());
		// final double diag = Math.sqrt(nameDim.getWidth() * nameDim.getWidth()
		// + nameDim.getHeight()
		// * nameDim.getHeight());
		// return new Dimension2DDouble(diag * eps, diag / eps);
		final double eps = 1.7;
		return new Dimension2DDouble(nameDim.getWidth() * eps, nameDim.getHeight() * eps);
	}

	@Override
	public void draw(ColorMapper colorMapper, Graphics2D g2d) {
		final Dimension2D dimTotal = getDimension(StringBounderUtils.asStringBounder(g2d));

		// Shape ellipse = new Ellipse2D.Double(0, 0, dimTotal.getWidth(),
		// dimTotal.getHeight());
		final GeneralPath ellipse = new GeneralPath();
		final double h = dimTotal.getHeight();
		final double w = dimTotal.getWidth();
		ellipse.append(new QuadCurve2D.Double(0, h / 2, 0, 0, w / 2, 0), true);
		ellipse.append(new QuadCurve2D.Double(w / 2, 0, w, 0, w, h / 2), true);
		ellipse.append(new QuadCurve2D.Double(w, h / 2, w, h, w / 2, h), true);
		ellipse.append(new QuadCurve2D.Double(w / 2, h, 0, h, 0, h / 2), true);
		g2d.setColor(colorMapper.getMappedColor(getYellow()));
		g2d.fill(ellipse);

		g2d.setColor(colorMapper.getMappedColor(getRed()));
		g2d.draw(ellipse);

//		final Dimension2D nameDim = name.calculateDimension(StringBounderUtils.asStringBounder(g2d));
//		final double posx = (w - nameDim.getWidth()) / 2;
//		final double posy = (h - nameDim.getHeight()) / 2;
		// final Shape rect = new Rectangle2D.Double(posx, posy, nameDim.getWidth(), nameDim.getHeight());
		// g2d.draw(rect);

		g2d.setColor(Color.BLACK);
		// name.drawTOBEREMOVED(colorMapper, g2d, posx, posy);
	}
}
