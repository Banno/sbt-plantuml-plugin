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
package net.sourceforge.plantuml.cucadiagram.dot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sourceforge.plantuml.cucadiagram.CucaDiagram;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.svek.GroupPngMakerState;
import net.sourceforge.plantuml.svek.IEntityImage;

public final class CucaDiagramSimplifierState {

	private final CucaDiagram diagram;

	public CucaDiagramSimplifierState(CucaDiagram diagram, List<String> dotStrings) throws IOException,
			InterruptedException {
		this.diagram = diagram;
		boolean changed;
		do {
			changed = false;
			final Collection<IGroup> groups = putConcurrentStateAtEnd(diagram.getGroups(false));
			for (IGroup g : groups) {
				if (diagram.isAutarkic(g)) {
					final IEntityImage img = computeImage(g);
					g.overideImage(img, g.getGroupType() == GroupType.CONCURRENT_STATE ? LeafType.STATE_CONCURRENT
							: LeafType.STATE);

					changed = true;
				}
			}
		} while (changed);
	}

	private Collection<IGroup> putConcurrentStateAtEnd(Collection<IGroup> groups) {
		final List<IGroup> result = new ArrayList<IGroup>();
		final List<IGroup> end = new ArrayList<IGroup>();
		for (IGroup g : groups) {
			if (g.getGroupType() == GroupType.CONCURRENT_STATE) {
				end.add(g);
			} else {
				result.add(g);
			}
		}
		result.addAll(end);
		return result;
	}

	private IEntityImage computeImage(IGroup g) throws IOException, InterruptedException {
		final GroupPngMakerState maker = new GroupPngMakerState(diagram, g);
		return maker.getImage();
	}

}
