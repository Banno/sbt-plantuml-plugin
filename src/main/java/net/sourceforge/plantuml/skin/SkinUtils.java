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
package net.sourceforge.plantuml.skin;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.plantuml.Log;
import net.sourceforge.plantuml.StringUtils;

public class SkinUtils {

	static public Skin loadSkin(String className) {
		final List<String> errors = new ArrayList<String>();
		Skin result = tryLoading(className, errors);
		if (result != null) {
			return result;
		}
		result = tryLoading("net.sourceforge.plantuml.skin." + className, errors);
		if (result != null) {
			return result;
		}
		final String packageName = StringUtils.goLowerCase(className);
		result = tryLoading(packageName + "." + className, errors);
		if (result != null) {
			return result;
		}
		result = tryLoading("net.sourceforge.plantuml.skin." + packageName + "." + className, errors);
		if (result != null) {
			return result;
		}
		for (String e : errors) {
			Log.println("err="+e);
		}
		return null;
	}

	private static Skin tryLoading(String className, List<String> errors) {
		try {
			final Class<Skin> cl = (Class<Skin>) Class.forName(className);
			return cl.newInstance();
		} catch (Exception e) {
			errors.add("Cannot load " + className);
			return null;
		}
	}
}
