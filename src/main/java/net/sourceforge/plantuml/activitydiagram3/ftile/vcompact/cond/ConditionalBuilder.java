/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
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
package net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.cond;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.ftile.Diamond;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileEmpty;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileMinWidth;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileUtils;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileIfDown;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDiamond;
import net.sourceforge.plantuml.activitydiagram3.ftile.vertical.FtileDiamondInside;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.creole.CreoleParser;
import net.sourceforge.plantuml.creole.Sheet;
import net.sourceforge.plantuml.creole.SheetBlock1;
import net.sourceforge.plantuml.creole.SheetBlock2;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.svek.ConditionStyle;

public class ConditionalBuilder {

	private final Swimlane swimlane;
	private final HtmlColor borderColor;
	private final HtmlColor backColor;
	private final Rainbow arrowColor;
	private final FtileFactory ftileFactory;
	private final ConditionStyle conditionStyle;
	private final Branch branch1;
	private final Branch branch2;
	private final ISkinParam skinParam;
	private final StringBounder stringBounder;
	private final FontConfiguration fontArrow;
	private final FontConfiguration fontTest;

	private final Ftile tile1;
	private final Ftile tile2;

	public ConditionalBuilder(Swimlane swimlane, HtmlColor borderColor, HtmlColor backColor, Rainbow arrowColor,
			FtileFactory ftileFactory, ConditionStyle conditionStyle, Branch branch1, Branch branch2,
			ISkinParam skinParam, StringBounder stringBounder, FontConfiguration fontArrow, FontConfiguration fontTest) {
		this.swimlane = swimlane;
		this.borderColor = borderColor;
		this.backColor = backColor;
		this.arrowColor = arrowColor;
		this.ftileFactory = ftileFactory;
		this.conditionStyle = conditionStyle;
		this.branch1 = branch1;
		this.branch2 = branch2;
		this.skinParam = skinParam;
		this.stringBounder = stringBounder;
		this.fontArrow = fontArrow;
		this.fontTest = fontTest;

		this.tile1 = new FtileMinWidth(branch1.getFtile(), 30);
		this.tile2 = new FtileMinWidth(branch2.getFtile(), 30);

	}

	static public Ftile create(Swimlane swimlane, HtmlColor borderColor, HtmlColor backColor, Rainbow arrowColor,
			FtileFactory ftileFactory, ConditionStyle conditionStyle, Branch branch1, Branch branch2,
			ISkinParam skinParam, StringBounder stringBounder, FontConfiguration fcArrow, FontConfiguration fcTest) {
		final ConditionalBuilder builder = new ConditionalBuilder(swimlane, borderColor, backColor, arrowColor,
				ftileFactory, conditionStyle, branch1, branch2, skinParam, stringBounder, fcArrow, fcTest);
//		if (isEmptyOrOnlySingleStop(branch2) && isEmptyOrOnlySingleStop(branch1) == false) {
//			return builder.createDown(builder.getLabelBranch1(), builder.getLabelBranch2());
//		}
//		if (branch1.isEmpty() && branch2.isOnlySingleStop()) {
//			return builder.createDown(builder.getLabelBranch1(), builder.getLabelBranch2());
//		}
//		if (isEmptyOrOnlySingleStop(branch1) && isEmptyOrOnlySingleStop(branch2) == false) {
//			return builder.createDown(builder.getLabelBranch2(), builder.getLabelBranch1());
//		}
//		if (branch2.isEmpty() && branch1.isOnlySingleStop()) {
//			return builder.createDown(builder.getLabelBranch2(), builder.getLabelBranch1());
//		}
		return builder.createWithLinks();
		// return builder.createWithDiamonds();
		// return builder.createNude();
	}

	private static boolean isEmptyOrOnlySingleStop(Branch branch) {
		return branch.isEmpty() || branch.isOnlySingleStop();
	}

	private Ftile createDown(TextBlock tb1, TextBlock tb2) {
		final Ftile diamond1 = getDiamond1(false, tb1, tb2);
		final Ftile diamond2 = getDiamond2();
		if (branch2.isOnlySingleStop()) {
			return FtileIfDown.create(diamond1, diamond2, swimlane, FtileUtils.addHorizontalMargin(tile1, 10),
					arrowColor, ftileFactory, branch2.getFtile());
		}
		if (branch1.isOnlySingleStop()) {
			return FtileIfDown.create(diamond1, diamond2, swimlane, FtileUtils.addHorizontalMargin(tile2, 10),
					arrowColor, ftileFactory, branch1.getFtile());
		}
		if (branch1.isEmpty()) {
			return FtileIfDown.create(diamond1, diamond2, swimlane, FtileUtils.addHorizontalMargin(tile2, 10),
					arrowColor, ftileFactory, null);
		}
		return FtileIfDown.create(diamond1, diamond2, swimlane, FtileUtils.addHorizontalMargin(tile1, 10), arrowColor,
				ftileFactory, null);
	}

	private Ftile createNude() {
		return new FtileIfNude(tile1, tile2, swimlane);

	}

	private Ftile createWithDiamonds() {
		final Ftile diamond1 = getDiamond1(true);
		final Ftile diamond2 = getDiamond2();
		final FtileIfWithDiamonds ftile = new FtileIfWithDiamonds(diamond1, tile1, tile2, diamond2, swimlane,
				stringBounder);
		final Dimension2D label1 = getLabelBranch1().calculateDimension(stringBounder);
		final Dimension2D label2 = getLabelBranch2().calculateDimension(stringBounder);
		final double diff1 = ftile.computeMarginNeedForBranchLabe1(stringBounder, label1);
		final double diff2 = ftile.computeMarginNeedForBranchLabe2(stringBounder, label2);
		Ftile result = FtileUtils.addHorizontalMargin(ftile, diff1, diff2);
		final double suppHeight = ftile.computeVerticalMarginNeedForBranchs(stringBounder, label1, label2);
		result = FtileUtils.addVerticalMargin(result, suppHeight, 0);
		return result;
	}

