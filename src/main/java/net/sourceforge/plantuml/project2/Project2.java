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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.plantuml.graphic.HorizontalAlignment;
import net.sourceforge.plantuml.graphic.TextBlock;
import net.sourceforge.plantuml.graphic.TextBlockUtils;

public class Project2 implements TaskContainer {

	private final TimeLine timeline;
	private final Knowledge knowledge;
	private final List<TaskImpl> tasks = new ArrayList<TaskImpl>();

	public Project2() {
		this.timeline = new TimeLineDay();
		this.knowledge = new Knowledge(this, timeline);
	}

	public TimeConverter getTimeConverter(double dayWith) {
		return new TimeConverterDay(timeline, getStart(), dayWith);
	}

	public Value getExpression(String exp) {
		return knowledge.evaluate(exp);
	}

	public boolean affectation(String var, Value exp) {
		final int idx = var.indexOf('$');
		if (idx != -1) {
			return affectationTask(var.substring(0, idx), var.substring(idx + 1), exp);
		}
		if (var.startsWith("^")) {
			return affectationJalon(var.substring(1), exp);
		}
		knowledge.set(var, exp);
		return true;
	}

	private boolean affectationJalon(String taskCode, Value exp) {
		final TaskImpl result = new TaskImpl(timeline, taskCode);
		result.setStart(((ValueTime) exp).getValue());
		result.setDuration(0);
		tasks.add(result);
		knowledge.set(taskCode, exp);
		return true;
	}

	private boolean affectationTask(String taskCode, String attribute, Value exp) {
		final TaskImpl t = getOrCreateTask(taskCode);
		final TaskAttribute att = TaskAttribute.fromString(attribute);
		if (att == TaskAttribute.START) {
			t.setStart(((ValueTime) exp).getValue());
			return true;
		}
		if (att == TaskAttribute.DURATION) {
			t.setDuration(((ValueInt) exp).getValue());
			return true;
		}
		if (att == TaskAttribute.LOAD) {
			t.setLoad(((ValueInt) exp).getValue());
			return true;
		}
		throw new UnsupportedOperationException();
	}

	private TaskImpl getOrCreateTask(String taskCode) {
		TaskImpl result = (TaskImpl) getTask(taskCode);
		if (result != null) {
			return result;
		}
		result = new TaskImpl(timeline, taskCode);
		tasks.add(result);
		return result;
	}

	public final List<Task> getTasks() {
		final List<Task> result = new ArrayList<Task>(tasks);
		return Collections.unmodifiableList(result);
	}

	public Task getTask(String code) {
		for (TaskImpl t : tasks) {
			if (t.getCode().equals(code)) {
				return t;
			}
		}
		Task result = null;
		for (Task t : tasks) {
			if (t.getCode().startsWith(code) == false) {
				continue;
			}
			if (result == null) {
				result = t;
			} else {
				result = new TaskMerge(result.getCode(), result.getName(), result, t);
			}

		}
		return result;
	}

	public TextBlock getTimeHeader(double dayWith) {
		final TimeHeaderDay day = new TimeHeaderDay(getStart(), getEnd(), timeline, dayWith);
		final TimeHeaderMonth month = new TimeHeaderMonth(getStart(), getEnd(), timeline, dayWith);
		return TextBlockUtils.mergeTB(month, day, HorizontalAlignment.CENTER);
	}

	private Day getStart() {
		Day result = null;
		for (Task t : tasks) {
			if (result == null || result.compareTo(t.getStart()) > 0) {
				result = (Day) t.getStart();
			}
		}
		return result;
	}

	private Day getEnd() {
		Day result = null;
		for (Task t : tasks) {
			if (result == null || result.compareTo(t.getEnd()) < 0) {
				result = (Day) t.getEnd();
			}
		}
		return result;
	}

}
