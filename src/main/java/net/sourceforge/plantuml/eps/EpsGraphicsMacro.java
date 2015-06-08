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
package net.sourceforge.plantuml.eps;

import java.awt.geom.PathIterator;
import java.util.HashMap;
import java.util.Map;

public class EpsGraphicsMacro extends EpsGraphics {

	private final PostScriptData data = new PostScriptData();
	private final PostScriptCommandMacro rquadto = new PostScriptCommandMacro("rquadto");

	public EpsGraphicsMacro() {
		super();
		rquadto.add(new PostScriptCommandRaw("3 index 3 index 4 2 roll rcurveto", true));
	}

	@Override
	protected void append(String s, boolean checkConsistence) {
		if (checkConsistence && s.indexOf("  ") != -1) {
			throw new IllegalArgumentException(s);
		}
		data.add(new PostScriptCommandRaw(s, checkConsistence));
	}

	@Override
	protected String getBodyString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(rquadto.getPostStringDefinition());
		for (PostScriptCommandMacro macro : macros.keySet()) {
			sb.append(macro.getPostStringDefinition());
		}
		sb.append(data.toPostString());
		return sb.toString();
	}

	// FONT
	private double posX;
	private double posY;
	private int macroCpt;
	private final Map<PostScriptCommandMacro, String> macros = new HashMap<PostScriptCommandMacro, String>();

	@Override
	public void newpath() {
		append("0 setlinewidth", true);
		append("[] 0 setdash", true);
		appendColor(getColor());
		append("newpath", true);
	}

	@Override
	public void closepath() {
		macroInProgress.add(new PostScriptCommandRaw("closepath", true));
		closeMacro();
	}

	@Override
	public void fill(int windingRule) {
		if (windingRule == PathIterator.WIND_EVEN_ODD) {
			append("eofill", true);
		} else if (windingRule == PathIterator.WIND_NON_ZERO) {
			append("fill", true);
		}
	}

	private PostScriptCommandMacro macroInProgress = null;

	@Override
	public void moveto(double x1, double y1) {
		data.add(new PostScriptCommandMoveTo(x1, y1));
		this.posX = x1;
		this.posY = y1;
		openMacro();
		ensureVisible(x1, y1);
	}

	@Override
	public void lineto(double x1, double y1) {
		final PostScriptCommand cmd = new PostScriptCommandLineTo(x1 - posX, y1 - posY);
		macroInProgress.add(cmd);
		this.posX = x1;
		this.posY = y1;
		ensureVisible(x1, y1);
	}

	@Override
	public void curveto(double x1, double y1, double x2, double y2, double x3, double y3) {
		final PostScriptCommandCurveTo cmd = new PostScriptCommandCurveTo(x1 - posX, y1 - posY, x2 - posX, y2 - posY,
				x3 - posX, y3 - posY);
		macroInProgress.add(cmd);
		this.posX = x3;
		this.posY = y3;
		ensureVisible(x1, y1);
		ensureVisible(x2, y2);
		ensureVisible(x3, y3);
	}

	@Override
	public void quadto(double x1, double y1, double x2, double y2) {
		final PostScriptCommandQuadTo cmd = new PostScriptCommandQuadTo(x1 - posX, y1 - posY, x2 - posX, y2 - posY);
		macroInProgress.add(cmd);
		this.posX = x2;
		this.posY = y2;
		ensureVisible(x1, y1);
		ensureVisible(x2, y2);
	}

	private void openMacro() {
		if (macroInProgress != null) {
			throw new IllegalStateException();
		}
		macroInProgress = new PostScriptCommandMacro(macroName());
	}

	private String macroName() {
		return "P$" + Integer.toString(macroCpt, 36);
	}

	private void closeMacro() {
		if (macroInProgress == null) {
			throw new IllegalStateException();
		}
		final String existingName = macros.get(macroInProgress);
		if (existingName == null) {
			macros.put(macroInProgress, macroInProgress.getName());
			append(macroName(), true);
			macroCpt++;
		} else {
			append(existingName, true);
		}
		macroInProgress = null;
	}

	@Override
	protected void epsHLine(double x, double ymin, double ymax) {
		append(format(x) + " " + format(ymin) + " moveto", true);
		int nb = (int) ((ymax - ymin) / (getDashVisible() + getDashSpace()));
		final double lastY = ymin + nb * (getDashVisible() + getDashSpace());
		double v = ymax - lastY;
		if (v > getDashVisible()) {
			v = getDashVisible();
			nb++;
			v = 0;
		}
		append(nb + "{", true);
		append("0 " + format(getDashVisible()) + " rlineto", true);
		append("0 " + format(getDashSpace()) + " rmoveto", true);
		append("} repeat", true);
		if (v > 0) {
			append("0 " + format(v) + " rlineto", true);
		}
	}

	@Override
	protected void epsVLine(double y, double xmin, double xmax) {
		append(format(xmin) + " " + format(y) + " moveto", true);
		int nb = (int) ((xmax - xmin) / (getDashVisible() + getDashSpace()));
		final double lastX = xmin + nb * (getDashVisible() + getDashSpace());
		double v = xmax - lastX;
		if (v > getDashVisible()) {
			v = getDashVisible();
			nb++;
			v = 0;
		}
		append(nb + "{", true);
		append(format(getDashVisible()) + " 0 rlineto", true);
		append(format(getDashSpace()) + " 0 rmoveto", true);
		append("} repeat", true);
		if (v > 0) {
			append(format(v) + " 0 rlineto", true);
		}
	}

}
