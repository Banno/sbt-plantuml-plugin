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

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.activitydiagram3.ftile.BoxStyle;
import net.sourceforge.plantuml.activitydiagram3.ftile.Ftile;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileFactory;
import net.sourceforge.plantuml.activitydiagram3.ftile.FtileKilled;
import net.sourceforge.plantuml.activitydiagram3.ftile.Swimlane;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.sequencediagram.NotePosition;

public class InstructionSimple extends MonoSwimable implements Instruction {

	private boolean killed = false;
	private final Display label;
	private final HtmlColor color;
	private final LinkRendering inlinkRendering;
	private Display note;
	private NotePosition notePosition;
	private final BoxStyle style;
	private final Url url;

	public InstructionSimple(Display label, HtmlColor color, LinkRendering inlinkRendering, Swimlane swimlane,
			BoxStyle style, Url url) {
		super(swimlane);
		this.url = url;
		this.style = style;
		this.label = label;
		this.color = color;
		this.inlinkRendering = inlinkRendering;
	}

	public Ftile createFtile(FtileFactory factory) {
		Ftile result = factory.activity(label, color, getSwimlaneIn(), style);
		if (url != null) {
			result = factory.addUrl(result, url);
		}
		if (note != null) {
			result = factory.addNote(result, note, notePosition);
		}
		if (killed) {
			return new FtileKilled(result);
		}
		return result;
	}

	public void add(Instruction other) {
		throw new UnsupportedOperationException();
	}

	final public boolean kill() {
		this.killed = true;
		return true;
	}

	public LinkRendering getInLinkRendering() {
		return inlinkRendering;
	}

	public void addNote(Display note, NotePosition position) {
		this.note = note;
		this.notePosition = position;
	}

}
