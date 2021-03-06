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
package net.sourceforge.plantuml.font;

import net.sourceforge.plantuml.AbstractPSystem;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.command.PSystemSingleLineFactory;

public class PSystemListFontsFactory extends PSystemSingleLineFactory {

	@Override
	protected AbstractPSystem executeLine(String line) {
		final String lineLower = StringUtils.goLowerCase(line);
		if (lineLower.equals("listfont") || lineLower.equals("listfonts") || lineLower.startsWith("listfont ")
				|| lineLower.startsWith("listfonts ")) {
			final int idx = line.indexOf(' ');
			return new PSystemListFonts(idx == -1 ? "This is a test" : StringUtils.trin(line.substring(idx)));
		}
		return null;
	}

}
