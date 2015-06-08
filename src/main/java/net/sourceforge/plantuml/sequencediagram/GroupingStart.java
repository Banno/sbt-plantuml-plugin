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
package net.sourceforge.plantuml.sequencediagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.graphic.HtmlColor;

public class GroupingStart extends Grouping {

	private final List<GroupingLeaf> children = new ArrayList<GroupingLeaf>();
	private final HtmlColor backColorGeneral;

	final private GroupingStart parent;

	public GroupingStart(String title, String comment, HtmlColor backColorGeneral, HtmlColor backColorElement,
			GroupingStart parent) {
		super(title, comment, GroupingType.START, backColorElement);
		this.backColorGeneral = backColorGeneral;
		this.parent = parent;
	}

	List<GroupingLeaf> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public void addChildren(GroupingLeaf g) {
		children.add(g);
	}

	public int getLevel() {
		if (parent == null) {
			return 0;
		}
		return parent.getLevel() + 1;
	}

	@Override
	public HtmlColor getBackColorGeneral() {
		return backColorGeneral;
	}

	public boolean dealWith(Participant someone) {
		return false;
	}

	public Url getUrl() {
		return null;
	}
	
	public boolean hasUrl() {
		return false;
	}


	@Override
	public boolean isParallel() {
		return getTitle().equals("par2");
	}

}
