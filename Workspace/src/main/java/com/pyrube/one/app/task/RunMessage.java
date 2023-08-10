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

import com.pyrube.one.app.AppMessage;

/**
 * job running message with total items, finished items and error items
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class RunMessage extends AppMessage {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1078499261598311239L;
	/**
	 * total items to be processed
	 */
	private long itemsTotal = 0;
	/**
	 * number of items processed successfully
	 */
	private long itemsFinished = 0;
	/**
	 * number of items processed but with error
	 */
	private long itemsError = 0;

	/**
	 * default constructor
	 */
	public RunMessage() {
		this("message.info.job-running");
	}

	/**
	 * constructor
	 * @param code
	 */
	public RunMessage(String code) {
		this(code, 0, 0, 0);
	}

	/**
	 * constructor
	 * @param code
	 * @param params
	 */
	public RunMessage(String code, Object[] params) {
		this(code, params, 0, 0, 0);
	}

	/**
	 * constructor
	 * @param code
	 * @param itemsTotal
	 * @param itemsFinished
	 * @param itemsError
	 */
	public RunMessage(String code, long itemsTotal, long itemsFinished, long itemsError) {
		this(code, null, itemsTotal, itemsFinished, itemsError);
	}

	/**
	 * constructor
	 * @param code
	 * @param params
	 * @param itemsTotal
	 * @param itemsFinished
	 * @param itemsError
	 */
	public RunMessage(String code, Object[] params, long itemsTotal, long itemsFinished, long itemsError) {
		super(code);

		this.setLevel((this.itemsError > 0) ? LEVEL_ERROR : LEVEL_INFO);
		this.itemsTotal = itemsTotal;
		this.itemsFinished = itemsFinished;
		this.itemsError = itemsError;
		
		Object[] _params = (params != null) ? new Object[params.length + 3] : params;
		if (params != null) {
			System.arraycopy(params, 0, _params, 0, params.length);
			_params[params.length + 0] = itemsTotal;
			_params[params.length + 1] = itemsFinished;
			_params[params.length + 2] = itemsError;
		}
		this.setParams(params);
	}
	
	/**
	 * this message is with errors
	 * @return
	 */
	public RunMessage withErrors() {
		this.setLevel(LEVEL_ERROR);
		return this;
	}
	
	/**
	 * @return the itemsTotal
	 */
	public long getItemsTotal() {
		return itemsTotal;
	}

	/**
	 * @param itemsTotal the itemsTotal to set
	 */
	public void setItemsTotal(long itemsTotal) {
		this.itemsTotal = itemsTotal;
	}

	/**
	 * @return the itemsFinished
	 */
	public long getItemsFinished() {
		return itemsFinished;
	}

	/**
	 * @param itemsFinished the itemsFinished to set
	 */
	public void setItemsFinished(long itemsFinished) {
		this.itemsFinished = itemsFinished;
	}

	/**
	 * @return the itemsError
	 */
	public long getItemsError() {
		return itemsError;
	}

	/**
	 * @param itemsError the itemsError to set
	 */
	public void setItemsError(long itemsError) {
		this.itemsError = itemsError;
	}
}
