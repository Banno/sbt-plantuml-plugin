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

import net.sourceforge.plantuml.FontParam;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.Url;
import net.sourceforge.plantuml.cucadiagram.Bodier;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.EntityPosition;
import net.sourceforge.plantuml.cucadiagram.EntityUtils;
import net.sourceforge.plantuml.cucadiagram.GroupRoot;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.cucadiagram.Link;
import net.sourceforge.plantuml.cucadiagram.LongCode;
import net.sourceforge.plantuml.cucadiagram.Stereotype;
import net.sourceforge.plantuml.cucadiagram.dot.Neighborhood;
import net.sourceforge.plantuml.graphic.HtmlColor;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.svek.IEntityImage;
import net.sourceforge.plantuml.svek.PackageStyle;
import net.sourceforge.plantuml.svek.SingleStrategy;
import net.sourceforge.plantuml.ugraphic.UStroke;
import net.sourceforge.plantuml.utils.UniqueSequence;

final class EntityImpl implements ILeaf, IGroup {

	private final EntityFactory entityFactory;

	// Entity
	private final Code code;
	private final LongCode longCode;

	private Url url;

	private final Bodier bodier;
	private final String uid = StringUtils.getUid("cl", UniqueSequence.getValue());
	private Display display = Display.empty();

	private LeafType leafType;
	private Stereotype stereotype;
	private String generic;
	private IGroup parentContainer;

	private boolean top;

	// Group
	private Code namespace2;

	private GroupType groupType;

	private boolean autonom = true;

	// Other

	private HtmlColor specificBackcolor;
	private boolean nearDecoration = false;
	private int xposition;
	private IEntityImage svekImage;

	private boolean removed = false;
	private HtmlColor specificLineColor;
	private UStroke specificStroke;
	private USymbol symbol;
	private final int rawLayout;
	private char concurrentSeparator;

	// Back to Entity
	public final boolean isTop() {
		checkNotGroup();
		return top;
	}

	public final void setTop(boolean top) {
		checkNotGroup();
		this.top = top;
	}

	private EntityImpl(EntityFactory entityFactory, Code code, Bodier bodier, IGroup parentContainer,
			LongCode longCode, String namespaceSeparator, int rawLayout) {
		if (code == null) {
			throw new IllegalArgumentException();
		}
		this.entityFactory = entityFactory;
		this.bodier = bodier;
		this.code = code;
		this.parentContainer = parentContainer;
		this.longCode = longCode;
		this.rawLayout = rawLayout;
	}

	EntityImpl(EntityFactory entityFactory, Code code, Bodier bodier, IGroup parentContainer, LeafType leafType,
			LongCode longCode, String namespaceSeparator, int rawLayout) {
		this(entityFactory, code, bodier, parentContainer, longCode, namespaceSeparator, rawLayout);
		this.leafType = leafType;
	}

	EntityImpl(EntityFactory entityFactory, Code code, Bodier bodier, IGroup parentContainer, GroupType groupType,
			Code namespace2, LongCode longCode, String namespaceSeparator, int rawLayout) {
		this(entityFactory, code, bodier, parentContainer, longCode, namespaceSeparator, rawLayout);
		this.groupType = groupType;
		this.namespace2 = namespace2;
	}

	public void setContainer(IGroup container) {
		checkNotGroup();
		if (container == null) {
			throw new IllegalArgumentException();
		}
		this.parentContainer = container;
	}

	public LeafType getEntityType() {
		return leafType;
	}

	public void muteToType(LeafType newType, USymbol newSymbol) {
		checkNotGroup();
		if (newType == null) {
			throw new IllegalArgumentException();
		}
		if (leafType != LeafType.STILL_UNKNOWN) {
			if (leafType != LeafType.ANNOTATION && leafType != LeafType.ABSTRACT_CLASS && leafType != LeafType.CLASS
					&& leafType != LeafType.ENUM && leafType != LeafType.INTERFACE) {
				throw new IllegalArgumentException("type=" + leafType);
			}
			if (newType != LeafType.ANNOTATION && newType != LeafType.ABSTRACT_CLASS && newType != LeafType.CLASS
					&& newType != LeafType.ENUM && newType != LeafType.INTERFACE) {
				throw new IllegalArgumentException("newtype=" + newType);
			}
		}
		this.leafType = newType;
		this.symbol = newSymbol;
	}

	public Code getCode() {
		return code;
	}

	public Display getDisplay() {
		return display;
	}

	public void setDisplay(Display display) {
		this.display = display;
	}

