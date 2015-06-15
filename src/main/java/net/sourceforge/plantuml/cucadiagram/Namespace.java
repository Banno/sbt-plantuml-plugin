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
package net.sourceforge.plantuml.cucadiagram;

public class Namespace {

	private final String namespace;

	private Namespace(String namespace) {
		if (namespace == null) {
			throw new IllegalArgumentException();
		}
		this.namespace = namespace;
	}

	public final String getNamespace() {
		return namespace;
	}

	public static Namespace of(String namespace) {
		return new Namespace(namespace);
	}

	@Override
	public int hashCode() {
		return namespace.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		final Namespace other = (Namespace) obj;
		return this.namespace.equals(other.namespace);
	}

	public boolean isMain() {
		return namespace.length() == 0;
	}

}
