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
package net.sourceforge.plantuml.cucadiagram.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.plantuml.cucadiagram.Bodier;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.GroupRoot;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.IEntity;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LongCode;
import net.sourceforge.plantuml.skin.VisibilityModifier;

public class EntityFactory {

	private final Map<Code, ILeaf> leafs = new Protect<ILeaf>(new LinkedHashMap<Code, ILeaf>());
	private final List<Link> links = new ArrayList<Link>();
	private final Map<Code, IGroup> groups = new Protect<IGroup>(new LinkedHashMap<Code, IGroup>());
	private int rawLayout;

	private final IGroup rootGroup = new GroupRoot(this);
	private final Set<LeafType> hiddenTypes;

	public EntityFactory(Set<LeafType> hiddenTypes) {
		this.hiddenTypes = hiddenTypes;
	}

	public boolean isHidden(LeafType leafType) {
		return hiddenTypes.contains(leafType);
	}

	public ILeaf createLeaf(Code code, Display display, LeafType entityType, IGroup parentContainer,
			Set<VisibilityModifier> hides, String namespaceSeparator) {
		if (entityType == null) {
			throw new IllegalArgumentException();
		}
		final Bodier bodier = new Bodier(entityType, hides);
		final LongCode longCode = getLongCode(code, namespaceSeparator);
		final EntityImpl result = new EntityImpl(this, code, bodier, parentContainer, entityType, longCode,
				namespaceSeparator, rawLayout);
		result.setDisplay(display);
		return result;
	}

	private LongCode getLongCode(Code code, String namespaceSeparator) {
		final LongCode result = LongCode.of(code.getFullName(), namespaceSeparator);
		// if (result.toString().equals(code.toString()) == false) {
		// System.err.println("result=" + result);
		// System.err.println(" code =" + code);
		// throw new UnsupportedOperationException();
		// }
		return result;
	}

	public IGroup createGroup(Code code, Display display, Code namespace2, GroupType groupType, IGroup parentContainer,
			Set<VisibilityModifier> hides, String namespaceSeparator) {
		if (groupType == null) {
			throw new IllegalArgumentException();
		}
		final Bodier bodier = new Bodier(null, hides);
		final LongCode longCode = getLongCode(code, namespaceSeparator);
		final EntityImpl result = new EntityImpl(this, code, bodier, parentContainer, groupType, namespace2, longCode,
				namespaceSeparator, rawLayout);
		if (display != null) {
			result.setDisplay(display);
		}
		return result;
	}

	public IGroup getRootGroup() {
		return rootGroup;
	}

	public final Map<Code, ILeaf> getLeafs() {
		return Collections.unmodifiableMap(leafs);
	}

	public void addLeaf(ILeaf entity) {
		leafs.put(entity.getCode(), entity);
	}

	public void incRawLayout() {
		rawLayout++;
	}

	void removeLeaf(Code code) {
		final IEntity removed = leafs.remove(code);
		if (removed == null) {
			throw new IllegalArgumentException();
		}
	}

	public void addGroup(IGroup group) {
		groups.put(group.getCode(), group);
	}

	void removeGroup(Code code) {
		final IEntity removed = groups.remove(code);
		if (removed == null) {
			throw new IllegalArgumentException();
		}
	}

	public final Map<Code, IGroup> getGroups() {
		return Collections.unmodifiableMap(groups);
	}

	public final List<Link> getLinks() {
		return Collections.unmodifiableList(links);
	}

	public void addLink(Link link) {
		links.add(link);
	}

	public void removeLink(Link link) {
		final boolean ok = links.remove(link);
		if (ok == false) {
			throw new IllegalArgumentException();
		}
	}

	public IGroup muteToGroup(Code code, Code namespace2, GroupType type, IGroup parent) {
		final ILeaf leaf = getLeafs().get(code);
		((EntityImpl) leaf).muteToGroup(namespace2, type, parent);
		final IGroup result = (IGroup) leaf;
		removeLeaf(code);
		return result;
	}

	static class Protect<O extends Object> implements Map<Code, O> {

		private final Map<Code, O> m;

		public Protect(Map<Code, O> data) {
			this.m = data;
		}

		public O remove(Object key) {
			if (key instanceof Code == false) {
				throw new IllegalArgumentException();
			}
			return m.remove(key);
		}

		public O get(Object key) {
			if (key instanceof Code == false) {
				throw new IllegalArgumentException();
			}
			return m.get(key);
		}

		public Set<Code> keySet() {
			return m.keySet();
		}

		public void putAll(Map<? extends Code, ? extends O> m) {
			this.m.putAll(m);
		}

		public boolean containsKey(Object key) {
			if (key instanceof Code == false) {
				throw new IllegalArgumentException();
			}
			return m.containsKey(key);
		}

		public boolean isEmpty() {
			return m.isEmpty();
		}

		public O put(Code key, O value) {
			if (key instanceof Code == false) {
				throw new IllegalArgumentException();
			}
			return m.put(key, value);
		}

		public boolean containsValue(Object value) {
			return m.containsValue(value);
		}

		public Set<Map.Entry<Code, O>> entrySet() {
			return m.entrySet();
		}

		public Collection<O> values() {
			return m.values();
		}

		public void clear() {
			m.clear();

		}

		public int size() {
			return m.size();
		}

	}

}
