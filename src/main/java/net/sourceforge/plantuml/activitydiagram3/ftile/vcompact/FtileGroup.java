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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact;

import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.ftile.AbstractFtile;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileMarged;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.SymbolContext;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.utils.MathUtils;

public class FtileGroup extends AbstractFtile {

	private final double diffYY2 = 20;
	private final Ftile inner;
	private final TextBlock name;
	private final TextBlock headerNote;
	private final HtmlColor color;
	private final HtmlColor backColor;
	private final HtmlColor titleColor;

	public FtileGroup(Ftile inner, Display title, Display displayNote, HtmlColor color, HtmlColor backColor,
			HtmlColor titleColor, ISkinParam skinParam) {
		super(inner.shadowing());
		this.backColor = backColor == null ? HtmlColorUtils.WHITE : backColor;
		this.inner = new FtileMarged(inner, 10);
		this.color = color;
		this.titleColor = titleColor;
		final UFont font = new UFont("Serif", Font.PLAIN, 14);
		final FontConfiguration fc = new FontConfiguration(font, HtmlColorUtils.BLACK, skinParam.getHyperlinkColor(),
				skinParam.useUnderlineForHyperlink());
		if (title == null) {
			this.name = TextBlockUtils.empty(0, 0);
		} else {
			this.name = TextBlockUtils.create(title, fc, HorizontalAlignment.LEFT, skinParam);
		}
		if (displayNote == null) {
			this.headerNote = TextBlockUtils.empty(0, 0);
		} else {
			this.headerNote = new FloatingNote(displayNote, skinParam);
		}
	}
	
	@Override
	public LinkRendering getInLinkRendering() {
		return inner.getInLinkRendering();
	}

	public Set<Swimlane> getSwimlanes() {
		return inner.getSwimlanes();
	}

	public Swimlane getSwimlaneIn() {
		return inner.getSwimlaneIn();
	}

	public Swimlane getSwimlaneOut() {
		return inner.getSwimlaneOut();
	}

	private double diffHeightTitle(StringBounder stringBounder) {
		final Dimension2D dimTitle = name.calculateDimension(stringBounder);
		return Math.max(25, dimTitle.getHeight() + 20);
	}

	private UTranslate getTranslate(StringBounder stringBounder) {
		final double suppWidth = suppWidth(stringBounder);
		return new UTranslate(suppWidth / 2, diffHeightTitle(stringBounder) + headerNoteHeight(stringBounder));
	}

	public double suppWidth(StringBounder stringBounder) {
		final FtileGeometry orig = inner.calculateDimension(stringBounder);
		final Dimension2D dimTitle = name.calculateDimension(stringBounder);
		final Dimension2D dimHeaderNote = headerNote.calculateDimension(stringBounder);
		final double suppWidth = MathUtils
				.max(orig.getWidth(), dimTitle.getWidth() + 20, dimHeaderNote.getWidth() + 20) - orig.getWidth();
		return suppWidth;
	}

	public FtileGeometry calculateDimension(StringBounder stringBounder) {
		final FtileGeometry orig = inner.calculateDimension(stringBounder);
		final double suppWidth = suppWidth(stringBounder);
		final double width = orig.getWidth() + suppWidth;
		final double height = orig.getHeight() + diffHeightTitle(stringBounder) + diffYY2
				+ headerNoteHeight(stringBounder);
		final double titleAndHeaderNoteHeight = diffHeightTitle(stringBounder) + headerNoteHeight(stringBounder);
		if (orig.hasPointOut()) {
			return new FtileGeometry(width, height, orig.getLeft() + suppWidth / 2, orig.getInY()
					+ titleAndHeaderNoteHeight, orig.getOutY() + titleAndHeaderNoteHeight);
		}
		return new FtileGeometry(width, height, orig.getLeft() + suppWidth / 2, orig.getInY()
				+ titleAndHeaderNoteHeight);
	}

	private double headerNoteHeight(StringBounder stringBounder) {
		return headerNote.calculateDimension(stringBounder).getHeight();
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		final Dimension2D dimTotal = calculateDimension(stringBounder);

		final SymbolContext symbolContext = new SymbolContext(backColor, HtmlColorUtils.BLACK).withShadow(shadowing())
				.withStroke(new UStroke(2));
		USymbol.FRAME.asBig(name, TextBlockUtils.empty(0, 0), dimTotal.getWidth(), dimTotal.getHeight(), symbolContext)
				.drawU(ug);

		final Dimension2D dimHeaderNote = headerNote.calculateDimension(stringBounder);
		headerNote.drawU(ug.apply(new UTranslate(dimTotal.getWidth() - dimHeaderNote.getWidth() - 10,
				diffHeightTitle(ug.getStringBounder()) - 10)));

		ug.apply(getTranslate(stringBounder)).draw(inner);

	}

}
