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
package net.sourceforge.plantuml.core;

public class DiagramDescriptionImpl implements DiagramDescription {

	private final String description;
	private final String cmapData;
	private final Class clazz;

	public DiagramDescriptionImpl(String description, Class clazz) {
		this(description, clazz, null);
	}

	private DiagramDescriptionImpl(String description, Class clazz, String cmapData) {
		this.description = description;
		this.cmapData = cmapData;
		this.clazz = clazz;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return clazz.getSimpleName();
	}

	public String getCmapData() {
		return cmapData;
	}

	public DiagramDescription withCMapData(String cmapData) {
		return new DiagramDescriptionImpl(this.description, this.clazz, cmapData);
	}

	@Override
	public String toString() {
		return description;
	}

}
