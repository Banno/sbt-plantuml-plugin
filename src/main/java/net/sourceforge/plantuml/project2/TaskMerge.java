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
package net.sourceforge.plantuml.project2;

class TaskMerge implements Task {

	private final String code;
	private final String name;
	private final Task task1;
	private final Task task2;

	TaskMerge(String code, String name, Task task1, Task task2) {
		this.code = code;
		this.name = name;
		this.task1 = task1;
		this.task2 = task2;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public long getLoad() {
		throw new UnsupportedOperationException();
	}

	public TimeElement getStart() {
		return TimeUtils.min(task1.getStart(), task2.getStart());
	}

	public TimeElement getEnd() {
		return TimeUtils.max(task1.getEnd(), task2.getEnd());
	}

	public TimeElement getCompleted() {
		return TimeUtils.max(task1.getCompleted(), task2.getCompleted());
	}

}
