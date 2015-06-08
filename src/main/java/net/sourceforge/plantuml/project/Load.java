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

class Load implements Numeric {

	private final long minuteMen;

	public Load(long minuteMen) {
		this.minuteMen = minuteMen;
	}

	public Load(NumericNumber value) {
		this(value.getIntValue() * 24L * 60 * 60);
	}

	public Numeric add(Numeric other) {
		return new Load(((Load) other).minuteMen + minuteMen);
	}

	public NumericType getNumericType() {
		return NumericType.LOAD;
	}

	public int compareTo(Numeric other) {
		final Load this2 = (Load) other;
		if (this2.minuteMen > minuteMen) {
			return -1;
		}
		if (this2.minuteMen < minuteMen) {
			return 1;
		}
		return 0;
	}

	public final long getMinuteMen() {
		return minuteMen;
	}
	
	@Override
	public String toString() {
		return "LOAD:" + minuteMen / (24 * 60 * 60);
	}



}
