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

enum ItemCaract {
	BEGIN(NumericType.INSTANT), //
	COMPLETED(NumericType.INSTANT), //
	DURATION(NumericType.DURATION), //
	LOAD(NumericType.LOAD), //
	WORK(NumericType.NUMBER);

	private final NumericType type;

	private ItemCaract(NumericType type) {
		this.type = type;
	}

	public NumericType getNumericType() {
		return type;
	}

	public Numeric getData(Item item) {
		if (this == BEGIN) {
			return item.getBegin();
		}
		if (this == COMPLETED) {
			return item.getCompleted();
		}
		if (this == DURATION) {
			return item.getDuration();
		}
		if (this == LOAD) {
			return item.getLoad();
		}
		if (this == WORK) {
			return item.getWork();
		}
		throw new UnsupportedOperationException();
	}
}
