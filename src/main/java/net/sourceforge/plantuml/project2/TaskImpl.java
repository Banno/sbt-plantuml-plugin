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

public class TaskImpl implements Task {

	private final String code;
	private TimeElement start;
	private TimeElement end;
	private int duration;
	private final TimeLine timeLine;

	public TaskImpl(TimeLine timeLine, String code) {
		this.code = code;
		this.timeLine = timeLine;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return code;
	}

	public long getLoad() {
		throw new UnsupportedOperationException();
	}

	public TimeElement getStart() {
		return start;
	}

	public TimeElement getEnd() {
		TimeElement result = start;
		for (int i = 1; i < duration; i++) {
			result = timeLine.next(result);
		}
		return result;
	}

	public TimeElement getCompleted() {
		return timeLine.next(getEnd());
	}

	public void setStart(TimeElement exp) {
		this.start = exp;
	}

	public void setDuration(int value) {
		this.duration = value;
	}

	public void setLoad(int value) {
	}

}
