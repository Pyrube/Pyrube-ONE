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
/**
 * the application constants
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public interface AppConstants {
	
	/**
	 * constants for property name configured in AppConfig
	 */
	public static final String APPCONF_FIRST_FISCALMONTH = "FIRST_FISCALMONTH";
	public static final String APPCONF_SETUP_DUALCONTROL = "SETUP_DUALCONTROL";
	
	/**
	 * general constants
	 */
	public static final String YES      = "Y";
	public static final String NO       = "N";
	
	/**
	 * constants for data setup status
	 */
	public static final String SETUP_STAT_CREATED_PENDING  = "C"; // created, but pending for verification
	public static final String SETUP_STAT_CREATED_REJECTED = "J"; // created, but rejected by verifier
	public static final String SETUP_STAT_UPDATED_PENDING  = "U"; // updated, but pending for verification
	public static final String SETUP_STAT_UPDATED_REJECTED = "T"; // updated, but rejected by verifier
	public static final String SETUP_STAT_DELETED_PENDING  = "D"; // deleted, but pending for verification
	public static final String SETUP_STAT_DELETED_REJECTED = "N"; // deleted, but rejected by verifier
	public static final String SETUP_STAT_VERIFIED = "V";
	public static final String DATA_STAT_ADDED     = "A";
	public static final String DATA_STAT_MODIFIED  = "M";
	public static final String DATA_STAT_REMOVED   = "R";
	/**
	 * constants for data setup event
	 */
	public static final String SETUP_EVENT_CREATED_REJECTED = "SETUP_" + SETUP_STAT_CREATED_REJECTED;
	public static final String SETUP_EVENT_UPDATED_REJECTED = "SETUP_" + SETUP_STAT_UPDATED_REJECTED;
	public static final String SETUP_EVENT_DELETED_REJECTED = "SETUP_" + SETUP_STAT_DELETED_REJECTED;
}
