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
package net.sourceforge.plantuml.skin;

import net.sourceforge.plantuml.ISkinParam;
import net.sourceforge.plantuml.cucadiagram.Display;

public class ProtectedSkin implements Skin {

	final private Skin skinToProtect;

	public ProtectedSkin(Skin skinToProtect) {
		this.skinToProtect = skinToProtect;

	}

	public Component createComponent(ComponentType type, ArrowConfiguration config, ISkinParam param, Display stringsToDisplay) {
		Component result = null;
		try {
			result = skinToProtect.createComponent(type, config, param, stringsToDisplay);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		if (result == null) {
			return new GrayComponent(type);
		}
		return result;
	}

	public Object getProtocolVersion() {
		return skinToProtect.getProtocolVersion();
	}
}
