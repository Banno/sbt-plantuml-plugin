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
package net.sourceforge.plantuml.svek;

public class PortGeometry {

	private final double position;
	private final double height;

	public PortGeometry(double position, double height) {
		this.position = position;
		this.height = height;
	}

	public PortGeometry translateY(double deltaY) {
		return new PortGeometry(position + deltaY, height);
	}

	@Override
	public String toString() {
		return "pos=" + position + " height=" + height;
	}

	public double getHeight() {
		return height;
	}

	public double getPosition() {
		return position;
	}

	public double getLastY() {
		return position + height;
	}

}
