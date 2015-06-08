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
package net.sourceforge.plantuml.ugraphic.g2d;

import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import net.sourceforge.plantuml.EnsureVisible;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorGradient;
import net.sourceforge.plantuml.graphic.HtmlColorSimple;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UDriver;
import net.sourceforge.plantuml.ugraphic.UParam;
import net.sourceforge.plantuml.ugraphic.UPattern;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UShape;

public class DriverRectangleG2d extends DriverShadowedG2d implements UDriver<Graphics2D> {

	private final double dpiFactor;
	private final EnsureVisible visible;

	public DriverRectangleG2d(double dpiFactor, EnsureVisible visible) {
		this.dpiFactor = dpiFactor;
		this.visible = visible;
	}

	public void draw(UShape ushape, double x, double y, ColorMapper mapper, UParam param, Graphics2D g2d) {
		g2d.setStroke(new BasicStroke((float) param.getStroke().getThickness()));
		final URectangle shape = (URectangle) ushape;
		final double rx = shape.getRx();
		final double ry = shape.getRy();
		final Shape rect;
		if (rx == 0 && ry == 0) {
			rect = new Rectangle2D.Double(x, y, shape.getWidth(), shape.getHeight());
		} else {
			rect = new RoundRectangle2D.Double(x, y, shape.getWidth(), shape.getHeight(), rx, ry);
		}

		visible.ensureVisible(x, y);
		visible.ensureVisible(x + shape.getWidth(), y + shape.getHeight());

		// Shadow
		if (shape.getDeltaShadow() != 0) {
			drawShadow(g2d, rect, shape.getDeltaShadow(), dpiFactor);
		}

		final HtmlColor back = param.getBackcolor();
		if (back instanceof HtmlColorGradient) {
			final GradientPaint paint = getPaintGradient(x, y, mapper, shape, back);
			g2d.setPaint(paint);
			g2d.fill(rect);

			if (param.getColor() != null && param.getColor() instanceof HtmlColorGradient == false) {
				g2d.setColor(mapper.getMappedColor(param.getColor()));
				DriverLineG2d.manageStroke(param, g2d);
				g2d.draw(rect);
			}

		} else {
			if (param.getBackcolor() != null) {
				g2d.setColor(mapper.getMappedColor(param.getBackcolor()));
				DriverLineG2d.manageStroke(param, g2d);
				managePattern(param, g2d);
				g2d.fill(rect);
			}
			if (param.getColor() != null && param.getColor().equals(param.getBackcolor()) == false) {
				g2d.setColor(mapper.getMappedColor(param.getColor()));
				DriverLineG2d.manageStroke(param, g2d);
				g2d.draw(rect);
			}
		}
	}

	private GradientPaint getPaintGradient(double x, double y, ColorMapper mapper, final URectangle shape,
			final HtmlColor back) {
		final HtmlColorGradient gr = (HtmlColorGradient) back;
		final char policy = gr.getPolicy();
		final GradientPaint paint;
		if (policy == '|') {
			paint = new GradientPaint((float) x, (float) (y + shape.getHeight()) / 2, mapper.getMappedColor(gr
					.getColor1()), (float) (x + shape.getWidth()), (float) (y + shape.getHeight()) / 2,
					mapper.getMappedColor(gr.getColor2()));
		} else if (policy == '\\') {
			paint = new GradientPaint((float) x, (float) (y + shape.getHeight()), mapper.getMappedColor(gr
					.getColor1()), (float) (x + shape.getWidth()), (float) y, mapper.getMappedColor(gr.getColor2()));
		} else if (policy == '-') {
			paint = new GradientPaint((float) (x + shape.getWidth()) / 2, (float) y, mapper.getMappedColor(gr
					.getColor1()), (float) (x + shape.getWidth()) / 2, (float) (y + shape.getHeight()),
					mapper.getMappedColor(gr.getColor2()));
		} else {
			// for /
			paint = new GradientPaint((float) x, (float) y, mapper.getMappedColor(gr.getColor1()),
					(float) (x + shape.getWidth()), (float) (y + shape.getHeight()), mapper.getMappedColor(gr
							.getColor2()));
		}
		return paint;
	}

	public static void managePattern(UParam param, Graphics2D g2d) {
		final UPattern pattern = param.getPattern();
		if (pattern == UPattern.VERTICAL_STRIPE) {
			final BufferedImage bi = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
			final Rectangle r = new Rectangle(0, 0, 4, 4);
			final int rgb = ((HtmlColorSimple) param.getBackcolor()).getColor999().getRGB();
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					if (i == 0 || i == 1) {
						bi.setRGB(i, j, rgb);
					}
				}
			}
			g2d.setPaint(new TexturePaint(bi, r));
		} else if (pattern == UPattern.HORIZONTAL_STRIPE) {
			final BufferedImage bi = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
			final Rectangle r = new Rectangle(0, 0, 4, 4);
			final int rgb = ((HtmlColorSimple) param.getBackcolor()).getColor999().getRGB();
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					if (j == 0 || j == 1) {
						bi.setRGB(i, j, rgb);
					}
				}
			}
			g2d.setPaint(new TexturePaint(bi, r));
		} else if (pattern == UPattern.SMALL_CIRCLE) {
			final BufferedImage bi = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
			final Rectangle r = new Rectangle(0, 0, 4, 4);
			final int rgb = ((HtmlColorSimple) param.getBackcolor()).getColor999().getRGB();
			bi.setRGB(0, 1, rgb);
			bi.setRGB(1, 0, rgb);
			bi.setRGB(1, 1, rgb);
			bi.setRGB(1, 2, rgb);
			bi.setRGB(2, 1, rgb);
			g2d.setPaint(new TexturePaint(bi, r));
		}
	}

}
