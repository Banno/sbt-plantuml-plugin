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
package net.sourceforge.plantuml.command;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.ErrorUmlType;
import net.sourceforge.plantuml.PSystemError;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.DiagramType;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.utils.StartUtils;
import net.sourceforge.plantuml.version.IteratorCounter;

public abstract class PSystemBasicFactory<P extends AbstractPSystem> extends PSystemAbstractFactory {

	public PSystemBasicFactory(DiagramType diagramType) {
		super(diagramType);
	}

	public PSystemBasicFactory() {
		this(DiagramType.UML);
	}

	public abstract P executeLine(P system, String line);

	public P init(String startLine) {
		return null;
	}


	final public Diagram createSystem(UmlSource source) {
		final IteratorCounter it = source.iterator();
		final String startLine = it.next();
		P system = init(startLine);
		while (it.hasNext()) {
			final String s = it.next();
			if (StartUtils.isArobaseEndDiagram(s)) {
				if (source.getTotalLineCount() == 2) {
					return buildEmptyError(source);
				}
				if (system != null) {
					system.setSource(source);
				}
				return system;
			}
			system = executeLine(system, s);
			if (system == null) {
				return new PSystemError(source, new ErrorUml(ErrorUmlType.SYNTAX_ERROR, "Syntax Error?",
						it.currentNum() - 1), null);
			}
		}
		if (system != null) {
			system.setSource(source);
		}
		return system;
	}



}
