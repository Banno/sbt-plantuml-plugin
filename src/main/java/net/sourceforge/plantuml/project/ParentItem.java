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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ParentItem implements Item {

	private final String code;
	private final Item parent;

	private final List<Item> children = new ArrayList<Item>();

	public ParentItem(String code, Item parent) {
		this.code = code;
		this.parent = parent;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(code + " {");
		for (final Iterator<Item> it = children.iterator(); it.hasNext();) {
			final Item child = it.next();
			sb.append(child.getCode());
			if (it.hasNext()) {
				sb.append(", ");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	public Instant getBegin() {
		Instant result = null;
		for (Item it : children) {
			if (result == null || result.compareTo(it.getBegin()) > 0) {
				result = it.getBegin();
			}
		}
		return result;
	}

	public Instant getCompleted() {
		Instant result = null;
		for (Item it : children) {
			if (result == null || result.compareTo(it.getCompleted()) < 0) {
				result = it.getCompleted();
			}
		}
		return result;
	}

	public Duration getDuration() {
		throw new UnsupportedOperationException();
	}

	public Load getLoad() {
		throw new UnsupportedOperationException();
	}

	public NumericNumber getWork() {
		throw new UnsupportedOperationException();
	}

	public boolean isLeaf() {
		return false;
	}

	public Item getParent() {
		return parent;
	}

	public List<Item> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public String getCode() {
		return code;
	}

	public void addChild(Item child) {
		this.children.add(child);
	}

	public boolean isValid() {
		if (children.size() == 0) {
			return false;
		}
		for (Item it : children) {
			if (it.isValid() == false) {
				return false;
			}
		}
		return true;
	}

}
