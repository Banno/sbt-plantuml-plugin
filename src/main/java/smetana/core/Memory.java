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

import smetana.core.amiga.StarArrayOfPtr;
import smetana.core.amiga.StarStar;
import smetana.core.amiga.StarStruct;

public class Memory {

	public static __ptr__ malloc(Class cl) {
		JUtils.LOG("MEMORY::malloc " + cl);
		return StarStruct.malloc(cl);
	}

	public static __ptr__ malloc(size_t size) {
		return (__ptr__) size.malloc();
	}

	public static __ptr__ realloc(__ptr__ old, size_t size) {
		if (old instanceof StarArrayOfPtr) {
			((StarArrayOfPtr) old).realloc(((size_t_array_of_something) size).getNb());
			return old;
		}
		if (old instanceof StarStar) {
			((StarStar) old).realloc(((size_t_array_of_array_of_something_empty) size).getNb());
			return old;
		}
		throw new UnsupportedOperationException();
	}

	public static void free(Object arg) {
	}

	public static int identityHashCode(CString data) {
		if (data==null) {
			return 0;
		}
		//int result = 2 * System.identityHashCode(data);
		int result = data.getUid();
		Z.z().all.put(result, data);
		// System.err.println("Memory::identityHashCode data=" + data);
		// System.err.println("Memory::identityHashCode result=" + result + " " + Z.z().all.size());
		return result;
	}

	public static Object fromIdentityHashCode(int hash) {
		// System.err.println("Memory::fromIdentityHashCode hash=" + hash);
		if (hash % 2 != 0) {
			throw new IllegalArgumentException();
		}
		Object result = Z.z().all.get(hash);
		// System.err.println("Memory::fromIdentityHashCode result=" + result);
		if (result == null) {
			throw new UnsupportedOperationException();
		}
		return result;
	}

}
