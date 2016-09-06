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
package net.sourceforge.plantuml.ugraphic.sprite;

import java.awt.image.BufferedImage;
import java.util.List;

public class SpriteUtils {

	public static final String SPRITE_NAME = "[-\\p{L}0-9_/]+";

	private SpriteUtils() {
	}

	public static String encode(BufferedImage img, String name, SpriteGrayLevel level) {
		final StringBuilder sb = new StringBuilder();
		sb.append("sprite $" + name + " [" + img.getWidth() + "x" + img.getHeight() + "/" + level.getNbColor()
				+ "] {\n");
		final List<String> result = level.encode(img);
		for (String s : result) {
			sb.append(s);
			sb.append("\n");
		}
		sb.append("}\n");
		return sb.toString();
	}

	public static String encodeCompressed(BufferedImage img, String name, SpriteGrayLevel level) {
		final StringBuilder sb = new StringBuilder();
		sb.append("sprite $" + name + " [" + img.getWidth() + "x" + img.getHeight() + "/" + level.getNbColor() + "z] ");
		final List<String> list = level.encodeZ(img);
		if (list.size() == 1) {
			sb.append(list.get(0));
			sb.append("\n");
		} else {
			sb.append("{\n");
			for (String s : list) {
				sb.append(s);
				sb.append("\n");
			}
			sb.append("}\n");
		}
		return sb.toString();
	}

}
