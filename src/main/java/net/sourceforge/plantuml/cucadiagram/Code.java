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
package net.sourceforge.plantuml.cucadiagram;

import java.util.Map;

import net.sourceforge.plantuml.StringUtils;

public class Code implements Comparable<Code> {

	private final String fullName;
	private final String separator;

	private Code(String fullName, String separator) {
		if (fullName == null) {
			throw new IllegalArgumentException();
		}
		this.fullName = fullName;
		this.separator = separator;
	}

	public Code removeMemberPart() {
		final int x = fullName.lastIndexOf("::");
		if (x == -1) {
			return null;
		}
		return new Code(fullName.substring(0, x), separator);
	}

	public String getPortMember() {
		final int x = fullName.lastIndexOf("::");
		if (x == -1) {
			return null;
		}
		return fullName.substring(x + 2);
	}

	// public String getNamespaceSeparator() {
	// return separator;
	// }

	public Code withSeparator(String separator) {
		if (separator == null) {
			throw new IllegalArgumentException();
		}
		if (this.separator != null && this.separator.equals(separator) == false) {
			throw new IllegalStateException();
		}
		return new Code(fullName, separator);
	}

	public static Code of(String code) {
		return of(code, null);
	}

	public static Code of(String code, String separator) {
		if (code == null) {
			return null;
		}
		return new Code(code, separator);
	}

	public final String getFullName() {
		return fullName;
	}

	@Override
	public String toString() {
		return fullName + "(" + separator + ")";
	}

	@Override
	public int hashCode() {
		return fullName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		final Code other = (Code) obj;
		return this.fullName.equals(other.fullName);
	}

	public Code addSuffix(String suffix) {
		return new Code(fullName + suffix, separator);
	}

	public int compareTo(Code other) {
		return this.fullName.compareTo(other.fullName);
	}

	public Code eventuallyRemoveStartingAndEndingDoubleQuote(String format) {
		return Code.of(StringUtils.eventuallyRemoveStartingAndEndingDoubleQuote(fullName, format), separator);
	}

	private final String getNamespace(Map<Code, ILeaf> leafs) {
		String name = this.getFullName();
		if (separator == null) {
			throw new IllegalArgumentException(toString());
		}
		do {
			final int x = name.lastIndexOf(separator);
			if (x == -1) {
				return null;
			}
			name = name.substring(0, x);
		} while (leafs.containsKey(Code.of(name, separator)));
		return name;
	}

	public final Code getShortName(Map<Code, ILeaf> leafs) {
		if (separator == null) {
			throw new IllegalArgumentException();
		}
		final String code = this.getFullName();
		final String namespace = getNamespace(leafs);
		if (namespace == null) {
			return Code.of(code, separator);
		}
		return Code.of(code.substring(namespace.length() + separator.length()), separator);
	}

	public final Code getFullyQualifiedCode(IGroup g) {
		if (separator == null) {
			throw new IllegalArgumentException();
		}
		final String full = this.getFullName();
		if (full.startsWith(separator)) {
			return Code.of(full.substring(separator.length()), separator);
		}
		if (full.contains(separator)) {
			return Code.of(full, separator);
		}
		if (EntityUtils.groupRoot(g)) {
			return Code.of(full, separator);
		}
		final Code namespace2 = g.getNamespace2();
		if (namespace2 == null) {
			return Code.of(full, separator);
		}
		return Code.of(namespace2.fullName + separator + full, separator);
	}

}
