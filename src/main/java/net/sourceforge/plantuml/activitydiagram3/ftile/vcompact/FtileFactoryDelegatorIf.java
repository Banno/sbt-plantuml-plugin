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

import java.util.List;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactoryDelegator;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.svek.ConditionStyle;
import net.sourceforge.plantuml.ugraphic.UFont;

public class FtileFactoryDelegatorIf extends FtileFactoryDelegator {

	public FtileFactoryDelegatorIf(FtileFactory factory, ISkinParam skinParam) {
		super(factory, skinParam);
	}

	@Override
	public Ftile createIf(Swimlane swimlane, List<Branch> thens, Branch elseBranch) {

		final ConditionStyle conditionStyle = getSkinParam().getConditionStyle();
		final UFont fontArrow = getSkinParam().getFont(FontParam.ACTIVITY_ARROW, null, false);
		final UFont fontTest = getSkinParam().getFont(
				conditionStyle == ConditionStyle.INSIDE ? FontParam.ACTIVITY_DIAMOND : FontParam.ACTIVITY_ARROW, null, false);
		final Branch branch0 = thens.get(0);

		final HtmlColor borderColor = getRose().getHtmlColor(getSkinParam(), ColorParam.activityBorder);
		final HtmlColor backColor = branch0.getColor() == null ? getRose().getHtmlColor(getSkinParam(),
				ColorParam.activityBackground) : branch0.getColor();
		final HtmlColor arrowColor = getRose().getHtmlColor(getSkinParam(), ColorParam.activityArrow);

		if (thens.size() > 1) {
			return FtileIfLong2.create(swimlane, borderColor, backColor, fontArrow, arrowColor, getFactory(),
					conditionStyle, thens, elseBranch, getSkinParam().getHyperlinkColor(), getSkinParam().useUnderlineForHyperlink());
		}
		return FtileIf.create(swimlane, borderColor, backColor, fontArrow, fontTest, arrowColor, getFactory(),
				conditionStyle, thens.get(0), elseBranch, getSkinParam(), getStringBounder());
	}

}
