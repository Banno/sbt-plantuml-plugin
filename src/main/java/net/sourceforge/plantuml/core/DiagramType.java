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
package net.sourceforge.plantuml.core;

public enum DiagramType {
	UML, DITAA, DOT, PROJECT, JCCKIT, SALT, TURING, FLOW, CREOLE, JUNGLE, CUTE, UNKNOWN;

	static public DiagramType getTypeFromArobaseStart(String s) {
//		if (s.startsWith("@startuml2")) {
//			return UML2;
//		}
		if (s.startsWith("@startuml")) {
			return UML;
		}
		if (s.startsWith("@startdot")) {
			return DOT;
		}
		if (s.startsWith("@startjcckit")) {
			return JCCKIT;
		}
		if (s.startsWith("@startditaa")) {
			return DITAA;
		}
		if (s.startsWith("@startproject")) {
			return PROJECT;
		}
		if (s.startsWith("@startsalt")) {
			return SALT;
		}
		if (s.startsWith("@startturing")) {
			return TURING;
		}
		if (s.startsWith("@startflow")) {
			return FLOW;
		}
		if (s.startsWith("@startcreole")) {
			return CREOLE;
		}
		if (s.startsWith("@starttree")) {
			return JUNGLE;
		}
		if (s.startsWith("@startcute")) {
			return CUTE;
		}
		return UNKNOWN;
	}
}
