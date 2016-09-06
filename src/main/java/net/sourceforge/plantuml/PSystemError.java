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
package net.sourceforge.plantuml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

import net.sourceforge.plantuml.api.ImageDataSimple;
import net.sourceforge.plantuml.asciiart.UmlCharArea;
import net.sourceforge.plantuml.core.DiagramDescription;
import net.sourceforge.plantuml.core.DiagramDescriptionImpl;
import net.sourceforge.plantuml.core.ImageData;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.graphic.GraphicStrings;
import net.sourceforge.plantuml.ugraphic.ColorMapperIdentity;
import net.sourceforge.plantuml.ugraphic.ImageBuilder;
import net.sourceforge.plantuml.ugraphic.txt.UGraphicTxt;

public class PSystemError extends AbstractPSystem {

	private final int higherErrorPosition;
	private final List<ErrorUml> printedErrors;
	private final List<String> debugLines = new ArrayList<String>();

	public PSystemError(UmlSource source, ErrorUml singleError, List<String> debugLines) {
		this(source, Collections.singletonList(singleError), debugLines);
	}

	private PSystemError(UmlSource source, List<ErrorUml> all, List<String> debugLines) {
		this.setSource(source);

		final int higherErrorPositionExecution = getHigherErrorPosition(ErrorUmlType.EXECUTION_ERROR, all);
		final int higherErrorPositionSyntax = getHigherErrorPosition(ErrorUmlType.SYNTAX_ERROR, all);

		if (higherErrorPositionExecution == Integer.MIN_VALUE && higherErrorPositionSyntax == Integer.MIN_VALUE) {
			throw new IllegalStateException();
		}

		if (higherErrorPositionExecution >= higherErrorPositionSyntax) {
			higherErrorPosition = higherErrorPositionExecution;
			printedErrors = getErrorsAt(higherErrorPositionExecution, ErrorUmlType.EXECUTION_ERROR, all);
		} else {
			assert higherErrorPositionSyntax > higherErrorPositionExecution;
			higherErrorPosition = higherErrorPositionSyntax;
			printedErrors = getErrorsAt(higherErrorPositionSyntax, ErrorUmlType.SYNTAX_ERROR, all);
		}

		if (debugLines != null) {
			this.debugLines.addAll(debugLines);
		}

	}

	private String getSuggestColor(boolean useRed) {
		if (useRed) {
			return "black";
		}
		return "white";
	}

	private String getRed(boolean useRed) {
		if (useRed) {
			return "#CD0A0A";
		}
		return "red";
	}

	public ImageData exportDiagram(OutputStream os, int num, FileFormatOption fileFormat) throws IOException {
		if (fileFormat.getFileFormat() == FileFormat.ATXT || fileFormat.getFileFormat() == FileFormat.UTXT) {
			final UGraphicTxt ugt = new UGraphicTxt();
			final UmlCharArea area = ugt.getCharArea();
			area.drawStringsLR(getTextStrings(), 0, 0);
			area.print(new PrintStream(os));
			return new ImageDataSimple(1, 1);

		}
		final boolean useRed = fileFormat.isUseRedForError();
		final GraphicStrings result = GraphicStrings.createDefault(getHtmlStrings(useRed), useRed);
		final ImageBuilder imageBuilder = new ImageBuilder(new ColorMapperIdentity(), 1.0, result.getBackcolor(),
				getMetadata(), null, 0, 0, null, false);
		imageBuilder.setUDrawable(result);
		return imageBuilder.writeImageTOBEMOVED(fileFormat, os);
	}

	private List<String> getTextStrings() {
		final List<String> result = new ArrayList<String>(getStack());
		if (result.size() > 0) {
			result.add(" ");
		}

		final int limit = 4;
		int start;
		final int skip = higherErrorPosition - limit + 1;
		if (skip <= 0) {
			start = 0;
		} else {
			if (skip == 1) {
				result.add("... (skipping 1 line) ...");
			} else {
				result.add("... (skipping " + skip + " lines) ...");
			}
			start = higherErrorPosition - limit + 1;
		}
		for (int i = start; i < higherErrorPosition; i++) {
			result.add(getSource().getLine(i));
		}
		final String errorLine = getSource().getLine(higherErrorPosition);
		final String err = StringUtils.hideComparatorCharacters(errorLine);
		if (StringUtils.isNotEmpty(err)) {
			result.add(err);
		}
		final StringBuilder underscore = new StringBuilder();
		for (int i = 0; i < errorLine.length(); i++) {
			underscore.append("^");
		}
		result.add(underscore.toString());
		final Collection<String> textErrors = new LinkedHashSet<String>();
		for (ErrorUml er : printedErrors) {
			textErrors.add(er.getError());
		}
		for (String er : textErrors) {
			result.add(" " + er);
		}
		boolean first = true;
		for (String s : getSuggest()) {
			if (first) {
				result.add(" " + s);
			} else {
				result.add(s);
			}
			first = false;
		}
		result.addAll(this.debugLines);

		return result;
	}

	private List<String> getStack() {
		LineLocation lineLocation = getLineLocation();
		final List<String> result = new ArrayList<String>();
		if (lineLocation != null) {
			append(result, lineLocation);
			while (lineLocation.getParent() != null) {
				lineLocation = lineLocation.getParent();
				append(result, lineLocation);
			}
		}
		return result;
	}

