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
package net.sourceforge.plantuml.preproc;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sourceforge.plantuml.CharSequence2;
import net.sourceforge.plantuml.CharSequence2Impl;
import net.sourceforge.plantuml.LineLocation;

class ReadLineInsertable implements ReadLine {

	private final ReadLine source;
	private final List<CharSequence2> inserted = new LinkedList<CharSequence2>();

	public ReadLineInsertable(ReadLine source) {
		this.source = source;
	}

	public void close() throws IOException {
		source.close();
	}

	public CharSequence2 readLine() throws IOException {
		if (inserted.size() > 0) {
			final Iterator<CharSequence2> it = inserted.iterator();
			final CharSequence2 result = it.next();
			it.remove();
			return result;
		}
		return source.readLine();
	}

	public void insert(List<? extends CharSequence> data, LineLocation location) {
		for (CharSequence s : data) {
			inserted.add(new CharSequence2Impl(s, location));
		}
	}

}
