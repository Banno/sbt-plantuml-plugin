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

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.Cluster;
import net.sourceforge.plantuml.svek.ClusterDecoration;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;

public class EntityImageEmptyPackage2 extends AbstractEntityImage {

	final private TextBlock desc;
	final private static int MARGIN = 10;
	final private HtmlColor specificBackColor;
	final private ISkinParam skinParam;
	final private Stereotype stereotype;

	public EntityImageEmptyPackage2(ILeaf entity, ISkinParam skinParam) {
		super(entity, skinParam);
		this.skinParam = skinParam;
		this.specificBackColor = entity.getSpecificBackColor();
		this.stereotype = entity.getStereotype();
		this.desc = TextBlockUtils.create(entity.getDisplay(),
				new FontConfiguration(SkinParamUtils.getFont(getSkinParam(), FontParam.PACKAGE, stereotype),
						SkinParamUtils.getFontColor(getSkinParam(), FontParam.PACKAGE, stereotype), getSkinParam()
								.getHyperlinkColor(), getSkinParam().useUnderlineForHyperlink()),
				HorizontalAlignment.CENTER, skinParam);
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dim = desc.calculateDimension(stringBounder);
		return Dimension2DDouble.delta(dim, MARGIN * 2, MARGIN * 2 + dim.getHeight() * 2);
	}

	private UStroke getStroke() {
		UStroke stroke = getSkinParam().getThickness(LineParam.packageBorder, getStereo());
		if (stroke == null) {
			stroke = new UStroke(2.0);
		}
		return stroke;
	}

	final public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = calculateDimension(stringBounder);

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();

		final HtmlColor stateBack = Cluster.getStateBackColor(specificBackColor, skinParam, stereotype);

		final ClusterDecoration decoration = new ClusterDecoration(getSkinParam().getPackageStyle(), null, desc,
				TextBlockUtils.empty(0, 0), stateBack, 0, 0, widthTotal, heightTotal, getStroke());

		decoration.drawU(ug, SkinParamUtils.getColor(getSkinParam(), ColorParam.packageBorder, getStereo()),
				getSkinParam().shadowing());
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	public int getShield() {
		return 0;
	}

}
