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
package net.sourceforge.plantuml.api;

import java.awt.geom.Dimension2D;

import net.sourceforge.plantuml.CMapData;
import net.sourceforge.plantuml.core.ImageData;

public class ImageDataComplex implements ImageData {

	private final Dimension2D info;
	private final CMapData cmap;
	private final String warningOrError;

//	public ImageDataComplex(Dimension2D info, CMapData cmap) {
//		this(info, cmap, null);
//	}

	public ImageDataComplex(Dimension2D info, CMapData cmap, String warningOrError) {
		if (info==null) {
			throw new IllegalArgumentException();
		}
		this.info = info;
		this.cmap = cmap;
		this.warningOrError = warningOrError;
	}

	public int getWidth() {
		return (int) info.getWidth();
	}

	public int getHeight() {
		return (int) info.getHeight();
	}

	public boolean containsCMapData() {
		return cmap != null && cmap.containsData();
	}

	public String getCMapData(String nameId) {
		return cmap.asString(nameId);
	}

	public String getWarningOrError() {
		return warningOrError;
	}

}
