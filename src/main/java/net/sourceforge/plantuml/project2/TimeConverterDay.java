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

public class TimeConverterDay implements TimeConverter {

	private Day biggest;
	private Day smallest;
	private final double dayWith;
	private final Map<Day, Integer> map1 = new HashMap<Day, Integer>();
	private final Map<Integer, Day> map2 = new HashMap<Integer, Day>();
	private final TimeLine timeLine;

	public TimeConverterDay(TimeLine timeLine, Day start, double dayWith) {
		this.timeLine = timeLine;
		this.dayWith = dayWith;
		this.biggest = start;
		this.smallest = start;
		putDay(start, 0);
	}

//	private boolean isClosed(Day d) {
//		WeekDay wd = d.getWeekDay();
//		if (wd == WeekDay.SAT || wd == WeekDay.SUN) {
//			return true;
//		}
//		return false;
//	}

	private int getPosition(Day d) {
		Integer result = map1.get(d);
		if (result != null) {
			return result.intValue();
		}
		while (d.compareTo(biggest) > 0) {
			int n = getPosition(biggest);
			biggest = biggest.next();
			if (timeLine.isClosed(biggest) == false) {
				n++;
			}
			putDay(biggest, n);
		}
		while (d.compareTo(smallest) < 0) {
			int n = getPosition(smallest);
			smallest = smallest.previous();
			if (timeLine.isClosed(smallest) == false) {
				n--;
			}
			putDay(smallest, n);
		}
		result = map1.get(d);
		if (result != null) {
			return result.intValue();
		}
		throw new UnsupportedOperationException();
	}

	private void putDay(Day d, int n) {
		map1.put(d, n);
		map2.put(n, d);

	}

	public Day getCorrespondingElement(long position) {
		throw new UnsupportedOperationException();
	}

	public double getStartPosition(TimeElement timeElement) {
		return getPosition((Day) timeElement) * dayWith;
	}

	public double getEndPosition(TimeElement timeElement) {
		return (getPosition((Day) timeElement) + 1) * dayWith;
	}

}
