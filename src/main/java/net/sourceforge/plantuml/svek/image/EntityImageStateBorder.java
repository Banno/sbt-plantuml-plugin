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
package net.sourceforge.plantuml.svek.image;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.cucadiagram.EntityPosition;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Bibliotekon;
import net.sourceforge.plantuml.svek.Cluster;
import net.sourceforge.plantuml.svek.Shape;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UEllipse;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class EntityImageStateBorder extends AbstractEntityImage {

	public static final double RADIUS = 6;
	private final TextBlock desc;
	private final Cluster stateParent;
	private final EntityPosition entityPosition;
	private final Bibliotekon bibliotekon;

	public EntityImageStateBorder(ILeaf leaf, ISkinParam skinParam, Cluster stateParent, final Bibliotekon bibliotekon) {
		super(leaf, skinParam);
		this.bibliotekon = bibliotekon;

		this.entityPosition = leaf.getEntityPosition();
		if (entityPosition == EntityPosition.NORMAL) {
			throw new IllegalArgumentException();
		}
		this.stateParent = stateParent;
		final Stereotype stereotype = leaf.getStereotype();

		this.desc = TextBlockUtils.create(leaf.getDisplay(),
				new FontConfiguration(SkinParamUtils.getFont(getSkinParam(), FontParam.STATE, stereotype), SkinParamUtils.getFontColor(getSkinParam(), FontParam.STATE, stereotype),
						getSkinParam().getHyperlinkColor(), getSkinParam().useUnderlineForHyperlink()),
				HorizontalAlignment.CENTER, skinParam);
	}

	private boolean upPosition() {
		final Point2D clusterCenter = stateParent.getClusterPosition().getPointCenter();
		final Shape sh = bibliotekon.getShape(getEntity());
		return sh.getMinY() < clusterCenter.getY();
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		return new Dimension2DDouble(RADIUS * 2, RADIUS * 2);
	}

	public double getMaxWidthFromLabelForEntryExit(StringBounder stringBounder) {
		final Dimension2D dimDesc = desc.calculateDimension(stringBounder);
		return dimDesc.getWidth();
	}

	final public void drawU(UGraphic ug) {
		final Shadowable circle = new UEllipse(RADIUS * 2, RADIUS * 2);
		// if (getSkinParam().shadowing()) {
		// circle.setDeltaShadow(4);
		// }

		double y = 0;
		final Dimension2D dimDesc = desc.calculateDimension(ug.getStringBounder());
		final double x = 0 - (dimDesc.getWidth() - 2 * RADIUS) / 2;
		if (upPosition()) {
			y -= 2 * RADIUS + dimDesc.getHeight();
		} else {
			y += 2 * RADIUS;
		}
		desc.drawU(ug.apply(new UTranslate(x, y)));

		ug = ug.apply(new UStroke(1.5)).apply(
				new UChangeColor(SkinParamUtils.getColor(getSkinParam(), ColorParam.stateBorder, getStereo())));
		HtmlColor backcolor = getEntity().getSpecificBackColor();
		if (backcolor == null) {
			backcolor = SkinParamUtils.getColor(getSkinParam(), ColorParam.stateBackground, getStereo());
		}
		ug = ug.apply(new UChangeBackColor(backcolor));

		ug.draw(circle);
		if (entityPosition == EntityPosition.EXIT_POINT) {
			final double xc = 0 + RADIUS + .5;
			final double yc = 0 + RADIUS + .5;
			final double radius = RADIUS - .5;
			drawLine(ug, getPointOnCircle(xc, yc, Math.PI / 4, radius),
					getPointOnCircle(xc, yc, Math.PI + Math.PI / 4, radius));
			drawLine(ug, getPointOnCircle(xc, yc, -Math.PI / 4, radius),
					getPointOnCircle(xc, yc, Math.PI - Math.PI / 4, radius));
		}
	}

	private Point2D getPointOnCircle(double xc, double yc, double angle, double radius) {
		final double x = xc + radius * Math.cos(angle);
		final double y = yc + radius * Math.sin(angle);
		return new Point2D.Double(x, y);
	}

	static private void drawLine(UGraphic ug, Point2D p1, Point2D p2) {
		final double dx = p2.getX() - p1.getX();
		final double dy = p2.getY() - p1.getY();
		ug.apply(new UTranslate(p1.getX(), p1.getY())).draw(new ULine(dx, dy));

	}

	public ShapeType getShapeType() {
		return ShapeType.CIRCLE;
	}

	public int getShield() {
		return 0;
	}

}
