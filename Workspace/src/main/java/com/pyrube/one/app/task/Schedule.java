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

package com.pyrube.one.app.task;

import java.io.Serializable;
import java.util.Calendar;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.i18n.format.FormatManager;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.lang.Strings;

/**
 * the <code>Schedule</code> data is based on a given time zone.
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class Schedule implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8651224505926826596L;
	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(Schedule.class.getName());
	/**
	 * schedule dates type: Normal. setup years, months, days and weekdays
	 */
	public static final String SCHEDULE_TYPE_NORMAL = "N";
	/**
	 * schedule dates type: number of days. every number of days.
	 */
	public static final String SCHEDULE_TYPE_DAYS = "D";
	/**
	 * schedule dates type: number of months. every number of months.
	 */
	public static final String SCHEDULE_TYPE_MONTHS = "M";
	/**
	 * schedule dates type: number of years. every number of years.
	 */
	public static final String SCHEDULE_TYPE_YEARS = "Y";
	/**
	 * month names, 
	 * 1 = JANUARY, 2 = FEBRUARY, 3 = MARCH, 4 = APRIL, 5 = MAY, 6 = JUNE, 
	 * 7 = JULY, 8 = AUGUST, 9 = SEPTEMBER, 10 = OCTOBER, 11 = NOVEMBER, 12 = DECEMBER
	 */
	public static final String[] MONTH_NAMES =
		new String[] {
			"JANUARY",
			"FEBRUARY",
			"MARCH",
			"APRIL",
			"MAY",
			"JUNE",
			"JULY",
			"AUGUST",
			"SEPTEMBER",
			"OCTOBER",
			"NOVEMBER",
			"DECEMBER" };
	/**
	 * month short names, 
	 * 1 = JAN, 2 = FEB, 3 = MAR,  4 = APR,  5 = MAY,  6 = JUN, 
	 * 7 = JUL, 8 = AUG, 9 = SEP, 10 = OCT, 11 = NOV, 12 = DEC
	 */
	public static final String[] MONTH_NAMES_3 =
		new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
	/**
	 * week day names, 
	 * 1 = Sunday, 2 = Monday, 3 = Tuesday, 4 = Wednesday, 5 = Thursday, 6 = Friday, 7 = Saturday
	 */
	public static final String[] WEEKDAY_NAMES =
		new String[] { "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" };
	/**
	 * week day short names, 
	 * 1 = Sun, 2 = Mon, 3 = Tue, 4 = Wed, 5 = Thu, 6 = Fri, 7 = Sat
	 */
	public static final String[] WEEKDAY_NAMES_3 = new String[] { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };
	/**
	 * time zone ID
	 */
	private String timezoneId = null;
	/**
	 * time zone. The schedule date/time is in this time zone.
	 */
	private TimeZone timezone = null;
	/**
	 * schedule dates type
	 */
	private String scheduleType = SCHEDULE_TYPE_NORMAL;
	/**
	 * the schedule dates period  for Type of DAYS, MONTHS and YEARS
	 */
	private int period = 0;
	/**
	 * the schedule dates period starting date (yyyy-MM-dd) for Type of DAYS, MONTHS and YEARS
	 */
	private String strPeriodStart = null;
	private Calendar periodStart = null;
	/**
	 * years in format "yyyy,yyyy-yyyy,yyyy", yyyy is the four-digit year. 
	 * NULL or empty means all years. For example, "2006" (only in year 2006); 
	 * "2006,2008" (in year 2006 and 2008); "2006-2010,2015" (from year 2006 to 2010 
	 * and year 2015).
	 */
	private String strYears;
	private TreeSet<Integer> years = null; // sorted 4-digit years
	/**
	 * months in format "mm,mm-mm,mm", mm is from 1 to 12 or month names 
	 * (month names: JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, 
	 * OCTOBER, NOVEMBER, DECEMBER, or JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, 
	 * NOV, DEC).  NULL or empty means all months. For example, "1" (every January); 
	 * "1-6" (for Jan, Feb, Mar, Apr, May and Jun); "1,7" (for Jan and Jul); 
	 * "1-4,7" (for Jan, Feb, Mar, Apr and Jul); "Jan-May,8-Oct".
	 */
	private String strMonths;
	private TreeSet<Integer> months = null; // sorted Integer 0 to 11
	/**
	 * days of the month in format "dd,dd-dd,dd", dd is from 1 to 31. 
	 * dd also could be "LAST" to indicate the last day of the month, NULL or empty means all 
	 * days of the month. For example, "1" (every first day of the month); "10,20" (every 10th 
	 * and 20th of the month); "1-20,25" (from first day to 20th day and 25th of the month); 
	 * "Last" (the last day of each month); "25-Last" (from 20th to the last day of each month).
	 */
	private String strDays;
	private TreeSet<Integer> days = null; // sorted Integer 1 to 31, and 32 for Last
	private boolean lastDayOfMonth = false; // whether scheduled on last day of month
	/**
	 * days of the week in format "wd,wd-wd,wd", wd is from 1 to 7 (1 - Sunday, 2 - Monday, ..., 7 - Saturday) 
	 * or uses day names. NULL or empty means all days of the week. The day names of the week are: 
	 * Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, or Sun, Mon, Tue, Wed, Thu, Fri, Sat. 
	 * For example, "2-6"; "Monday-Friday"; "Mon-Fri,Sunday"; "2,Saturday,Mon-Wednesday".  
	 * Note: if both SCHD_DAY and SCHD_WEEKDAY are provided, the schedule will satisfy both conditions. For example, 
	 * SCHD_DAY="1" and SCHD_WEEKDAY="Monday", then schedule will be the dates that are the first day of month and also Monday.
	 */
	private String strWeekdays;
	private TreeSet<Integer> weekdays = null; // sorted Integer 1 to 7
	/**
	 * hours in format "hh,hh-hh,hh", hh is from 0 to 23. 
	 * NULL or empty means all hours. For example, "0", "8-12,14-17".
	 */
	private String strHours;
	private TreeSet<Integer> hours = null; // sorted Integer 0 to 23
	/**
	 * minutes in format "mm,mm-mm,mm", mm is from 0 to 59. 
	 * NULL or empty means 0. For example, "0"; "0,10,20,30,40,50"; "10-20".
	 */
	private String strMinutes;
	private TreeSet<Integer> minutes = null; // sorted Integer 0 to 59
	/**
	 * constructor
	 */
	public Schedule(String timezoneId) {
		setTimezoneId(timezoneId);
	}
	/**
	 * constructor for schedule type of NORMAL
	 * @param timezoneId
	 * @param years
	 * @param months
	 * @param days
	 * @param weekdays
	 * @param hours
	 * @param minutes
	 * @exception AppException
	 */
	public Schedule(
		String timezoneId,
		String years,
		String months,
		String days,
		String weekdays,
		String hours,
		String minutes)
		throws AppException {
		setTimezoneId(timezoneId);
		this.scheduleType = SCHEDULE_TYPE_NORMAL;
		setYears(years);
		setMonths(months);
		setDays(days);
		setWeekdays(weekdays);
		setHours(hours);
		setMinutes(minutes);
	}
	/**
	 * constructor for schedule type of every number of DAYS, MONTHS or YEARS.
	 * @param timezoneId
	 * @param scheduleType
	 * @param hours
	 * @param minutes
	 * @param period
	 * @param periodStart
	 * @throws AppException
	 */
	public Schedule(
		String timezoneId,
		String scheduleType,
		String hours,
		String minutes,
		int period,
		String periodStart)
		throws AppException {
		setTimezoneId(timezoneId);
		setHours(hours);
		setMinutes(minutes);
		setSchedulePeriod(scheduleType, period, periodStart);
	}
	/**
	 * constructor for any type of schedule
	 * @param timezoneId
	 * @param scheduleType
	 * @param years
	 * @param months
	 * @param days
	 * @param weekdays
	 * @param hours
	 * @param minutes
	 * @param period
	 * @param periodStart
	 * @throws AppException
	 */
	public Schedule(
		String timezoneId,
		String scheduleType,
		String years,
		String months,
		String days,
		String weekdays,
		String hours,
		String minutes,
		int period,
		String periodStart)
		throws AppException {
		setTimezoneId(timezoneId);
		if (SCHEDULE_TYPE_NORMAL.equals(scheduleType)) {
			this.scheduleType = scheduleType;
			setYears(years);
			setMonths(months);
			setDays(days);
			setWeekdays(weekdays);
		} else {
			setSchedulePeriod(scheduleType, period, periodStart);
		}
		setHours(hours);
		setMinutes(minutes);
	}
	/**
	 * @param timezoneId
	 */
	public void setTimezoneId(String timezoneId) {
		this.timezoneId = timezoneId;
		if (this.timezoneId != null) {
			this.timezone = TimeZone.getTimeZone(timezoneId);
		} else {
			this.timezone = TimeZone.getDefault();
		}
	}
	/**
	 * @return
	 */
	public String getTimezoneId() {
		return (timezoneId);
	}
	/**
	 * @return
	 */
	public TimeZone getTimezone() {
		return(timezone);
	}
	/**
	 * @return
	 */
	public String getScheduleType() {
		return (scheduleType);
	}
	/**
	 * set years
	 * @param pYears is the Years in format "yyyy,yyyy-yyyy,yyyy", yyyy is 2005, etc
	 * @exception AppException
	 */
	public void setYears(String pYears) throws AppException {
		strYears = pYears;
		years = null;
		if (pYears != null && pYears.length() > 0) {
			years = parseIntRange(pYears, 1970, 3000);
		}
	}
	/**
	 * get years
	 * @return
	 */
	public String getYears() {
		return (strYears);
	}
	/**
	 * set months
	 * @param pMonths is the Years in format "mm,mm-mm,mm", mm is from 1 to 12 or month names <br>
	 *  JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER.  or <br>
	 *  JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC
	 *
	 * @exception AppException
	 */
	public void setMonths(String pMonths) throws AppException {
		strMonths = pMonths;
		months = null;
		if (pMonths != null && pMonths.length() > 0) {
			pMonths = pMonths.toUpperCase();
			// check full names
			for (int i = 0; i < MONTH_NAMES.length; ++i) {
				int iPos = -1;
				while ((iPos = pMonths.indexOf(MONTH_NAMES[i])) >= 0) {
					pMonths =
						pMonths.substring(0, iPos)
							+ String.valueOf(i + 1)
							+ (iPos + (MONTH_NAMES[i]).length() < pMonths.length()
								? pMonths.substring(iPos + (MONTH_NAMES[i]).length())
								: "");
				}
			}
			// check short names
			for (int i = 0; i < MONTH_NAMES_3.length; ++i) {
				int iPos = -1;
				while ((iPos = pMonths.indexOf(MONTH_NAMES_3[i])) >= 0) {
					pMonths =
						pMonths.substring(0, iPos)
							+ String.valueOf(i + 1)
							+ (iPos + (MONTH_NAMES_3[i]).length() < pMonths.length()
								? pMonths.substring(iPos + (MONTH_NAMES_3[i]).length())
								: "");
				}
			}
			months = parseIntRange(pMonths, 1, 12, -1);
		}
	}
	/**
	 * get months
	 * @return
	 */
	public String getMonths() {
		return (strMonths);
	}
	/**
	 * set days in month
	 * @param pDays is the days of the month in format "dd,dd-dd,dd", dd is from 1 to 31. 
	 * dd also could be "LAST" to indicate the last calendar day of the month. NULL or empty means all days of the month. 
	 * For example, "1" (every first day of the month); "10,20" (every 10th and 20th of the month); 
	 * "1-20,25" (from first day to 20th day and 25th of the month); "Last" (the last calendar day of each month); 
	 * "25-Last" (from 25th to the last calendar day of each month); 
	 * @exception AppException
	 */
	public void setDays(String pDays) throws AppException {
		strDays = pDays;
		lastDayOfMonth = false;
		days = null;
		if (pDays != null && pDays.length() > 0) {
			pDays = pDays.toUpperCase();
			int iPos = -1;
			while ((iPos = pDays.indexOf("LAST")) >= 0) {
				this.lastDayOfMonth = true;
				pDays =
					pDays.substring(0, iPos)
						+ String.valueOf(31)
						+ (iPos + ("LAST").length() < pDays.length() ? pDays.substring(iPos + ("LAST").length()) : "");
			}
			days = parseIntRange(pDays, 1, 31);
		}
	}
	/**
	 * remove text from a string. the text must not be in range like dd-dd, meaning the 
	 * char at left and right of the text must not be -.
	 * 
	 * @param str string to be filtered
	 * @param text the text to be removed
	 * @return array of two objects, the first is Boolean indicating whether the text is 
	 * found in the string, the second is the string with the text removed. 
	 * @exception AppException
	 */
	private Object[] filterString(String str, String text) throws AppException {
		Object[] res = new Object[2];
		boolean found = false;
		if (str != null && str.length() > 0) {
			String strLeft = null;
			String strRight = null;
			int iPos = -1;
			while ((iPos = str.indexOf(text)) >= 0) {
				found = true;
				strLeft = str.substring(0, iPos);
				strRight = (iPos + text.length() < str.length()) ? str.substring(iPos + text.length()) : "";
				if (strLeft.endsWith("-") || strRight.startsWith("-"))
					throw new AppException("message.error.invalid-day-range");
				if (strLeft.endsWith(","))
					strLeft = strLeft.substring(0, strLeft.length() - 1);
				if (strLeft.length() == 0 && strRight.startsWith(",")) {
					strRight = strRight.length() > 1 ? strRight.substring(1) : "";
				}
				str = strLeft + strRight;
			}
		}
		res[0] = new Boolean(found);
		res[1] = (str != null && str.length() > 0) ? str : "";
		return (res);
	}
	/**
	 * get days of month
	 * @return
	 */
	public String getDays() {
		return (strDays);
	}
	/**
	 * set days in week
	 * @param pWeekDays is the days of the week in format "wd,wd-wd,wd", wd is from 1 to 7 (1 - Sunday, 2 - Monday, ..., 7 - Saturday) 
	 * or uses day names. wd also could be "LASTBIZ" to indicate the last business day of the week, "FIRSTBIZ" to 
	 * indicate the first business day of the week. "FIRSTBIZ" and "LASTBIZ" can not be used in range (wd-wd). NULL or empty means 
	 * all days of the week. The day names of the week are: Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, 
	 * or Sun, Mon, Tue, Wed, Thu, Fri, Sat. 
	 * For example, "2-6"; "Monday-Friday"; "Mon-Fri,Sunday"; "2,Saturday,Mon-Wednesday"; "LastBiz"; "FirstBiz,LastBiz".  
	 * Note: if both SCHD_DAY and SCHD_WEEKDAY are provided, the schedule will satisfy both conditions. For example, 
	 * SCHD_DAY="1" and SCHD_WEEKDAY="Monday", then schedule will be the dates that are the first day of month and also Monday.
	 * 
	 * @exception AppException
	 */
	public void setWeekdays(String pWeekDays) throws AppException {
		strWeekdays = pWeekDays;
		weekdays = null;
		if (pWeekDays != null && pWeekDays.length() > 0) {
			pWeekDays = pWeekDays.toUpperCase();
			// check full names
			for (int i = 0; i < WEEKDAY_NAMES.length; ++i) {
				int iPos = -1;
				while ((iPos = pWeekDays.indexOf(WEEKDAY_NAMES[i])) >= 0) {
					pWeekDays =
						pWeekDays.substring(0, iPos)
							+ String.valueOf(i + 1)
							+ (iPos + (WEEKDAY_NAMES[i]).length() < pWeekDays.length()
								? pWeekDays.substring(iPos + (WEEKDAY_NAMES[i]).length())
								: "");
				}
			}
			// check short names
			for (int i = 0; i < WEEKDAY_NAMES_3.length; ++i) {
				int iPos = -1;
				while ((iPos = pWeekDays.indexOf(WEEKDAY_NAMES_3[i])) >= 0) {
					pWeekDays =
						pWeekDays.substring(0, iPos)
							+ String.valueOf(i + 1)
							+ (iPos + (WEEKDAY_NAMES_3[i]).length() < pWeekDays.length()
								? pWeekDays.substring(iPos + (WEEKDAY_NAMES_3[i]).length())
								: "");
				}
			}
			weekdays = parseIntRange(pWeekDays, 1, 7);
		}
	}
	/**
	 * get days of week
	 * @return
	 */
	public String getWeekdays() {
		return (strWeekdays);
	}
	/**
	 * set hours in day
	 * @param pHours is the hours in format "hh,hh-hh,hh", hh is from 0 to 23
	 * @exception AppException
	 */
	public void setHours(String pHours) throws AppException {
		if(Strings.isEmpty(pHours))
			pHours = "0";
		strHours = pHours;
		hours = null;
		hours = parseIntRange(pHours, 0, 23);
	}
	/**
	 * get hours
	 * @return
	 */
	public String getHours() {
		return (strHours);
	}
	/**
	 * set minutes in hour
	 * @param pMinutes is the minutes in format "mm,mm-mm,mm", mm is from 0 to 59
	 * @exception AppException
	 */
	public void setMinutes(String pMinutes) throws AppException {
		strMinutes = pMinutes;
		minutes = null;
		minutes = parseIntRange(pMinutes, 0, 59);
	}
	/**
	 * get minutes
	 * @return
	 */
	public String getMinutes() {
		return (strMinutes);
	}
	/**
	 * 
	 * @param pPeriodStart
	 */
	public void setPeriodStart(String pPeriodStart) {
		strPeriodStart = pPeriodStart;
		periodStart = null;
	}
	/**
	 * @return periodStart
	 */
	public Calendar getPeriodStart() {
		if (this.periodStart != null) return this.periodStart;
		Calendar periodStart = Calendar.getInstance(timezone);
		if (!Strings.isEmpty(this.strPeriodStart)) {
			try { periodStart.setTime(FormatManager.dateFormatOf("yyyy-MM-dd").parse(this.strPeriodStart)); } catch (Exception e) {
				logger.warn("Invalid value (" + this.strPeriodStart + ") for periodStart. Default value will be used.");
			}
		}
		this.periodStart = Calendar.getInstance(timezone);
		this.periodStart.set(periodStart.get(Calendar.YEAR), 
						periodStart.get(Calendar.MONTH),
						periodStart.get(Calendar.DAY_OF_MONTH),
						0, 0, 0);
		this.periodStart.set(Calendar.MILLISECOND, 0);
		return this.periodStart;
	}
	/**
	 * parse int range
	 * @param strVal is the ranges in string, separated by comma (,), vv,vv-vv,vv
	 * @param min is the minumum number
	 * @param max is the maximum number
	 * @return sorted set of integer
	 * @exception AppException
	 */
	private TreeSet<Integer> parseIntRange(String strVal, int min, int max) throws AppException {
		return (parseIntRange(strVal, min, max, 0));
	}
	/**
	 * parse int range
	 * @param strVal is the ranges in string, separated by comma (,), vv,vv-vv,vv
	 * @param min is the minumum number in strVal
	 * @param max is the maximum number in strVal
	 * @param adjust is the adjustment number for each integer in strVal, default is 0. it may be -1, 1, 2, etc. this value is added after min/max checking.
	 * @return sorted set of integer
	 * @exception jobAdminException
	 */
	private static TreeSet<Integer> parseIntRange(String strVal, int min, int max, int adjust) throws AppException {
		if (strVal == null || strVal.length() == 0)
			return (null);
		try {
			StringTokenizer tok = new StringTokenizer(strVal, ",");
			TreeSet<Integer> numbers = new TreeSet<Integer>();
			while (tok.hasMoreTokens()) {
				String vals = tok.nextToken();
				int begin = -1000;
				int end = -1000;
				int iPos = vals.indexOf("-");
				if (iPos < 0) {
					begin = Integer.parseInt(vals);
					end = begin;
				} else if (iPos == 0) {
					if (vals.length() > 1) {
						end = Integer.parseInt(vals.substring(1));
						begin = end;
					}
				} else if (iPos == vals.length() - 1) {
					begin = Integer.parseInt(vals.substring(0, iPos));
					end = begin;
				} else {
					begin = Integer.parseInt(vals.substring(0, iPos));
					end = Integer.parseInt(vals.substring(iPos + 1));
				}
				if (begin != -1000) {
					if (begin < min)
						begin = min;
					if (begin > max)
						begin = max;
				}
				if (end != -1000) {
					if (end < min)
						end = min;
					if (end > max)
						end = max;
				}
				if (begin > end) {
					int j = begin;
					begin = end;
					end = j;
				}
				if (begin != -1000) {
					for (int i = begin; i <= end; ++i)
						numbers.add(new Integer(i + adjust));
				}
			}
			if (numbers.isEmpty())
				numbers = null;
			return (numbers);
		} catch (Exception e) {
			logger.error("error", e);
			throw new AppException("message.error.invalid-schedule-number");
		}
	}
	/**
	 * get current time in current time zone
	 * @return Calendar
	 */
	public Calendar getCurrentTime() {
		return(Calendar.getInstance(timezone));
	}
	/**
	 * get first year. This is for ScheduleType=Normal
	 */
	@SuppressWarnings("unused")
	private int getFirstYear() {
		if (years == null)
			return (getCurrentTime().get(Calendar.YEAR));
		else
			return (((years.first())).intValue());
	}
	/**
	 * get next year from fromYear (including this). This is for ScheduleType=Normal
	 * @param fromYear is the year to search from
	 * @return the next year, or -1 if there is no next year found
	 */
	private int getNextYear(int fromYear) {
		if (years == null)
			return (fromYear);
		SortedSet<Integer> subset = years.tailSet(new Integer(fromYear));
		if (subset.isEmpty())
			return (-1);
		return (subset.first().intValue());
	}
	/**
	 * get first month. This is for ScheduleType=Normal
	 * @return month (0 - 11)
	 */
	@SuppressWarnings("unused")
	private int getFirstMonth() {
		if (months == null)
			return (0);
		else
			return (months.first().intValue());
	}
	/**
	 * get next month from fromMonth (including this). This is for ScheduleType=Normal
	 * @param fromMonth is the month to search from
	 * @return the next month, or -1 if there is no next month found
	 */
	private int getNextMonth(int fromMonth) {
		if (fromMonth > 11)
			return (-1);
		if (months == null)
			return (fromMonth);
		SortedSet<Integer> subset = months.tailSet(new Integer(fromMonth));
		if (subset.isEmpty())
			return (-1);
		return (subset.first().intValue());
	}
	/**
	 * get first day of month and consider the weekday schedule. This is for ScheduleType=Normal
	 * @param pYear is the year for the day
	 * @param pMonth is the month for the day (0 - 11)
	 * @return the day of the month, or -1 if there is no day found
	 * @exception AppException
	 */
	@SuppressWarnings("unused")
	private int getFirstDay(int pYear, int pMonth) throws AppException {
		Calendar c = Calendar.getInstance(timezone);
		c.set(pYear, pMonth, 1, 0, 0, 0);
		return (getNextDay(c));
	}
	/**
	 * get next day of month and consider the weekday schedule. This is for ScheduleType=Normal
	 * @param fromDate is the date it starts to find (including this date). Its time zone is same as siteTimeZone.
	 * @return the day of the month, or -1 if there is no day found
	 * @exception AppException
	 */
	private int getNextDay(Calendar fromDate) throws AppException {
		int d = fromDate.get(Calendar.DAY_OF_MONTH);
		int m = fromDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		while (d <= m) {
			// check Days of month
			if (days != null || lastDayOfMonth) {
				boolean found = false;
				while (d <= m) {
					// check day of month
					if (days != null) {
						SortedSet<Integer> subset = days.tailSet(new Integer(d));
						if (!subset.isEmpty()) {
							int d1 = subset.first().intValue(); // d <= d1
							d = d1;
							if (d <= m) {
								found = true;
								break;
							}
						}
					}
					// check last calendar day of month
					if (lastDayOfMonth) {
						d = m;
						found = true;
						break;
					}
					// check next day
					++d;
				}
				// if still not found, then return -1 (not found)
				if (!found)
					return (-1);
			}
			// ensure it is also scheduled in days of week
			if (weekdays == null) {
				// all days of week are allowed, so got the date. return the day
				break;
			} else {
				fromDate.set(Calendar.DAY_OF_MONTH, d);
				int wd = fromDate.get(Calendar.DAY_OF_WEEK);
				// check days of week, if yes, return the day
				if (weekdays != null && weekdays.contains(new Integer(wd)))
					break;
			}
			// a day found in days of month is not in days of week, so try next day of month
			++d;
		}
		// if all days of month are not satisfied, then return -1 (not found)
		if (d > m)
			return (-1);
		return (d);
	}
	/**
	 * get first hour
	 */
	@SuppressWarnings("unused")
	private int getFirstHour() {
		if (hours == null)
			return (0);
		else
			return (hours.first().intValue());
	}
	/**
	 * get next hour from fromHour
	 * @param fromHour is the hour to search from (including this)
	 * @return the next hour, or -1 if there is no next hour found
	 */
	private int getNextHour(int fromHour) {
		if (fromHour > 23)
			return (-1);
		if (hours == null)
			return (fromHour);
		SortedSet<Integer> subset = hours.tailSet(new Integer(fromHour));
		if (subset.isEmpty())
			return (-1);
		return (subset.first().intValue());
	}
	/**
	 * get first minute
	 */
	@SuppressWarnings("unused")
	private int getFirstMinute() {
		if (minutes == null)
			return (0);
		else
			return (minutes.first().intValue());
	}
	/**
	 * get next minute from fromMinute
	 * @param fromMinute is the minute to search from (including this)
	 * @return the next minute, or -1 if there is no next minute found
	 */
	private int getNextMinute(int fromMinute) {
		if (fromMinute > 59)
			return (-1);
		if (minutes == null)
			return (fromMinute);
		SortedSet<Integer> subset = minutes.tailSet(new Integer(fromMinute));
		if (subset.isEmpty())
			return (-1);
		return (subset.first().intValue());
	}
	/**
	 * set the schedule period info
	 * @param scheduleType
	 * @param period
	 * @param periodStart
	 * @throws AppException
	 */
	private void setSchedulePeriod(String scheduleType, int period, String periodStart)
		throws AppException {
		if (!SCHEDULE_TYPE_DAYS.equals(scheduleType)
			&& !SCHEDULE_TYPE_MONTHS.equals(scheduleType)
			&& !SCHEDULE_TYPE_YEARS.equals(scheduleType))
			throw new AppException("message.error.invalid-schedule-type", scheduleType);
		if (period <= 0)
			throw new AppException("message.error.invalid-schedule-period");
		if (periodStart == null)
			throw new AppException("message.error.invalid-schedule-start");
		this.scheduleType = scheduleType;
		this.period = period;
		setPeriodStart(periodStart);
	}
	/**
	 * get next time in the schedule from now (including now on minute).
	 * @return Calendar object presenting the next schedule time. return null if there is no scheduled time
	 * @exception AppException
	 */
	public Calendar nextScheduledTime() throws AppException {
		return (nextScheduledTime(getCurrentTime()));
	}
	/**
	 * get next time in the schedule from a given time which will be rounded up to the next minute if the given from time has non-zero seconds.
	 * @param fromTime is the from time. it will be changed to the next time after this call. Its time zone is same as siteTimeZone.
	 * @return Calendar object presenting the next schedule time from the fromTime (including this time). return null if there is no scheduled time
	 * @exception AppException
	 */
	public Calendar nextScheduledTime(Calendar fromTime) throws AppException {
		if (fromTime == null)
			return (null);
		if (fromTime.get(Calendar.SECOND) > 0 || fromTime.get(Calendar.MILLISECOND) > 0) {
			// round to next minute
			fromTime.add(Calendar.MINUTE, 1);
			fromTime.set(Calendar.SECOND, 0);
			fromTime.set(Calendar.MILLISECOND, 0);
		}
		return (obtainNextTime(fromTime));
	}
	/**
	 * get next time in the schedule
	 * @param currTime is the from time. it will be changed after this call. Its time zone is same as siteTimeZone.
	 * @return Calendar object presenting the next schedule time from currTime (including this time). return null if there is no scheduled time
	 * @exception AppException
	 */
	private Calendar obtainNextTime(Calendar currTime) throws AppException {
		// obtain next date
		currTime = obtainNextDate(currTime);
		//return null if there is no scheduled time
		if(currTime==null)
			return null;
		//check time	
		int iHour = currTime.get(Calendar.HOUR_OF_DAY); // 0-23
		int nHour = getNextHour(iHour);
		if (nHour == -1) { // need next day
			currTime.add(Calendar.DAY_OF_MONTH, 1);
			currTime.set(Calendar.HOUR_OF_DAY, 0);
			currTime.set(Calendar.MINUTE, 0);
			return (obtainNextTime(currTime)); // recursive
		} else if (nHour != iHour) { // a new hour
			currTime.set(Calendar.HOUR_OF_DAY, nHour);
			currTime.set(Calendar.MINUTE, 0);
			return (obtainNextTime(currTime)); // recursive
		}
		int iMinute = currTime.get(Calendar.MINUTE); // 0-59
		int nMinute = getNextMinute(iMinute);
		if (nMinute == -1) { // need next hour
			currTime.add(Calendar.HOUR_OF_DAY, 1);
			currTime.set(Calendar.MINUTE, 0);
			return (obtainNextTime(currTime)); // recursive
		} else if (nMinute != iMinute) { // a new Minute
			currTime.set(Calendar.MINUTE, nMinute);
		}
		return (currTime);
	}
	/**
	 * get next date in the schedule from a given date
	 * @param fromDate is the from date. it will be changed to the next date after this call. Its time zone is same as siteTimeZone.
	 * @return Calendar object presenting the next schedule date from the fromDate (including this date). return null if there is no scheduled date
	 * @exception AppException
	 */
	public Calendar nextScheduledDate(Calendar fromDate) throws AppException {
		if (fromDate == null)
			return (null);
		return (obtainNextDate(fromDate));
	}
	/**
	 * get next Date (no time) in the schedule
	 * @param currDate is the from date (no time). it will be changed after this call. Its time zone is same as siteTimeZone.
	 * @return Calendar object presenting the next schedule date from currDate (including this date). return null if there is no scheduled date
	 * @exception AppException
	 */
	private Calendar obtainNextDate(Calendar currDate) throws AppException {
		Calendar periodStart = this.getPeriodStart();
		if (!SCHEDULE_TYPE_NORMAL.equals(scheduleType)) {
			if (currDate.before(periodStart)) { // currTime is earlier than start date, than use the start date
				currDate.set(Calendar.YEAR, periodStart.get(Calendar.YEAR));
				currDate.set(Calendar.MONTH, periodStart.get(Calendar.MONTH));
				currDate.set(Calendar.DAY_OF_MONTH, periodStart.get(Calendar.DAY_OF_MONTH));
				currDate.set(Calendar.HOUR_OF_DAY, 0);
				currDate.set(Calendar.MINUTE, 0);
			} else if (SCHEDULE_TYPE_DAYS.equals(scheduleType)) {
				long numDiffDays = diffDays(periodStart, currDate);
				int nLeft = (int) (numDiffDays % period);
				if (nLeft > 0) { // currTime is in middle of a period, use the next one
					currDate.add(Calendar.DAY_OF_MONTH, period - nLeft);
					currDate.set(Calendar.HOUR_OF_DAY, 0);
					currDate.set(Calendar.MINUTE, 0);
				}
			} else if (SCHEDULE_TYPE_MONTHS.equals(scheduleType)) {
				long numDiffMonths = diffMonths(periodStart, currDate);
				int nLeft = (int) (numDiffMonths % period);
				if (nLeft > 0) { // currTime is in middle of a period, use the next scheduled month
					currDate.set(Calendar.DAY_OF_MONTH, 1); // set to the first day of next schduled month
					currDate.add(Calendar.MONTH, period - nLeft);
					currDate.set(Calendar.HOUR_OF_DAY, 0);
					currDate.set(Calendar.MINUTE, 0);
					return (obtainNextDate(currDate));
				} else {
					// check the day of the month, it must be same as the periodStart. if ths day of periodStart is biger than the maximum days of the current month, then use the last day of this month
					if (currDate.get(Calendar.DAY_OF_MONTH) < periodStart.get(Calendar.DAY_OF_MONTH)) {
						if (periodStart.get(Calendar.DAY_OF_MONTH) <= currDate.getMaximum(Calendar.DAY_OF_MONTH)) {
							currDate.set(Calendar.DAY_OF_MONTH, periodStart.get(Calendar.DAY_OF_MONTH));
							// if the new day to be set is bigger than the maximum day, then it is rounded to next month.
						} else {
							currDate.set(Calendar.DAY_OF_MONTH, currDate.getMaximum(Calendar.DAY_OF_MONTH));
						}
						currDate.set(Calendar.HOUR_OF_DAY, 0);
						currDate.set(Calendar.MINUTE, 0);
					} else if (currDate.get(Calendar.DAY_OF_MONTH) > periodStart.get(Calendar.DAY_OF_MONTH)) {
						// next scheduled month and day. 
						currDate.set(Calendar.DAY_OF_MONTH, 1); // set to the first day of next schduled month
						currDate.add(Calendar.MONTH, period);
						// if the current day is 30, and the new month has maximum 28 days, then the new day will be changed to 28 (its maximum day)
						currDate.set(Calendar.HOUR_OF_DAY, 0);
						currDate.set(Calendar.MINUTE, 0);
						return (obtainNextDate(currDate));
					}
				}
			} else if (SCHEDULE_TYPE_YEARS.equals(scheduleType)) {
				int numDiffYears = currDate.get(Calendar.YEAR) - periodStart.get(Calendar.YEAR);
				int nLeft = numDiffYears % period;
				if (nLeft > 0) { // currTime is in middel od a period, use the next scheduled year
					currDate.add(Calendar.YEAR, period - nLeft);
					currDate.set(Calendar.MONTH, 0);
					currDate.set(Calendar.DAY_OF_MONTH, 1);
					currDate.set(Calendar.HOUR_OF_DAY, 0);
					currDate.set(Calendar.MINUTE, 0);
					return (obtainNextDate(currDate)); // recursive
				}
				// check the month and day. they must be same as the periodStart
				if (currDate.get(Calendar.MONTH) < periodStart.get(Calendar.MONTH)) {
					currDate.set(Calendar.DAY_OF_MONTH, 1);
					currDate.set(Calendar.MONTH, periodStart.get(Calendar.MONTH));
					currDate.set(Calendar.HOUR_OF_DAY, 0);
					currDate.set(Calendar.MINUTE, 0);
					return (obtainNextDate(currDate)); // recursive
				} else if (currDate.get(Calendar.MONTH) > periodStart.get(Calendar.MONTH)) {
					// passed the month, use the next scheduled year
					currDate.set(Calendar.DAY_OF_MONTH, 1);
					currDate.set(Calendar.MONTH, periodStart.get(Calendar.MONTH));
					currDate.add(Calendar.YEAR, period);
					currDate.set(Calendar.HOUR_OF_DAY, 0);
					currDate.set(Calendar.MINUTE, 0);
					return (obtainNextDate(currDate)); // recursive
				} else { // month is same
					// check day of the month
					if (currDate.get(Calendar.DAY_OF_MONTH) < periodStart.get(Calendar.DAY_OF_MONTH)) {
						if (periodStart.get(Calendar.DAY_OF_MONTH) <= currDate.getMaximum(Calendar.DAY_OF_MONTH)) {
							currDate.set(Calendar.DAY_OF_MONTH, periodStart.get(Calendar.DAY_OF_MONTH));
						} else {
							currDate.set(Calendar.DAY_OF_MONTH, currDate.getMaximum(Calendar.DAY_OF_MONTH));
						}
						currDate.set(Calendar.HOUR_OF_DAY, 0);
						currDate.set(Calendar.MINUTE, 0);
					} else if (currDate.get(Calendar.DAY_OF_MONTH) > periodStart.get(Calendar.DAY_OF_MONTH)) {
						// passed the day, use the next scheduled year
						currDate.set(Calendar.DAY_OF_MONTH, 1);
						currDate.set(Calendar.MONTH, periodStart.get(Calendar.MONTH));
						currDate.add(Calendar.YEAR, period);
						currDate.set(Calendar.HOUR_OF_DAY, 0);
						currDate.set(Calendar.MINUTE, 0);
						return (obtainNextDate(currDate)); // recursive
					}
				}
			}
		} else {
			int iYear = currDate.get(Calendar.YEAR); // 4 digits
			int nYear = getNextYear(iYear);
			if (nYear == -1) { // no more years available
				return (null);
			} else if (nYear != iYear) { // a new year
				currDate.set(Calendar.YEAR, nYear);
				currDate.set(Calendar.MONTH, 0);
				currDate.set(Calendar.DAY_OF_MONTH, 1);
				currDate.set(Calendar.HOUR_OF_DAY, 0);
				currDate.set(Calendar.MINUTE, 0);
				return (obtainNextDate(currDate)); // recursive
			}
			int iMonth = currDate.get(Calendar.MONTH); // 0-11
			int nMonth = getNextMonth(iMonth);
			if (nMonth == -1) { // need next year
				currDate.set(Calendar.YEAR, iYear + 1);
				currDate.set(Calendar.MONTH, 0);
				currDate.set(Calendar.DAY_OF_MONTH, 1);
				currDate.set(Calendar.HOUR_OF_DAY, 0);
				currDate.set(Calendar.MINUTE, 0);
				return (obtainNextDate(currDate)); // recursive
			} else if (nMonth != iMonth) { // a new month
				currDate.set(Calendar.MONTH, nMonth);
				currDate.set(Calendar.DAY_OF_MONTH, 1);
				currDate.set(Calendar.HOUR_OF_DAY, 0);
				currDate.set(Calendar.MINUTE, 0);
				return (obtainNextDate(currDate)); // recursive
			}
			int iDay = currDate.get(Calendar.DAY_OF_MONTH); // 1-31
			int nDay = getNextDay(currDate);
			if (nDay == -1) { // need next Month
				currDate.add(Calendar.MONTH, 1);
				currDate.set(Calendar.DAY_OF_MONTH, 1);
				currDate.set(Calendar.HOUR_OF_DAY, 0);
				currDate.set(Calendar.MINUTE, 0);
				return (obtainNextDate(currDate)); // recursive
			} else if (nDay != iDay) { // a new day
				currDate.set(Calendar.DAY_OF_MONTH, nDay);
				currDate.set(Calendar.HOUR_OF_DAY, 0);
				currDate.set(Calendar.MINUTE, 0);
				return (obtainNextDate(currDate)); // recursive
			}
		}
		return (currDate);
	}
	/**
	 * calculate the number of days difference between two dates (toDate - fromDate)
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	private long diffDays(Calendar fromDate, Calendar toDate) {
		return (toDate.getTime().getTime() - fromDate.getTime().getTime()) / (24 * 60 * 60 * 1000L);
	}
	/**
	 * calculate the number of months difference between two dates (toDate - fromDate).
	 * These two calendar dates must be in same time zone.
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	private long diffMonths(Calendar fromDate, Calendar toDate) {
		//((toDate.get(Calendar.YEAR) - 1970) * 12 + toDate.get(Calendar.MONTH)) - ((fromDate.get(Calendar.YEAR) - 1970) * 12 + fromDate.get(Calendar.MONTH));
		return (
			(toDate.get(Calendar.YEAR) - fromDate.get(Calendar.YEAR)) * 12
				+ (toDate.get(Calendar.MONTH) - fromDate.get(Calendar.MONTH)));
	}
	/**
	 * check whether the schedule meets a given datetime
	 * @param time the time to check. Its time zone must same as timezone ID.
	 * @return true - the time is a scheduled time
	 * @exception AppException
	 */
	public boolean meetsDatetime(Calendar time) throws AppException {
		// check date
		if (!meetsDate(time))
			return (false);
		// check time
		int iHour = time.get(Calendar.HOUR_OF_DAY); // 0-23
		if (getNextHour(iHour) != iHour)
			return (false);
		int iMinute = time.get(Calendar.MINUTE); // 0-59
		if (getNextMinute(iMinute) != iMinute)
			return (false);
		return (true);
	}
	/**
	 * check whether the schedule meets a given date. Its time zone is same as timezone ID.
	 * @param date the date to check
	 * @return true - the date is a scheduled date
	 * @exception AppException
	 */
	public boolean meetsDate(Calendar date) throws AppException {
		Calendar periodStart = this.getPeriodStart();
		if (SCHEDULE_TYPE_NORMAL.equals(scheduleType)) {
			int iYear = date.get(Calendar.YEAR); // 4 digits
			if (getNextYear(iYear) != iYear)
				return (false);
			int iMonth = date.get(Calendar.MONTH); // 0-11
			if (getNextMonth(iMonth) != iMonth)
				return (false);
			int iDay = date.get(Calendar.DAY_OF_MONTH); // 1-31
			if (getNextDay(date) != iDay)
				return (false);
		} else if (date.before(periodStart)) {
			// the date is earlier than start date
			return (false);
		} else if (SCHEDULE_TYPE_DAYS.equals(scheduleType)) {
			long numDiffDays = diffDays(periodStart, date);
			if (numDiffDays % period != 0)
				return (false);
		} else if (SCHEDULE_TYPE_MONTHS.equals(scheduleType)) {
			long numDiffMonths = diffMonths(periodStart, date);
			if (numDiffMonths % period != 0)
				return (false);
			// check the day of the month, it must be same as the periodStart
			if (date.get(Calendar.DAY_OF_MONTH) != periodStart.get(Calendar.DAY_OF_MONTH))
				return (false);
		} else if (SCHEDULE_TYPE_YEARS.equals(scheduleType)) {
			int numDiffYears = date.get(Calendar.YEAR) - periodStart.get(Calendar.YEAR);
			if (numDiffYears % period != 0)
				return (false);
			// check the month and day. they must be same as the periodStart
			if (date.get(Calendar.MONTH) != periodStart.get(Calendar.MONTH)
				|| date.get(Calendar.DAY_OF_MONTH) != periodStart.get(Calendar.DAY_OF_MONTH))
				return (false);
		} else {
			return (false);
		}
		return (true);
	}
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("schedule:");
		if (timezone != null)
			sb.append(" timezone=").append(timezone.getID());
		sb.append(" type=").append(scheduleType);
		if (strYears != null && strYears.length() > 0)
			sb.append(" year=").append(strYears);
		if (strMonths != null && strMonths.length() > 0)
			sb.append(" month=").append(strMonths);
		if (strDays != null && strDays.length() > 0)
			sb.append(" day=").append(strDays);
		if (strWeekdays != null && strWeekdays.length() > 0)
			sb.append(" weekday=").append(strWeekdays);
		if (strHours != null && strHours.length() > 0)
			sb.append(" hour=").append(strHours);
		if (strMinutes != null && strMinutes.length() > 0)
			sb.append(" minute=").append(strMinutes);
		if (period > 0) {
			Calendar periodStart = this.getPeriodStart();
			sb.append(" every ").append(period);
			if (SCHEDULE_TYPE_DAYS.equals(scheduleType))
				sb.append(" days");
			else if (SCHEDULE_TYPE_MONTHS.equals(scheduleType))
				sb.append(" months");
			else if (SCHEDULE_TYPE_YEARS.equals(scheduleType))
				sb.append(" years");
			sb
				.append(" from ")
				.append(periodStart.get(Calendar.YEAR))
				.append("-")
				.append(periodStart.get(Calendar.MONTH) + 1)
				.append("-")
				.append(periodStart.get(Calendar.DAY_OF_MONTH))
				.append(" (yyyy-MM-dd)");
		}
		return (sb.toString());
	}
}
