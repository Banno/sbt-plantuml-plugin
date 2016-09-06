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

package smetana.core;



public interface __c__fields {

	public __ptr__ getPtr(String fieldName);
	public __struct__ getStruct(String fieldName);
	public boolean getBoolean(String fieldName);
	public double getDouble(String fieldName);
	public CString getCString(String fieldName);
	public int getInt(String fieldName);
	
	public __ptr__ setPtr(String fieldName, __ptr__ value);
	public void setCString(String fieldName, CString value);
	public void setStruct(String fieldName, __struct__ value);
	public void setInt(String fieldName, int value);
	public void setBoolean(String fieldName, boolean value);
	public void setDouble(String fieldName, double value);
	
	// public __array__ getArray(String fieldName);
	public __array_of_integer__ getArrayOfInteger(String fieldName);
	public __array_of_struct__ getArrayOfStruct(String fieldName);
	public __array_of_ptr__ getArrayOfPtr(String fieldName);
	public __array_of_cstring__ getArrayOfCString(String fieldName);
	

}
