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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

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

class EntityImageComponent extends AbstractEntityImage {

	final private TextBlock name;
	private final float thickness = (float) 1.6;

	public EntityImageComponent(IEntity entity) {
		super(entity);
		this.name = TextBlockUtils.create(entity.getDisplay(), new FontConfiguration(getFont14(), 
				HtmlColorUtils.BLACK, HtmlColorUtils.BLUE, true), HorizontalAlignment.CENTER, new SpriteContainerEmpty());
	}

	@Override
	public Dimension2D getDimension(StringBounder stringBounder) {
		final Dimension2D nameDim = name.calculateDimension(stringBounder);
		return Dimension2DDouble.delta(nameDim, 20, 14);
	}

	private void drawRect(ColorMapper colorMapper, Graphics2D g2d, double x, double y, double width, double height) {
		g2d.setStroke(new BasicStroke(thickness));
		final Shape head = new Rectangle2D.Double(x, y, width, height);
		g2d.setColor(colorMapper.getMappedColor(getYellow()));
		g2d.fill(head);
		g2d.setColor(colorMapper.getMappedColor(getRed()));
		g2d.draw(head);

		g2d.setStroke(new BasicStroke());
	}

	@Override
	public void draw(ColorMapper colorMapper, Graphics2D g2d) {
		final Dimension2D dimTotal = getDimension(StringBounderUtils.asStringBounder(g2d));
		name.calculateDimension(StringBounderUtils.asStringBounder(g2d));

		drawRect(colorMapper, g2d, 6, 0, dimTotal.getWidth(), dimTotal.getHeight());
		drawRect(colorMapper, g2d, 0, 7, 12, 6);
		drawRect(colorMapper, g2d, 0, dimTotal.getHeight() - 7 - 6, 12, 6);

		g2d.setColor(Color.BLACK);
//		name.drawTOBEREMOVED(colorMapper, g2d, 6 + (dimTotal.getWidth() - nameDim.getWidth()) / 2,
//				(dimTotal.getHeight() - nameDim.getHeight()) / 2);
	}
}
