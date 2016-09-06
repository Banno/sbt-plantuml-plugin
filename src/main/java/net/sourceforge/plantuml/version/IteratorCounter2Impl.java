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
package net.sourceforge.plantuml.version;

import java.util.List;

import net.sourceforge.plantuml.CharSequence2;

public class IteratorCounter2Impl implements IteratorCounter2 {

	private final List<CharSequence2> data;
	private int nb;

	public IteratorCounter2Impl(List<CharSequence2> data) {
		this(data, 0);
	}

	private IteratorCounter2Impl(List<CharSequence2> data, int nb) {
		this.data = data;
		this.nb = nb;
	}

	public int currentNum() {
		return nb;
	}

	public boolean hasNext() {
		return nb < data.size();
	}

	public CharSequence2 next() {
		return data.get(nb++);
	}

	public CharSequence2 peek() {
		return data.get(nb);
	}

	public CharSequence2 peekPrevious() {
		if (nb == 0) {
			return null;
		}
		return data.get(nb - 1);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public IteratorCounter2 cloneMe() {
		return new IteratorCounter2Impl(data, nb);
	}

}
