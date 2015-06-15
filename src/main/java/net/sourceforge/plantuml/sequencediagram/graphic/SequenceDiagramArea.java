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
package net.sourceforge.plantuml.sequencediagram.graphic;

import net.sourceforge.plantuml.graphic.HorizontalAlignment;

public class SequenceDiagramArea {

	private final double sequenceWidth;
	private final double sequenceHeight;

	private double headerWidth;
	private double headerHeight;
	private double headerMargin;

	private double titleWidth;
	private double titleHeight;

	private double footerWidth;
	private double footerHeight;
	private double footerMargin;

	public SequenceDiagramArea(double width, double height) {
		this.sequenceWidth = width;
		this.sequenceHeight = height;
	}

	public void setTitleArea(double titleWidth, double titleHeight) {
		this.titleWidth = titleWidth;
		this.titleHeight = titleHeight;
	}

	public void setHeaderArea(double headerWidth, double headerHeight, double headerMargin) {
		this.headerWidth = headerWidth;
		this.headerHeight = headerHeight;
		this.headerMargin = headerMargin;
	}

	public void setFooterArea(double footerWidth, double footerHeight, double footerMargin) {
		this.footerWidth = footerWidth;
		this.footerHeight = footerHeight;
		this.footerMargin = footerMargin;
	}

	public double getWidth() {
		double result = sequenceWidth;
		if (headerWidth > result) {
			result = headerWidth;
		}
		if (titleWidth > result) {
			result = titleWidth;
		}
		if (footerWidth > result) {
			result = footerWidth;
		}
		return result;
	}

	public double getHeight() {
		return sequenceHeight + headerHeight + headerMargin + titleHeight + footerMargin + footerHeight;
	}

	public double getTitleX() {
		return (getWidth() - titleWidth) / 2;
	}

	public double getTitleY() {
		return headerHeight + headerMargin;
	}

	public double getSequenceAreaX() {
		return (getWidth() - sequenceWidth) / 2;
	}

	public double getSequenceAreaY() {
		return getTitleY() + titleHeight;
	}

	public double getHeaderY() {
		return 0;
	}

	public double getFooterY() {
		return sequenceHeight + headerHeight + headerMargin + titleHeight + footerMargin;
	}

	public double getFooterX(HorizontalAlignment align) {
		if (align == HorizontalAlignment.LEFT) {
			return 0;
		}
		if (align == HorizontalAlignment.RIGHT) {
			return getWidth() - footerWidth;
		}
		if (align == HorizontalAlignment.CENTER) {
			return (getWidth() - footerWidth) / 2;
		}
		throw new IllegalStateException();
	}

	public double getHeaderX(HorizontalAlignment align) {
		if (align == HorizontalAlignment.LEFT) {
			return 0;
		}
		if (align == HorizontalAlignment.RIGHT) {
			return getWidth() - headerWidth;
		}
		if (align == HorizontalAlignment.CENTER) {
			return (getWidth() - headerWidth) / 2;
		}
		throw new IllegalStateException();
	}

}
