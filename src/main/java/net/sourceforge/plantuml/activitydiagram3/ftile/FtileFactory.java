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

import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.activitydiagram3.Branch;
import net.sourceforge.plantuml.activitydiagram3.LinkRendering;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.sequencediagram.NotePosition;

public interface FtileFactory extends ISkinSimple {

	public StringBounder getStringBounder();

	public boolean shadowing();

	public Ftile start(Swimlane swimlane);

	public Ftile stop(Swimlane swimlane);

	public Ftile end(Swimlane swimlane);

	public Ftile activity(Display label, HtmlColor color, Swimlane swimlane, BoxStyle style);

	public Ftile addNote(Ftile ftile, Display note, NotePosition notePosition);

	public Ftile addUrl(Ftile ftile, Url url);

	public Ftile decorateIn(Ftile ftile, LinkRendering linkRendering);

	public Ftile decorateOut(Ftile ftile, LinkRendering linkRendering);

	public Ftile assembly(Ftile tile1, Ftile tile2);

	public Ftile repeat(Swimlane swimlane, Ftile repeat, Display test, Display yes, Display out, HtmlColor color, LinkRendering backRepeatLinkRendering);

	public Ftile createWhile(Swimlane swimlane, Ftile whileBlock, Display test, Display yes, Display out,
			LinkRendering afterEndwhile, HtmlColor color);

	public Ftile createIf(Swimlane swimlane, List<Branch> thens, Branch elseBranch);

	public Ftile createFork(Swimlane swimlane, List<Ftile> all);

	public Ftile createSplit(List<Ftile> all);

	public Ftile createGroup(Ftile list, Display name, HtmlColor backColor, HtmlColor titleColor, Display headerNote);

}
