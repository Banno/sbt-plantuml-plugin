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
package net.sourceforge.plantuml.svek;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.Dimension2DDouble;
import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.LineParam;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.EntityPosition;
import net.sourceforge.plantuml.cucadiagram.EntityUtils;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.cucadiagram.Member;
import net.sourceforge.plantuml.cucadiagram.MethodsOrFieldsArea;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.dot.DotData;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizVersion;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorTransparent;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockEmpty;
import net.sourceforge.plantuml.graphic.TextBlockWidth;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.posimo.Moveable;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.svek.image.EntityImageState;
import net.sourceforge.plantuml.ugraphic.UChangeBackColor;
import net.sourceforge.plantuml.ugraphic.UChangeColor;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.ULine;
import net.sourceforge.plantuml.ugraphic.URectangle;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.ugraphic.UTranslate;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.utils.UniqueSequence;

public class Cluster implements Moveable {

	private final Cluster parent;
	private final IGroup group;
	private final List<Shape> shapes = new ArrayList<Shape>();
	private final List<Cluster> children = new ArrayList<Cluster>();
	private final int color;
	private final int colorTitle;
	private final ISkinParam skinParam;

	private int titleAndAttributeWidth;
	private int titleAndAttributeHeight;
	private TextBlock ztitle;
	private TextBlock zstereo;

	private double xTitle;
	private double yTitle;

	private double minX;
	private double minY;
	private double maxX;
	private double maxY;

	public void moveSvek(double deltaX, double deltaY) {
		this.xTitle += deltaX;
		this.minX += deltaX;
		this.maxX += deltaX;
		this.yTitle += deltaY;
		this.minY += deltaY;
		this.maxY += deltaY;

	}

	private boolean hasEntryOrExitPoint() {
		for (Shape sh : shapes) {
			if (sh.getEntityPosition() != EntityPosition.NORMAL) {
				return true;
			}
		}
		return false;
	}

	public Cluster(ColorSequence colorSequence, ISkinParam skinParam, IGroup root) {
		this(null, root, colorSequence, skinParam);
	}

	private ColorParam border;

	private Cluster(Cluster parent, IGroup group, ColorSequence colorSequence, ISkinParam skinParam) {
		if (group == null) {
			throw new IllegalStateException();
		}
		this.parent = parent;
		this.group = group;
		if (group.getUSymbol() != null) {
			border = group.getUSymbol().getColorParamBorder();
		}
		this.color = colorSequence.getValue();
		this.colorTitle = colorSequence.getValue();
		this.skinParam = skinParam;
	}

	@Override
	public String toString() {
		return super.toString() + " " + group;
	}

	public final Cluster getParent() {
		return parent;
	}

	public void addShape(Shape sh) {
		if (sh == null) {
			throw new IllegalArgumentException();
		}
		this.shapes.add(sh);
		sh.setCluster(this);
	}

	public final List<Shape> getShapes() {
		return Collections.unmodifiableList(shapes);
	}

	private List<Shape> getShapesOrderedTop(Collection<Line> lines) {
		final List<Shape> firsts = new ArrayList<Shape>();
		final Set<String> tops = new HashSet<String>();
		final Map<String, Shape> shs = new HashMap<String, Shape>();

		for (final Iterator<Shape> it = shapes.iterator(); it.hasNext();) {
			final Shape sh = it.next();
			shs.put(sh.getUid(), sh);
			if (sh.isTop() && sh.getEntityPosition() == EntityPosition.NORMAL) {
				firsts.add(sh);
				tops.add(sh.getUid());
			}
		}

		for (Line l : lines) {
			if (tops.contains(l.getStartUid())) {
				final Shape sh = shs.get(l.getEndUid());
				if (sh != null && sh.getEntityPosition() == EntityPosition.NORMAL) {
					firsts.add(0, sh);
				}
			}

			if (l.isInverted()) {
				final Shape sh = shs.get(l.getStartUid());
				if (sh != null && sh.getEntityPosition() == EntityPosition.NORMAL) {
					firsts.add(0, sh);
				}
			}
		}

		return firsts;
	}

	private List<Shape> getShapesEntryExit(EntityPosition position) {
		final List<Shape> result = new ArrayList<Shape>();

		for (final Iterator<Shape> it = shapes.iterator(); it.hasNext();) {
			final Shape sh = it.next();
			if (sh.getEntityPosition() == position) {
				result.add(sh);
			}
		}
		return result;
	}

