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
package net.sourceforge.plantuml.project;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum Month {

	JAN(31), FEB(28), MAR(31), APR(30), MAY(31), JUN(30), JUL(31), AUG(31), SEP(30), OCT(31), NOV(30), DEC(31);

	final private int nbDays;

	private Month(int nbDays) {
		this.nbDays = nbDays;
	}

	public final int getNbDays(int year) {
		if (this == FEB && year % 4 == 0) {
			return 29;
		}
		return nbDays;
	}

	public final int getNum() {
		return ordinal() + 1;
	}

	public final int getNumNormal() {
		return ordinal();
	}

	public Month next() {
		if (this == DEC) {
			return null;
		}
		final List<Month> all = new ArrayList<Month>(EnumSet.allOf(Month.class));
		return all.get(getNum());
	}

	public Month prev() {
		if (this == JAN) {
			return null;
		}
		final List<Month> all = new ArrayList<Month>(EnumSet.allOf(Month.class));
		return all.get(getNum() - 2);
	}

	public static Month fromNum(int num) {
		if (num < 1 || num > 12) {
			throw new IllegalArgumentException();
		}
		final List<Month> all = new ArrayList<Month>(EnumSet.allOf(Month.class));
		return all.get(num - 1);
	}
}
