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
package net.sourceforge.plantuml.real;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class RealLine {

	private final List<PositiveForce> forces = new ArrayList<PositiveForce>();

	private double min;
	private double max;

	void register(double v) {
		min = Math.min(min, v);
		max = Math.max(max, v);
	}

	public double getAbsoluteMin() {
		return min;
	}

	public double getAbsoluteMax() {
		return max;
	}

	public void addForce(PositiveForce force) {
		this.forces.add(force);
	}

	static private int CPT;

	public void compile() {
		int cpt = 0;
		final Map<PositiveForce, Integer> counter = new HashMap<PositiveForce, Integer>();
		do {
			min = 0;
			max = 0;
			boolean done = true;
			for (PositiveForce f : forces) {
				// System.err.println("force=" + f);
				final boolean change = f.apply();
				if (change) {
					incCounter(counter, f);
					// System.err.println("changed! " + f);
					done = false;
				}
			}
			if (done) {
				// System.err.println("cpt=" + cpt + " size=" + forces.size());
				CPT += cpt;
				// System.err.println("CPT=" + CPT);
				return;
			}
			cpt++;
			if (cpt > 99999) {
				printCounter(counter);
				throw new IllegalStateException("Inifinite Loop?");
			}
		} while (true);

	}

	private void printCounter(Map<PositiveForce, Integer> counter) {
		for (Map.Entry<PositiveForce, Integer> ent : counter.entrySet()) {
			System.err.println("count=" + ent.getValue() + " for " + ent.getKey());
		}
	}

	private static void incCounter(Map<PositiveForce, Integer> counter, PositiveForce f) {
		final Integer v = counter.get(f);
		counter.put(f, v == null ? 1 : v + 1);
	}

	Real asMaxAbsolute() {
		return new MaxAbsolute();
	}

	Real asMinAbsolute() {
		return new MinAbsolute();
	}

	class MaxAbsolute extends AbstractAbsolute {
		public double getCurrentValue() {
			return max;
		}
	}

	class MinAbsolute extends AbstractAbsolute {
		public double getCurrentValue() {
			return min;
		}
	}

	abstract class AbstractAbsolute implements Real {

		public void printCreationStackTrace() {
		}

		public String getName() {
			return getClass().getName();
		}

		public Real addFixed(double delta) {
			throw new UnsupportedOperationException();
		}

		public Real addAtLeast(double delta) {
			throw new UnsupportedOperationException();
		}

		public void ensureBiggerThan(Real other) {
			throw new UnsupportedOperationException();
		}

		public Real getMaxAbsolute() {
			return asMaxAbsolute();
		}

		public Real getMinAbsolute() {
			return asMinAbsolute();
		}

	}

}
