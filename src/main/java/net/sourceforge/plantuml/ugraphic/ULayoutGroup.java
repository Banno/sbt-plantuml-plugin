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
package net.sourceforge.plantuml.ugraphic;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import net.sourceforge.plantuml.graphic.StringBounder;
import net.sourceforge.plantuml.graphic.TextBlock;

public class ULayoutGroup {

	private final PlacementStrategy placementStrategy;

	public ULayoutGroup(PlacementStrategy placementStrategy) {
		this.placementStrategy = placementStrategy;
	}

	public void drawU(UGraphic ug, double width, double height) {
		for (Map.Entry<TextBlock, Point2D> ent : placementStrategy.getPositions(width, height).entrySet()) {
			final TextBlock block = ent.getKey();
			final Point2D pos = ent.getValue();
			block.drawU(ug.apply(new UTranslate(pos)));
		}
	}

	public void add(TextBlock block) {
		placementStrategy.add(block);

	}

	public Rectangle2D getInnerPosition(String member, double width, double height, StringBounder stringBounder) {
		for (Map.Entry<TextBlock, Point2D> ent : placementStrategy.getPositions(width, height).entrySet()) {
			final TextBlock block = ent.getKey();
			final Rectangle2D result = block.getInnerPosition(member, stringBounder);
			if (result != null) {
				final UTranslate translate = new UTranslate(ent.getValue());
				return translate.apply(result);
			}
		}
		return null;
	}

}
