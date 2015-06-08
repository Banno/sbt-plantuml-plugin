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

import net.sourceforge.plantuml.graphic.HtmlColor;

public abstract class Grouping implements Event {

	private final String title;
	private final GroupingType type;
	private final String comment;
	private final HtmlColor backColorElement;

	public Grouping(String title, String comment, GroupingType type,
			HtmlColor backColorElement) {
		this.title = title;
		this.comment = comment;
		this.type = type;
		this.backColorElement = backColorElement;
	}

	@Override
	public final String toString() {
		return super.toString() + " " + type + " " + title;
	}

	final public String getTitle() {
		return title;
	}

	final public GroupingType getType() {
		return type;
	}

	public abstract int getLevel();

	public abstract HtmlColor getBackColorGeneral();
	
	final public String getComment() {
		return comment;
	}

	public final HtmlColor getBackColorElement() {
		return backColorElement;
	}
	
	public abstract boolean isParallel(); 

}
