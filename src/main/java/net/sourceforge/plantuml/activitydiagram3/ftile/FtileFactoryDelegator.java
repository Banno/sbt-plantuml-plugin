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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.util.List;

import net.sourceforge.plantuml.ColorParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.skin.rose.Rose;
import net.sourceforge.plantuml.ugraphic.Sprite;

public class FtileFactoryDelegator implements FtileFactory {

	private final FtileFactory factory;
	private final ISkinParam skinParam;
	private final Rose rose = new Rose();

	protected HtmlColor getInLinkRenderingColor(Ftile tile) {
		final HtmlColor color;
		final LinkRendering linkRendering = tile.getInLinkRendering();
		if (linkRendering == null || linkRendering.getColor() == null) {
			color = rose.getHtmlColor(getSkinParam(), ColorParam.activityArrow);
		} else {
			color = linkRendering.getColor();
		}
		return color;
	}

	protected Display getInLinkRenderingDisplay(Ftile tile) {
		final LinkRendering linkRendering = tile.getInLinkRendering();
		if (linkRendering == null) {
			return null;
		}
		return linkRendering.getDisplay();
	}

	public FtileFactoryDelegator(FtileFactory factory, ISkinParam skinParam) {
		this.factory = factory;
		this.skinParam = skinParam;
	}

	public Ftile start(Swimlane swimlane) {
		return factory.start(swimlane);
	}

	public Ftile end(Swimlane swimlane) {
		return factory.end(swimlane);
	}

	public Ftile stop(Swimlane swimlane) {
		return factory.stop(swimlane);
	}

	public Ftile activity(Display label, HtmlColor color, Swimlane swimlane, BoxStyle style) {
		return factory.activity(label, color, swimlane, style);
	}

	public Ftile addNote(Ftile ftile, Display note, NotePosition notePosition) {
		return factory.addNote(ftile, note, notePosition);
	}

	public Ftile addUrl(Ftile ftile, Url url) {
		return factory.addUrl(ftile, url);
	}

	public Ftile decorateIn(Ftile ftile, LinkRendering linkRendering) {
		return factory.decorateIn(ftile, linkRendering);
	}

	public Ftile decorateOut(Ftile ftile, LinkRendering linkRendering) {
		return factory.decorateOut(ftile, linkRendering);
	}

	public Ftile assembly(Ftile tile1, Ftile tile2) {
		return factory.assembly(tile1, tile2);
	}

	public Ftile repeat(Swimlane swimlane, Ftile repeat, Display test, Display yes, Display out, HtmlColor color, LinkRendering backRepeatLinkRendering) {
		return factory.repeat(swimlane, repeat, test, yes, out, color, backRepeatLinkRendering);
	}

	public Ftile createWhile(Swimlane swimlane, Ftile whileBlock, Display test, Display yes, Display out,
			LinkRendering afterEndwhile, HtmlColor color) {
		return factory.createWhile(swimlane, whileBlock, test, yes, out, afterEndwhile, color);
	}

	public Ftile createIf(Swimlane swimlane, List<Branch> thens, Branch elseBranch) {
		return factory.createIf(swimlane, thens, elseBranch);
	}

	public Ftile createFork(Swimlane swimlane, List<Ftile> all) {
		return factory.createFork(swimlane, all);
	}

	public Ftile createSplit(List<Ftile> all) {
		return factory.createSplit(all);
	}

	public Ftile createGroup(Ftile list, Display name, HtmlColor backColor, HtmlColor titleColor, Display headerNote) {
		return factory.createGroup(list, name, backColor, titleColor, headerNote);
	}

	public StringBounder getStringBounder() {
		return factory.getStringBounder();
	}

	protected final ISkinParam getSkinParam() {
		return skinParam;
	}

	protected final Rose getRose() {
		return rose;
	}

	public boolean shadowing() {
		return skinParam.shadowing();
	}

	protected FtileFactory getFactory() {
		return factory;
	}

	public Sprite getSprite(String name) {
		return skinParam.getSprite(name);
	}

	public String getValue(String key) {
		return skinParam.getValue(key);
	}

	public double getPadding() {
		return skinParam.getPadding();
	}

	public boolean useGuillemet() {
		return skinParam.useGuillemet();
	}

}
