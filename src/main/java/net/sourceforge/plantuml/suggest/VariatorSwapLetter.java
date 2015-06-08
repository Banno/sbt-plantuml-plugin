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

public class VariatorSwapLetter extends VariatorIteratorAdaptor {

	private final String data;
	private int i;

	public VariatorSwapLetter(String data) {
		this.data = data;
		ensureTwoLetters();
	}

	private void ensureTwoLetters() {
		while (i < data.length() - 1 && areTwoLetters() == false) {
			i++;
		}

	}

	private boolean areTwoLetters() {
		return Character.isLetter(data.charAt(i)) && Character.isLetter(data.charAt(i + 1));

	}

	@Override
	Variator getVariator() {
		return new Variator() {
			public String getData() {
				if (i >= data.length() - 1) {
					return null;
				}
				return data.substring(0, i) + data.charAt(i + 1) + data.charAt(i) + data.substring(i + 2);
			}

			public void nextStep() {
				i++;
				ensureTwoLetters();
			}
		};
	}
}
