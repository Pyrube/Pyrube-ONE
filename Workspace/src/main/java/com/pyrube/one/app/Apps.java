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

package com.pyrube.one.app;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.pyrube.one.app.cache.CacheManager;
import com.pyrube.one.app.i18n.format.FormatManager;
import com.pyrube.one.app.i18n.locale.AppLocaleManager;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.app.memo.Note;
import com.pyrube.one.app.persistence.Data;
import com.pyrube.one.app.security.AppPolicy;
import com.pyrube.one.app.security.SecurityManagerFactory;
import com.pyrube.one.app.user.User;
import com.pyrube.one.app.user.UserHolder;
import com.pyrube.one.lang.Strings;
import com.pyrube.one.util.Dates;
import com.pyrube.one.util.Option;
import com.pyrube.one.util.math.Arith;

/**
 * <code>Pyrube-ONE</code> application objects/utilities
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class Apps {

	/**
	 * it cannot be allowed for new <code>Apps</code>
	 */
	private Apps() {}

	/**
	 * <code>constants</code> is for general application constants
	 * 
	 * @author Aranjuez
	 * @since Pyrube-ONE 1.0
	 */
	public static class constants {

		/** application constants */
		public static final String YES = AppConstants.YES;
		public static final String NO  = AppConstants.NO;

		/** used for general status constants */
		public static class stat {

			/** data added/modified/removed */
			public static final String ADDED    = AppConstants.DATA_STAT_ADDED;
			public static final String MODIFIED = AppConstants.DATA_STAT_MODIFIED;
			public static final String REMOVED  = AppConstants.DATA_STAT_REMOVED;
		}
	}

	/**
	 * application config utilities
	 * 
	 * @author Aranjuez
	 * @since Pyrube-ONE 1.0
	 */
	public static class config {

		/**
		 * returns the given application property
		 * @param propName the property name. it could be a slash (/) separated path if it is in a hierarchy. i.e. commProps/host
		 * @return Object the value of the property. it could be String, List or Map.
		 */
		public static Object property(String propName) {
			return AppConfig.getAppConfig().getAppProperty(propName);
		}
		/**
		 * returns all application config properties
		 * @return Map
		 */
		public static Map<String, ?> properties() {
			return AppConfig.getAppConfig().getAppProperties();
		}
	}

	/**
	 * <code>a</code> is for one instance of <code>Pyrube-ONE</code> object
	 * 
	 * @author Aranjuez
	 * @since Pyrube-ONE 1.0
	 */
	public static class a {
		/**
		 * returns a <code>Data</code> instance of a given class
		 * @return <code>Data</code>
		 */
		public static <D extends Data<?>> D data(Class<D> clz) {
			try {
				return clz.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw Apps.an.exception.due(e);
			}
		}

		/** application logger */
		public static class logger {
			/**
			 * returns a <code>Logger</code> instance with a logger name. 
			 * @param loggerName String. 
			 *        the name of the <code>Logger</code> to get. it could be a class 
			 *        name.
			 */
			public static Logger named(String loggerName) {
				return Logger.getInstance(loggerName);
			}
		}

		/**
		 * returns a <code>Apps.a.locale</code> of a given locale
		 * @return <code>Apps.a.locale</code>
		 */
		public static locale locale(String localeCode) { return new a.locale(localeCode); }
		public static locale locale(Locale locale)     { return new a.locale(locale); }
		/** application locale */
		public static class locale {
			/**
			 * this <code>Apps.a.locale</code>
			 */
			private locale $this = this;
			/**
			 * the raw value of <code>Apps.a.locale</code>
			 */
			private Locale value;
			/**
			 * functions
			 */
			public final is is;
			public final to to;

			/**
			 * constructor
			 * @param localeCode String. the locale code
			 */
			private locale(String localeCode) {
				this(AppLocaleManager.localeOf(localeCode));
			}
			/**
			 * constructor
			 * @param locale Locale. the locale
			 */
			private locale(Locale locale) {
				this.value(locale);
				this.is = new is();
				this.to = new to();
			}

			/**
			 * returns a <code>Apps.a.locale</code> of a given locale code
			 * @param localeCode String. the locale code
			 * @return <code>Apps.a.locale</code>
			 */
			public static locale of(String localeCode) { return new a.locale(localeCode); }

			/**
			 * returns the <code>Locale</code> value
			 * @return Locale
			 */
			public Locale value() { return(this.value); }
			/**
			 * sets the <code>Locale</code> value
			 * @param value Locale
			 * @return <code>Apps.a.locale</code>
			 */
			public locale value(Locale value) {
				this.value = value;
				return(this);
			}

			/**
			 * <code>is</code> function is to check whether <code>Apps.a.locale</code>
			 * is ...
			 */
			public class is {
				/**
				 * checks whether <code>Apps.a.locale</code> is supported
				 * @return boolean
				 */
				public boolean supported() {
					return AppLocaleManager.supports($this.value().toString());
				}
			}

			/**
			 * <code>to</code> function is to change <code>Apps.a.locale</code>
			 * to something else
			 */
			public class to {
				/**
				 * returns the code of this <code>Apps.a.locale</code>
				 * @return String
				 */
				public String code() { return($this.value().toString()); }
			}
		}

		/** application message */
		public static class message {
			/**
			 * <code>with</code> function is for new <code>AppMessage</code>
			 * with message code and level
			 */
			public static class with {
				/**
				 * returns an info-level <code>AppMessage</code> with code
				 * @param code String
				 * @return <code>AppMessage</code>
				 */
				public static AppMessage info(String code) { return(AppMessage.info(code)); }
				/**
				 * returns an warn-level <code>AppMessage</code> with code
				 * @param code String
				 * @return <code>AppMessage</code>
				 */
				public static AppMessage warn(String code) { return(AppMessage.warn(code)); }
				/**
				 * returns an error-level <code>AppMessage</code> with code
				 * @param code String
				 * @return <code>AppMessage</code>
				 */
				public static AppMessage error(String code) { return(AppMessage.error(code)); }
				/**
				 * returns an <code>AppMessage</code> with code and success-level
				 * @param code String
				 * @return <code>AppMessage</code>
				 */
				public static AppMessage success(String code) { return(AppMessage.success(code)); }
				/**
				 * returns an <code>AppMessage</code> with code and failure-level
				 * @param code String
				 * @return <code>AppMessage</code>
				 */
				public static AppMessage failure(String code) { return(AppMessage.failure(code)); }
			}

		}

		/**
		 * returns a <code>Apps.a.note</code> with content
		 * @return <code>Apps.a.note</code>
		 */
		public static note note(String content) { return new a.note(content); }
		/** application note */
		public static class note {
			/**
			 * this <code>Apps.a.note</code>
			 */
			private note $this = this;
			/**
			 * the raw value of <code>Apps.a.note</code>
			 */
			protected Note value;
			/**
			 * functions
			 */
			public in in;
			public to to;
			/**
			 * constructor
			 * @param value Date
			 */
			private note(String content) {
				this(new Note(content));
			}
			/**
			 * constructor
			 * @param value Date
			 */
			private note(Note note) {
				this.value(note);
				this.in = new in();
				this.to = new to();
			}

			/**
			 * @param dataType String
			 * @param dataId Long
			 * @return Note
			 */
			public note of(String dataType, Long dataId) {
				return(of(dataType, String.valueOf(dataId), null));
			}
			/**
			 * @param dataType String
			 * @param dataId Long
			 * @param dataStatus String
			 * @return Note
			 */
			public note of(String dataType, Long dataId, String dataStatus) {
				return(of(dataType, String.valueOf(dataId), dataStatus));
			}
			/**
			 * @param dataType String
			 * @param dataId String
			 * @return Note
			 */
			public note of(String dataType, String dataId) {
				return(of(dataType, dataId, null));
			}
			/**
			 * @param dataType String
			 * @param dataId String
			 * @param dataStatus String
			 * @return Note
			 */
			public note of(String dataType, String dataId, String dataStatus) {
				this.value().setDataType(dataType);
				this.value().setDataId(dataId);
				this.value().setDataStatus(dataStatus);
				return(this);
			}

			/**
			 * returns the <code>Note</code> value
			 * @return Date
			 */
			public Note value() { return(this.value); }
			/**
			 * sets the <code>Note</code> value
			 * @param value Note
			 * @return <code>Apps.a.note</code>
			 */
			public note value(Note value) {
				this.value = value;
				return(this);
			}

			/**
			 * <code>in</code> function is to set something in <code>Apps.a.note</code>
			 */
			public class in {
				/**
				 * sets event code in this <code>Note</code>
				 * @param eventCode String
				 * @return Note
				 */
				public note event(String eventCode) {
					$this.value().setEventCode(eventCode);
					return($this);
				}
			}
			/**
			 * <code>to</code> function is to have this <code>Apps.a.note</code> to do something
			 */
			public class to {
				/**
				 * leaves this <code>Note</code>
				 * @return Note
				 */
				public note leave() {
					return $this.value((SecurityManagerFactory.getSecurityManager().leaveNote($this.value())));
				}
			}
		}

		/**
		 * returns a <code>Apps.a.date</code> of system date
		 * @return <code>Apps.a.date</code>
		 */
		public static date date() { return date(new Date()); }
		/**
		 * returns a <code>Apps.a.date</code> of a given date
		 * @return <code>Apps.a.date</code>
		 */
		public static date date(Date date) { return new a.date(date); }
		/** application date */
		public static class date {
			/**
			 * this <code>Apps.a.date</code>
			 */
			private date $this = this;
			/**
			 * the raw value of <code>Apps.a.date</code>
			 */
			protected Date value;
			private LocalDate l_value;

			/**
			 * <code>Apps.a.date</code> with null value
			 */
			public static final date NULVAL = a.date(null);
			
			/**
			 * functions
			 */
			public final to to;
			public final is is;
			public final adds<date> adds;

			/**
			 * constructor
			 */
			private date() {
				this(new Date());
			}
			/**
			 * constructor
			 * @param value Date
			 */
			private date(Date value) {
				this.value(value);
				this.to = new to();
				this.is = new is();
				this.adds = new adds<date>();
			}

			/**
			 * returns the year of this <code>Apps.a.date</code>.
			 * @return int
			 */
			public int year() {
				return(Dates.getYear(this.value()));
			}
			/**
			 * returns the quarter of this <code>Apps.a.date</code>.
			 * @return int
			 */
			public int quarter() {
				return(Dates.getQuarter(this.value()));
			}
			/**
			 * returns the natural month of this <code>Apps.a.date</code>.
			 * @return int
			 */
			public int month() {
				return(Dates.getMonth(this.value()));
			}

			/**
			 * returns the <code>Date</code> value
			 * @return Date
			 */
			public Date value() { return(this.value); }
			/**
			 * sets the <code>Date</code> value
			 * @param value Date
			 * @return <code>Apps.a.date</code>
			 */
			public date value(Date value) {
				if (value != null) {
					this.l_value = value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					this.value = Date.from(this.l_value.atStartOfDay(ZoneId.systemDefault()).toInstant());
				}
				return(this);
			}

			/**
			 * <code>to</code> function is to have this <code>Apps.a.date</code> to do something
			 */
			public class to {
				/**
				 * formats a <code>date</code> with a user/default locale and default name.
				 * into a date string
				 * @return String
				 */
				public String format() { return format(i18n.format.name.DATE); }
				/**
				 * formats a <code>date</code> with a user/default locale and pre-defined name or pattern.
				 * into a date string
				 * @param nameOrPattern String
				 * @return String
				 */
				public String format(String nameOrPattern) { return format((String) null, nameOrPattern); }
				/**
				 * formats a <code>date</code> with a given locale and pre-defined name or pattern.
				 * into a date string
				 * @param locale <code>Apps.a.locale</code>
				 * @param nameOrPattern String
				 * @return String
				 */
				public String format(a.locale locale, String nameOrPattern) {
					return format(locale.value(), nameOrPattern);
				}
				/**
				 * formats a <code>date</code> with a given locale and pre-defined name or pattern.
				 * into a date string
				 * @param locale Locale
				 * @param nameOrPattern String
				 * @return String
				 */
				public String format(Locale locale, String nameOrPattern) {
					return format(locale.toString(), nameOrPattern);
				}
				/**
				 * formats a <code>date</code> with a given locale and pre-defined name or pattern.
				 * into a date string
				 * @param localeCode String
				 * @param nameOrPattern String
				 * @return String
				 */
				public String format(String localeCode, String nameOrPattern) {
					return FormatManager.dateFormatOf(localeCode, nameOrPattern).format($this.value());
				}
			}

			/**
			 * <code>is</code> function is to check whether this <code>Apps.a.date</code>
			 * is same, before or after that <code>Date</code>.
			 */
			public class is {

				/**
				 * check whether a <code>Apps.a.date</code> is after that <code>Date</code> 
				 * ignoring time
				 * @param that Date
				 * @return boolean
				 */
				public boolean after(Date that) { return Dates.afterDate($this.value(), that); }
				/**
				 * check whether a <code>Apps.a.date</code> is before that <code>Date</code> 
				 * ignoring time
				 * @param that Date
				 * @return boolean
				 */
				public boolean before(Date that) { return Dates.beforeDate($this.value(), that); }
				/**
				 * check whether a <code>Apps.a.date</code> is same with that <code>Date</code> 
				 * ignoring time
				 * @param that Date
				 * @return boolean
				 */
				public boolean same(Date that) { return Dates.sameDate($this.value(), that); }
				/**
				 * check whether a <code>Apps.a.date</code> is weekend 
				 * @return boolean
				 */
				public boolean weekend() { return Dates.isWeekend($this.value()); }
			}

			/**
			 * <code>adds</code> function is to add years, months, days, etc.
			 */
			public class adds<S> {
				/**
				 * the subject for this <code>adds</code> function
				 */
				@SuppressWarnings("unchecked")
				private S $subject = (S) $this;

				/**
				 * add N years onto this <code>Apps.a.date</code> or its subclass.
				 * @param years the years of date or time to be added to the field.
				 * @return <code>Apps.a.date</code> or its subclass
				 */
				public S years(int years) {
					$this.value(Dates.addYears($this.value(), years));
					return $subject;
				}
				/**
				 * add N months onto this <code>Apps.a.date</code> or its subclass.
				 * @param months the months of date or time to be added to the field.
				 * @return <code>Apps.a.date</code> or its subclass
				 */
				public S months(int months) {
					$this.value(Dates.addMonths($this.value(), months));
					return $subject;
				}
				/**
				 * add N days onto this <code>Apps.a.date</code> or its subclass.
				 * @param hours the hours of date or time to be added to the field.
				 * @return <code>Apps.a.date</code> or its subclass
				 */
				public S days(int days) {
					$this.value(Dates.addDays($this.value(), days));
					return $subject;
				}
			}

			/**
			 * <code>of</code> utility is for the specified date.
			 */
			public static class of {
				/**
				 * returns the <code>Apps.a.date</code> of today for the current user
				 * @return <code>Apps.a.date</code>
				 */
				public static date today() {
					return(a.date(Dates.getDate(the.user.timezone())));
				}
				/**
				 * returns the <code>Apps.a.date</code> of today at the a given time zone
				 * @param timezoneId String
				 * @return <code>Apps.a.date</code>
				 */
				public static date today(String timezoneId) {
					return(today(TimeZone.getTimeZone(timezoneId)));
				}
				/**
				 * returns the <code>Apps.a.date</code> of today at the given time zone
				 * @param timezone TimeZone
				 * @return <code>Apps.a.date</code>
				 */
				public static date today(TimeZone timezone) {
					return(a.date(Dates.getDate(timezone)));
				}
			}

			/** application date format */
			public static class format {
				/**
				 * the raw value of <code>date.format</code
				 */
				private DateFormat value;

				/**
				 * constructor
				 * @param localeCode String
				 * @param nameOrPattern String
				 * @param z TimeZone
				 */
				private format(String localeCode, String nameOrPattern, TimeZone z) {
					this.value(FormatManager.dateFormatOf(localeCode, nameOrPattern, z));
				}
				/**
				 * return a <code>date.format</code> for the user/default locale and pre-defined name or pattern.
				 * @param nameOrPattern the pre-defined format name or the format pattern
				 * @param z the time zone
				 * @return <code>Apps.a.date.format</code>
				 */
				public static format of(String nameOrPattern) {
					return of((String) null, nameOrPattern, (TimeZone) null);
				}
				/**
				 * return a <code>date.format</code> for the user/default locale and pre-defined name or pattern.
				 * @param nameOrPattern the pre-defined format name or the format pattern
				 * @param z the time zone
				 * @return <code>Apps.a.date.format</code>
				 */
				public static format of(String nameOrPattern, TimeZone z) {
					return of((String) null, nameOrPattern, z);
				}
				/**
				 * returns a <code>date.format</code> for a given locale and pre-defined name or pattern.
				 * @param locale <code>Apps.a.locale</code>
				 * @param nameOrPattern String
				 * @return <code>Apps.a.date.format</code>
				 */
				public static format of(a.locale locale, String nameOrPattern) {
					return of(locale.value(), nameOrPattern);
				}
				/**
				 * returns a <code>date.format</code> for a given locale and pre-defined name or pattern.
				 * @param locale Locale
				 * @param nameOrPattern String
				 * @return <code>Apps.a.date.format</code>
				 */
				public static format of(Locale locale, String nameOrPattern) {
					return of(locale.toString(), nameOrPattern);
				}
				/**
				 * returns a <code>date.format</code> for a given locale and pre-defined name or pattern.
				 * @param localeCode String
				 * @param nameOrPattern String
				 * @return <code>Apps.a.date.format</code>
				 */
				public static format of(String localeCode, String nameOrPattern) {
					return of(localeCode, nameOrPattern, (TimeZone) null);
				}
				/**
				 * returns a <code>date.format</code> for a given locale and pre-defined name or pattern.
				 * @param locale <code>Apps.a.locale</code>
				 * @param nameOrPattern String
				 * @param z TimeZone
				 * @return <code>Apps.a.date.format</code>
				 */
				public static format of(a.locale locale, String nameOrPattern, TimeZone z) {
					return of(locale.value(), nameOrPattern, z);
				}
				/**
				 * returns a <code>date.format</code> for a given locale and pre-defined name or pattern.
				 * @param locale Locale
				 * @param nameOrPattern String
				 * @param z TimeZone
				 * @return <code>Apps.a.date.format</code>
				 */
				public static format of(Locale locale, String nameOrPattern, TimeZone z) {
					return of(locale.toString(), nameOrPattern, z);
				}
				/**
				 * returns a <code>date.format</code> for a given locale and pre-defined name or pattern.
				 * @param localeCode String
				 * @param nameOrPattern String
				 * @param z TimeZone
				 * @return <code>Apps.a.date.format</code>
				 */
				public static format of(String localeCode, String nameOrPattern, TimeZone z) {
					return new a.date.format(localeCode, nameOrPattern, z);
				}

				/**
				 * formats a <code>Apps.a.date</code> into a date/time string
				 * @param date <code>Apps.a.date</code>
				 * @return String
				 */
				public String formats(a.date date) {
					return formats(date.value());
				}
				/**
				 * formats a <code>Date</code> into a date/time string
				 * @param date Date
				 * @return String
				 */
				public String formats(Date date) {
					if (date == null) throw new IllegalArgumentException("Null argement.");
					return this.value().format(date);
				}
				/**
				 * parses text from the given string to produce a <code>Apps.a.date</code>
				 * @param string String
				 * @return <code>Apps.a.date</code>
				 */
				public date parses(String string) {
					if (Strings.isEmpty(string)) throw new IllegalArgumentException("Null or empty argement.");
					try {
						return a.date(this.value().parse(string));
					} catch (ParseException e) {
						throw Apps.an.exception.due("message.error.date.format-invalid", e).params(string, this.value().format(new Date()));
					}
				}
				/**
				 * returns the <code>DateFormat</code> value
				 * @return DateFormat
				 */
				public DateFormat value() { return(this.value); }
				/**
				 * sets the <code>DateFormat</code> value
				 * @param value DateFormat
				 * @return <code>Apps.a.date.format</code>
				 */
				public format value(DateFormat value) {
					this.value = value;
					return(this);
				}
			}

		}

		/**
		 * returns a <code>Apps.a.datetime</code> of system date
		 * @return <code>Apps.a.datetime</code>
		 */
		public static datetime datetime() { return datetime(new Date()); }
		/**
		 * returns a <code>Apps.a.datetime</code> of a given date
		 * @return <code>Apps.a.datetime</code>
		 */
		public static datetime datetime(Date date) { return new a.datetime(date); }
		/** application datetime, instead of timestamp */
		public static class datetime extends date {
			/**
			 * this <code>Apps.a.datetime</code>
			 */
			private datetime $this = this;
			/**
			 * the raw value of <code>datetime</code>
			 */
			private LocalDateTime l_value;
			/**
			 * functions
			 */
			public final to to;
			public adds<datetime> adds;

			/**
			 * constructor
			 */
			private datetime() {
				this(new Date());
			}
			/**
			 * constructor
			 * @param value Date
			 */
			private datetime(Date value) {
				this.value(value);
				this.to = new to();
				this.adds = new adds<datetime>();
			}

			
			/**
			 * sets the <code>Date</code> value
			 * @param value Date
			 * @return <code>Apps.a.date</code>
			 */
			public datetime value(Date value) {
				this.l_value = value.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				this.value = Date.from(this.l_value.atZone(ZoneId.systemDefault()).toInstant());
				return(this);
			}

			/**
			 * <code>to</code> function is to have this <code>Apps.a.datetime</code> to do something
			 */
			public class to extends date.to {
				/**
				 * formats a <code>datetime</code> with a user/default locale and default name.
				 * into a date/time string
				 * @return String
				 */
				@Override
				public String format() { return format(i18n.format.name.TIMESTAMP); }
			}

			/**
			 * <code>is</code> function is to check whether this <code>Apps.a.datetime</code>
			 * is same, before or after that <code>Date</code>.
			 */
			public class is extends date.is { }

			/**
			 * <code>adds</code> extends <code>Apps.a.date.adds</code>
			 * to bind function onto <code>Apps.a.datetime</code> for more.
			 */
			public class adds<S> extends date.adds<datetime> {
				/**
				 * the subject for this <code>adds</code> function
				 */
				@SuppressWarnings("unchecked")
				private S $subject = (S) $this;
				
				/**
				 * adds N hours onto this <code>Apps.a.datetime</code> or its subclass.
				 * @param hours the hours of date or time to be added to the field.
				 * @return <code>Apps.a.datetime</code> or its subclass
				 */
				public S hours(int hours) {
					$this.value(Dates.addHours($this.value(), hours));
					return $subject;
				}
				
				/**
				 * adds N minutes onto this <code>Apps.a.datetime</code> or its subclass.
				 * @param minutes the minutes of date or time to be added to the field.
				 * @return <code>Apps.a.datetime</code> or its subclass
				 */
				public S minutes(int minutes) {
					$this.value(Dates.addMinutes($this.value(), minutes));
					return $subject;
				}
			}

		}

		/**
		 * returns a <code>Apps.a.l_timestamp</code> of a given date
		 * @return <code>Apps.a.l_timestamp</code>
		 */
		public static l_timestamp l_timestamp(Date date) { return new a.l_timestamp(date); }
		/** application long-timestamp */
		public static class l_timestamp extends datetime {
			/**
			 * this <code>Apps.a.l_timestamp</code>
			 */
			private l_timestamp $this = this;
			/**
			 * functions
			 */
			public final to to;
			public final is is;
			public adds<l_timestamp> adds;
			
			/**
			 * constructor
			 */
			private l_timestamp(Date value) {
				this.value(value);
				this.to = new to();
				this.is = new is();
				this.adds = new adds<l_timestamp>();
			}

			/**
			 * sets the <code>Date</code> value
			 * @param value Date
			 * @return <code>Apps.a.l_timestamp</code>
			 */
			public l_timestamp value(Date value) {
				this.value = value;
				return(this);
			}

			/**
			 * <code>to</code> function is to have this <code>Apps.a.l_timestamp</code> to do something
			 */
			public class to extends datetime.to {

				/**
				 * formats a <code>l_timestamp</code> with a user/default locale and default name.
				 * into a date/time string
				 * @return String
				 */
				@Override
				public String format() { return format(i18n.format.name.LONGTIMESTAMP); }
			}

			/**
			 * <code>is</code> function is to check whether this <code>Apps.a.l_timestamp</code>
			 * is same, before or after that <code>Date</code>.
			 */
			public class is extends datetime.is {

				/**
				 * check whether a <code>Apps.a.l_timestamp</code> is after that <code>Date</code> 
				 * @param that Date
				 * @return boolean
				 */
				public boolean after(Date that) { return Dates.after($this.value(), that); }

				/**
				 * check whether a <code>Apps.a.l_timestamp</code> is before that <code>Date</code> 
				 * @param that Date
				 * @return boolean
				 */
				public boolean before(Date that) { return Dates.before($this.value(), that); }

				/**
				 * check whether a <code>Apps.a.l_timestamp</code> is same with that <code>Date</code> 
				 * @param that Date
				 * @return boolean
				 */
				public boolean same(Date that) { return Dates.same($this.value(), that); }
			}

			/**
			 * <code>adds</code> extends <code>Apps.a.datetime.adds</code>
			 * to bind function onto <code>Apps.a.l_timestamp</code>.
			 */
			public class adds<S> extends datetime.adds<l_timestamp> { }

			/**
			 * <code>in</code> function is for the time zone 
			 * that the date is in
			 */
			public static class in {
				/**
				 * returns a <code>Apps.a.l_timestamp</code> in GMT time zone.
				 * @return <code>Apps.a.l_timestamp</code>
				 */
				public static l_timestamp GMT() {
					return(a.l_timestamp(Dates.getGmtLongTimestamp()));
				}
			}
		}

		/**
		 * returns a <code>Apps.a.number</code> of a given number
		 * @return <code>Apps.a.number</code>
		 */
		public static number number(Number number) { return new a.number(number); }
		/** application number */
		public static class number {
			/**
			 * this <code>Apps.a.date</code>
			 */
			private number $this = this;

			/**
			 * the raw value of <code>number</code>
			 */
			protected Number value;

			/**
			 * functions
			 */
			public final to to;

			/**
			 * constructor
			 */
			private number() {
				this(null);
			}
			/**
			 * constructor
			 * @param value Number
			 */
			private number(Number value) {
				this.value(value);
				this.to = new to();
			}

			/**
			 * returns the <code>Number</code> value
			 * @return Number
			 */
			public Number value() { return(this.value); }
			/**
			 * sets the <code>Number</code> value
			 * @param value Date
			 * @return <code>Apps.a.number</code>
			 */
			public number value(Number value) {
				this.value = value;
				return(this);
			}

			/**
			 * <code>to</code> function is to have this <code>Apps.a.date</code> to do something
			 */
			public class to {

				/**
				 * formats a <code>number</code> with a user/default locale and default name.
				 * into a number string
				 * @return String
				 */
				public String format() { return format(i18n.format.name.NUMBER); }
				/**
				 * formats a <code>number</code> with a user/default locale and pre-defined name or pattern.
				 * into a number string
				 * @param nameOrPattern String
				 * @return String
				 */
				public String format(String nameOrPattern) { return format((String) null, nameOrPattern); }
				/**
				 * formats a <code>number</code> with a given locale and pre-defined name or pattern.
				 * into a number string
				 * @param locale <code>Apps.a.locale</code>
				 * @param nameOrPattern String
				 * @return String
				 */
				public String format(a.locale locale, String nameOrPattern) {
					return format(locale.value(), nameOrPattern);
				}
				/**
				 * formats a <code>number</code> with a given locale and pre-defined name or pattern.
				 * into a number string
				 * @param locale Locale
				 * @param nameOrPattern String
				 * @return String
				 */
				public String format(Locale locale, String nameOrPattern) {
					return format(locale.toString(), nameOrPattern);
				}
				/**
				 * formats a <code>number</code> with a given locale and pre-defined name or pattern.
				 * into a number string
				 * @param localeCode String
				 * @param nameOrPattern String
				 * @return String
				 */
				public String format(String localeCode, String nameOrPattern) {
					return FormatManager.dateFormatOf(localeCode, nameOrPattern).format($this.value());
				}
			}

			/** application number format */
			public static class format {
				/**
				 * the raw value of <code>number.format</code
				 */
				private NumberFormat value;

				/**
				 * constructor
				 */
				private format() { }
				/**
				 * constructor
				 * @param localeCode String
				 * @param nameOrPattern String
				 */
				private format(String localeCode, String nameOrPattern) {
					this.value(FormatManager.numberFormatOf(localeCode, nameOrPattern));
				}
				/**
				 * returns a <code>number.format</code> for a user/default locale and pre-defined name or pattern.
				 * @param nameOrPattern String
				 * @return <code>Apps.a.number.format</code>
				 */
				public static format of(String nameOrPattern) { return of((String) null, nameOrPattern); }
				/**
				 * returns a <code>number.format</code> for a given locale and pre-defined name or pattern.
				 * @param locale <code>Apps.a.locale</code>
				 * @param nameOrPattern String
				 * @return <code>Apps.a.number.format</code>
				 */
				public static format of(a.locale locale, String nameOrPattern) {
					return of(locale.value(), nameOrPattern);
				}
				/**
				 * returns a <code>number.format</code> for a given locale and pre-defined name or pattern.
				 * @param locale Locale
				 * @param nameOrPattern String
				 * @return <code>Apps.a.number.format</code>
				 */
				public static format of(Locale locale, String nameOrPattern) {
					return of(locale.toString(), nameOrPattern);
				}
				/**
				 * returns a <code>number.format</code> for a given locale and pre-defined name or pattern.
				 * @param localeCode String
				 * @param nameOrPattern String
				 * @return <code>Apps.a.number.format</code>
				 */
				public static format of(String localeCode, String nameOrPattern) {
					return new a.number.format(localeCode, nameOrPattern);
				}

				/**
				 * formats a <code>Apps.a.number</code> into a number string
				 * @param number <code>Apps.a.number</code>
				 * @return String
				 */
				public String formats(a.number number) {
					return formats(number.value());
				}
				/**
				 * formats a <code>Number</code> into a number string
				 * @param number Number
				 * @return String
				 */
				public String formats(Number number) {
					if (number == null) throw new IllegalArgumentException("Null argument.");
					return this.value().format(number);
				}

				/**
				 * parses text from the given string to produce a <code>Apps.a.number</code>
				 * @param string String
				 * @return <code>Apps.a.number</code>
				 */
				public number parses(String string) {
					if (Strings.isEmpty(string)) throw new IllegalArgumentException("Null or empty argument.");
					try {
						return a.number(this.value().parse(string));
					} catch (ParseException e) {
						throw Apps.an.exception.due("message.error.number.format-invalid", e).params(string, this.value().format(1234567.89));
					}
				}

				/**
				 * returns the <code>NumberFormat</code> value
				 * @return NumberFormat
				 */
				public NumberFormat value() { return(this.value); }
				/**
				 * sets the <code>NumberFormat</code> value
				 * @param value NumberFormat
				 * @return <code>Apps.a.number.format</code>
				 */
				public format value(NumberFormat value) {
					this.value = value;
					return(this);
				}
			}

		}

		/**
		 * returns a <code>Apps.a.decimal</code> of a given number
		 * @return <code>Apps.a.decimal</code>
		 */
		public static decimal decimal(Number number) { return new a.decimal(number); }
		/**
		 * application decimal is a subclass of <code>Apps.a.number</code> to provide
		 * operations and format conversion.
		 */
		public static class decimal extends number {
			/**
			 * this <code>Apps.a.decimal</code>
			 */
			private decimal $this = this;
			/**
			 * the raw value of <code>Apps.a.decimal</code>
			 */
			private BigDecimal value;

			/**
			 * a <code>Apps.a.decimal</code> of zero
			 */
			public static final decimal ZERO = new a.decimal(BigDecimal.ZERO);
			/**
			 * a <code>Apps.a.decimal</code> of one
			 */
			public static final decimal ONE = new a.decimal(BigDecimal.ONE);
			
			/**
			 * functions
			 */
			public final is is;

			/**
			 * constructor
			 * @param value BigDecimal
			 */
			private decimal(BigDecimal value) {
				this.value(value);
				this.is = new is();
			}
			/**
			 * constructor
			 * @param value Number
			 */
			private decimal(Number value) {
				if (value == null) value = (BigDecimal) null;
				else value = new BigDecimal(value.toString());
				this.value((BigDecimal) value);
				this.is = new is();
			}

			/**
			 * 
			 * @param addends BigDecimal[]
			 * @return <code>Apps.a.decimal</code>
			 */
			public decimal adds(BigDecimal...addends) {
				this.value(Arith.sum(this.value(), addends));
				return(this);
			}
			/**
			 * 
			 * @param subtrahend BigDecimal
			 * @return <code>Apps.a.decimal</code>
			 */
			public decimal subtracts(BigDecimal subtrahend) {
				this.value(Arith.differe(this.value(), subtrahend));
				return(this);
			}
			/**
			 * 
			 * @param multipliers BigDecimal[]
			 * @return <code>Apps.a.decimal</code>
			 */
			public decimal multiplies(BigDecimal...multipliers) {
				this.value(Arith.product(this.value(), multipliers));
				return(this);
			}
			/**
			 * 
			 * @param divisor BigDecimal
			 * @return <code>Apps.a.decimal</code>
			 */
			public decimal divides(BigDecimal divisor) {
				this.value(Arith.quotient(this.value(), divisor));
				return(this);
			}

			/**
			 * compares this <code>Apps.a.decimal</code> with that given
			 * number
			 * @param that Number
			 * @return -1, 0, or 1 as this <code>Apps.a.decimal</code> is numerically
			 *         less than, equal to, or greater than that number
			 */
			public int comparesTo(Number that) {
				return(Arith.compare(this.value(), that));
			}

			/**
			 * returns the <code>BigDecimal</code> value
			 * @return BigDecimal
			 */
			public BigDecimal value() { return(this.value); }
			/**
			 * sets the <code>BigDecimal</code> value
			 * @param value BigDecimal
			 * @return <code>Apps.a.decimal</code>
			 */
			public decimal value(BigDecimal value) {
				this.value = value;
				return(this);
			}

			/**
			 * <code>is</code> function is to check whether this <code>Apps.a.decimal</code>
			 * is lt, le, gt, ge, eq, ne that <code>BigDecimal</code>
			 */
			public class is {
				/**
				 * checks whether this <code>Apps.a.decimal</code> is lt that one
				 * @param that Number
				 * @return boolean
				 */
				public boolean lt(Number that) {
					return(Arith.compare($this.value(), that) < 0);
				}
				/**
				 * checks whether this <code>Apps.a.decimal</code> is le that one
				 * @param that BigDecimal
				 * @return boolean
				 */
				public boolean le(Number that) {
					return(Arith.compare($this.value(), that) <= 0);
				}
				/**
				 * checks whether this <code>Apps.a.decimal</code> is gt that one
				 * @param that Number
				 * @return boolean
				 */
				public boolean gt(Number that) {
					return(Arith.compare($this.value(), that) > 0);
				}
				/**
				 * checks whether this <code>Apps.a.decimal</code> is ge that one
				 * @param that Number
				 * @return boolean
				 */
				public boolean ge(Number that) {
					return(Arith.compare($this.value(), that) >= 0);
				}
				/**
				 * checks whether this <code>Apps.a.decimal</code> is eq that one
				 * @param that Number
				 * @return boolean
				 */
				public boolean eq(Number that) {
					return(Arith.compare($this.value(), that) == 0);
				}
				/**
				 * checks whether this <code>Apps.a.decimal</code> is not eq that one
				 * @param that Number
				 * @return boolean
				 */
				public boolean ne(Number that) {
					return(Arith.compare($this.value(), that) != 0);
				}
			}
	
			/**
			 * <code>format</code> extends <code>Apps.a.number.format</code> 
			 * to bind format conversion object onto <code>Apps.a.decimal</code>.
			 */
			public static class format extends number.format { }
		}

	}

	/**
	 * <code>an</code> is for one instance of <code>Pyrube-ONE</code> object
	 * 
	 * @author Aranjuez
	 * @since Pyrube-ONE 1.0
	 */
	public static class an {

		/** application exception utility */
		public static class exception {
			/**
			 * returns an <code>AppException</code> with the specified message code 
			 * and cause. 
			 * @param code String. the message code/the detail message.
			 * @param cause Throwable. the cause.
			 * @return AppException
			 */
			public static AppException due(String code, Throwable cause) {
				return AppException.due(code, cause);
			}
			/**
			 * returns an <code>AppException</code> with the specified message code.
			 * @param code String. the message code/the detail message.
			 * @return AppException
			 */
			public static AppException due(String code) {
				return AppException.due(code);
			}
			/**
			 * returns an <code>AppException</code> with the specified cause and a detail
			 * message of <tt>(cause == null ? null : cause.toString())</tt>
			 * @param cause Throwable. the cause.
			 * @return AppException
			 */
			public static AppException due(Throwable cause) {
				return AppException.due(cause);
			}

		}

		/** application option */
		public static class option {

			/**
			 * returns an <code>Option</code> of a value string and label string
			 * @param value String
			 * @param label String
			 * @return <code>Option</code>
			 */
			public static Option of(String value, String label) { return Option.of(value, label); }
			/**
			 * returns an <code>Option</code> of a value string
			 * @param value String
			 * @return <code>Option</code>
			 */
			public static Option of(String value) { return Option.of(value); }
		}

	}

	/**
	 * <code>the</code> is for the specific instance of <code>Pyrube-ONE</code> object
	 * 
	 * @author Aranjuez
	 * @since Pyrube-ONE 1.0
	 */
	public static class the {

		/** current fiscal utilities */
		public static class fiscal {
			/**
			 * application config property
			 */
			private static final String APPCONF_FIRST_FISCALMONTH = AppConstants.APPCONF_FIRST_FISCALMONTH;
			/**
			 * returns the current fiscal year formatted at the given time zone
			 * @param timezoneId String
			 * @return String
			 */
			public static String year(String timezoneId) {
				int soyMonth = Integer.parseInt((String) config.property(APPCONF_FIRST_FISCALMONTH));
				return Apps.a.date.of.today(timezoneId).adds.months(1 - soyMonth).to.format(the.sys_default.locale(), i18n.format.name.FISCALYEAR);
			}
			/**
			 * returns the current fiscal quarter at the given time zone
			 * @param timezoneId String
			 * @return Integer
			 */
			public static Integer quarter(String timezoneId) {
				int soyMonth = Integer.parseInt((String) config.property(APPCONF_FIRST_FISCALMONTH));
				return Apps.a.date.of.today(timezoneId).adds.months(1 - soyMonth).quarter();
			}
			/** fiscal months utilities */
			public static class months {

				/** fiscal months <code>in</code> functions */
				public static class in {
					/**
					 * returns an array of the fiscal months in the given quarter
					 * @param quarter
					 * @return Integer[]
					 */
					public static Integer[] quarter(int quarter) {
						Integer[] months = new Integer[3];
						int soyMonth = Integer.parseInt((String) config.property(APPCONF_FIRST_FISCALMONTH));
						Integer soqMonth = ((soqMonth = (quarter - 1) * 3 + soyMonth) > 12) ? soqMonth - 12 : soqMonth;
						for (int i = 0, month = soqMonth; i < months.length;) {
							months[i++] = month++;
						}
						return months;
					}
					/**
					 * returns an array of the fiscal months in a year
					 * @param quarter
					 * @return Integer[]
					 */
					public static Integer[] year() {
						Integer[] months = new Integer[12];
						int soyMonth = Integer.parseInt((String) config.property(APPCONF_FIRST_FISCALMONTH));
						for (int i = 0, month = soyMonth; i < months.length; i++, month++) {
							months[i] = (month > 12 ? month - 12 : month) ;
						}
						return months;
					}
				}
			}
		}

		/**
		 * returns the current <code>User</code>
		 * @return User
		 */
		public static User user() { return UserHolder.getUser(); }
		/** current login user utilities */
		public static class user {

			/** user attribute utilities */
			public static class attr {

				/** user attribute name utilities */
				public static class name {
					/** constants for user attribute */
					public static final String FISCAL_YEAR    = "ONE.KEY_USERATTRNAME_FISCALYEAR";
					public static final String FISCAL_QUARTER = "ONE.KEY_USERATTRNAME_FISCALQUAR";
				}
			}
			/**
			 * returns the uuk of current login user
			 * @return String
			 */
			public static String uuk() { return UserHolder.getUser().uuk(); }
			/**
			 * returns the login name of current login user
			 * @return String
			 */
			public static String loginame() { return UserHolder.getUser().loginame(); }
			/**
			 * returns the name of current login user
			 * @return String
			 */
			public static String name() { return UserHolder.getUser().getName(); }
			/**
			 * returns the locale of current login user
			 * @return Locale
			 */
			public static Locale locale() { return UserHolder.getUser().locale(); }
			/**
			 * returns the time zone of current login user
			 * @return TimeZone
			 */
			public static TimeZone timezone() { return UserHolder.getUser().timezone(); }
			/**
			 * returns the country code of current login user
			 * @return String
			 */
			public static String country() { return UserHolder.getUser().country(); }
			/**
			 * returns the user specified attribute
			 * @return Object
			 */
			public static Object current(String attrName) { return UserHolder.getUser().attribute(attrName); }
		}

		/**
		 * application object/utility for system default
		 */
		public static class sys_default {
			/**
			 * returns a <code>Apps.a.locale</code> for system default
			 * @return <code>Apps.a.locale</code>
			 */
			public static a.locale locale() {
				return a.locale(AppLocaleManager.getDefaultLocale());
			}

			/**
			 * returns an <code>AppPolicy</code> of password for system default
			 * @return <code>AppPolicy</code>
			 */
			public static AppPolicy pass_policy() {
				return AppConfig.getAppConfig().getPasswordPolicy();
			}
		}

	}

	/**
	 * <code>some</code> are for some instances of <code>Pyrube-ONE</code> object
	 * 
	 * @author Aranjuez
	 * @since Pyrube-ONE 1.0
	 */
	public static class some {

		/** application objects */
		public static class objects {
			/**
			 * returns cached object in application cache
			 * @param cacheName String. the cache name
			 * @return Serializable
			 */
			public static Serializable cached(String cacheName) {
				return CacheManager.applicationGet(cacheName);
			}
		}
		
		/** application countries */
		public static class countries {

			/**
			 * this <code>countries</code>
			 */
			private countries $this = this;
			/**
			 * the raw values of <code>countries</code>
			 */
			private Option[] values;
			/**
			 * functions
			 */
			public final to to;
			/**
			 * constructor
			 * @param values Option[]
			 */
			private countries(Option[] values) {
				this.values(values);
				this.to = new to();
			}
			/**
			 * return some <code>Apps.some.countries</code> application-allowed
			 * @return <code>Apps.some.countries</code>
			 */
			public static countries allowed() {
				Option[] countries;
				@SuppressWarnings("unchecked")
				ArrayList<Option> listCountries = (ArrayList<Option>) some.objects.cached("listCountries");
				if (listCountries == null || listCountries.size() == 0) {
					countries = new Option[1];
					countries[0] = an.option.of(Apps.config.property("APP_COUNTRY_DEFAULT").toString());
				} else {
					countries = new Option[listCountries.size()];
					for (int i = 0; i < countries.length; i++) {
						countries[i] = listCountries.get(i);
					}
				}
				return new some.countries(countries);
			}

			/**
			 * returns the array of <code>Option</code> values
			 * @return Option[]
			 */
			public Option[] values() { return(this.values); }
			/**
			 * sets the array of <code>Option</code> values
			 * @param values Option[]
			 * @return <code>Apps.some.countries</code>
			 */
			public countries values(Option[] values) {
				this.values = values;
				return(this);
			}

			/**
			 * <code>to</code> function is to change <code>Apps.some.countries</code>
			 * to some things else
			 */
			public class to {
				/**
				 * returns an array of <code>Apps.some.countries</code> codes
				 * @return String[]
				 */
				public String[] codes() {
					Option[] countries = $this.values();
					String[] countryCodes = new String[countries.length];
					for (int i = 0; i < countries.length; i++) {
						countryCodes[i] = countries[i].getValue();
					}
					return(countryCodes);
				}
			}

		}
		
		/** application locales */
		public static class locales {

			/**
			 * this <code>locales</code>
			 */
			private locales $this = this;
			/**
			 * the raw values of <code>locales</code>
			 */
			private Locale[] values;
			/**
			 * functions
			 */
			public final to to;
			/**
			 * constructor
			 * @param values Locale[]
			 */
			private locales(Locale[] values) {
				this.values(values);
				this.to = new to();
			}
			/**
			 * return some <code>Apps.some.locales</code> application-supported
			 * @return <code>Apps.some.locales</code>
			 */
			public static locales supported() {
				Locale[] locales;
				String[] localeCodes = AppLocaleManager.getLocaleCodes();
				if (localeCodes == null || localeCodes.length == 0) {
					locales = new Locale[1];
					locales[0] = AppLocaleManager.getDefaultLocale();
				} else {
					locales = new Locale[localeCodes.length];
					for (int i = 0; i < locales.length; i++) {
						locales[i] = AppLocaleManager.localeOf(localeCodes[i]);
					}
				}
				return new some.locales(locales);
			}

			/**
			 * returns the array of <code>Locale</code> values
			 * @return Locale[]
			 */
			public Locale[] values() { return(this.values); }
			/**
			 * sets the array of <code>Locale</code> values
			 * @param values Locale[]
			 * @return <code>Apps.some.locales</code>
			 */
			public locales values(Locale[] values) {
				this.values = values;
				return(this);
			}

			/**
			 * <code>to</code> function is to change <code>Apps.some.locales</code>
			 * to some things else
			 */
			public class to {
				/**
				 * returns an array of <code>Apps.some.locales</code> codes
				 * @return String[]
				 */
				public String[] codes() {
					Locale[] locales = $this.values();
					String[] localeCodes = new String[locales.length];
					for (int i = 0; i < locales.length; i++) {
						localeCodes[i] = locales[i].toString();
					}
					return(localeCodes);
				}
			}

		}

		/**
		 * returns an array of <code>AppMessage</code>s
		 * @param messages AppMessage[]
		 * @return AppMessage[]
		 */
		public static AppMessage[] messages(AppMessage...messages) {
			return(messages);
		}

		/** application options */
		public static class options {

			/**
			 * returns an array of <code>Option</code>s of some given values
			 * @param values String[]
			 * @param i18nPrefix String
			 * @return Option[]
			 */
			public static Option[] of(String[] values, String i18nPrefix) {
				if (values == null || values.length == 0) return new Option[0];
				Option[] options = new Option[values.length];
				for (int i = 0; i < values.length; i++) {
					options[i] = an.option.of(values[i], i18nPrefix + "." + values[i]);
				}
				return(options);
			}
		}

	}

	/**
	 * application event utilities
	 * 
	 * @author Aranjuez
	 * @since Pyrube-ONE 1.0
	 */
	public static class event {
		/** manual event */
		public static class manual {
			/** manual-note event */
			public static final String NOTE = "MANUAL_NOTE";
		}
		/** setup event */
		public static class setup {
			/** constants for setup events */
			public static final String CREATED_REJECTED = AppConstants.SETUP_EVENT_CREATED_REJECTED;
			public static final String UPDATED_REJECTED = AppConstants.SETUP_EVENT_UPDATED_REJECTED;
			public static final String DELETED_REJECTED = AppConstants.SETUP_EVENT_DELETED_REJECTED;
		}
	}
	
	/**
	 * application i18n utilities
	 * 
	 * @author Aranjuez
	 * @since Pyrube-ONE 1.0
	 */
	public static class i18n {

		/**
		 * application i18n format utilities
		 */
		public static class format {

			/** format name utility */
			public static class name {
				/** Date Format name of Long Timestamp with Zone. i.e. "yyyy-MM-dd HH:mm:ss:SSS Z" */
				public static final String LONGTIMESTAMPZ = FormatManager.DFN_LONGTIMESTAMPZ;
				/** Date Format name of Long Timestamp without Zone. i.e. "yyyy-MM-dd HH:mm:ss:SSS" */
				public static final String LONGTIMESTAMP = FormatManager.DFN_LONGTIMESTAMP;
				/** Date Format name of Timestamp without Zone. i.e. "yyyy-MM-dd HH:mm:ss" */
				public static final String TIMESTAMP = FormatManager.DFN_TIMESTAMP;
				/** Date Format name of Date. i.e. "yyyy-MM-dd" */
				public static final String DATE = FormatManager.DFN_DATE;
				/** Date Format name of Month. i.e. "yyyy-MM" */
				public static final String MONTH = FormatManager.DFN_MONTH;
				/** Date Format name of Year. i.e. "yyyy" */
				public static final String YEAR = FormatManager.DFN_YEAR;
				/** Date Format name of Long Time. i.e. "HH:mm:ss:SSS" */
				public static final String LONGTIME = FormatManager.DFN_LONGTIME;
				/** Date Format name of Time. i.e. "HH:mm:ss" */
				public static final String TIME = FormatManager.DFN_TIME;
				/** Date Format name of Short Time. i.e. "HH:mm" */
				public static final String SHORTTIME = FormatManager.DFN_SHORTTIME;
				/** Date Format name of Fiscal Year. i.e. "'FY'yy" */
				public static final String FISCALYEAR = FormatManager.DFN_FISCALYEAR;
				/** Number Format name of Number. i.e. "#.#" */
				public static final String NUMBER = FormatManager.NFN_NUMBER;
				/** Number Format name of Integer. i.e. "#0", "#,##0" */
				public static final String INTEGER = FormatManager.NFN_INTEGER;
				/** Number Format name of Float. i.e. "#0.0##############" */
				public static final String FLOAT = FormatManager.NFN_FLOAT;
				/** Number Format name of Percent. i.e. "#0.#%" */
				public static final String PERCENT = FormatManager.NFN_PERCENT;
				/** Number Format name of Percent without decimals. i.e. "#0%" */
				public static final String PERCENT0 = FormatManager.NFN_PERCENT0;
				/** Number Format name of Percent with one decimal. i.e. "#0.0%" */
				public static final String PERCENT1 = FormatManager.NFN_PERCENT1;
				/** Number Format name of Percent with two decimals. i.e. "#0.00%" */
				public static final String PERCENT2 = FormatManager.NFN_PERCENT2;
				/** Number Format name of Amount. i.e. "#0.00#" */
				public static final String AMOUNT = FormatManager.NFN_AMOUNT;
				/** Number Format name of Money. i.e. "#,##0.00#" */
				public static final String MONEY = FormatManager.NFN_MONEY;
				/** Number Format name of Money without decimals. i.e. "#,##0" */
				public static final String MONEY0 = FormatManager.NFN_MONEY0;
				/** Number Format name of Money with one decimal. i.e. "#,##0.0" */
				public static final String MONEY1 = FormatManager.NFN_MONEY1;
				/** Number Format name of Money with two decimals. i.e. "#,##0.00" */
				public static final String MONEY2 = FormatManager.NFN_MONEY2;
				/** Number Format name of Money with three decimals. i.e. "#,##0.000" */
				public static final String MONEY3 = FormatManager.NFN_MONEY3;
				/** Number Format name of Number Place in Words. i.e. "#${g3}#${p2}#${p1}#${g2}#${p2}#${p1}#${g1}#${p2}#${p1}0.#" */
				public static final String PLACEINWORDS = FormatManager.NFN_PLACEINWORDS;
			}
		}

	}
	
	/**
	 * application data setup utilities.
	 *
	 * @author Aranjuez
	 * @since Pyrube-ONE 1.0
	 */
	public static class setup {
		/**
		 * application config property
		 */
		private static final String APPCONF_SETUP_DUALCONTROL = AppConstants.APPCONF_SETUP_DUALCONTROL;

		/** setup status utilities. */
		public static class stat {
			/** constants for setup status */
			public static final String VERIFIED         = AppConstants.SETUP_STAT_VERIFIED;
			public static final String CREATED_PENDING  = AppConstants.SETUP_STAT_CREATED_PENDING;
			public static final String CREATED_REJECTED = AppConstants.SETUP_STAT_CREATED_REJECTED;
			public static final String UPDATED_PENDING  = AppConstants.SETUP_STAT_UPDATED_PENDING;
			public static final String UPDATED_REJECTED = AppConstants.SETUP_STAT_UPDATED_REJECTED;
			public static final String DELETED_PENDING  = AppConstants.SETUP_STAT_DELETED_PENDING;
			public static final String DELETED_REJECTED = AppConstants.SETUP_STAT_DELETED_REJECTED;
		}

		/** <code>is</code> is for yes or no. */
		public static class is {
			/**
			 * whether data setup need dual-control
			 * @return boolean
			 */
			public static boolean dualcontrol() {
				return Boolean.parseBoolean((String) config.property(APPCONF_SETUP_DUALCONTROL));
			}
			
		}
	}
}
