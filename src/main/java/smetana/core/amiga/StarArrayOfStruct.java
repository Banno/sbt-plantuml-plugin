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

package smetana.core.amiga;

import smetana.core.AllH;
import smetana.core.UnsupportedC;
import smetana.core.__array_of_struct__;
import smetana.core.__ptr__;
import smetana.core.__struct__;

public class StarArrayOfStruct extends UnsupportedC implements Area, AllH {

	private final __array_of_struct__ array;

	public StarArrayOfStruct(__array_of_struct__ array) {
		this.array = array;
	}

	public void realloc(int nb) {
		array.realloc(nb);
	}

	public String getUID36() {
		return array.getUID36();
	}

	public void memcopyFrom(Area source) {
		throw new UnsupportedOperationException();
	}

	public final __array_of_struct__ getInternalArray() {
		return array;
	}

	public __ptr__ plus(int pointerMove) {
		return new StarArrayOfStruct(array.move(pointerMove));
	}

	public __struct__ getStruct() {
		return array.getStruct();
	}

	public __ptr__ getPtr() {
		return array.getPtr();
	}

	public int minus(__ptr__ other) {
		StarArrayOfStruct other2 = (StarArrayOfStruct) other;
		int res = array.comparePointerInternal(other2.array);
		return res;
	}

	public void setStruct(__struct__ value) {
		array.setStruct(value);
	}

	public __ptr__ getPtrForEquality() {
		return array.getStruct().amp();
	}

	public int comparePointer(__ptr__ other) {
		return array.comparePointerInternal(((StarArrayOfStruct) other).array);
	}

	public boolean isSameThan(StarArrayOfStruct other) {
		return array.comparePointerInternal(other.array) == 0;
	}

	// Fieldname

	public __ptr__ setPtr(String fieldName, __ptr__ data) {
		final Area tmp1 = array.getInternal(0);
		return ((__struct__) tmp1).setPtr(fieldName, data);
	}

	public __ptr__ getPtr(String fieldName) {
		final Area tmp1 = array.getInternal(0);
		return ((__struct__) tmp1).getPtr(fieldName);
	}

	public int getInt(String fieldName) {
		final Area tmp1 = array.getInternal(0);
		return ((__struct__) tmp1).getInt(fieldName);
	}

	public double getDouble(String fieldName) {
		final Area tmp1 = array.getInternal(0);
		return ((__struct__) tmp1).getDouble(fieldName);
	}

	public void setDouble(String fieldName, double data) {
		final Area tmp1 = array.getInternal(0);
		((__struct__) tmp1).setDouble(fieldName, data);
	}

}
