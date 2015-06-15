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
import java.awt.Polygon;
import java.awt.geom.Dimension2D;

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

class EntityImageNote extends AbstractEntityImage {

	final private TextBlock text;

	private final int xMargin = 10;
	private final int yMargin = 10;

	public EntityImageNote(IEntity entity) {
		super(entity);
		this.text = TextBlockUtils.create(entity.getDisplay(), new FontConfiguration(getFont14(), 
				HtmlColorUtils.BLACK, HtmlColorUtils.BLUE, true), HorizontalAlignment.CENTER, new SpriteContainerEmpty());
	}

	@Override
	public Dimension2D getDimension(StringBounder stringBounder) {
		final Dimension2D dim = text.calculateDimension(stringBounder);
		return Dimension2DDouble.delta(dim, 2 * xMargin, 2 * yMargin);
	}

	@Override
	public void draw(ColorMapper colorMapper, Graphics2D g2d) {
		final Dimension2D dimTotal = getDimension(StringBounderUtils.asStringBounder(g2d));

		final int width = (int) dimTotal.getWidth();
		final int height = (int) dimTotal.getHeight();

		final Polygon p = new Polygon();
		p.addPoint(0, 0);
		p.addPoint(width - xMargin, 0);
		p.addPoint(width, yMargin);
		p.addPoint(width, height);
		p.addPoint(0, height);

		g2d.setColor(colorMapper.getMappedColor(getYellowNote()));
		g2d.fill(p);

		g2d.setColor(colorMapper.getMappedColor(getRed()));
		g2d.draw(p);
		g2d.drawLine(width - xMargin, 0, width - xMargin, yMargin);
		g2d.drawLine(width - xMargin, yMargin, width, yMargin);

		g2d.setColor(Color.BLACK);
//		text.drawTOBEREMOVED(colorMapper, g2d, xMargin, yMargin);

	}
}
