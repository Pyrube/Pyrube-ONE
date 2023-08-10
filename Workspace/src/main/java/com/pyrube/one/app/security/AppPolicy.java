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

package com.pyrube.one.app.security;

import java.util.Date;

import com.pyrube.one.app.Apps;

/**
 * application policy for system security
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class AppPolicy {
	
	/**
	 * this <code>AppPolicy</code>
	 */
	private AppPolicy $this = this;

	/**
	 * whether this policy is enabled
	 */
	private boolean enabled = false;
	/**
	 * the minimum length
	 * 0 means no length limit
	 */
	private int minimumLength = 8;
	/**
	 * the maximum length
	 */
	private int maximumLength = 16;
	/**
	 * the special characters
	 */
	private char[] specialChars = null;
	/**
	 * the expiry age - month of expiry limit
	 * 0 means never to get expired
	 */
	private int expiryAge = 0;
	/**
	 * the maximum attempts
	 * if the maximum attempts reached, user will be locked
	 * 0 means no limit on the number of attempts
	 */
	private int maximumAttempts = 0;
	/**
	 * the locking period
	 * when the locking period in minute ends, it will auto-unlock
	 * 0 means no auto-unlock. just only unlocked manually by
	 * system administrator
	 */
	private int lockingPeriod = 0;

	/**
	 * functions
	 */
	public final has has = new has();
	public final checks checks = new checks();
	public final decides decides = new decides();
	
	/**
	 * @return the enabled
	 */
	public boolean on() { return(isEnabled()); }
	/**
	 * @deprecated use <code>on</code> instead
	 */
	private boolean isEnabled() {
		return enabled;
	}
	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	/**
	 * @return the minimumLength
	 */
	public int min_length() { return(minimumLength); }
	/**
	 * @deprecated use <code>min_length</code> instead
	 */
	private int getMinimumLength() {
		return minimumLength;
	}
	/**
	 * @param minimumLength the minimumLength to set
	 */
	public void setMinimumLength(int minimumLength) {
		this.minimumLength = minimumLength;
	}
	/**
	 * @return the maximumLength
	 */
	public int max_length() { return(getMaximumLength()); }
	/**
	 * @deprecated use <code>max_length</code> instead
	 */
	private int getMaximumLength() {
		return maximumLength;
	}
	/**
	 * @param maximumLength the maximumLength to set
	 */
	public void setMaximumLength(int maximumLength) {
		this.maximumLength = maximumLength;
	}
	/**
	 * @return the specialChars
	 */
	public char[] special_chars() { return(getSpecialChars()); }
	/**
	 * @deprecated use <code>special_chars</code> instead
	 */
	private char[] getSpecialChars() {
		return specialChars;
	}
	/**
	 * @param specialChars the specialChars to set
	 */
	public void setSpecialChars(char[] specialChars) {
		this.specialChars = specialChars;
	}
	/**
	 * returns the expiry age - days of expiry age
	 * 0 means never to get expired
	 * @return int
	 */
	public int expiry_age() { return(getExpiryAge()); }
	/**
	 * @deprecated use <code>expiry_age</code> instead
	 */
	private int getExpiryAge() {
		return expiryAge;
	}
	/**
	 * 
	 * @param expiryAge the expiryAge to set
	 */
	public void setExpiryAge(int expiryAge) {
		this.expiryAge = expiryAge;
	}
	/**
	 * returns the maximum attempts
	 * if the maximum attempts reached, user will be locked
	 * 0 means no limit on the number of attempts
	 * @return int
	 */
	public int max_attempts() { return(getMaximumAttempts()); }
	/**
	 * @deprecated use <code>max_attempts</code> instead
	 */
	private int getMaximumAttempts() {
		return maximumAttempts;
	}
	/**
	 * @param maximumAttempts the maximumAttempts to set
	 */
	public void setMaximumAttempts(int maximumAttempts) {
		this.maximumAttempts = maximumAttempts;
	}
	/**
	 * returns the locking period
	 * when the locking period in minute ends, it will auto-unlock
	 * 0 means no auto-unlock. just only unlocked manually by
	 * system administrator
	 * @return int
	 */
	public int locking_period() { return(getLockingPeriod()); }
	/**
	 * @deprecated use <code>locking_period</code> instead
	 */
	private int getLockingPeriod() {
		return lockingPeriod;
	}
	/**
	 * @param lockingPeriod the lockingPeriod to set
	 */
	public void setLockingPeriod(int lockingPeriod) {
		this.lockingPeriod = lockingPeriod;
	}

	/**
	 * <code>generates</code> function is to generate random string matched 
	 * with this <code>AppPolicy</code>, or something else.
	 */
	public String generates() {
		return "1234Abcd";
	}

	/**
	 * tests whether a given string matches this <code>AppPolicy</code>
	 * @param password String
	 * @return boolean
	 */
	public boolean tests(String string) {
		return (!this.enabled || true);
	}
	
	/**
	 * <code>has</code> function checks whether this <code>AppPolicy</code> has ...
	 */
	public class has {
		/**
		 * whether this <code>AppPolicy</code> has length limit
		 * @return boolean
		 */
		public boolean length_limit() {
			return($this.isEnabled() && $this.getMinimumLength() > 0);
		}
		/**
		 * whether this <code>AppPolicy</code> has expiry age
		 * @return boolean
		 */
		public boolean expiry_age() {
			return($this.isEnabled() && $this.getExpiryAge() > 0);
		}
		/**
		 * whether this <code>AppPolicy</code> has maximum-attempts limit
		 * @return boolean
		 */
		public boolean attempt_limit() {
			return($this.isEnabled() && $this.getMaximumAttempts() > 0);
		}
		/**
		 * whether this <code>AppPolicy</code> has locking-period limit
		 * @return boolean
		 */
		public boolean locking_period() {
			return($this.isEnabled() && $this.getLockingPeriod() > 0);
		}
	}

	/**
	 * <code>checks</code> function checks whether this <code>AppPolicy</code> is obeyed.
	 */
	public class checks {
		/**
		 * checks whether a given times is over maximum attempt times
		 * @param times int
		 * @return boolean
		 */
		public boolean attempts(int times) {
			if (!$this.isEnabled() || $this.getMaximumAttempts() <= 0) return true;
			return($this.getMaximumAttempts() >= times);
		}
		/**
		 * checks whether a given locked time + locking period in minutes is before now.
		 * if true, it means it can auto-unlock now
		 * @param lockTime Date
		 * @return boolean
		 */
		public boolean autounlocking(Date lockTime) {
			if (!$this.isEnabled() || $this.getLockingPeriod() <= 0) return false;
			return(Apps.a.l_timestamp(lockTime).adds.minutes($this.getLockingPeriod()).is.before(Apps.a.l_timestamp.in.GMT().value()));
		}
	}
	
	/**
	 * <code>decides</code> function is to decide some concrete dates, 
	 * or something else.
	 */
	public class decides {
		/**
		 * returns the expiry date based on expiry age.
		 * @return <code>Apps.a.date</code>, 
		 *         or empty <code>Apps.a.date</code> if policy is disabled or 0 expiry age.
		 */
		public Apps.a.date exp_date() {
			if (!$this.isEnabled() || $this.getExpiryAge() <= 0) return Apps.a.date.NULVAL;
			return Apps.a.date.of.today().adds.days($this.getExpiryAge());
		}
		/**
		 * returns the expiry date based on expiry age in a given time zone
		 * @param timezoneId String
		 * @return <code>Apps.a.date</code>, 
		 *         or empty <code>Apps.a.date</code> if policy is disabled or 0 expiry age.
		 */
		public Apps.a.date exp_date(String timezoneId) {
			if (!$this.isEnabled() || $this.getExpiryAge() <= 0) return Apps.a.date.NULVAL;
			return Apps.a.date.of.today(timezoneId).adds.days($this.getExpiryAge());
		}
	}
}
