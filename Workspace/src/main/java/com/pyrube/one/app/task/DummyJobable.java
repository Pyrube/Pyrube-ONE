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

import com.pyrube.one.app.logging.Logger;

/**
 * the <code>DummyJobable</code>.
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class DummyJobable extends Jobable {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 4098666285337130898L;

	/**
	 * logger
	 */
	private static Logger logger = Logger.getInstance(DummyJobable.class.getName());

	/**
	 * constructor
	 */
	public DummyJobable() { }

	/**
	 * constructor
	 * @param job
	 */
	public DummyJobable(Job job) {
		super(job);
	}

	@Override
	public RunResult run() {
		if (logger.isDebugEnabled()) logger.debug("Run the Dummy job.");
		return null;
	}

}
