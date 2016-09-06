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
package net.sourceforge.plantuml.activitydiagram3.ftile;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.ugraphic.UTranslate;

public class WormMutation {

	private final List<UTranslate> translations = new ArrayList<UTranslate>();

	private WormMutation() {

	}

	public static WormMutation create(Worm worm, double delta) {
		final String signature = worm.getDirectionsCode();
		if (signature.length() > 2) {
			return createFromLongSignature(signature, delta);
		}
		return createFromSimpleSignature(signature, delta);
	}

	private static WormMutation createFromLongSignature(final String signature, final double delta) {
		final WormMutation result = new WormMutation();
		for (int i = 0; i < signature.length() - 1; i++) {
			WormMutation tmp = createFromSimpleSignature(signature.substring(i, i + 2), delta);
			if (i == 0) {
				result.translations.add(tmp.translations.get(0));
			} else {
				UTranslate last = result.getLast();
				if (last.isAlmostSame(tmp.translations.get(0)) == false) {
					tmp = tmp.reverse();
				}
			}
			result.translations.add(tmp.translations.get(1));
			if (i == signature.length() - 2) {
				result.translations.add(tmp.translations.get(2));
			}
		}
		return result;
	}

	private WormMutation reverse() {
		final WormMutation result = new WormMutation();
		for (UTranslate tr : translations) {
			result.translations.add(tr.reverse());
		}
		return result;
	}

	public UTranslate getLast() {
		return translations.get(translations.size() - 1);
	}

	public UTranslate getFirst() {
		return translations.get(0);
	}
	
	public int size() {
		return translations.size();
	}

	private static WormMutation createFromSimpleSignature(final String signature, final double delta) {
		final WormMutation result = new WormMutation();
		// System.err.println("signature=" + signature);
		if (signature.equals("D") || signature.equals("U")) {
			final UTranslate translate = new UTranslate(delta, 0);
			result.translations.add(translate);
			result.translations.add(translate);
			return result;
		}
		if (signature.equals("L") || signature.equals("R")) {
			final UTranslate translate = new UTranslate(0, delta);
			result.translations.add(translate);
			result.translations.add(translate);
			return result;
		}
		if (signature.equals("RD")) {
			result.translations.add(new UTranslate(0, -delta));
			result.translations.add(new UTranslate(delta, -delta));
			result.translations.add(new UTranslate(delta, 0));
			return result;
		}
		if (signature.equals("RU")) {
			result.translations.add(new UTranslate(0, delta));
			result.translations.add(new UTranslate(delta, delta));
			result.translations.add(new UTranslate(delta, 0));
			return result;
		}
		if (signature.equals("LD")) {
			result.translations.add(new UTranslate(0, -delta));
			result.translations.add(new UTranslate(-delta, -delta));
			result.translations.add(new UTranslate(-delta, 0));
			return result;
		}
		if (signature.equals("DL")) {
			result.translations.add(new UTranslate(delta, 0));
			result.translations.add(new UTranslate(delta, delta));
			result.translations.add(new UTranslate(0, delta));
			return result;
		}
		if (signature.equals("DR")) {
			result.translations.add(new UTranslate(-delta, 0));
			result.translations.add(new UTranslate(-delta, delta));
			result.translations.add(new UTranslate(0, delta));
			return result;
		}
		if (signature.equals("UL")) {
			result.translations.add(new UTranslate(delta, 0));
			result.translations.add(new UTranslate(delta, -delta));
			result.translations.add(new UTranslate(0, -delta));
			return result;
		}
		if (signature.equals("UR")) {
			result.translations.add(new UTranslate(-delta, 0));
			result.translations.add(new UTranslate(-delta, -delta));
			result.translations.add(new UTranslate(0, -delta));
			return result;
		}
		throw new UnsupportedOperationException(signature);
	}

	static private class MinMax {

		private double min = Double.MAX_VALUE;
		private double max = Double.MIN_VALUE;

		private void append(double v) {
			if (v > max) {
				max = v;
			}
			if (v < min) {
				min = v;
			}
		}

		private double getExtreme() {
			if (Math.abs(max) > Math.abs(min)) {
				return max;
			}
			return min;
		}

	}

	public UTranslate getTextTranslate(int size) {
		final MinMax result = new MinMax();
		for (UTranslate tr : translations) {
			result.append(tr.getDx());
		}
		return new UTranslate(result.getExtreme() * (size - 1), 0);
	}

	public Worm mute(Worm original) {
		final Worm result = new Worm();
		for (int i = 0; i < original.size(); i++) {
			result.addPoint(translations.get(i).getTranslated(original.get(i)));
		}
		return result;
	}

}
