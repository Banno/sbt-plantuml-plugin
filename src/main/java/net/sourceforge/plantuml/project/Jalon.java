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
package net.sourceforge.plantuml.project;

import java.util.List;

public class Jalon implements Item {

	private Instant begin;
	private final String code;
	private final Item parent;

	public Jalon(String code, Item parent) {
		this.code = code;
		this.parent = parent;
	}

	public Instant getBegin() {
		return begin;
	}

	public Instant getCompleted() {
		return begin;
	}

	public Duration getDuration() {
		return new Duration(0);
	}

	public Load getLoad() {
		return new Load(0);
	}

	public NumericNumber getWork() {
		return new NumericNumber(1);
	}

	public boolean isLeaf() {
		return true;
	}

	public Item getParent() {
		return parent;
	}

	public List<Item> getChildren() {
		return null;
	}

	public String getCode() {
		return code;
	}

	public boolean isValid() {
		return begin != null;
	}

	public void setInstant(Numeric value) {
		this.begin = (Instant) value;
	}

}
