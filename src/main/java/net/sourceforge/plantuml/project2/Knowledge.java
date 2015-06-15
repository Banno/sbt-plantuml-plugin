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

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.plantuml.StringUtils;

public class Knowledge {

	private final TaskContainer taskContainer;
	private final TimeLine timeline;
	private final Map<String, Value> variables = new HashMap<String, Value>();

	public Knowledge(TaskContainer taskContainer, TimeLine timeline) {
		this.taskContainer = taskContainer;
		this.timeline = timeline;
	}

	public Value evaluate(String exp) {
		exp = StringUtils.trin(exp);
		int idx = exp.indexOf('$');
		if (idx != -1) {
			return evaluate(exp.substring(0, idx), exp.substring(idx + 1));
		}
		if (exp.matches("\\d+")) {
			return new ValueInt(Integer.parseInt(exp));
		}
		if (Day.isValidDesc(exp)) {
			final Day day = new Day(exp);
			return new ValueTime(day);
		}
		if (exp.startsWith("^")) {
			exp = exp.substring(1);
		}
		if (variables.containsKey(exp)) {
			return variables.get(exp);
		}
		idx = exp.indexOf("+");
		if (idx != -1) {
			return plus(exp.substring(0, idx), exp.substring(idx + 1));
		}
		throw new UnsupportedOperationException(exp);

	}

	private Value plus(String arg1, String arg2) {
		final Value v1 = evaluate(arg1);
		final Value v2 = evaluate(arg2);
		if (v1 instanceof ValueInt && v2 instanceof ValueInt) {
			return new ValueInt(((ValueInt) v1).getValue() + ((ValueInt) v2).getValue());
		}
		if (v1 instanceof ValueTime && v2 instanceof ValueInt) {
			final int nb = ((ValueInt) v2).getValue();
			TimeElement t = ((ValueTime) v1).getValue();
			if (nb > 0) {
				for (int i = 0; i < nb; i++) {
					t = timeline.next(t);
				}
			}
			if (nb < 0) {
				for (int i = 0; i < -nb; i++) {
					t = timeline.previous(t);
				}
			}
			return new ValueTime(t);
		}
		throw new UnsupportedOperationException();
	}

	private Value evaluate(String task, String attribute) {
		final Task t = taskContainer.getTask(task);
		final TaskAttribute att = TaskAttribute.fromString(attribute);
		if (att == TaskAttribute.COMPLETED) {
			return new ValueTime(t.getCompleted());
		}
		throw new UnsupportedOperationException();
	}

	public void set(String var, Value exp) {
		variables.put(var, exp);
	}

}
