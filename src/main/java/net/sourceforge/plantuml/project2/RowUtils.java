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
package net.sourceforge.plantuml.project2;

import java.util.List;

public abstract class RowUtils {

	public static Row overwrite(Row r1, Row r2) {
		return new RowOverwrite(r1, r2);
	}

	public static Row merge(Row r1, Row r2) {
		return new RowMerge(r1, r2);
	}

	public static Row merge(List<Row> rows) {
		Row result = rows.get(0);
		for (int i = 1; i < rows.size(); i++) {
			result = merge(result, rows.get(i));
		}
		return result;

	}

}
