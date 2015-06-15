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
package net.sourceforge.plantuml.cucadiagram;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockLineBefore;
import net.sourceforge.plantuml.graphic.TextBlockUtils;
import net.sourceforge.plantuml.graphic.TextBlockWidth;
import net.sourceforge.plantuml.skin.VisibilityModifier;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.PlacementStrategy;
import net.sourceforge.plantuml.ugraphic.PlacementStrategyVisibility;
import net.sourceforge.plantuml.ugraphic.PlacementStrategyY1Y2Center;
import net.sourceforge.plantuml.ugraphic.PlacementStrategyY1Y2Left;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULayoutGroup;
import net.sourceforge.plantuml.utils.CharHidder;

public class MethodsOrFieldsArea extends AbstractTextBlock implements TextBlockWidth, TextBlock {

	public TextBlock asBlockMemberImpl() {
		return new TextBlockLineBefore(TextBlockUtils.withMargin(this, 6, 4));
	}

	private final UFont font;
	private final ISkinParam skinParam;
	private final HtmlColor color;
	private final HtmlColor hyperlinkColor;
	private final boolean useUnderlineForHyperlink;
	private final Rose rose = new Rose();
	private final List<Member> members = new ArrayList<Member>();
	private final HorizontalAlignment align;

	public MethodsOrFieldsArea(List<Member> members, FontParam fontParam, ISkinParam skinParam) {
		this(members, fontParam, skinParam, HorizontalAlignment.LEFT);
	}

	public MethodsOrFieldsArea(List<Member> members, FontParam fontParam, ISkinParam skinParam,
			HorizontalAlignment align) {
		this.align = align;
		this.skinParam = skinParam;
		this.font = skinParam.getFont(fontParam, null, false);
		this.color = rose.getFontColor(skinParam, fontParam);
		this.hyperlinkColor = skinParam.getHyperlinkColor();
		this.useUnderlineForHyperlink = skinParam.useUnderlineForHyperlink();
		this.members.addAll(members);
	}

	private boolean hasSmallIcon() {
		if (skinParam.classAttributeIconSize() == 0) {
			return false;
		}
		for (Member m : members) {
			if (m.getVisibilityModifier() != null) {
				return true;
			}
		}
		return false;
	}

	public Dimension2D calculateDimension(StringBounder stringBounder) {
		double smallIcon = 0;
		if (hasSmallIcon()) {
			smallIcon = skinParam.getCircledCharacterRadius() + 3;
		}
		double x = 0;
		double y = 0;
		for (Member m : members) {
			final TextBlock bloc = createTextBlock(m);
			final Dimension2D dim = bloc.calculateDimension(stringBounder);
			x = Math.max(dim.getWidth(), x);
			y += dim.getHeight();
		}
		x += smallIcon;
		return new Dimension2DDouble(x, y);
	}

	private TextBlock createTextBlock(Member m) {
		final boolean withVisibilityChar = skinParam.classAttributeIconSize() == 0;
		String s = m.getDisplay(withVisibilityChar);
		if (withVisibilityChar && s.startsWith("#")) {
			s = CharHidder.addTileAtBegin(s);
		}
		FontConfiguration config = new FontConfiguration(font, color, hyperlinkColor, useUnderlineForHyperlink);
		if (m.isAbstract()) {
			config = config.italic();
		}
		if (m.isStatic()) {
			config = config.underline();
		}
		TextBlock bloc = TextBlockUtils.create(Display.getWithNewlines(s), config, align, skinParam, true);
		bloc = TextBlockUtils.fullInnerPosition(bloc, m.getDisplay(false));
		return new TextBlockTracer(m, bloc);
	}

	static class TextBlockTracer extends AbstractTextBlock implements TextBlock {

		private final TextBlock bloc;
		private final Url url;

		public TextBlockTracer(Member m, TextBlock bloc) {
			this.bloc = bloc;
			this.url = m.getUrl();
		}

		public void drawU(UGraphic ug) {
			if (url != null) {
				ug.startUrl(url);
			}
			bloc.drawU(ug);
			if (url != null) {
				ug.closeAction();
			}
		}

		public Dimension2D calculateDimension(StringBounder stringBounder) {
			final Dimension2D dim = bloc.calculateDimension(stringBounder);
			return dim;
		}
		
		@Override
		public Rectangle2D getInnerPosition(String member, StringBounder stringBounder) {
			return bloc.getInnerPosition(member, stringBounder);
		}

	}

	private TextBlock getUBlock(final VisibilityModifier modifier) {
		if (modifier == null) {
			return new AbstractTextBlock() {

				public void drawU(UGraphic ug) {
				}

				public Dimension2D calculateDimension(StringBounder stringBounder) {
					return new Dimension2DDouble(1, 1);
				}
			};
		}
		final HtmlColor back = modifier.getBackground() == null ? null : rose.getHtmlColor(skinParam,
				modifier.getBackground());
		final HtmlColor fore = rose.getHtmlColor(skinParam, modifier.getForeground());

		final TextBlock uBlock = modifier.getUBlock(skinParam.classAttributeIconSize(), fore, back);
		return uBlock;
	}

	public TextBlock asTextBlock(final double widthToUse) {
		return this;
	}

	public boolean contains(String member) {
		for (Member att : members) {
			if (att.getDisplay(false).startsWith(member)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Rectangle2D getInnerPosition(String member, StringBounder stringBounder) {
		final ULayoutGroup group = getLayout(stringBounder);
		final Dimension2D dim = calculateDimension(stringBounder);
		return group.getInnerPosition(member, dim.getWidth(), dim.getHeight(), stringBounder);
	}

	private ULayoutGroup getLayout(final StringBounder stringBounder) {
		final ULayoutGroup group;
		if (hasSmallIcon()) {
			group = new ULayoutGroup(new PlacementStrategyVisibility(stringBounder,
					skinParam.getCircledCharacterRadius() + 3));
			for (Member att : members) {
				final TextBlock bloc = createTextBlock(att);
				final VisibilityModifier modifier = att.getVisibilityModifier();
				group.add(getUBlock(modifier));
				group.add(bloc);
			}
		} else {
			final PlacementStrategy placementStrategy;
			if (align == HorizontalAlignment.LEFT) {
				placementStrategy = new PlacementStrategyY1Y2Left(stringBounder);
			} else if (align == HorizontalAlignment.CENTER) {
				placementStrategy = new PlacementStrategyY1Y2Center(stringBounder);
			} else {
				throw new IllegalStateException();
			}
			group = new ULayoutGroup(placementStrategy);
			for (Member att : members) {
				final TextBlock bloc = createTextBlock(att);
				group.add(bloc);
			}
		}
		return group;
	}

	public void drawU(UGraphic ug) {
		final ULayoutGroup group = getLayout(ug.getStringBounder());
		final Dimension2D dim = calculateDimension(ug.getStringBounder());
		group.drawU(ug, dim.getWidth(), dim.getHeight());
	}

}
