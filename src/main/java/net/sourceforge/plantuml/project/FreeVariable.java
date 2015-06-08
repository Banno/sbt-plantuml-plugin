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

class FreeVariable implements Expression {

	private Expression value;
	private final String name;
	private final NumericType type;

	public FreeVariable(String name, NumericType type) {
		this.name = name;
		this.type = type;
	}

	public String getDescription() {
		return "$" + name + "=" + (value == null ? "null" : value.getDescription());
	}

	public NumericType getNumericType() {
		return type;
	}

	public Numeric getValue() {
		if (value == null) {
			return null;
		}
		return value.getValue();
	}

	public void setValue(Expression expression) {
		if (expression.getNumericType() != type) {
			throw new IllegalArgumentException("Bad type");
		}
		this.value = expression;
	}

}
