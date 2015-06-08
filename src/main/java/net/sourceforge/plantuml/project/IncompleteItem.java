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

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

class IncompleteItem implements Item {

	private Map<ItemCaract, Numeric> data = new EnumMap<ItemCaract, Numeric>(ItemCaract.class);

	private final InstantArithmetic math;

	private final Item parent;

	private final String code;

	public IncompleteItem(String code, Item parent, InstantArithmetic math) {
		this.math = math;
		this.code = code;
		this.parent = parent;
	}

	public void setData(ItemCaract caract, Numeric value) {
		if (caract.getNumericType() != value.getNumericType()) {
			throw new IllegalArgumentException();
		}
		if (data.containsKey(caract.getNumericType())) {
			throw new IllegalStateException();
		}
		data.put(caract, value);
		boolean change = false;
		do {
			change = false;
			change = eventuallyUseBeginComplete() || change;
			change = eventuallyUseBeginDuration() || change;
			change = eventuallyUseCompleteDuration() || change;
			change = eventuallyUseDurationWork() || change;
			change = eventuallyUseDurationLoad() || change;
			change = eventuallyUseLoadWork() || change;
		} while (change);
	}

	private boolean eventuallyUseDurationWork() {
		if (data.containsKey(ItemCaract.DURATION) && data.containsKey(ItemCaract.WORK)
				&& data.containsKey(ItemCaract.LOAD) == false) {
			final Duration d = (Duration) data.get(ItemCaract.DURATION);
			final NumericNumber w = (NumericNumber) data.get(ItemCaract.WORK);
			data.put(ItemCaract.LOAD, new Load(d.getMinutes() * w.getIntValue()));
			return true;
		}
		return false;
	}

	private boolean eventuallyUseLoadWork() {
		if (data.containsKey(ItemCaract.LOAD) && data.containsKey(ItemCaract.WORK)
				&& data.containsKey(ItemCaract.DURATION) == false) {
			final Load l = (Load) data.get(ItemCaract.LOAD);
			final NumericNumber w = (NumericNumber) data.get(ItemCaract.WORK);
			data.put(ItemCaract.DURATION, new Duration(l.getMinuteMen() / w.getIntValue()));
			return true;
		}
		return false;
	}

	private boolean eventuallyUseDurationLoad() {
		if (data.containsKey(ItemCaract.DURATION) && data.containsKey(ItemCaract.LOAD)
				&& data.containsKey(ItemCaract.WORK) == false) {
			final Duration d = (Duration) data.get(ItemCaract.DURATION);
			final Load l = (Load) data.get(ItemCaract.LOAD);
			data.put(ItemCaract.WORK, new NumericNumber((int) (l.getMinuteMen() / d.getMinutes())));
			return true;
		}
		return false;
	}

	private boolean eventuallyUseBeginDuration() {
		if (data.containsKey(ItemCaract.BEGIN) && data.containsKey(ItemCaract.DURATION)
				&& data.containsKey(ItemCaract.COMPLETED) == false) {
			final Instant i1 = (Instant) data.get(ItemCaract.BEGIN);
			final Duration d = (Duration) data.get(ItemCaract.DURATION);
			data.put(ItemCaract.COMPLETED, math.add(i1, d));
			return true;
		}
		return false;
	}

	private boolean eventuallyUseCompleteDuration() {
		if (data.containsKey(ItemCaract.COMPLETED) && data.containsKey(ItemCaract.DURATION)
				&& data.containsKey(ItemCaract.BEGIN) == false) {
			final Instant i2 = (Instant) data.get(ItemCaract.COMPLETED);
			final Duration d = (Duration) data.get(ItemCaract.DURATION);
			data.put(ItemCaract.BEGIN, math.sub(i2, d));
			return true;
		}
		return false;
	}

	private boolean eventuallyUseBeginComplete() {
		if (data.containsKey(ItemCaract.BEGIN) && data.containsKey(ItemCaract.COMPLETED)
				&& data.containsKey(ItemCaract.DURATION) == false) {
			final Instant i1 = (Instant) data.get(ItemCaract.BEGIN);
			final Instant i2 = (Instant) data.get(ItemCaract.COMPLETED);
			if (i2.compareTo(i1) <= 0) {
				throw new IllegalArgumentException();
			}
			data.put(ItemCaract.DURATION, math.diff(i1, i2));
			return true;
		}
		return false;
	}

	public boolean isValid() {
		return data.size() == EnumSet.allOf(ItemCaract.class).size();
	}

	public Instant getBegin() {
		return (Instant) data.get(ItemCaract.BEGIN);
	}

	public Instant getCompleted() {
		return (Instant) data.get(ItemCaract.COMPLETED);
	}

	public Duration getDuration() {
		return (Duration) data.get(ItemCaract.DURATION);
	}

	public Load getLoad() {
		return (Load) data.get(ItemCaract.LOAD);
	}

	public NumericNumber getWork() {
		return (NumericNumber) data.get(ItemCaract.WORK);
	}

	public boolean isLeaf() {
		return true;
	}

	public Item getParent() {
		return parent;
	}

	public List<Item> getChildren() {
		return null;
	}

	public String getCode() {
		return code;
	}

	@Override
	public String toString() {
		return code + " " + data.toString();
	}
}