	private List<Shape> getShapesOrderedWithoutTop(Collection<Line> lines) {
		final List<Shape> all = new ArrayList<Shape>(shapes);
		final Set<String> tops = new HashSet<String>();
		final Map<String, Shape> shs = new HashMap<String, Shape>();

		for (final Iterator<Shape> it = all.iterator(); it.hasNext();) {
			final Shape sh = it.next();
			if (sh.getEntityPosition() != EntityPosition.NORMAL) {
				it.remove();
				continue;
			}
			shs.put(sh.getUid(), sh);
			if (sh.isTop()) {
				tops.add(sh.getUid());
				it.remove();
			}
		}

		for (Line l : lines) {
			if (tops.contains(l.getStartUid())) {
				final Shape sh = shs.get(l.getEndUid());
				if (sh != null) {
					all.remove(sh);
				}
			}

			if (l.isInverted()) {
				final Shape sh = shs.get(l.getStartUid());
				if (sh != null) {
					all.remove(sh);
				}
			}
		}

		return all;
	}

	public final List<Cluster> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public Cluster createChild(IGroup g, int titleAndAttributeWidth, int titleAndAttributeHeight, TextBlock title,
			TextBlock stereo, ColorSequence colorSequence, ISkinParam skinParam) {
		final Cluster child = new Cluster(this, g, colorSequence, skinParam);
		child.titleAndAttributeWidth = titleAndAttributeWidth;
		child.titleAndAttributeHeight = titleAndAttributeHeight;
		child.ztitle = title;
		child.zstereo = stereo;
		this.children.add(child);
		return child;
	}

	public final IGroup getGroup() {
		return group;
	}

	public final int getTitleAndAttributeWidth() {
		return titleAndAttributeWidth;
	}

	public final int getTitleAndAttributeHeight() {
		return titleAndAttributeHeight;
	}

	public double getWidth() {
		return maxX - minX;
	}

	public double getMinX() {
		return minX;
	}

	public ClusterPosition getClusterPosition() {
		return new ClusterPosition(minX, minY, maxX, maxY);
	}

	public void setTitlePosition(double x, double y) {
		this.xTitle = x;
		this.yTitle = y;
	}

	private static HtmlColor getColor(ColorParam colorParam, ISkinParam skinParam) {
		return new Rose().getHtmlColor(skinParam, colorParam);
	}

	public void drawU(UGraphic ug, DotData dotData, UStroke stroke) {
		HtmlColor borderColor;
		if (dotData.getUmlDiagramType() == UmlDiagramType.STATE) {
			borderColor = getColor(ColorParam.stateBorder, dotData.getSkinParam());
		} else {
			borderColor = getColor(ColorParam.packageBorder, dotData.getSkinParam());
		}

		final Url url = group.getUrl99();
		if (url != null) {
			ug.startUrl(url);
		}
		try {
			if (hasEntryOrExitPoint()) {
				manageEntryExitPoint(dotData, ug.getStringBounder());
			}
			if (skinParam.useSwimlanes(dotData.getUmlDiagramType())) {
				drawSwinLinesState(ug, borderColor, dotData);
				return;
			}
			final boolean isState = dotData.getUmlDiagramType() == UmlDiagramType.STATE;
			if (isState) {
				if (group.getSpecificLineStroke() != null) {
					stroke = group.getSpecificLineStroke();
				}
				if (group.getSpecificLineColor() != null) {
					borderColor = group.getSpecificLineColor();
				}
				drawUState(ug, borderColor, dotData, stroke);
				return;
			}
			PackageStyle style = group.getPackageStyle();
			if (style == null) {
				style = dotData.getSkinParam().getPackageStyle();
			}
			if (border != null) {
				final HtmlColor tmp = dotData.getSkinParam().getHtmlColor(border, null, false);
				if (tmp != null) {
					borderColor = tmp;
				}
			}

			if (ztitle != null || zstereo != null) {
				final HtmlColor stateBack = getStateBackColor(getBackColor(), dotData.getSkinParam(),
						group.getStereotype());
				final ClusterDecoration decoration = new ClusterDecoration(style, group.getUSymbol(), ztitle, zstereo,
						stateBack, minX, minY, maxX, maxY, getStroke(dotData.getSkinParam(), group.getStereotype()));
				decoration.drawU(ug, borderColor, dotData.getSkinParam().shadowing());
				return;
			}
			final URectangle rect = new URectangle(maxX - minX, maxY - minY);
			if (dotData.getSkinParam().shadowing()) {
				rect.setDeltaShadow(3.0);
			}
			final HtmlColor stateBack = getStateBackColor(getBackColor(), dotData.getSkinParam(), group.getStereotype());
			ug = ug.apply(new UChangeBackColor(stateBack)).apply(new UChangeColor(borderColor));
			ug.apply(new UStroke(2)).apply(new UTranslate(minX, minY)).draw(rect);

		} finally {
			if (url != null) {
				ug.closeAction();
			}
		}

	}

