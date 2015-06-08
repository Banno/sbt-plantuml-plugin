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

public class Instant implements Numeric {

	private final Day value;

	public Instant(Day d) {
		this.value = d;
	}

	public Numeric add(Numeric other) {
		throw new UnsupportedOperationException();
	}

	public NumericType getNumericType() {
		return NumericType.INSTANT;
	}

	public Day getDay() {
		return value;
	}

	public Instant next(DayClose dayClose) {
		return new Instant(value.next(dayClose));
	}

	public Instant prev(DayClose dayClose) {
		return new Instant(value.prev(dayClose));
	}

	@Override
	public String toString() {
		return "Instant:" + value;
	}

	public int compareTo(Numeric other) {
		final Instant this2 = (Instant) other;
		return value.compareTo(this2.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		final Instant other = (Instant) obj;
		return value.equals(other.value);
	}

}
