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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.util.Collection;
import java.util.List;

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.activitydiagram3.PositionedNote;
import net.sourceforge.plantuml.creole.CreoleMode;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.HtmlColorAndStyle;
import net.sourceforge.plantuml.graphic.Rainbow;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.color.Colors;
import net.sourceforge.plantuml.skin.rose.Rose;

public class FtileFactoryDelegator implements FtileFactory {

	private final FtileFactory factory;

	private final Rose rose = new Rose();

	protected final Rainbow getInLinkRenderingColor(Ftile tile) {
		Rainbow color;
		final LinkRendering linkRendering = tile.getInLinkRendering();
		if (linkRendering == null) {
			color = HtmlColorAndStyle.build(skinParam());
		} else {
			color = linkRendering.getRainbow();
		}
		if (color.size() == 0) {
			color = HtmlColorAndStyle.build(skinParam());
		}
		return color;
	}

	protected final TextBlock getTextBlock(Display display) {
		if (Display.isNull(display)) {
			return null;
		}
		final FontConfiguration fontConfiguration = new FontConfiguration(skinParam(), FontParam.ACTIVITY_ARROW, null);
		return display.create(fontConfiguration, HorizontalAlignment.LEFT, skinParam(), CreoleMode.SIMPLE_LINE);
	}

	protected Display getInLinkRenderingDisplay(Ftile tile) {
		final LinkRendering linkRendering = tile.getInLinkRendering();
		if (linkRendering == null) {
			return Display.NULL;
		}
		return linkRendering.getDisplay();
	}

	public FtileFactoryDelegator(FtileFactory factory) {
		this.factory = factory;
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

	public Ftile activity(Display label, Swimlane swimlane, BoxStyle style, Colors colors) {
		return factory.activity(label, swimlane, style, colors);
	}

	public Ftile addNote(Ftile ftile, Swimlane swimlane, Collection<PositionedNote> notes) {
		return factory.addNote(ftile, swimlane, notes);
	}

	public Ftile addUrl(Ftile ftile, Url url) {
		return factory.addUrl(ftile, url);
	}

	public Ftile decorateIn(Ftile ftile, LinkRendering linkRendering) {
		if (linkRendering == null) {
			throw new IllegalArgumentException();
		}
		return factory.decorateIn(ftile, linkRendering);
	}

	public Ftile decorateOut(Ftile ftile, LinkRendering linkRendering) {
		if (linkRendering == null) {
			throw new IllegalArgumentException();
		}
		return factory.decorateOut(ftile, linkRendering);
	}

	public Ftile assembly(Ftile tile1, Ftile tile2) {
		return factory.assembly(tile1, tile2);
	}

	public Ftile repeat(Swimlane swimlane, Swimlane swimlaneOut, Ftile repeat, Display test, Display yes, Display out,
			HtmlColor color, LinkRendering backRepeatLinkRendering) {
		return factory.repeat(swimlane, swimlaneOut, repeat, test, yes, out, color, backRepeatLinkRendering);
	}

	public Ftile createWhile(Swimlane swimlane, Ftile whileBlock, Display test, Display yes, Display out,
			LinkRendering afterEndwhile, HtmlColor color) {
		return factory.createWhile(swimlane, whileBlock, test, yes, out, afterEndwhile, color);
	}

	public Ftile createIf(Swimlane swimlane, List<Branch> thens, Branch elseBranch, LinkRendering afterEndwhile,
			LinkRendering topInlinkRendering) {
		return factory.createIf(swimlane, thens, elseBranch, afterEndwhile, topInlinkRendering);
	}

	public Ftile createFork(Swimlane swimlane, List<Ftile> all) {
		return factory.createFork(swimlane, all);
	}

	public Ftile createSplit(List<Ftile> all) {
		return factory.createSplit(all);
	}

	public Ftile createGroup(Ftile list, Display name, HtmlColor backColor, HtmlColor titleColor, Display headerNote,
			HtmlColor borderColor) {
		return factory.createGroup(list, name, backColor, titleColor, headerNote, borderColor);
	}

	public StringBounder getStringBounder() {
		return factory.getStringBounder();
	}

	protected final Rose getRose() {
		return rose;
	}

	public final ISkinParam skinParam() {
		return factory.skinParam();
	}

	protected FtileFactory getFactory() {
		return factory;
	}
}
