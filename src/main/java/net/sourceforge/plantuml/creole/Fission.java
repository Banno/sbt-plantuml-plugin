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
package net.sourceforge.plantuml.creole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.graphic.StringBounder;

public class Fission {

	private final Stripe stripe;
	private final double maxWidth;

	public Fission(Stripe stripe, double maxWidth) {
		this.stripe = stripe;
		this.maxWidth = maxWidth;
	}

	public List<Stripe> getSplitted(StringBounder stringBounder) {
		if (maxWidth == 0) {
			return Arrays.asList(stripe);
		}
		final List<Stripe> result = new ArrayList<Stripe>();
		StripeSimple current = new StripeSimple();
		for (Atom a1 : stripe.getAtoms()) {
			for (Atom atom : getSplitted(stringBounder, a1)) {
				final double width = atom.calculateDimension(stringBounder).getWidth();
				if (current.totalWidth + width > maxWidth) {
					result.add(current);
					current = new StripeSimple();
				}
				current.addAtom(atom, width);
			}
		}
		if (current.totalWidth > 0) {
			result.add(current);
		}
		return Collections.unmodifiableList(result);
	}

	private Collection<? extends Atom> getSplitted(StringBounder stringBounder, Atom atom) {
		if (atom instanceof AtomText) {
			return ((AtomText) atom).getSplitted(stringBounder, maxWidth);
		}
		return Collections.singleton(atom);
	}

	private List<Stripe> getSplittedSimple() {
		final StripeSimple result = new StripeSimple();
		for (Atom atom : stripe.getAtoms()) {
			result.addAtom(atom, 0);

		}
		return Arrays.asList((Stripe) result);
	}

	static class StripeSimple implements Stripe {

		private final List<Atom> atoms = new ArrayList<Atom>();
		private double totalWidth;

		public List<Atom> getAtoms() {
			return Collections.unmodifiableList(atoms);
		}

		private void addAtom(Atom atom, double width) {
			this.atoms.add(atom);
			this.totalWidth += width;
		}

	}

}