	private UStroke getStroke(ISkinParam skinParam, Stereotype stereo) {
		UStroke stroke = skinParam.getThickness(LineParam.packageBorder, stereo);
		if (stroke == null) {
			stroke = new UStroke(2.0);
		}
		return stroke;
	}

	private void manageEntryExitPoint(DotData dotData, StringBounder stringBounder) {
		final Collection<ClusterPosition> insides = new ArrayList<ClusterPosition>();
		final List<Point2D> points = new ArrayList<Point2D>();
		for (Shape sh : shapes) {
			if (sh.getEntityPosition() == EntityPosition.NORMAL) {
				insides.add(sh.getClusterPosition());
			} else {
				points.add(sh.getClusterPosition().getPointCenter());
			}
		}
		for (Cluster in : children) {
			insides.add(in.getClusterPosition());
		}
		final FrontierCalculator frontierCalculator = new FrontierCalculator(getClusterPosition(), insides, points);
		if (titleAndAttributeHeight > 0 && titleAndAttributeWidth > 0) {
			frontierCalculator.ensureMinWidth(titleAndAttributeWidth + 10);
		}
		final ClusterPosition forced = frontierCalculator.getSuggestedPosition();
		xTitle += ((forced.getMinX() - minX) + (forced.getMaxX() - maxX)) / 2;
		minX = forced.getMinX();
		minY = forced.getMinY();
		maxX = forced.getMaxX();
		maxY = forced.getMaxY();
		yTitle = minY + IEntityImage.MARGIN;
		final double widthTitle = ztitle.calculateDimension(stringBounder).getWidth();
		xTitle = minX + ((maxX - minX - widthTitle) / 2);
	}

	private void drawSwinLinesState(UGraphic ug, HtmlColor borderColor, DotData dotData) {
		if (ztitle != null) {
			ztitle.drawU(ug.apply(new UTranslate(xTitle, 0)));
		}
		final ULine line = new ULine(0, maxY - minY);
		ug = ug.apply(new UChangeColor(borderColor));
		ug.apply(new UTranslate(minX, 0)).draw(line);
		ug.apply(new UTranslate(maxX, 0)).draw(line);

	}

	private HtmlColor getColor(DotData dotData, ColorParam colorParam, Stereotype stereo) {
		return new Rose().getHtmlColor(dotData.getSkinParam(), colorParam, stereo);
	}

	private void drawUState(UGraphic ug, HtmlColor borderColor, DotData dotData, UStroke stroke) {
		final Dimension2D total = new Dimension2DDouble(maxX - minX, maxY - minY);
		final double suppY;
		if (ztitle == null) {
			suppY = 0;
		} else {
			suppY = ztitle.calculateDimension(ug.getStringBounder()).getHeight() + IEntityImage.MARGIN
					+ IEntityImage.MARGIN_LINE;
		}

		HtmlColor stateBack = getBackColor();
		if (stateBack == null) {
			stateBack = getColor(dotData, ColorParam.stateBackground, group.getStereotype());
		}
		final HtmlColor background = getColor(dotData, ColorParam.background, null);
		final TextBlockWidth attribute = getTextBlockAttribute(dotData);
		final double attributeHeight = attribute.calculateDimension(ug.getStringBounder()).getHeight();
		final RoundedContainer r = new RoundedContainer(total, suppY, attributeHeight
				+ (attributeHeight > 0 ? IEntityImage.MARGIN : 0), borderColor, stateBack, background, stroke);
		r.drawU(ug.apply(new UTranslate(minX, minY)), dotData.getSkinParam().shadowing());

		if (ztitle != null) {
			ztitle.drawU(ug.apply(new UTranslate(xTitle, yTitle)));
		}

		if (attributeHeight > 0) {
			attribute.asTextBlock(total.getWidth()).drawU(
					ug.apply(new UTranslate(minX + IEntityImage.MARGIN, minY + suppY + IEntityImage.MARGIN / 2.0)));
		}

		final Stereotype stereotype = group.getStereotype();
		final boolean withSymbol = stereotype != null && stereotype.isWithOOSymbol();
		if (withSymbol) {
			EntityImageState.drawSymbol(ug.apply(new UChangeColor(borderColor)), maxX, maxY);
		}

	}

