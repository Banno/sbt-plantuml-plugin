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
package net.sourceforge.plantuml.sequencediagram.graphic;

import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.sequencediagram.AbstractMessage;
import net.sourceforge.plantuml.sequencediagram.LifeEvent;
import net.sourceforge.plantuml.sequencediagram.LifeEventType;
import net.sourceforge.plantuml.sequencediagram.MessageNumber;
import net.sourceforge.plantuml.sequencediagram.NotePosition;
import net.sourceforge.plantuml.sequencediagram.Participant;
import net.sourceforge.plantuml.skin.ArrowConfiguration;
import net.sourceforge.plantuml.skin.Component;

abstract class Step1Abstract {

	private final StringBounder stringBounder;

	private final DrawableSet drawingSet;

	private final AbstractMessage message;

	private Frontier freeY2;

	// private ComponentType type;
	private ArrowConfiguration config;

	private Component note;

	private ParticipantRange range;

	Step1Abstract(ParticipantRange range, StringBounder stringBounder, AbstractMessage message, DrawableSet drawingSet,
			Frontier freeY2) {
		if (freeY2 == null) {
			throw new IllegalArgumentException();
		}
		this.range = range;
		this.stringBounder = stringBounder;
		this.message = message;
		this.freeY2 = freeY2;
		this.drawingSet = drawingSet;
	}

	protected final ParticipantRange getParticipantRange() {
		return range;
	}

	abstract Frontier prepareMessage(ConstraintSet constraintSet, InGroupablesStack groupingStructures);

	protected final ArrowConfiguration getConfig() {
		return config;
	}

	protected final void setConfig(ArrowConfiguration config) {
		this.config = config;
	}

	protected final Component getNote() {
		return note;
	}

	protected final void setNote(Component note) {
		this.note = note;
	}

	protected final StringBounder getStringBounder() {
		return stringBounder;
	}

	protected final AbstractMessage getMessage() {
		return message;
	}

	protected final DrawableSet getDrawingSet() {
		return drawingSet;
	}

	protected final Frontier getFreeY() {
		return freeY2;
	}

	protected final void incFreeY(double v) {
		freeY2 = freeY2.add(v, range);
	}

	protected final NoteBox createNoteBox(StringBounder stringBounder, Arrow arrow, Component noteComp,
			NotePosition notePosition, Url url) {
		final LivingParticipantBox p = arrow.getParticipantAt(stringBounder, notePosition);
		final NoteBox noteBox = new NoteBox(arrow.getStartingY(), noteComp, p, null, notePosition, url);

		if (arrow instanceof MessageSelfArrow && notePosition == NotePosition.RIGHT) {
			noteBox.pushToRight(arrow.getPreferredWidth(stringBounder));
		}
		// if (arrow instanceof MessageExoArrow) {
		// final MessageExoType type = ((MessageExoArrow) arrow).getType();
		// if (type.isRightBorder()) {
		// final double width = noteBox.getPreferredWidth(stringBounder);
		// noteBox.pushToRight(-width);
		// }
		// }

		return noteBox;
	}

}
