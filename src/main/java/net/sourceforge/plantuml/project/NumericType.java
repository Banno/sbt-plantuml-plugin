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

enum NumericType {

	NUMBER, INSTANT, LOAD, DURATION;

	public NumericType add(NumericType other) {
		if (this == NUMBER) {
			return addNumber(other);
		}
		if (this == INSTANT) {
			return null;
		}
		if (this == LOAD) {
			return addLoad(other);
		}
		if (this == DURATION) {
			return addDuration(other);
		}
		throw new UnsupportedOperationException();

	}

	private NumericType addDuration(NumericType other) {
		if (other == DURATION) {
			return DURATION;
		}
		return null;
	}

	private NumericType addLoad(NumericType other) {
		if (other == LOAD) {
			return LOAD;
		}
		return null;
	}

	private NumericType addNumber(NumericType other) {
		if (other == NUMBER) {
			return NUMBER;
		}
		return null;
	}

}
