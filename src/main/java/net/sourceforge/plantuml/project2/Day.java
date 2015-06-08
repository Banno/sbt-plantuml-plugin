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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

import net.sourceforge.plantuml.project.Month;
import net.sourceforge.plantuml.project.WeekDay;

public class Day implements TimeElement {

	private final int numDay;
	private final Month month;
	private final int year;
	private final WeekDay weekDay;

	private Day(int year, Month month, int numDay, WeekDay weekDay) {
		this.year = year;
		this.month = month;
		this.numDay = numDay;
		this.weekDay = weekDay;
	}

	public static boolean isValidDesc(String desc) {
		if (desc.matches("^\\d{4}/\\d{2}/\\d{2}$")) {
			return true;
		}
		if (desc.matches("^\\d{2}-[A-Za-z]{3}-\\d{4}$")) {
			return true;
		}
		return false;
	}

	public Day(String desc) {
		if (desc.matches("^\\d{4}/\\d{2}/\\d{2}$")) {
			this.year = Integer.parseInt(desc.substring(0, 4));
			this.month = Month.fromNum(Integer.parseInt(desc.substring(5, 7)));
			this.numDay = Integer.parseInt(desc.substring(8, 10));
		} else if (desc.matches("^\\d{2}-[A-Za-z]{3}-\\d{4}$")) {
			this.year = Integer.parseInt(desc.substring(7, 11));
			this.month = Month.valueOf(desc.substring(3, 6));
			this.numDay = Integer.parseInt(desc.substring(0, 2));
		} else {
			throw new IllegalArgumentException(desc);
		}
		final int wd = new GregorianCalendar(year, month.getNum() - 1, numDay).get(Calendar.DAY_OF_WEEK);
		this.weekDay = WeekDay.values()[wd - 1];
	}

	public Day next() {
		if (numDay < month.getNbDays(year)) {
			return new Day(year, month, numDay + 1, weekDay.next());
		}
		final Month next = month.next();
		if (next == null) {
			return new Day(year + 1, Month.JAN, 1, weekDay.next());
		}
		return new Day(year, next, 1, weekDay.next());
	}

	public Day previous() {
		if (numDay > 1) {
			return new Day(year, month, numDay - 1, weekDay.prev());
		}
		final Month prev = month.prev();
		if (prev == null) {
			return new Day(year - 1, Month.DEC, 31, weekDay.prev());
		}
		return new Day(year, prev, prev.getNbDays(year), weekDay.prev());
	}

	@Override
	public String toString() {
		return "" + weekDay + " " + year + "-" + month + "-" + String.format("%02d", numDay);
	}

	public final int getNumDay() {
		return numDay;
	}

	public final Month getMonth() {
		return month;
	}

	public final int getYear() {
		return year;
	}

	public int compareTo(TimeElement other2) {
		final Day other = (Day) other2;
		if (year > other.year) {
			return 1;
		}
		if (year < other.year) {
			return -1;
		}
		final int cmpMonth = month.compareTo(other.month);
		if (cmpMonth != 0) {
			return cmpMonth;
		}
		return numDay - other.numDay;
	}

	@Override
	public boolean equals(Object obj) {
		final Day this2 = (Day) obj;
		return this.numDay == this2.numDay && this.month == this2.month && this.year == this2.year;
	}

	@Override
	public int hashCode() {
		return numDay * 420 + year + month.hashCode();
	}

	public final WeekDay getWeekDay() {
		return weekDay;
	}

	public long getTypicalDuration() {
		return 1000L * 60 * 60 * 24;
	}

	public long getStartUTC() {
		final GregorianCalendar cal = new GregorianCalendar(new SimpleTimeZone(0, "GMT"));
		cal.setTimeInMillis(0);
		cal.set(GregorianCalendar.YEAR, year);
		cal.set(GregorianCalendar.MONTH, month.getNumNormal());
		cal.set(GregorianCalendar.DAY_OF_MONTH, numDay);
		return cal.getTimeInMillis();
	}

	public long getEndUTC() {
		return next().getStartUTC();
	}

}
