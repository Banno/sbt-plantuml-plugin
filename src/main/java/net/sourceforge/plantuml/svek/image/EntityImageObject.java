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
import net.sourceforge.plantuml.LineConfigurable;
import net.sourceforge.plantuml.LineParam;
import net.sourceforge.plantuml.SkinParamUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.creole.Stencil;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.EntityPortion;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.PortionShower;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockEmpty;
import net.sourceforge.plantuml.graphic.TextBlockLineBefore;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.svek.AbstractEntityImage;
import net.sourceforge.plantuml.svek.ShapeType;
import net.sourceforge.plantuml.ugraphic.PlacementStrategyY1Y2;
import net.sourceforge.plantuml.ugraphic.Shadowable;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UGraphicStencil;
import net.sourceforge.plantuml.ugraphic.ULayoutGroup;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class EntityImageObject extends AbstractEntityImage implements Stencil {

	final private TextBlock name;
	final private TextBlock stereo;
	final private TextBlock fields;
	final private Url url;
	final private double roundCorner;

	final private LineConfigurable lineConfig;

	public EntityImageObject(ILeaf entity, ISkinParam skinParam) {
		super(entity, skinParam);
		this.lineConfig = entity;
		final Stereotype stereotype = entity.getStereotype();
		this.roundCorner = skinParam.getRoundCorner();
		this.name = TextBlockUtils.withMargin(TextBlockUtils.create(entity.getDisplay(),
				new FontConfiguration(SkinParamUtils.getFont(getSkinParam(), FontParam.OBJECT, stereotype),
						SkinParamUtils.getFontColor(getSkinParam(), FontParam.OBJECT, stereotype), getSkinParam()
								.getHyperlinkColor(), getSkinParam().useUnderlineForHyperlink()),
				HorizontalAlignment.CENTER, skinParam), 2, 2);
		if (stereotype == null || stereotype.getLabel(false) == null) {
			this.stereo = null;
		} else {
			this.stereo = TextBlockUtils
					.create(Display.getWithNewlines(stereotype.getLabel(getSkinParam().useGuillemet())),
							new FontConfiguration(SkinParamUtils.getFont(getSkinParam(), FontParam.OBJECT_STEREOTYPE,
									stereotype), SkinParamUtils.getFontColor(getSkinParam(),
									FontParam.OBJECT_STEREOTYPE, stereotype), getSkinParam().getHyperlinkColor(),
									getSkinParam().useUnderlineForHyperlink()), HorizontalAlignment.CENTER, skinParam);
		}

		if (entity.getBodier().getFieldsToDisplay().size() == 0) {
			this.fields = new TextBlockLineBefore(new TextBlockEmpty(10, 16));
		} else {
			this.fields = entity.getBodier().getBody(FontParam.OBJECT_ATTRIBUTE, skinParam, false, true);
		}
		this.url = entity.getUrl99();

	}

	private int marginEmptyFieldsOrMethod = 13;

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		final Dimension2D dimTitle = getTitleDimension(stringBounder);
		final Dimension2D dimFields = fields.calculateDimension(stringBounder);
		final double width = Math.max(dimFields.getWidth(), dimTitle.getWidth() + 2 * xMarginCircle);

		final double height = getMethodOrFieldHeight(dimFields) + dimTitle.getHeight();
		return new Dimension2DDouble(width, height);
	}

	final public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = calculateDimension(stringBounder);
		final Dimension2D dimTitle = getTitleDimension(stringBounder);

		final double widthTotal = dimTotal.getWidth();
		final double heightTotal = dimTotal.getHeight();
		final Shadowable rect = new URectangle(widthTotal, heightTotal, roundCorner, roundCorner);
		if (getSkinParam().shadowing()) {
			rect.setDeltaShadow(4);
		}

		ug = ug.apply(new UChangeColor(SkinParamUtils.getColor(getSkinParam(), ColorParam.objectBorder, getStereo())));
		HtmlColor backcolor = getEntity().getSpecificBackColor();
		if (backcolor == null) {
			backcolor = SkinParamUtils.getColor(getSkinParam(), ColorParam.objectBackground, getStereo());
		}
		ug = ug.apply(new UChangeBackColor(backcolor));
		if (url != null) {
			ug.startUrl(url);
		}

		final UStroke stroke = getStroke();
		ug.apply(stroke).draw(rect);

		final ULayoutGroup header = new ULayoutGroup(new PlacementStrategyY1Y2(ug.getStringBounder()));
		if (stereo != null) {
			header.add(stereo);
		}
		header.add(name);
		header.drawU(ug, dimTotal.getWidth(), dimTitle.getHeight());

		final UGraphic ug2 = new UGraphicStencil(ug, this, stroke);
		fields.drawU(ug2.apply(new UTranslate(0, dimTitle.getHeight())));

		if (url != null) {
			ug.closeAction();
		}
	}

	private UStroke getStroke() {
		UStroke stroke = lineConfig.getSpecificLineStroke();
		if (stroke == null) {
			stroke = getSkinParam().getThickness(LineParam.objectBorder, getStereo());
		}
		if (stroke == null) {
			stroke = new UStroke(1.5);
		}
		return stroke;
	}

	private double getMethodOrFieldHeight(final Dimension2D dim) {
		final double fieldsHeight = dim.getHeight();
		if (fieldsHeight == 0) {
			return marginEmptyFieldsOrMethod;
		}
		return fieldsHeight;
	}

	private int xMarginCircle = 5;

	private Dimension2D getTitleDimension(StringBounder stringBounder) {
		return getNameAndSteretypeDimension(stringBounder);
	}

	private Dimension2D getNameAndSteretypeDimension(StringBounder stringBounder) {
		final Dimension2D nameDim = name.calculateDimension(stringBounder);
		final Dimension2D stereoDim = stereo == null ? new Dimension2DDouble(0, 0) : stereo
				.calculateDimension(stringBounder);
		final Dimension2D nameAndStereo = new Dimension2DDouble(Math.max(nameDim.getWidth(), stereoDim.getWidth()),
				nameDim.getHeight() + stereoDim.getHeight());
		return nameAndStereo;
	}

	public ShapeType getShapeType() {
		return ShapeType.RECTANGLE;
	}

	public int getShield() {
		return 0;
	}

	public double getStartingX(StringBounder stringBounder, double y) {
		return 0;
	}

	public double getEndingX(StringBounder stringBounder, double y) {
		return calculateDimension(stringBounder).getWidth();
	}

}
