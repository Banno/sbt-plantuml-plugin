/* ========================================================================
 * PlantUML : a free UML diagram generator
 * ========================================================================
 *
 * (C) Copyright 2009-2017, Arnaud Roques
 *
 * Project Info:  http://plantuml.com
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

class NumericNumber implements Numeric {

	private final int value;

	public NumericNumber(int v) {
		this.value = v;
	}

	public Numeric add(Numeric other) {
		if (other.getNumericType() != getNumericType()) {
			throw new IllegalArgumentException();
		}
		return new NumericNumber(value + ((NumericNumber) other).value);
	}

	public NumericType getNumericType() {
		return NumericType.NUMBER;
	}

	public int getIntValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Number:" + value;
	}

	public int compareTo(Numeric other) {
		final NumericNumber this2 = (NumericNumber) other;
		if (this2.value > value) {
			return -1;
		}
		if (this2.value < value) {
			return 1;
		}
		return 0;
	}

}
