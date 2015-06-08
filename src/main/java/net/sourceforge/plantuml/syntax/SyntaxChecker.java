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
package net.sourceforge.plantuml.syntax;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.BlockUml;
import net.sourceforge.plantuml.ErrorUml;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.PSystemError;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.UmlDiagram;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.preproc.Defines;

public class SyntaxChecker {

	public static SyntaxResult checkSyntax(List<String> source) {
		final StringBuilder sb = new StringBuilder();
		for (String s : source) {
			sb.append(s);
			sb.append("\n");
		}
		return checkSyntax(sb.toString());
	}

	public static SyntaxResult checkSyntax(String source) {
		OptionFlags.getInstance().setQuiet(true);
		final SyntaxResult result = new SyntaxResult();

		if (source.startsWith("@startuml\n") == false) {
			result.setError(true);
			result.setErrorLinePosition(0);
			result.addErrorText("No @startuml found");
			result.setSuggest(Arrays.asList("Did you mean:", "@startuml"));
			return result;
		}
		if (source.endsWith("@enduml\n") == false && source.endsWith("@enduml") == false) {
			result.setError(true);
			result.setErrorLinePosition(lastLineNumber(source));
			result.addErrorText("No @enduml found");
			result.setSuggest(Arrays.asList("Did you mean:", "@enduml"));
			return result;
		}
		final SourceStringReader sourceStringReader = new SourceStringReader(new Defines(), source,
				Collections.<String> emptyList());

		final List<BlockUml> blocks = sourceStringReader.getBlocks();
		if (blocks.size() == 0) {
			result.setError(true);
			result.setErrorLinePosition(lastLineNumber(source));
			result.addErrorText("No @enduml found");
			result.setSuggest(Arrays.asList("Did you mean:", "@enduml"));
			return result;
		}
		final Diagram system = blocks.get(0).getDiagram();
		result.setCmapData(system.hasUrl());
		if (system instanceof UmlDiagram) {
			result.setUmlDiagramType(((UmlDiagram) system).getUmlDiagramType());
			result.setDescription(system.getDescription().getDescription());
		} else if (system instanceof PSystemError) {
			result.setError(true);
			final PSystemError sys = (PSystemError) system;
			result.setErrorLinePosition(sys.getHigherErrorPosition());
			for (ErrorUml er : sys.getErrorsUml()) {
				result.addErrorText(er.getError());
			}
			result.setSuggest(sys.getSuggest());
		} else {
			result.setDescription(system.getDescription().getDescription());
		}
		return result;
	}

	public static SyntaxResult checkSyntaxFair(String source) {
		final SyntaxResult result = new SyntaxResult();
		final SourceStringReader sourceStringReader = new SourceStringReader(new Defines(), source,
				Collections.<String> emptyList());

		final List<BlockUml> blocks = sourceStringReader.getBlocks();
		if (blocks.size() == 0) {
			result.setError(true);
			result.setErrorLinePosition(lastLineNumber(source));
			result.addErrorText("No @enduml found");
			result.setSuggest(Arrays.asList("Did you mean:", "@enduml"));
			return result;
		}

		final Diagram system = blocks.get(0).getDiagram();
		result.setCmapData(system.hasUrl());
		if (system instanceof UmlDiagram) {
			result.setUmlDiagramType(((UmlDiagram) system).getUmlDiagramType());
			result.setDescription(system.getDescription().getDescription());
		} else if (system instanceof PSystemError) {
			result.setError(true);
			final PSystemError sys = (PSystemError) system;
			result.setErrorLinePosition(sys.getHigherErrorPosition());
			for (ErrorUml er : sys.getErrorsUml()) {
				result.addErrorText(er.getError());
			}
			result.setSystemError(sys);
			result.setSuggest(sys.getSuggest());
		} else {
			result.setDescription(system.getDescription().getDescription());
		}
		return result;
	}

	private static int lastLineNumber(String source) {
		int result = 0;
		for (int i = 0; i < source.length(); i++) {
			if (source.charAt(i) == '\n') {
				result++;
			}
		}
		return result;
	}
}
