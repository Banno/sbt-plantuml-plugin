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
package net.sourceforge.plantuml.suggest;

public class VariatorAddTwoChar extends VariatorIteratorAdaptor {

	private final String data;
	private final char toAdd;
	private int i;
	private int j = 1;

	public VariatorAddTwoChar(String data, char toAdd) {
		this.data = data;
		this.toAdd = toAdd;
	}

	@Override
	Variator getVariator() {
		return new Variator() {
			public String getData() {
				if (i >= data.length()) {
					return null;
				}
				return data.substring(0, i) + toAdd + data.substring(i, j) + toAdd + data.substring(j);
			}

			public void nextStep() {
				j++;
				if (j > data.length()) {
					i++;
					j = i + 1;
				}
			}
		};
	}
}
