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
package net.sourceforge.plantuml.cucadiagram;

import net.sourceforge.plantuml.StringUtils;

public enum LeafType {

	EMPTY_PACKAGE,

	ABSTRACT_CLASS, CLASS, INTERFACE, ANNOTATION, LOLLIPOP, NOTE, TIPS, OBJECT, ASSOCIATION, ENUM,
	
	USECASE, 

	DESCRIPTION,

	ARC_CIRCLE,

	ACTIVITY, BRANCH, SYNCHRO_BAR, CIRCLE_START, CIRCLE_END, POINT_FOR_ASSOCIATION, ACTIVITY_CONCURRENT,

	STATE, STATE_CONCURRENT, PSEUDO_STATE, STATE_CHOICE, STATE_FORK_JOIN,

	BLOCK,

	STILL_UNKNOWN;

	public static LeafType getLeafType(String arg0) {
		arg0 = StringUtils.goUpperCase(arg0);
		if (arg0.startsWith("ABSTRACT")) {
			return LeafType.ABSTRACT_CLASS;
		}
		return LeafType.valueOf(arg0);
	}

	public boolean isLikeClass() {
		return this == LeafType.ANNOTATION || this == LeafType.ABSTRACT_CLASS || this == LeafType.CLASS
				|| this == LeafType.INTERFACE || this == LeafType.ENUM;
	}

	public String toHtml() {
		final String html = StringUtils.goLowerCase(toString().replace('_', ' '));
		return StringUtils.capitalize(html);
	}

	public boolean manageModifier() {
		if (this == ANNOTATION || this == ABSTRACT_CLASS || this == CLASS || this == INTERFACE || this == ENUM
				|| this == OBJECT) {
			return true;
		}
		return false;
	}
}
