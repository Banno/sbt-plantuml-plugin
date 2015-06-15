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

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.real.Real;
import net.sourceforge.plantuml.sequencediagram.Delay;
import net.sourceforge.plantuml.sequencediagram.Event;
import net.sourceforge.plantuml.skin.Area;
import net.sourceforge.plantuml.skin.Component;
import net.sourceforge.plantuml.skin.ComponentType;
import net.sourceforge.plantuml.skin.Context2D;
import net.sourceforge.plantuml.ugraphic.UGraphic;
import net.sourceforge.plantuml.ugraphic.UTranslate;

public class DelayTile implements Tile, TileWithCallbackY {

	private final Delay delay;
	private final TileArguments tileArguments;
	private Real first;
	private Real last;
	private double y;

	public Event getEvent() {
		return delay;
	}

	public void callbackY(double y) {
		this.y = y;
	}

	public DelayTile(Delay delay, TileArguments tileArguments) {
		this.delay = delay;
		this.tileArguments = tileArguments;
	}

	private void init(StringBounder stringBounder) {
		if (first != null) {
			return;
		}
		this.first = tileArguments.getFirstLivingSpace().getPosC(stringBounder);
		final Component comp = getComponent(stringBounder);
		final Dimension2D dim = comp.getPreferredDimension(stringBounder);
		this.last = tileArguments.getLastLivingSpace().getPosC(stringBounder).addAtLeast(0);
		this.last.ensureBiggerThan(this.first.addFixed(dim.getWidth()));

	}

	private Component getComponent(StringBounder stringBounder) {
		final Component comp = tileArguments.getSkin().createComponent(ComponentType.DELAY_TEXT, null,
				tileArguments.getSkinParam(), delay.getText());
		return comp;
	}

	public void drawU(UGraphic ug) {
		final StringBounder stringBounder = ug.getStringBounder();
		init(stringBounder);
		final Component comp = getComponent(stringBounder);
		final Dimension2D dim = comp.getPreferredDimension(stringBounder);
		final Area area = new Area(last.getCurrentValue() - first.getCurrentValue(), dim.getHeight());
		tileArguments.getLivingSpaces().delayOn(y, dim.getHeight());

		ug = ug.apply(new UTranslate(first.getCurrentValue(), 0));
		comp.drawU(ug, area, (Context2D) ug);
	}

	public double getPreferredHeight(StringBounder stringBounder) {
		final Component comp = getComponent(stringBounder);
		final Dimension2D dim = comp.getPreferredDimension(stringBounder);
		return dim.getHeight();
	}

	public void addConstraints(StringBounder stringBounder) {
	}

	public Real getMinX(StringBounder stringBounder) {
		init(stringBounder);
		return this.first;
	}

	public Real getMaxX(StringBounder stringBounder) {
		init(stringBounder);
		return this.last;
	}

	// private double startingY;
	//
	// public void setStartingY(double startingY) {
	// this.startingY = startingY;
	// }
	//
	// public double getStartingY() {
	// return startingY;
	// }

}
