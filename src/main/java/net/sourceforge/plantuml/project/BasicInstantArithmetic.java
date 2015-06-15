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

class BasicInstantArithmetic implements InstantArithmetic {

	private final DayClose dayClose;

	BasicInstantArithmetic(DayClose dayClose) {
		if (dayClose == null) {
			throw new IllegalArgumentException();
		}
		this.dayClose = dayClose;
	}

	public Instant add(Instant i1, Duration duration) {
		Instant result = i1;
		final long min = duration.getMinutes();
		if (min < 0) {
			throw new IllegalArgumentException();
		}
		for (long i = 0; i < min; i += 24 * 60 * 60) {
			result = result.next(dayClose);
		}
		return result;
	}

	public Instant sub(Instant i1, Duration duration) {
		Instant result = i1;
		final long min = duration.getMinutes();
		if (min < 0) {
			throw new IllegalArgumentException();
		}
		for (long i = 0; i < min; i += 24 * 60 * 60) {
			result = result.prev(dayClose);
		}
		return result;
	}

	public Duration diff(Instant i1, Instant i2) {
		if (i2.compareTo(i1) < 0) {
			throw new IllegalArgumentException();
		}
		long minutes = 0;
		while (i2.compareTo(i1) > 0) {
			minutes += 24 * 60 * 60;
			i1 = i1.next(null);
		}
		return new Duration(minutes);
	}
}
