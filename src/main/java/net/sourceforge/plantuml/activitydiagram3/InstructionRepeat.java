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

import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.sequencediagram.NotePosition;

public class InstructionRepeat implements Instruction {

	private final InstructionList repeatList = new InstructionList();
	private final Instruction parent;
	private final LinkRendering nextLinkRenderer;
	private final Swimlane swimlane;
	private final HtmlColor color;

	private Display test;
	private Display yes;
	private Display out;
	private LinkRendering endRepeatLinkRendering;
	private LinkRendering backRepeatLinkRendering;

	public InstructionRepeat(Swimlane swimlane, Instruction parent, LinkRendering nextLinkRenderer, HtmlColor color) {
		this.parent = parent;
		this.swimlane = swimlane;
		this.nextLinkRenderer = nextLinkRenderer;
		this.color = color;
	}

	public void add(Instruction ins) {
		repeatList.add(ins);
	}

	public Ftile createFtile(FtileFactory factory) {
		return factory.repeat(swimlane, factory.decorateOut(repeatList.createFtile(factory), endRepeatLinkRendering),
				test, yes, out, color, backRepeatLinkRendering);
	}

	public Instruction getParent() {
		return parent;
	}

	public void setTest(Display test, Display yes, Display out, LinkRendering endRepeatLinkRendering, LinkRendering backRepeatLinkRendering) {
		this.test = test;
		this.yes = yes;
		this.out = out;
		this.endRepeatLinkRendering = endRepeatLinkRendering;
		this.backRepeatLinkRendering = backRepeatLinkRendering;
	}

	final public boolean kill() {
		return repeatList.kill();
	}

	public LinkRendering getInLinkRendering() {
		return nextLinkRenderer;
	}

	public void addNote(Display note, NotePosition position) {
		repeatList.addNote(note, position);
	}

	public Set<Swimlane> getSwimlanes() {
		return repeatList.getSwimlanes();
	}

	public Swimlane getSwimlaneIn() {
		return parent.getSwimlaneOut();
	}

	public Swimlane getSwimlaneOut() {
		return repeatList.getSwimlaneOut();
	}

}
