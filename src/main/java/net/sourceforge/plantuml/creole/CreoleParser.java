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
package net.sourceforge.plantuml.creole;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.plantuml.EmbededDiagram;
import net.sourceforge.plantuml.ISkinSimple;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.FontConfiguration;
import net.sourceforge.plantuml.graphic.HorizontalAlignment;

public class CreoleParser {

	private final FontConfiguration fontConfiguration;
	private final ISkinSimple skinParam;
	private final HorizontalAlignment horizontalAlignment;
	private final boolean modeSimpleLine;

	public CreoleParser(FontConfiguration fontConfiguration, HorizontalAlignment horizontalAlignment,
			ISkinSimple skinParam, boolean modeSimpleLine) {
		this.modeSimpleLine = modeSimpleLine;
		this.fontConfiguration = fontConfiguration;
		this.skinParam = skinParam;
		this.horizontalAlignment = horizontalAlignment;
	}

	private Stripe createStripe(String line, CreoleContext context, Stripe lastStripe) {
		if (lastStripe instanceof StripeTable && line.startsWith("|") && line.endsWith("|")) {
			final StripeTable table = (StripeTable) lastStripe;
			table.analyzeAndAddNormal(line);
			return null;
		} else if (lastStripe instanceof StripeTree && isTreeStart(StringUtils.trinNoTrace(line))) {
			final StripeTree tree = (StripeTree) lastStripe;
			tree.analyzeAndAdd(line);
			return null;
		} else if (line.startsWith("|=") && line.endsWith("|")) {
			return new StripeTable(fontConfiguration, skinParam, line);
		} else if (isTreeStart(line)) {
			return new StripeTree(fontConfiguration, skinParam, line);
		}
		return new CreoleStripeSimpleParser(line, context, fontConfiguration, skinParam, modeSimpleLine)
				.createStripe(context);
	}

	public static boolean isTreeStart(String line) {
		// return false;
		return line.startsWith("|_");
	}

	public Sheet createSheet(Display display) {
		final Sheet sheet = new Sheet(horizontalAlignment);
		if (display != null) {
			final CreoleContext context = new CreoleContext();
			for (CharSequence cs : display) {
				final Stripe stripe;
				if (cs instanceof EmbededDiagram) {
					final Atom atom = new AtomEmbededSystem((EmbededDiagram) cs);
					stripe = new Stripe() {
						public List<Atom> getAtoms() {
							return Arrays.asList(atom);
						}
					};
				} else {
					stripe = createStripe(cs.toString(), context, sheet.getLastStripe());
				}
				if (stripe != null) {
					sheet.add(stripe);
				}
			}
		}
		return sheet;
	}
}
