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

class Duration implements Numeric {

	private final long minutes;

	public Duration(long minutes) {
		this.minutes = minutes;
	}

	public Duration(NumericNumber value) {
		this(value.getIntValue() * 24L * 60 * 60);
	}

	public Numeric add(Numeric other) {
		return new Duration(((Duration) other).minutes + minutes);
	}

	public static Duration of(long days) {
		return new Duration(days * 24 * 60 * 60);
	}

	public NumericType getNumericType() {
		return NumericType.DURATION;
	}

	public long getMinutes() {
		return minutes;
	}

	@Override
	public String toString() {
		return "DURATION:" + minutes / (24 * 60 * 60);
	}

	public int compareTo(Numeric other) {
		final Duration this2 = (Duration) other;
		if (this2.minutes > minutes) {
			return -1;
		}
		if (this2.minutes < minutes) {
			return 1;
		}
		return 0;
	}

}
