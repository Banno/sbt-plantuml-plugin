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
package net.sourceforge.plantuml.project;

import java.util.Comparator;

public class ItemComparator implements Comparator<Item> {

	public int compare(Item it1, Item it2) {
		final int cmp1 = it1.getBegin().compareTo(it2.getBegin());
		if (cmp1 != 0) {
			return cmp1;
		}
		if (it1 instanceof Jalon && it2 instanceof Jalon == false) {
			return -1;
		}
		if (it2 instanceof Jalon && it1 instanceof Jalon == false) {
			return 1;
		}
		final int cmp2 = it2.getCompleted().compareTo(it1.getCompleted());
		if (cmp2 != 0) {
			return cmp2;
		}
		return it1.getCode().compareTo(it2.getCode());
	}

}
