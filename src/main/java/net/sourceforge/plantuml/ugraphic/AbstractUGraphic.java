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

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractUGraphic<O> extends AbstractCommonUGraphic {

	private final O g2d;

	private final Map<Class<? extends UShape>, UDriver<O>> drivers = new HashMap<Class<? extends UShape>, UDriver<O>>();

	public AbstractUGraphic(ColorMapper colorMapper, O g2d) {
		super(colorMapper);
		this.g2d = g2d;
	}

	protected AbstractUGraphic(AbstractUGraphic<O> other) {
		super(other);
		this.g2d = other.g2d;
		// this.drivers.putAll(other.drivers);
	}

	protected final O getGraphicObject() {
		return g2d;
	}

	protected boolean manageHiddenAutomatically() {
		return true;
	}

	final protected void registerDriver(Class<? extends UShape> cl, UDriver<O> driver) {
		this.drivers.put(cl, driver);
	}

	public final void draw(UShape shape) {
		if (shape instanceof UEmpty) {
			return;
		}
		final UDriver<O> driver = drivers.get(shape.getClass());
		if (driver == null) {
			throw new UnsupportedOperationException(shape.getClass().toString() + " " + this.getClass());
		}
		if (getParam().isHidden() && manageHiddenAutomatically()) {
			return;
		}
		beforeDraw();
		if (shape instanceof Scalable) {
			final double scale = getParam().getScale();
			shape = ((Scalable) shape).getScaled(scale);
			driver.draw(shape, getTranslateX(), getTranslateY(), getColorMapper(), getParam(), g2d);
		} else {
			driver.draw(shape, getTranslateX(), getTranslateY(), getColorMapper(), getParam(), g2d);
		}
		afterDraw();
	}

	protected void beforeDraw() {
	}

	protected void afterDraw() {
	}

}