	private TextBlockWidth getTextBlockAttribute(DotData dotData) {
		final TextBlockWidth attribute;
		final List<Member> members = group.getBodier().getFieldsToDisplay();
		if (members.size() == 0) {
			attribute = new TextBlockEmpty();
		} else {
			attribute = new MethodsOrFieldsArea(members, FontParam.STATE_ATTRIBUTE, dotData.getSkinParam());
		}
		return attribute;
	}

	public void setPosition(double minX, double minY, double maxX, double maxY) {
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;

	}

	private boolean isThereALinkFromOrToGroup(Collection<Line> lines) {
		for (Line line : lines) {
			if (line.isLinkFromOrTo(group)) {
				return true;
			}
		}
		return false;
	}

	public void printCluster1(StringBuilder sb, Collection<Line> lines) {
		for (Shape sh : getShapesOrderedTop(lines)) {
			sh.appendShape(sb);
		}
	}

	private List<IShapePseudo> addProtection(List<Shape> entries, double width) {
		final List<IShapePseudo> result = new ArrayList<IShapePseudo>();
		result.add(entries.get(0));
		for (int i = 1; i < entries.size(); i++) {
			result.add(new ShapePseudoImpl("psd" + UniqueSequence.getValue(), width, 5));
			result.add(entries.get(i));
		}
		return result;
	}

	private double getMaxWidthFromLabelForEntryExit(List<Shape> entries, StringBounder stringBounder) {
		double result = -Double.MAX_VALUE;
		for (Shape shape : entries) {
			final double w = getMaxWidthFromLabelForEntryExit(shape, stringBounder);
			if (w > result) {
				result = w;
			}
		}
		return result;
	}

	private double getMaxWidthFromLabelForEntryExit(Shape shape, StringBounder stringBounder) {
		return shape.getMaxWidthFromLabelForEntryExit(stringBounder);
	}

	public void printClusterEntryExit(StringBuilder sb, StringBounder stringBounder) {
		// final List<? extends IShapePseudo> entries = getShapesEntryExit(EntityPosition.ENTRY_POINT);
		final List<Shape> shapesEntryExitList = getShapesEntryExit(EntityPosition.ENTRY_POINT);
		final double maxWith = getMaxWidthFromLabelForEntryExit(shapesEntryExitList, stringBounder);
		final double naturalSpace = 70;
		final List<? extends IShapePseudo> entries;
		if (maxWith > naturalSpace) {
			entries = addProtection(shapesEntryExitList, maxWith - naturalSpace);
		} else {
			entries = shapesEntryExitList;
		}
		if (entries.size() > 0) {
			sb.append("{rank=source;");
			for (IShapePseudo sh : entries) {
				sb.append(sh.getUid() + ";");
			}
			sb.append("}");
			for (IShapePseudo sh : entries) {
				sh.appendShape(sb);
			}
		}
		final List<Shape> exits = getShapesEntryExit(EntityPosition.EXIT_POINT);
		if (exits.size() > 0) {
			sb.append("{rank=sink;");
			for (Shape sh : exits) {
				sb.append(sh.getUid() + ";");
			}
			sb.append("}");
			for (Shape sh : exits) {
				sh.appendShape(sb);
			}
		}
	}

	public boolean printCluster2(StringBuilder sb, Collection<Line> lines, StringBounder stringBounder,
			DotMode dotMode, GraphvizVersion graphvizVersion, UmlDiagramType type) {
		// Log.println("Cluster::printCluster " + this);

		boolean added = false;
		for (Shape sh : getShapesOrderedWithoutTop(lines)) {
			sh.appendShape(sb);
			added = true;
		}

		if (dotMode != DotMode.NO_LEFT_RIGHT) {
			appendRankSame(sb, lines);
		}

		for (Cluster child : getChildren()) {
			child.printInternal(sb, lines, stringBounder, dotMode, graphvizVersion, type);
		}

		return added;
	}

	private void appendRankSame(StringBuilder sb, Collection<Line> lines) {
		for (String same : getRankSame(lines)) {
			sb.append(same);
			SvekUtils.println(sb);
		}
	}

	private Set<String> getRankSame(Collection<Line> lines) {
		final Set<String> rankSame = new HashSet<String>();
		for (Line l : lines) {
			if (l.hasEntryPoint()) {
				continue;
			}
			final String startUid = l.getStartUid();
			final String endUid = l.getEndUid();
			if (isInCluster(startUid) && isInCluster(endUid)) {
				final String same = l.rankSame();
				if (same != null) {
					rankSame.add(same);
				}
			}
		}
		return rankSame;
	}

