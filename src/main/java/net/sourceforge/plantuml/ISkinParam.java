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
package net.sourceforge.plantuml;

import net.sourceforge.plantuml.cucadiagram.Rankdir;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.dot.DotSplines;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizLayoutStrategy;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.IHtmlColorSet;
import net.sourceforge.plantuml.graphic.SkinParameter;
import net.sourceforge.plantuml.svek.ConditionStyle;
import net.sourceforge.plantuml.svek.PackageStyle;
import net.sourceforge.plantuml.ugraphic.ColorMapper;
import net.sourceforge.plantuml.ugraphic.UFont;
import net.sourceforge.plantuml.ugraphic.UStroke;

public interface ISkinParam extends ISkinSimple {

	public HtmlColor getHyperlinkColor();

	public boolean useUnderlineForHyperlink();

	public HtmlColor getBackgroundColor();

	public HtmlColor getHtmlColor(ColorParam param, Stereotype stereotype, boolean clickable);

	public HtmlColor getFontHtmlColor(FontParam param, Stereotype stereotype);

	public UStroke getThickness(LineParam param, Stereotype stereotype);

	public UFont getFont(FontParam fontParam, Stereotype stereotype, boolean inPackageTitle);

	public HorizontalAlignment getHorizontalAlignment(AlignParam param);

	public HorizontalAlignment getDefaultTextAlignment();

	public int getCircledCharacterRadius();

	public int classAttributeIconSize();

	public ColorMapper getColorMapper();

	public int getDpi();

	public DotSplines getDotSplines();

	public GraphvizLayoutStrategy getStrategy();

	public boolean shadowing();

	public boolean shadowing2(SkinParameter skinParameter);

	public PackageStyle getPackageStyle();

	public boolean useUml2ForComponent();

	public boolean stereotypePositionTop();

	public boolean useSwimlanes(UmlDiagramType type);

	public double getNodesep();

	public double getRanksep();

	public double getRoundCorner();

	public double maxMessageSize();

	public boolean strictUmlStyle();

	public boolean forceSequenceParticipantUnderlined();

	public ConditionStyle getConditionStyle();

	public double minClassWidth();

	public boolean sameClassWidth();

	public Rankdir getRankdir();

	public boolean useOctagonForActivity(Stereotype stereotype);

	public IHtmlColorSet getIHtmlColorSet();

	public int groupInheritance();
	
	public boolean useGuillemet();

	public boolean handwritten();

	public String getSvgLinkTarget();

}