	public LineLocation getLineLocation() {
		for (ErrorUml err : printedErrors) {
			if (err.getLineLocation() != null) {
				return err.getLineLocation();
			}
		}
		return null;
	}

	private void append(List<String> result, LineLocation lineLocation) {
		if (lineLocation.getDescription() != null) {
			result.add("[From " + lineLocation.getDescription() + " (line " + (lineLocation.getPosition() + 1) + ") ]");
		}
	}

	private List<String> getHtmlStrings(boolean useRed) {
		final List<String> htmlStrings = new ArrayList<String>(getStack());
		if (htmlStrings.size() > 0) {
			htmlStrings.add("----");
		}

		final int limit = 4;
		int start;
		final int skip = higherErrorPosition - limit + 1;
		if (skip <= 0) {
			start = 0;
		} else {
			if (skip == 1) {
				htmlStrings.add("... (skipping 1 line) ...");
			} else {
				htmlStrings.add("... (skipping " + skip + " lines) ...");
			}
			start = higherErrorPosition - limit + 1;
		}
		for (int i = start; i < higherErrorPosition; i++) {
			htmlStrings.add(StringUtils.hideComparatorCharacters(getSource().getLine(i)));
		}
		final String errorLine = getSource().getLine(higherErrorPosition);
		final String err = StringUtils.hideComparatorCharacters(errorLine);
		if (StringUtils.isNotEmpty(err)) {
			htmlStrings.add("<w:" + getRed(useRed) + ">" + err + "</w>");
		}
		// final StringBuilder underscore = new StringBuilder();
		// for (int i = 0; i < errorLine.length(); i++) {
		// underscore.append("^");
		// }
		final Collection<String> textErrors = new LinkedHashSet<String>();
		for (ErrorUml er : printedErrors) {
			textErrors.add(er.getError());
		}
		for (String er : textErrors) {
			htmlStrings.add(" <color:" + getRed(useRed) + ">" + er + "</color>");
		}
		boolean first = true;
		for (String s : getSuggest()) {
			if (first) {
				htmlStrings.add(" <color:" + getSuggestColor(useRed) + "><i>" + s + "</i></color>");
			} else {
				htmlStrings.add("<color:" + getSuggestColor(useRed) + ">" + StringUtils.hideComparatorCharacters(s)
						+ "</color>");
			}
			first = false;
		}
		htmlStrings.addAll(this.debugLines);

		return htmlStrings;
	}

	public List<String> getSuggest() {
		boolean suggested = false;
		for (ErrorUml er : printedErrors) {
			if (er.hasSuggest()) {
				suggested = true;
			}
		}
		if (suggested == false) {
			return Collections.emptyList();
		}
		final List<String> result = new ArrayList<String>();
		result.add("Did you mean:");
		for (ErrorUml er : printedErrors) {
			if (er.hasSuggest()) {
				result.add(er.getSuggest().getSuggestedLine());
			}
		}
		return Collections.unmodifiableList(result);

	}

	private Collection<ErrorUml> getErrors(ErrorUmlType type, List<ErrorUml> all) {
		final Collection<ErrorUml> result = new LinkedHashSet<ErrorUml>();
		for (ErrorUml error : all) {
			if (error.getType() == type) {
				result.add(error);
			}
		}
		return result;
	}

	private int getHigherErrorPosition(ErrorUmlType type, List<ErrorUml> all) {
		int max = Integer.MIN_VALUE;
		for (ErrorUml error : getErrors(type, all)) {
			if (error.getPosition() > max) {
				max = error.getPosition();
			}
		}
		// if (max == Integer.MIN_VALUE) {
		// throw new IllegalStateException();
		// }
		return max;
	}

	private List<ErrorUml> getErrorsAt(int position, ErrorUmlType type, List<ErrorUml> all) {
		final List<ErrorUml> result = new ArrayList<ErrorUml>();
		for (ErrorUml error : getErrors(type, all)) {
			if (error.getPosition() == position && StringUtils.isNotEmpty(error.getError())) {
				result.add(error);
			}
		}
		return result;
	}

	public DiagramDescription getDescription() {
		return new DiagramDescriptionImpl("(Error)", getClass());
	}

	public final int getHigherErrorPosition() {
		return higherErrorPosition;
	}

	public final Collection<ErrorUml> getErrorsUml() {
		return Collections.unmodifiableCollection(printedErrors);
	}

	@Override
	public String getWarningOrError() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getDescription());
		sb.append('\n');
		for (CharSequence t : getTitle().getDisplay()) {
			sb.append(t);
			sb.append('\n');
		}
		sb.append('\n');
		for (String s : getSuggest()) {
			sb.append(s);
			sb.append('\n');
		}
		return sb.toString();
	}

	public static PSystemError merge(Collection<PSystemError> ps) {
		UmlSource source = null;
		final List<ErrorUml> errors = new ArrayList<ErrorUml>();
		final List<String> debugs = new ArrayList<String>();
		for (PSystemError system : ps) {
			if (system == null) {
				continue;
			}
			if (system.getSource() != null && source == null) {
				source = system.getSource();
			}
			errors.addAll(system.getErrorsUml());
			debugs.addAll(system.debugLines);
			if (system.debugLines.size() > 0) {
				debugs.add("-");
			}
		}
		if (source == null) {
			throw new IllegalStateException();
		}
		return new PSystemError(source, errors, debugs);
	}

}
