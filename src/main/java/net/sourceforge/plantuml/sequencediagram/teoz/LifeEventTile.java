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
package net.sourceforge.plantuml.sequencediagram.teoz;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.sequencediagram.LifeEvent;
import net.sourceforge.plantuml.sequencediagram.LifeEventType;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.skin.Skin;
import net.sourceforge.plantuml.ugraphic.UGraphic;

public class LifeEventTile implements TileWithUpdateStairs {

	private final LifeEvent lifeEvent;
	private final TileArguments tileArguments;
	private final LivingSpace livingSpace;
	private final Skin skin;
	private final ISkinParam skinParam;

	public void updateStairs(StringBounder stringBounder, double y) {
		System.err.println("LifeEventTile::updateStairs " + lifeEvent + " " + livingSpace.getParticipant() + " y=" + y);
		livingSpace.addStepForLivebox(getEvent(), y);
	}

	public Event getEvent() {
		return lifeEvent;
	}

	public LifeEventTile(LifeEvent lifeEvent, TileArguments tileArguments, LivingSpace livingSpace, Skin skin,
			ISkinParam skinParam) {
		this.lifeEvent = lifeEvent;
		this.tileArguments = tileArguments;
		this.livingSpace = livingSpace;
		this.skin = skin;
		this.skinParam = skinParam;
	}

	public void drawU(UGraphic ug) {
	}

	public double getPreferredHeight(StringBounder stringBounder) {
		if (lifeEvent.isActivate()) {
			return 20;
		}
		return 0;
	}

	public void addConstraints(StringBounder stringBounder) {
	}

	public Real getMinX(StringBounder stringBounder) {
		return tileArguments.getLivingSpace(lifeEvent.getParticipant()).getPosB();
	}

	public Real getMaxX(StringBounder stringBounder) {
		return tileArguments.getLivingSpace(lifeEvent.getParticipant()).getPosD(stringBounder);
	}

}
