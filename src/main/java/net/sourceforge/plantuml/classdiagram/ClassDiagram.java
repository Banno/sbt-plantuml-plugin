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
package net.sourceforge.plantuml.classdiagram;

import java.io.IOException;
import java.io.OutputStream;

import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.UmlDiagramType;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.cucadiagram.Code;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.cucadiagram.EntityUtils;
import net.sourceforge.plantuml.cucadiagram.GroupType;
import net.sourceforge.plantuml.cucadiagram.IGroup;
import net.sourceforge.plantuml.cucadiagram.ILeaf;
import net.sourceforge.plantuml.cucadiagram.LeafType;
import net.sourceforge.plantuml.graphic.HtmlColorUtils;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.USymbol;
import net.sourceforge.plantuml.objectdiagram.AbstractClassOrObjectDiagram;
import net.sourceforge.plantuml.svek.image.EntityImageClass;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;

public class ClassDiagram extends AbstractClassOrObjectDiagram {

	private String namespaceSeparator = ".";

	@Override
	public ILeaf getOrCreateLeaf(Code code, LeafType type, USymbol symbol) {
		if (namespaceSeparator != null) {
			code = code.withSeparator(namespaceSeparator);
		}
		if (type == null) {
			code = code.eventuallyRemoveStartingAndEndingDoubleQuote("\"([:");
			if (namespaceSeparator == null) {
				return getOrCreateLeafDefault(code, LeafType.CLASS, symbol);
			}
			code = code.getFullyQualifiedCode(getCurrentGroup());
			if (super.leafExist(code)) {
				return getOrCreateLeafDefault(code, LeafType.CLASS, symbol);
			}
			return createEntityWithNamespace(code, Display.getWithNewlines(code.getShortName(getLeafs())),
					LeafType.CLASS, symbol);
		}
		if (namespaceSeparator == null) {
			return getOrCreateLeafDefault(code, LeafType.CLASS, symbol);
		}
		code = code.getFullyQualifiedCode(getCurrentGroup());
		if (super.leafExist(code)) {
			return getOrCreateLeafDefault(code, type, symbol);
		}
		return createEntityWithNamespace(code, Display.getWithNewlines(code.getShortName(getLeafs())), type, symbol);
	}

	public IGroup getOrCreateNamespace(Code namespace, Display display, GroupType type, IGroup parent) {
		if (namespaceSeparator != null) {
			namespace = namespace.withSeparator(namespaceSeparator).getFullyQualifiedCode(getCurrentGroup());
		}
		final IGroup g = getOrCreateNamespaceInternal(namespace, display, type, parent);
		currentGroup = g;
		return g;
	}

	private IGroup getOrCreateNamespaceInternal(Code namespace, Display display, GroupType type, IGroup parent) {
		IGroup result = entityFactory.getGroups().get(namespace);
		if (result != null) {
			return result;
		}
		if (entityFactory.getLeafs().containsKey(namespace)) {
			result = entityFactory.muteToGroup(namespace, namespace, type, parent);
			result.setDisplay(display);
		} else {
			result = entityFactory.createGroup(namespace, display, namespace, type, parent, getHides(),
					getNamespaceSeparator());
		}
		entityFactory.addGroup(result);
		return result;
	}

	@Override
	public ILeaf createLeaf(Code code, Display display, LeafType type, USymbol symbol) {
		if (namespaceSeparator != null) {
			code = code.withSeparator(namespaceSeparator);
		}
		if (type != LeafType.ABSTRACT_CLASS && type != LeafType.ANNOTATION && type != LeafType.CLASS
				&& type != LeafType.INTERFACE && type != LeafType.ENUM && type != LeafType.LOLLIPOP
				&& type != LeafType.NOTE) {
			return super.createLeaf(code, display, type, symbol);
		}
		if (namespaceSeparator == null) {
			return super.createLeaf(code, display, type, symbol);
		}
		code = code.getFullyQualifiedCode(getCurrentGroup());
		if (super.leafExist(code)) {
			throw new IllegalArgumentException("Already known: " + code);
		}
		return createEntityWithNamespace(code, display, type, symbol);
	}

	private ILeaf createEntityWithNamespace(Code fullyCode, Display display, LeafType type, USymbol symbol) {
		IGroup group = getCurrentGroup();
		final String namespace = getNamespace(fullyCode);
		if (namespace != null
				&& (EntityUtils.groupRoot(group) || group.getCode().getFullName().equals(namespace) == false)) {
			final Code namespace2 = Code.of(namespace);
			group = getOrCreateNamespaceInternal(namespace2, Display.getWithNewlines(namespace), GroupType.PACKAGE,
					getRootGroup());
		}
		return createLeafInternal(fullyCode,
				display == null ? Display.getWithNewlines(fullyCode.getShortName(getLeafs())) : display, type, group,
				symbol);
	}

	private final String getNamespace(Code fullyCode) {
		String name = fullyCode.getFullName();
		do {
			final int x = name.lastIndexOf(namespaceSeparator);
			if (x == -1) {
				return null;
			}
			name = name.substring(0, x);
		} while (getLeafs().containsKey(Code.of(name, namespaceSeparator)));
		return name;
	}

	@Override
	public final boolean leafExist(Code code) {
		if (namespaceSeparator == null) {
			return super.leafExist(code);
		}
		code = code.withSeparator(namespaceSeparator);
		return super.leafExist(code.getFullyQualifiedCode(getCurrentGroup()));
	}

	@Override
	public UmlDiagramType getUmlDiagramType() {
		return UmlDiagramType.CLASS;
	}

	public void setNamespaceSeparator(String namespaceSeparator) {
		this.namespaceSeparator = namespaceSeparator;
	}

	public String getNamespaceSeparator() {
		return namespaceSeparator;
	}

	private boolean allowMixing;

	public void setAllowMixing(boolean allowMixing) {
		this.allowMixing = allowMixing;
	}

	public boolean isAllowMixing() {
		return allowMixing;
	}

	private int useLayoutExplicit = 0;

	public void layoutNewLine() {
		useLayoutExplicit++;
		incRawLayout();
	}

	@Override
	final protected ImageData exportDiagramInternal(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		if (useLayoutExplicit != 0) {
			return exportLayoutExplicit(os, index, fileFormatOption);
		}
		return super.exportDiagramInternal(os, index, fileFormatOption);
	}

	final protected ImageData exportLayoutExplicit(OutputStream os, int index, FileFormatOption fileFormatOption)
			throws IOException {
		final FullLayout fullLayout = new FullLayout();
		for (int i = 0; i <= useLayoutExplicit; i++) {
			final RowLayout rawLayout = getRawLayout(i);
			fullLayout.addRowLayout(rawLayout);
		}
		final ImageBuilder imageBuilder = new ImageBuilder(getSkinParam().getColorMapper(), 1, HtmlColorUtils.WHITE,
				null, null, 0, 10, null, getSkinParam().handwritten());
		imageBuilder.addUDrawable(fullLayout);
		return imageBuilder.writeImageTOBEMOVED(fileFormatOption, os);
	}

	private RowLayout getRawLayout(int raw) {
		final RowLayout rawLayout = new RowLayout();
		for (ILeaf leaf : getLeafs().values()) {
			if (leaf.getRawLayout() == raw) {
				rawLayout.addLeaf(getEntityImageClass(leaf));
			}
		}
		return rawLayout;
	}

	private TextBlock getEntityImageClass(ILeaf entity) {
		return new EntityImageClass(null, entity, getSkinParam(), this);
	}

}
