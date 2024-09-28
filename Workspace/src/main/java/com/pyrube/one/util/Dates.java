/*******************************************************************************
 * Copyright 2019, 2023 Aranjuez Poon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.pyrube.one.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.pyrube.one.app.Apps;

/**
 * the <code>Dates</code> contains various methods for manipulating dates.
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class Dates {
	
	/**
	 * a GMT:00 <code>TimeZone</code>
	 */
	public static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");;
	
	/**
	 * suppresses default constructor, ensuring non-instantiability.
	 */
	private Dates() {}
	
	/**
	 * return a <code>Date</code> without time in a given time zone
	 * @param timezone TimeZone
	 * @return Date
	 */
	public static final Date getDate(TimeZone timezone) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance(timezone);
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}
	
	/**
	 * return a <code>Date</code> with date and time in a given time zone
	 * @param timezone TimeZone
	 * @return Date
	 */
	public static final Date getDatetime(TimeZone timezone) {
		Date date = new Date();
		Calendar cal = Calendar.getInstance(timezone);
		cal.setTime(date);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}

	/**
	 * return a current GMT:00 <code>Date</code>
	 * @return Date GMT:00
	 */
	public static final Date getGmtLongTimestamp() {
		return Calendar.getInstance(GMT_TIMEZONE).getTime();
	}
	
	/**
	 * return the first day of this year.
	 * @param date the given Date.
	 * @return Date
	 */
	public static final Date getFirstDayOfYear(Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}

	/**
	 * return the first day of this year.
	 * @param year the given year.
	 * @return Date
	 */ 
	public static final Date getFirstDayOfYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}

	/**
	 * return the last day of year.
	 * @param date the given Date.
	 * @return Date
	 */
	public static final Date getLastDayOfYear(Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DATE, 31);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}

	/**
	 * return the last day of year.
	 * @param year the given year.
	 * @return Date
	 */
	public static final Date getLastDayOfYear(int year) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, 11);
		cal.set(Calendar.DATE, 31);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		return cal.getTime();
	}
	
	/**
	 * return year of a given date
	 * @param date the given Date.
	 * @return int
	 */
	public static int getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	/**
	 * return quarter of a given date
	 * @param date the given Date.
	 * @return int
	 */
	public static int getQuarter(Date date) {
		int month = getMonth(date);
		return ((month - 1) / 3) + 1;
	}

	/**
	 * return natural month of a given date
	 * @param date the given Date.
	 * @return int
	 */
	public static int getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}

	/** 
	 * check whether a date is weekend
	 * @param  date the given Date.
	 * @return boolean
	 */
	public static final boolean isWeekend(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return ((cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
			|| (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY));
	}
	
	/**
	 * check whether a date is after another date without time
	 * @param date Date
	 * @param another Date
	 * @return boolean
	 */
	public static final boolean afterDate(Date date, Date another) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Calendar anotherCal = Calendar.getInstance();
		anotherCal.setTime(another);
		int year = cal.get(Calendar.YEAR);
		int anotherYear = anotherCal.get(Calendar.YEAR);
		if (year > anotherYear) return true;
		if (year < anotherYear) return false;
		return (cal.get(Calendar.DAY_OF_YEAR) > anotherCal.get(Calendar.DAY_OF_YEAR));
	}
	
	/**
	 * check whether a date is after another date
	 * @param date Date
	 * @param another Date
	 * @return boolean
	 */
	public static final boolean after(Date date, Date another) {
		return(date.after(another));
	}
	
	/**
	 * check whether a date is before another date without time
	 * @param date Date
	 * @param another Date
	 * @return boolean
	 */
	public static final boolean beforeDate(Date date, Date another) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Calendar anotherCal = Calendar.getInstance();
		anotherCal.setTime(another);
		int year = cal.get(Calendar.YEAR);
		int anotherYear = anotherCal.get(Calendar.YEAR);
		if (year < anotherYear) return true;
		if (year > anotherYear) return false;
		return (cal.get(Calendar.DAY_OF_YEAR) < anotherCal.get(Calendar.DAY_OF_YEAR));
	}
	
	/**
	 * check whether a date is before another date
	 * @param date Date
	 * @param another Date
	 * @return boolean
	 */
	public static final boolean before(Date date, Date another) {
		return(date.before(another));
	}
	
	/**
	 * check whether a date is same with another date without time
	 * @param date Date
	 * @param another Date
	 * @return boolean
	 */
	public static final boolean sameDate(Date date, Date another) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Calendar anotherCal = Calendar.getInstance();
		anotherCal.setTime(another);
		return ((cal.get(Calendar.YEAR) == anotherCal.get(Calendar.YEAR)) 
			&& (cal.get(Calendar.DAY_OF_YEAR) == anotherCal.get(Calendar.DAY_OF_YEAR)));
	}
	
	/**
	 * check whether a date is same with another date
	 * @param date Date
	 * @param another Date
	 * @return boolean
	 */
	public static final boolean same(Date date, Date another) {
		return(date.equals(another));
	}
	
	/**
	 * add N years base on the given date.
	 * @param date the given Date.
	 * @param years the years of date or time to be added to the field.
	 * @return Date
	 */
	public static final Date addYears(Date date, int years) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, years);
		return cal.getTime();
	}
	
	/**
	 * add N months base on the given date.
	 * @param date the given Date.
	 * @param months the months of date or time to be added to the field.
	 * @return Date
	 */
	public static final Date addMonths(Date date, int months) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, months);
		return cal.getTime();
	}
	
	/**
	 * add N days base on the given date.
	 * @param date the given Date.
	 * @param days the days of date or time to be added to the field.
	 * @return Date
	 */
	public static final Date addDays(Date date, int days) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}
	
	/**
	 * add N hours base on the given date.
	 * @param date the given Date.
	 * @param hours the hours of date or time to be added to the field.
	 * @return Date
	 */
	public static final Date addHours(Date date, int hours) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, hours);
		return cal.getTime();
	}
	
	/**
	 * add N minutes base on the given date.
	 * @param date the given Date.
	 * @param minutes the minutes of date or time to be added to the field.
	 * @return Date
	 */
	public static final Date addMinutes(Date date, int minutes) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return cal.getTime();
	}

	/**
	 * return a time zone offset in milliseconds based on a given date. 
	 * If Daylight Saving Time is in effect at the given date, the offset value is adjusted 
	 * with the amount of daylight saving.
	 * @param date the given Date.
	 * @param timeZone the TimeZone. if it is null, then use system host time zone.
	 * @return offset of the timezone in milliseconds
	 */
	public static int getTimezoneOffset(Date date, TimeZone timezone) {
		if (timezone == null) timezone = TimeZone.getDefault();
		if (timezone.inDaylightTime(date)) {
			return(timezone.getRawOffset() + timezone.getDSTSavings());
		} else {
			return(timezone.getRawOffset());
		}
	}
	
	/**
	 * In some cases, when parse a String into Date using a Time Zone (called fromTimezone), but the String
	 * actually represents a Date in another Time Zone (called toTimezone), so after the Date object is created 
	 * based on fromTimeZone, we need to adjust it to the right Date. <br>
	 * For example, assume "2013-01-01" is a date in "Asia/Shanghai" timezone, but we parse it using "America/New_York" 
	 * timezone (which is the default time zone of a host in New York): <br>
	 * <pre>
	 *   String strDate = "2013-01-01";
	 *   SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
	 *   Date date = fmt.parse(strDate);
	 *   date = DateTool.adjustDate(date, TimeZone.getDefault(), TimeZone.getTimeZone("Asia/Shanghai"));
	 * </pre>
	 * Of course we can create a formatter with the desired TimeZone (using fmt.setTimeZone()) and parse the string directly. 
	 * But in some cases, the string is already parsed using different TimeZone, then we need to adjust it.
	 * <br> 
	 * @param date the given Date.
	 * @param fromTimezone the fromTimezone. if it is null, then use system host timezone.
	 * @param toTimezone the toTimezone. if it is null, then use system host time =zone.
	 * @return java.util.Date
	 */
	public static final Date changeTimezone(Date date, TimeZone fromTimezone, TimeZone toTimezone) {
		return(new Date(date.getTime() + getTimezoneOffset(date, fromTimezone) - getTimezoneOffset(date, toTimezone)));
	}
	
	public static void main(String[] args) {

		/*List<Date> dates = new ArrayList<Date>();
		Date date = Dates.getFirstDayOfYear(2022);
		Date last = Dates.getLastDayOfYear(2022);
		while (!Dates.isSameDay(date, last)) {
			if (Dates.isWeekend(date)) dates.add(date);
			date = Dates.addDays(date, 1);
		}*/
		
		System.out.println(Apps.a.date().adds.days(2).adds.days(1).value());
		System.out.println(Apps.a.datetime().adds.days(1).adds.hours(3).value());
		System.out.println(new Date());
		Apps.a.date.format.of(Apps.i18n.format.name.DATE).formats(new Date());
		System.out.println(Apps.a.datetime().is.weekend());
		System.out.println(Apps.a.datetime().adds.days(4).is.weekend());
		Apps.a.datetime().to.format();
	}

}
