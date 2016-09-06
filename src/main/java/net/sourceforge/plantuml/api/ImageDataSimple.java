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
package net.sourceforge.plantuml.api;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.core.ImageData;

public class ImageDataSimple implements ImageData {

	private final int width;
	private final int height;

	public ImageDataSimple(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public ImageDataSimple() {
		this(0, 0);
	}

	public ImageDataSimple(Dimension2D dim) {
		this((int) dim.getWidth(), (int) dim.getHeight());
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean containsCMapData() {
		return false;
	}

	public String getCMapData(String nameId) {
		throw new UnsupportedOperationException();
	}

	public String getWarningOrError() {
		return null;
	}

}
