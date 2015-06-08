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
package net.sourceforge.plantuml.ugraphic;

import net.sourceforge.plantuml.graphic.UGraphicDelegator;

public class UGraphicCompress extends UGraphicDelegator {

	public UGraphic apply(UChange change) {
		if (change instanceof UTranslate) {
			return new UGraphicCompress(getUg(), compressionTransform, translate.compose((UTranslate) change));
		} else if (change instanceof UStroke || change instanceof UChangeBackColor || change instanceof UChangeColor) {
			return new UGraphicCompress(getUg().apply(change), compressionTransform, translate);
		}
		throw new UnsupportedOperationException();
	}

	private final CompressionTransform compressionTransform;
	private final UTranslate translate;

	public UGraphicCompress(UGraphic ug, CompressionTransform compressionTransform) {
		this(ug, compressionTransform, new UTranslate());
	}

	private UGraphicCompress(UGraphic ug, CompressionTransform compressionTransform, UTranslate translate) {
		super(ug);
		this.compressionTransform = compressionTransform;
		this.translate = translate;
	}

	public void draw(UShape shape) {
		final double x = translate.getDx();
		final double y = translate.getDy();
		if (shape instanceof ULine) {
			drawLine(x, y, (ULine) shape);
		} else {
			getUg().apply(new UTranslate(x, ct(y))).draw(shape);
		}
	}

	private void drawLine(double x, double y, ULine shape) {
		drawLine(x, ct(y), x + shape.getDX(), ct(y + shape.getDY()));
	}

	private double ct(double v) {
		return compressionTransform.transform(v);
	}

	private void drawLine(double x1, double y1, double x2, double y2) {
		if (y1 > y2) {
			drawLine(x2, y2, x1, y1);
			return;
		}
		assert y1 <= y2;
		final double xmin = Math.min(x1, x2);
		final double xmax = Math.max(x1, x2);
		final double ymin = Math.min(y1, y2);
		final double ymax = Math.max(y1, y2);
		if (x2 >= x1) {
			getUg().apply(new UTranslate(xmin, ymin)).draw(new ULine(xmax - xmin, ymax - ymin));
		} else {
			getUg().apply(new UTranslate(xmax, ymin)).draw(new ULine(-(xmax - xmin), ymax - ymin));
		}
	}

}
