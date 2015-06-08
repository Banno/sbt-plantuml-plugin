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
package net.sourceforge.plantuml.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.EmptyImageBuilder;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.graphic.StringBounderUtils;

public class Graph2 {

	final private static Graphics2D dummyGraphics2D;

	private final Elastane elastane;
	private int widthCell;
	private int heightCell;

	static {
		final EmptyImageBuilder builder = new EmptyImageBuilder(10, 10, Color.WHITE);
		dummyGraphics2D = builder.getGraphics2D();
	}

	public Graph2(Board board) {
		board.normalize();

		for (ANode n : board.getNodes()) {
			final Dimension2D dim = images(n).getDimension(StringBounderUtils.asStringBounder(dummyGraphics2D));
			widthCell = Math.max(widthCell, (int) dim.getWidth());
			heightCell = Math.max(heightCell, (int) dim.getHeight());
		}
		final Galaxy4 galaxy = new Galaxy4(board, widthCell, heightCell);
		elastane = new Elastane(galaxy);

		for (ANode n : board.getNodes()) {
			final Dimension2D dim = images(n).getDimension(StringBounderUtils.asStringBounder(dummyGraphics2D));
			elastane.addBox(n, (int) dim.getWidth(), (int) dim.getHeight());
		}

		final List<ALink> links = new ArrayList<ALink>(board.getLinks());
		Collections.sort(links, board.getLinkComparator());
		for (ALink link : links) {
			galaxy.addLink(link);
		}

		elastane.init();

	}

	private AbstractEntityImage images(ANode n) {
		return new EntityImageFactory().createEntityImage((IEntity)n.getUserData());
	}

	public Dimension2D getDimension() {
		return elastane.getDimension();

	}

	public void draw(final Graphics2D g2d) {
		elastane.draw(g2d);
	}

}
