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

import net.sourceforge.plantuml.suggest.SuggestEngineResult;
import net.sourceforge.plantuml.suggest.SuggestEngineStatus;

public class ErrorUml {

	private final String error;
	private final int position;
	private final ErrorUmlType type;
	private SuggestEngineResult suggest;
	private final LineLocation lineLocation;

	public ErrorUml(ErrorUmlType type, String error, int position, LineLocation lineLocation) {
		if (error == null || type == null || StringUtils.isEmpty(error)) {
			throw new IllegalArgumentException();
		}
		this.error = error;
		this.type = type;
		this.position = position;
		this.lineLocation = lineLocation;
	}

	@Override
	public boolean equals(Object obj) {
		final ErrorUml this2 = (ErrorUml) obj;
		return this.type == this2.type && this.position == this2.position && this.error.equals(this2.error);
	}

	@Override
	public int hashCode() {
		return error.hashCode() + type.hashCode() + position + (suggest == null ? 0 : suggest.hashCode());
	}

	@Override
	public String toString() {
		return type.toString() + " " + position + " " + error + " " + suggest;
	}

	public final String getError() {
		return error;
	}

	public final ErrorUmlType getType() {
		return type;
	}

	public int getPosition() {
		return position;
	}

	public LineLocation getLineLocation() {
		return lineLocation;
	}

	public final SuggestEngineResult getSuggest() {
		return suggest;
	}

	public final boolean hasSuggest() {
		return suggest != null && suggest.getStatus() == SuggestEngineStatus.ONE_SUGGESTION;
	}

	public void setSuggest(SuggestEngineResult suggest) {
		this.suggest = suggest;
	}


}