	public void fillRankMin(Set<String> rankMin) {
		for (Shape sh : getShapes()) {
			if (sh.isTop()) {
				rankMin.add(sh.getUid());
			}
		}

		for (Cluster child : getChildren()) {
			child.fillRankMin(rankMin);
		}
	}

	private boolean isInCluster(String uid) {
		for (Shape sh : shapes) {
			if (sh.getUid().equals(uid)) {
				return true;
			}
		}
		return false;
	}

	public String getClusterId() {
		return "cluster" + color;
	}

	public static String getSpecialPointId(IEntity group) {
		return CENTER_ID + group.getUid();
	}

	public final static String CENTER_ID = "za";

	private boolean protection0(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type)) {
			return false;
		}
		return true;
	}

	private boolean protection1(UmlDiagramType type) {
		if (group.getUSymbol() == USymbol.NODE) {
			return true;
		}
		if (skinParam.useSwimlanes(type)) {
			return false;
		}
		return true;
	}

	public String getMinPoint(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type)) {
			return "minPoint" + color;
		}
		return null;
	}

	public String getMaxPoint(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type)) {
			return "maxPoint" + color;
		}
		return null;
	}

	private String getSourceInPoint(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type)) {
			return "sourceIn" + color;
		}
		return null;
	}

	private String getSinkInPoint(UmlDiagramType type) {
		if (skinParam.useSwimlanes(type)) {
			return "sinkIn" + color;
		}
		return null;
	}

	private void printInternal(StringBuilder sb, Collection<Line> lines, StringBounder stringBounder, DotMode dotMode,
			GraphvizVersion graphvizVersion, UmlDiagramType type) {
		final boolean thereALinkFromOrToGroup2 = isThereALinkFromOrToGroup(lines);
		boolean thereALinkFromOrToGroup1 = thereALinkFromOrToGroup2;
		final boolean useProtectionWhenThereALinkFromOrToGroup = graphvizVersion
				.useProtectionWhenThereALinkFromOrToGroup();
		if (useProtectionWhenThereALinkFromOrToGroup == false) {
			thereALinkFromOrToGroup1 = false;
		}
		// final boolean thereALinkFromOrToGroup1 = false;
		if (thereALinkFromOrToGroup1) {
			subgraphCluster(sb, "a");
		}
		final boolean hasEntryOrExitPoint = hasEntryOrExitPoint();
		if (hasEntryOrExitPoint) {
			for (Line line : lines) {
				if (line.isLinkFromOrTo(group)) {
					line.setProjectionCluster(this);
				}
			}
		}
		boolean protection0 = protection0(type);
		boolean protection1 = protection1(type);
		if (hasEntryOrExitPoint || useProtectionWhenThereALinkFromOrToGroup == false) {
			protection0 = false;
			protection1 = false;
		}
		if (protection0) {
			subgraphCluster(sb, "p0");
		}
		sb.append("subgraph " + getClusterId() + " {");
		sb.append("style=solid;");
		sb.append("color=\"" + StringUtils.getAsHtml(color) + "\";");

		final boolean isLabel = getTitleAndAttributeHeight() > 0 && getTitleAndAttributeWidth() > 0;
		final String label;
		if (isLabel) {
			final StringBuilder sblabel = new StringBuilder("<");
			Line.appendTable(sblabel, getTitleAndAttributeWidth(), getTitleAndAttributeHeight() - 5, colorTitle);
			sblabel.append(">");
			label = sblabel.toString();
		} else {
			label = "\"\"";
		}

		if (hasEntryOrExitPoint) {
			printClusterEntryExit(sb, stringBounder);
			subgraphCluster(sb, "ee", label);
		} else {
			sb.append("label=" + label + ";");
			SvekUtils.println(sb);
		}

		// if (hasEntryOrExitPoint) {
		// printClusterEntryExit(sb);
		// subgraphCluster(sb, "ee");
		// }

		if (thereALinkFromOrToGroup2) {
			sb.append(getSpecialPointId(group) + " [shape=point,width=.01,label=\"\"];");
		}
		if (thereALinkFromOrToGroup1) {
			subgraphCluster(sb, "i");
		}
		if (protection1) {
			subgraphCluster(sb, "p1");
		}
		if (skinParam.useSwimlanes(type)) {
			sb.append("{rank = source; ");
			sb.append(getSourceInPoint(type));
			sb.append(" [shape=point,width=.01,label=\"\"];");
			sb.append(getMinPoint(type) + "->" + getSourceInPoint(type) + "  [weight=999];");
			sb.append("}");
			SvekUtils.println(sb);
			sb.append("{rank = sink; ");
			sb.append(getSinkInPoint(type));
			sb.append(" [shape=point,width=.01,label=\"\"];");
			sb.append("}");
			sb.append(getSinkInPoint(type) + "->" + getMaxPoint(type) + "  [weight=999];");
			SvekUtils.println(sb);
		}
		SvekUtils.println(sb);
		printCluster1(sb, lines);
		final boolean added = printCluster2(sb, lines, stringBounder, dotMode, graphvizVersion, type);
		if (hasEntryOrExitPoint && added == false) {
			final String empty = "empty" + color;
			sb.append(empty + " [shape=point,width=.01,label=\"\"];");
		}
		sb.append("}");
		if (protection1) {
			sb.append("}");
		}
		if (thereALinkFromOrToGroup1) {
			sb.append("}");
			sb.append("}");
		}
		if (hasEntryOrExitPoint) {
			sb.append("}");
		}
		if (protection0) {
			sb.append("}");
		}
		SvekUtils.println(sb);
	}

	private void subgraphCluster(StringBuilder sb, String id) {
		subgraphCluster(sb, id, "\"\"");
	}

	private void subgraphCluster(StringBuilder sb, String id, String label) {
		final String uid = getClusterId() + id;
		sb.append("subgraph " + uid + " {");
		sb.append("label=" + label + ";");
	}

	public int getColor() {
		return color;
	}

	public int getTitleColor() {
		return colorTitle;
	}

	private final HtmlColor getBackColor() {
		if (EntityUtils.groupRoot(group)) {
			return null;
		}
		final HtmlColor result = group.getSpecificBackColor();
		if (result != null) {
			return result;
		}
		final Stereotype stereo = group.getStereotype();
		final USymbol sym = group.getUSymbol() == null ? USymbol.PACKAGE : group.getUSymbol();
		final ColorParam backparam = sym.getColorParamBack();
		final HtmlColor c1 = skinParam.getHtmlColor(backparam, stereo, false);
		if (c1 != null) {
			return c1;
		}
		if (parent == null) {
			return null;
		}
		return parent.getBackColor();
	}

	public boolean isClusterOf(IEntity ent) {
		if (ent.isGroup() == false) {
			return false;
		}
		return group == ent;
	}

	public static HtmlColor getStateBackColor(HtmlColor stateBack, ISkinParam skinParam, Stereotype stereotype) {
		if (stateBack == null) {
			stateBack = skinParam.getHtmlColor(ColorParam.packageBackground, stereotype, false);
		}
		if (stateBack == null) {
			stateBack = skinParam.getHtmlColor(ColorParam.background, stereotype, false);
		}
		if (stateBack == null /* || stateBack instanceof HtmlColorTransparent */) {
			stateBack = new HtmlColorTransparent();
		}
		return stateBack;
	}

	public double checkFolderPosition(Point2D pt, StringBounder stringBounder) {
		if (getClusterPosition().isPointJustUpper(pt)) {
			if (ztitle == null) {
				return 0;
			}
			final Dimension2D dimTitle = ztitle.calculateDimension(stringBounder);

			if (pt.getX() < getClusterPosition().getMinX() + dimTitle.getWidth()) {
				return 0;
			}

			return getClusterPosition().getMinY() - pt.getY() + dimTitle.getHeight();
		}
		return 0;
	}

	// public Point2D projection(double x, double y) {
	// final double v1 = Math.abs(minX - x);
	// final double v2 = Math.abs(maxX - x);
	// final double v3 = Math.abs(minY - y);
	// final double v4 = Math.abs(maxY - y);
	// if (v1 <= v2 && v1 <= v3 && v1 <= v4) {
	// return new Point2D.Double(minX, y);
	// }
	// if (v2 <= v1 && v2 <= v3 && v2 <= v4) {
	// return new Point2D.Double(maxX, y);
	// }
	// if (v3 <= v1 && v3 <= v2 && v3 <= v4) {
	// return new Point2D.Double(x, minY);
	// }
	// if (v4 <= v1 && v4 <= v1 && v4 <= v3) {
	// return new Point2D.Double(x, maxY);
	// }
	// throw new IllegalStateException();
	// }

}