	public String getUid() {
		return uid;
	}

	public Stereotype getStereotype() {
		return stereotype;
	}

	public final void setStereotype(Stereotype stereotype) {
		this.stereotype = stereotype;
	}

	public final IGroup getParentContainer() {
		if (parentContainer == null) {
			throw new IllegalArgumentException();
		}
		return parentContainer;
	}

	@Override
	public String toString() {
		return code + " " + display + "(" + leafType + ") " + xposition + " " + getUid();
	}

	public HtmlColor getSpecificBackColor() {
		return specificBackcolor;
	}

	public void setSpecificBackcolor(HtmlColor color) {
		this.specificBackcolor = color;
	}

	public final Url getUrl99() {
		return url;
	}

	public boolean hasUrl() {
		if (display != null && display.hasUrl()) {
			return true;
		}
		if (bodier.hasUrl()) {
			return true;
		}
		return url != null;
	}

	public final void addUrl(Url url) {
		this.url = url;
	}

	public final boolean hasNearDecoration() {
		checkNotGroup();
		return nearDecoration;
	}

	public final void setNearDecoration(boolean nearDecoration) {
		// checkNotGroup();
		this.nearDecoration = nearDecoration;
	}

	public int getXposition() {
		checkNotGroup();
		return xposition;
	}

	public void setXposition(int pos) {
		checkNotGroup();
		xposition = pos;
	}

	public final IEntityImage getSvekImage() {
		checkNotGroup();
		return svekImage;
	}

	public final void setSvekImage(IEntityImage svekImage) {
		checkNotGroup();
		this.svekImage = svekImage;
	}

	public final void setGeneric(String generic) {
		checkNotGroup();
		this.generic = generic;
	}

	public final String getGeneric() {
		checkNotGroup();
		return generic;
	}

	public Bodier getBodier() {
		return bodier;
	}

	public EntityPosition getEntityPosition() {
		checkNotGroup();
		if (leafType != LeafType.STATE) {
			return EntityPosition.NORMAL;
		}
		if (getParentContainer() instanceof GroupRoot) {
			return EntityPosition.NORMAL;
		}
		final Stereotype stereotype = getStereotype();
		if (stereotype == null) {
			return EntityPosition.NORMAL;
		}
		final String label = stereotype.getLabel(false);
		if ("<<entrypoint>>".equalsIgnoreCase(label)) {
			return EntityPosition.ENTRY_POINT;
		}
		if ("<<exitpoint>>".equalsIgnoreCase(label)) {
			return EntityPosition.EXIT_POINT;
		}
		return EntityPosition.NORMAL;

	}

	// ----------

	private void checkGroup() {
		if (isGroup() == false) {
			throw new UnsupportedOperationException();
		}
	}

	private void checkNotGroup() {
		if (isGroup()) {
			throw new UnsupportedOperationException();
		}
	}

	public boolean containsLeafRecurse(ILeaf leaf) {
		if (leaf == null) {
			throw new IllegalArgumentException();
		}
		if (leaf.isGroup()) {
			throw new IllegalArgumentException();
		}
		checkGroup();
		if (leaf.getParentContainer() == this) {
			return true;
		}
		for (IGroup child : getChildren()) {
			if (child.containsLeafRecurse(leaf)) {
				return true;
			}
		}
		return false;
	}

	public Collection<ILeaf> getLeafsDirect() {
		checkGroup();
		final List<ILeaf> result = new ArrayList<ILeaf>();
		for (ILeaf ent : entityFactory.getLeafs().values()) {
			if (ent.isGroup()) {
				throw new IllegalStateException();
			}
			if (ent.getParentContainer() == this) {
				result.add(ent);
			}
		}
		return Collections.unmodifiableCollection(result);
	}

	public Collection<IGroup> getChildren() {
		checkGroup();
		final Collection<IGroup> result = new ArrayList<IGroup>();
		for (IGroup g : entityFactory.getGroups().values()) {
			if (g != this && g.getParentContainer() == this) {
				result.add(g);
			}
		}
		return Collections.unmodifiableCollection(result);
	}

	public void moveEntitiesTo(IGroup dest) {
		checkGroup();
		if (dest.isGroup() == false) {
			throw new UnsupportedOperationException();
		}
		for (ILeaf ent : getLeafsDirect()) {
			((EntityImpl) ent).parentContainer = dest;
		}
		for (IGroup g : dest.getChildren()) {
			// ((EntityImpl) g).parentContainer = dest;
			throw new IllegalStateException();
		}

		for (IGroup g : getChildren()) {
			if (g == dest) {
				continue;
			}
			((EntityImpl) g).parentContainer = dest;
		}

	}

