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
package net.sourceforge.plantuml.asciiart;

import java.util.Collection;

public interface UmlCharArea extends BasicCharArea {

	int STICKMAN_HEIGHT = 5;
	int STICKMAN_UNICODE_HEIGHT = 6;

	void drawBoxSimple(int x, int y, int width, int height);

	void drawBoxSimpleUnicode(int x, int y, int width, int height);

	void drawNoteSimple(int x, int y, int width, int height);

	void drawNoteSimpleUnicode(int x, int y, int width, int height);

	void drawStickMan(int x, int y);

	void drawStickManUnicode(int x, int y);

	void drawStringsLR(Collection<? extends CharSequence> strings, int x, int y);

}