	private Ftile createWithLinks() {
		final Ftile diamond1 = getDiamond1(true);
		final Ftile diamond2 = getDiamond2();
		final Ftile tmp1 = FtileUtils.addHorizontalMargin(tile1, 10);
		final Ftile tmp2 = FtileUtils.addHorizontalMargin(tile2, 10);
		final FtileIfWithLinks ftile = new FtileIfWithLinks(diamond1, tmp1, tmp2, diamond2, swimlane, arrowColor,
				stringBounder);
		final Dimension2D label1 = getLabelBranch1().calculateDimension(stringBounder);
		final Dimension2D label2 = getLabelBranch2().calculateDimension(stringBounder);
		final double diff1 = ftile.computeMarginNeedForBranchLabe1(stringBounder, label1);
		final double diff2 = ftile.computeMarginNeedForBranchLabe2(stringBounder, label2);
		final double suppHeight = ftile.computeVerticalMarginNeedForBranchs(stringBounder, label1, label2);
		Ftile result = ftile.addLinks(branch1, branch2, stringBounder);
		result = FtileUtils.addHorizontalMargin(result, diff1, diff2);
		result = FtileUtils.addVerticalMargin(result, suppHeight, 0);
		return result;
	}

	private Ftile getDiamond1(boolean eastWest) {
		return getDiamond1(eastWest, getLabelBranch1(), getLabelBranch2());
	}

	private Ftile getDiamond1(boolean eastWest, TextBlock tb1, TextBlock tb2) {
		final Display labelTest = branch1.getLabelTest();

		final Sheet sheet = new CreoleParser(fontTest, skinParam.getDefaultTextAlignment(HorizontalAlignment.LEFT),
				skinParam, CreoleMode.FULL).createSheet(labelTest);
		final SheetBlock1 sheetBlock1 = new SheetBlock1(sheet, 0, skinParam.getPadding());
		final TextBlock tbTest = new SheetBlock2(sheetBlock1, Diamond.asStencil(sheetBlock1), tile1.getThickness());

		final Ftile diamond1;
		if (conditionStyle == ConditionStyle.INSIDE) {
			if (eastWest) {
				diamond1 = new FtileDiamondInside(tile1.skinParam(), backColor, borderColor, swimlane, tbTest)
						.withWestAndEast(tb1, tb2);
			} else {
				diamond1 = new FtileDiamondInside(tile1.skinParam(), backColor, borderColor, swimlane, tbTest)
						.withSouth(tb1).withEast(tb2);
			}
		} else if (conditionStyle == ConditionStyle.DIAMOND) {
			if (eastWest) {
				diamond1 = new FtileDiamond(tile1.skinParam(), backColor, borderColor, swimlane).withNorth(tbTest)
						.withWestAndEast(tb1, tb2);
			} else {
				diamond1 = new FtileDiamond(tile1.skinParam(), backColor, borderColor, swimlane).withNorth(tbTest)
						.withSouth(tb1).withEast(tb2);
			}
		} else {
			throw new IllegalStateException();
		}
		return diamond1;
	}

	private TextBlock getLabelBranch2() {
		final TextBlock tb2 = branch2.getLabelPositive().create(fontArrow, HorizontalAlignment.LEFT,
				ftileFactory.skinParam(), CreoleMode.SIMPLE_LINE);
		return tb2;
	}

	private TextBlock getLabelBranch1() {
		final TextBlock tb1 = branch1.getLabelPositive().create(fontArrow, HorizontalAlignment.LEFT,
				ftileFactory.skinParam(), CreoleMode.SIMPLE_LINE);
		return tb1;
	}

	private Ftile getDiamond2() {
		final Ftile diamond2;
		if (hasTwoBranches()) {
			final Display out1 = branch1.getFtile().getOutLinkRendering().getDisplay();
			final TextBlock tbout1 = out1 == null ? null : out1.create(fontArrow, HorizontalAlignment.LEFT,
					ftileFactory.skinParam(), CreoleMode.SIMPLE_LINE);
			final Display out2 = branch2.getFtile().getOutLinkRendering().getDisplay();
			final TextBlock tbout2 = out2 == null ? null : out2.create(fontArrow, HorizontalAlignment.LEFT,
					ftileFactory.skinParam(), CreoleMode.SIMPLE_LINE);
			diamond2 = new FtileDiamond(tile1.skinParam(), backColor, borderColor, swimlane).withWest(tbout1).withEast(
					tbout2);
		} else {
			// diamond2 = new FtileEmpty(tile1.shadowing(), Diamond.diamondHalfSize * 2, Diamond.diamondHalfSize * 2,
			// swimlane, swimlane);
			diamond2 = new FtileEmpty(tile1.skinParam(), 0, Diamond.diamondHalfSize / 2, swimlane, swimlane);
		}
		return diamond2;
	}

	public boolean hasTwoBranches() {
		return tile1.calculateDimension(stringBounder).hasPointOut()
				&& tile2.calculateDimension(stringBounder).hasPointOut();
	}

	// private HtmlColor fontColor() {
	// return skinParam.getFontHtmlColor(FontParam.ACTIVITY_DIAMOND, null);
	// }

}