	public int size() {
		checkGroup();
		return getLeafsDirect().size();
	}

	public GroupType getGroupType() {
		checkGroup();
		return groupType;
	}

	public Code getNamespace2() {
		checkGroup();
		return namespace2;
	}

	public boolean isAutonom() {
		checkGroup();
		return autonom;
	}

	public void setAutonom(boolean autonom) {
		this.autonom = autonom;

	}

	public PackageStyle getPackageStyle() {
		checkGroup();
		if (stereotype == null) {
			return null;
		}
		return stereotype.getPackageStyle();
	}

	public boolean isGroup() {
		if (groupType != null && leafType != null) {
			throw new IllegalStateException();
		}
		if (groupType != null) {
			return true;
		}
		if (leafType != null) {
			return false;
		}
		throw new IllegalStateException();
	}

	// ---- other

	public void overideImage(IEntityImage img, LeafType leafType) {
		checkGroup();
		this.svekImage = img;
		this.url = null;

		for (final Link link : new ArrayList<Link>(entityFactory.getLinks())) {
			if (EntityUtils.isPureInnerLink12(this, link)) {
				entityFactory.removeLink(link);
			}
		}

		entityFactory.removeGroup(this.getCode());
		for (ILeaf ent : new ArrayList<ILeaf>(entityFactory.getLeafs().values())) {
			if (this != ent && this == ent.getParentContainer()) {
				entityFactory.removeLeaf(ent.getCode());
			}
		}

		entityFactory.addLeaf(this);
		this.groupType = null;
		this.leafType = leafType;
	}

	void muteToGroup(Code namespace2, GroupType groupType, IGroup parentContainer) {
		checkNotGroup();
		if (parentContainer.isGroup() == false) {
			throw new IllegalArgumentException();
		}
		this.namespace2 = namespace2;
		this.groupType = groupType;
		this.leafType = null;
		this.parentContainer = parentContainer;
	}

	public boolean isHidden() {
		if (entityFactory.isHidden(leafType)) {
			return true;
		}
		if (stereotype != null) {
			return stereotype.isHidden();
		}
		return false;
	}

	public USymbol getUSymbol() {
		return symbol;
	}

	public void setUSymbol(USymbol symbol) {
		this.symbol = symbol;
	}

	public SingleStrategy getSingleStrategy() {
		return SingleStrategy.SQUARRE;
	}

	public boolean isRemoved() {
		if (isGroup()) {
			if (getLeafsDirect().size() == 0) {
				return false;
			}
			for (ILeaf leaf : getLeafsDirect()) {
				if (leaf.isRemoved() == false) {
					return false;
				}
			}
			for (IGroup g : getChildren()) {
				if (g.isRemoved() == false) {
					return false;
				}
			}
			return true;
		}
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public HtmlColor getSpecificLineColor() {
		return specificLineColor;
	}

	public void setSpecificLineColor(HtmlColor specificLinecolor) {
		this.specificLineColor = specificLinecolor;
	}

	public UStroke getSpecificLineStroke() {
		return specificStroke;
	}

	public void setSpecificLineStroke(UStroke specificLineStroke) {
		this.specificStroke = specificLineStroke;
	}

	private int layer;

	public int getHectorLayer() {
		return layer;
	}

	public void setHectorLayer(int layer) {
		this.layer = layer;
		if (layer > 1000) {
			throw new IllegalArgumentException();
		}
	}

	public LongCode getLongCode() {
		return longCode;
	}

	public FontParam getTitleFontParam() {
		if (symbol != null) {
			return symbol.getFontParam();
		}
		return getGroupType() == GroupType.STATE ? FontParam.STATE : FontParam.PACKAGE;
	}

	public final int getRawLayout() {
		return rawLayout;
	}

	public char getConcurrentSeparator() {
		return concurrentSeparator;
	}

	public void setConcurrentSeparator(char separator) {
		this.concurrentSeparator = separator;
	}

	private Neighborhood neighborhood;

	public void setNeighborhood(Neighborhood neighborhood) {
		this.neighborhood = neighborhood;
	}

	public Neighborhood getNeighborhood() {
		return neighborhood;
	}

	private final Map<String, Display> tips = new LinkedHashMap<String, Display>();

	public void putTip(String member, Display display) {
		tips.put(member, display);
	}

	public Map<String, Display> getTips() {
		return Collections.unmodifiableMap(tips);
	}

}
