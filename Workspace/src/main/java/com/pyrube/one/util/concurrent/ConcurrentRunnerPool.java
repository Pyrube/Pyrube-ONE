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

package com.pyrube.one.util.concurrent;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Future;

import com.pyrube.one.app.AppException;

/**
 * the <code>ConcurrentRunnerPool</code> interface for managing the underlying threads.
 * the implementation class must be thread safe and provide default constructor. one instance 
 * will be used in one runner pool. 
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public interface ConcurrentRunnerPool extends Serializable {

	/**
	 * initialization
	 * @throws AppException
	 */
	public void init(Map<?, ?> params) throws AppException;
	
	/**
	 * start the runner and return a future result to be checked later
	 * @param runnable
	 * @param runnerName
	 * @return Future 
	 * @exception AppException
	 */
	public Future<?> start(ConcurrentRunnable runnable, String runnerName) throws AppException;

	/**
	 * start the runner and forget it (don't check the result and status after)
	 * @param runnable
	 * @param runnerName
	 * @exception AppException
	 */
	public void startIgnoreStat(ConcurrentRunnable runnable, String runnerName) throws AppException;
}
