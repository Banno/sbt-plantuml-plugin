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
package net.sourceforge.plantuml;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import net.sourceforge.plantuml.command.Command;
import net.sourceforge.plantuml.command.CommandExecutionResult;
import net.sourceforge.plantuml.command.ProtectedCommand;
import net.sourceforge.plantuml.core.Diagram;
import net.sourceforge.plantuml.core.UmlSource;
import net.sourceforge.plantuml.cucadiagram.Display;
import net.sourceforge.plantuml.graphic.AbstractTextBlock;
import net.sourceforge.plantuml.version.License;
import net.sourceforge.plantuml.version.Version;

public abstract class AbstractPSystem implements Diagram {

	private UmlSource source;

	private String getVersion() {
		final StringBuilder toAppend = new StringBuilder();
		toAppend.append("PlantUML version ");
		toAppend.append(Version.versionString());
		toAppend.append("(" + Version.compileTimeString() + ")\n");
		toAppend.append("(" + License.getCurrent() + " source distribution)\n");
		final Properties p = System.getProperties();
		toAppend.append(p.getProperty("java.runtime.name"));
		toAppend.append('\n');
		toAppend.append(p.getProperty("java.vm.name"));
		toAppend.append('\n');
		toAppend.append(p.getProperty("java.runtime.version"));
		toAppend.append('\n');
		toAppend.append(p.getProperty("os.name"));

		return toAppend.toString();
	}

	final public String getMetadata() {
		if (source == null) {
			return getVersion();
		}
		return source.getPlainString() + "\n" + getVersion();
	}

	final public UmlSource getSource() {
		return source;
	}

	final public void setSource(UmlSource source) {
		this.source = source;
	}

	public int getNbImages() {
		return 1;
	}

	public Display getTitle() {
		if (source == null) {
			return Display.empty();
		}
		return source.getTitle();
	}

	public String getWarningOrError() {
		return null;
	}

	public void makeDiagramReady() {
	}

	public boolean isOk() {
		return true;
	}

	public CommandExecutionResult executeCommand(Command cmd, List<String> lines) {
		cmd = new ProtectedCommand(cmd);
		return cmd.execute(this, lines);
	}
	
	public boolean hasUrl() {
		return false;
	}

}
