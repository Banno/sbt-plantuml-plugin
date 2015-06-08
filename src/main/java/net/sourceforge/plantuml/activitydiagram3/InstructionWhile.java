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
package net.sourceforge.plantuml.activitydiagram3;

import java.util.Set;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.activitydiagram3.ftile.vcompact.FtileWithNoteOpale;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.sequencediagram.NotePosition;

public class InstructionWhile implements Instruction {

	private final InstructionList repeatList = new InstructionList();
	private final Instruction parent;
	private final LinkRendering nextLinkRenderer;
	private final HtmlColor color;

	private final Display test;
	private final Display yes;
	private Display out;
	private LinkRendering endInlinkRendering;
	private LinkRendering afterEndwhile;
	private final Swimlane swimlane;
	private final ISkinParam skinParam;

	public InstructionWhile(Swimlane swimlane, Instruction parent, Display test, LinkRendering nextLinkRenderer,
			Display yes, HtmlColor color, ISkinParam skinParam) {
		this.parent = parent;
		this.test = test;
		this.nextLinkRenderer = nextLinkRenderer;
		this.yes = yes;
		this.swimlane = swimlane;
		this.color = color;
		this.skinParam = skinParam;
	}

	public void add(Instruction ins) {
		repeatList.add(ins);
	}

	private Display note;
	private NotePosition position;

	public Ftile createFtile(FtileFactory factory) {
		Ftile tmp = factory.decorateOut(repeatList.createFtile(factory), endInlinkRendering);
		tmp = factory.createWhile(swimlane, tmp, test, yes, out, afterEndwhile, color);
		if (note != null) {
			tmp = new FtileWithNoteOpale(tmp, note, position, skinParam, false);
		}
		// tmp = factory.decorateOut(tmp, afterEndwhile);
		return tmp;
	}

	public Instruction getParent() {
		return parent;
	}

	final public boolean kill() {
		return repeatList.kill();
	}

	public LinkRendering getInLinkRendering() {
		return nextLinkRenderer;
	}

	public void endwhile(LinkRendering nextLinkRenderer, Display out) {
		this.endInlinkRendering = nextLinkRenderer;
		this.out = out;
	}

	public void afterEndwhile(LinkRendering linkRenderer) {
		this.afterEndwhile = linkRenderer;
	}

	public void addNote(Display note, NotePosition position) {
		if (repeatList.isEmpty()) {
			this.note = note;
			this.position = position;
		} else {
			repeatList.addNote(note, position);
		}
	}

	public Set<Swimlane> getSwimlanes() {
		return repeatList.getSwimlanes();
	}

	public Swimlane getSwimlaneIn() {
		return parent.getSwimlaneOut();
	}

	public Swimlane getSwimlaneOut() {
		return getSwimlaneIn();
	}

}
