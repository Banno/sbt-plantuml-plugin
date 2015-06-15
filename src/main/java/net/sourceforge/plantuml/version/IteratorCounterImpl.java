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
package net.sourceforge.plantuml.version;

import java.util.List;

public class IteratorCounterImpl implements IteratorCounter {

	private final List<String> data;
	private int nb;

	public IteratorCounterImpl(List<String> data) {
		this(data, 0);
	}

	private IteratorCounterImpl(List<String> data, int nb) {
		this.data = data;
		this.nb = nb;
	}

	public int currentNum() {
		return nb;
	}

	public boolean hasNext() {
		return nb < data.size();
	}

	public String next() {
		return data.get(nb++);
	}

	public String peek() {
		return data.get(nb);
	}

	public String peekPrevious() {
		if (nb == 0) {
			return null;
		}
		return data.get(nb - 1);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

	public IteratorCounter cloneMe() {
		return new IteratorCounterImpl(data, nb);
	}

}
