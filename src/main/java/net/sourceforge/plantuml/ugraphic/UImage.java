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
package net.sourceforge.plantuml.ugraphic;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class UImage implements UShape {

	private final BufferedImage image;

	public UImage(BufferedImage image) {
		this.image = image;
	}

	public UImage(BufferedImage before, double scale) {
		if (scale == 1) {
			this.image = before;
			return;
		}

		final int w = (int) Math.round(before.getWidth() * scale);
		final int h = (int) Math.round(before.getHeight() * scale);
		final BufferedImage after = new BufferedImage(w, h, before.getType());
		final AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		final AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		this.image = scaleOp.filter(before, after);
	}

	public UImage scale(double scale) {
		return new UImage(image, scale);
	}

	public final BufferedImage getImage() {
		return image;
	}

	public double getWidth() {
		return image.getWidth()-1;
	}

	public double getHeight() {
		return image.getHeight()-1;
	}

}
