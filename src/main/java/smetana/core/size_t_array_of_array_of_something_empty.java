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

package smetana.core;

import smetana.core.amiga.StarStar;

public class size_t_array_of_array_of_something_empty implements size_t {

	final private int nb;
	final private Class cl;

	public size_t_array_of_array_of_something_empty(Class cl, int nb) {
		this.nb = nb;
		this.cl = cl;
	}

	public boolean isZero() {
		return nb == 0;
	}

	public __ptr__ realloc(Object old) {
		JUtils.LOG("realloc old1=" + old);
		if (nb == 0) {
			return null;
		}
		final StarStar data = (StarStar) old;
		data.realloc(nb);
		return data;
	}

	public __ptr__ malloc() {
		return StarStar.array_of_array_of_something_empty(cl, nb);
	}

	public size_t negate() {
		throw new UnsupportedOperationException();
	}

	public size_t plus(int length) {
		throw new UnsupportedOperationException();
	}

	public boolean isStrictPositive() {
		throw new UnsupportedOperationException();
	}

	public boolean isStrictNegative() {
		throw new UnsupportedOperationException();
	}

	public final int getNb() {
		return nb;
	}

}
