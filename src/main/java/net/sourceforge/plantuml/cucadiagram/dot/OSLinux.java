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
package net.sourceforge.plantuml.cucadiagram.dot;

import java.io.File;

import net.sourceforge.plantuml.StringUtils;

class OSLinux extends OS {

	@Override
	File getExecutable(GraphvizLayoutStrategy strategy) {
		final File result = strategy.getSystemForcedExecutable();
		if (result != null) {
			return result;
		}
		final String fileName = getFileName(strategy);
		final File usrLocalBin = new File("/usr/local/bin/" + fileName);

		if (usrLocalBin.exists()) {
			return usrLocalBin;
		}
		final File usrBin = new File("/usr/bin/" + fileName);
		return usrBin;
	}

	@Override
	String getFileName(GraphvizLayoutStrategy strategy) {
		return StringUtils.goLowerCase(strategy.name());
	}

	@Override
	public String getCommand(GraphvizLayoutStrategy strategy) {
		return getExecutable(strategy).getAbsolutePath();
	}

}
