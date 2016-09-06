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
package net.sourceforge.plantuml.suggest;

import net.sourceforge.plantuml.StringUtils;


public class SuggestEngineResult {

	private final SuggestEngineStatus status;
	private final String suggestedLine;

	public static final SuggestEngineResult CANNOT_CORRECT = new SuggestEngineResult(SuggestEngineStatus.CANNOT_CORRECT);
	public static final SuggestEngineResult SYNTAX_OK = new SuggestEngineResult(SuggestEngineStatus.SYNTAX_OK);

	private SuggestEngineResult(SuggestEngineStatus status) {
		if (status == SuggestEngineStatus.ONE_SUGGESTION) {
			throw new IllegalArgumentException();
		}
		this.status = status;
		this.suggestedLine = null;
	}

	@Override
	public String toString() {
		return status + " " + suggestedLine;
	}

	@Override
	public int hashCode() {
		return status.hashCode() + (suggestedLine == null ? 0 : suggestedLine.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		final SuggestEngineResult this2 = (SuggestEngineResult) obj;
		return status.equals(this2.status) && sameString(suggestedLine, this2.suggestedLine);
	}

	private static boolean sameString(String a, String b) {
		if (a == null && b == null) {
			return true;
		}
		if (a != null || b != null) {
			return false;
		}
		return a.equals(b);
	}

	public SuggestEngineResult(String suggestedLine) {
		if (StringUtils.trin(suggestedLine).length() == 0) {
			throw new IllegalArgumentException();
		}
		this.status = SuggestEngineStatus.ONE_SUGGESTION;
		this.suggestedLine = suggestedLine;
	}

	public final SuggestEngineStatus getStatus() {
		return status;
	}

	public final String getSuggestedLine() {
		return suggestedLine;
	}

}
