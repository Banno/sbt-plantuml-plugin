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

import java.io.PrintStream;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.plantuml.SkinParam;
import net.sourceforge.plantuml.graphic.HtmlColorSetSimple;

public class LanguageDescriptor {

	private final Set<String> type = new TreeSet<String>();
	private final Set<String> keyword = new TreeSet<String>();
	private final Set<String> preproc = new TreeSet<String>();

	public LanguageDescriptor() {
		
		type.add("actor");
		type.add("participant");
		type.add("usecase");
		type.add("class");
		type.add("interface");
		type.add("abstract");
		type.add("enum");
		type.add("component");
		type.add("state");
		type.add("object");
		type.add("artifact");
		type.add("folder");
		type.add("rect");
		type.add("node");
		type.add("frame");
		type.add("cloud");
		type.add("database");
		type.add("storage");
		type.add("agent");
		type.add("boundary");
		type.add("control");
		type.add("entity");
		type.add("card");

		keyword.add("@startuml");
		keyword.add("@enduml");
		keyword.add("as");
		keyword.add("also");
		keyword.add("autonumber");
		keyword.add("title");
		keyword.add("newpage");
		keyword.add("box");
		keyword.add("alt");
		keyword.add("else");
		keyword.add("opt");
		keyword.add("loop");
		keyword.add("par");
		keyword.add("break");
		keyword.add("critical");
		keyword.add("note");
		keyword.add("group");
		keyword.add("left");
		keyword.add("right");
		keyword.add("of");
		keyword.add("on");
		keyword.add("link");
		keyword.add("over");
		keyword.add("end");
		keyword.add("activate");
		keyword.add("deactivate");
		keyword.add("destroy");
		keyword.add("create");
		keyword.add("footbox");
		keyword.add("hide");
		keyword.add("show");
		keyword.add("skinparam");
		keyword.add("skin");
		keyword.add("top");
		keyword.add("bottom");
		keyword.add("top to bottom direction");
		keyword.add("package");
		keyword.add("namespace");
		keyword.add("page");
		keyword.add("up");
		keyword.add("down");
		keyword.add("if");
		keyword.add("else");
		keyword.add("elseif");
		keyword.add("endif");
		keyword.add("partition");
		keyword.add("footer");
		keyword.add("header");
		keyword.add("center");
		keyword.add("rotate");
		keyword.add("ref");
		keyword.add("return");
		keyword.add("is");
		keyword.add("repeat");
		keyword.add("start");
		keyword.add("stop");
		keyword.add("while");
		keyword.add("endwhile");
		keyword.add("fork");
		keyword.add("again");
		keyword.add("kill");

		preproc.add("!include");
		preproc.add("!define");
		preproc.add("!undef");
		preproc.add("!ifdef");
		preproc.add("!endif");
		preproc.add("!ifndef");
	}

	public void print(PrintStream ps) {
		print(ps, "type", type);
		print(ps, "keyword", keyword);
		print(ps, "preprocessor", preproc);
		print(ps, "skinparameter", SkinParam.getPossibleValues());
		print(ps, "color", new HtmlColorSetSimple().names());
		ps.println(";EOF");
	}

	private static void print(PrintStream ps, String name, Collection<String> data) {
		ps.println(";"+name);
		ps.println(";" + data.size());
		for (String k : data) {
			ps.println(k);
		}
		ps.println();
	}

}
