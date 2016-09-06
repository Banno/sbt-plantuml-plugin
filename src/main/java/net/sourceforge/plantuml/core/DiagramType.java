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
package net.sourceforge.plantuml.core;

import net.sourceforge.plantuml.utils.StartUtils;

public enum DiagramType {
	UML, DITAA, DOT, PROJECT, JCCKIT, SALT, TURING, FLOW, CREOLE, JUNGLE, CUTE, UNKNOWN;

	static public DiagramType getTypeFromArobaseStart(String s) {
		s = s.toLowerCase();
		// if (s.startsWith("@startuml2")) {
		// return UML2;
		// }
		if (StartUtils.startsWithSymbolAnd("startuml", s)) {
			return UML;
		}
		if (StartUtils.startsWithSymbolAnd("startdot", s)) {
			return DOT;
		}
		if (StartUtils.startsWithSymbolAnd("startjcckit", s)) {
			return JCCKIT;
		}
		if (StartUtils.startsWithSymbolAnd("startditaa", s)) {
			return DITAA;
		}
		if (StartUtils.startsWithSymbolAnd("startproject", s)) {
			return PROJECT;
		}
		if (StartUtils.startsWithSymbolAnd("startsalt", s)) {
			return SALT;
		}
		if (StartUtils.startsWithSymbolAnd("startturing", s)) {
			return TURING;
		}
		if (StartUtils.startsWithSymbolAnd("startflow", s)) {
			return FLOW;
		}
		if (StartUtils.startsWithSymbolAnd("startcreole", s)) {
			return CREOLE;
		}
		if (StartUtils.startsWithSymbolAnd("starttree", s)) {
			return JUNGLE;
		}
		if (StartUtils.startsWithSymbolAnd("startcute", s)) {
			return CUTE;
		}
		return UNKNOWN;
	}
}
