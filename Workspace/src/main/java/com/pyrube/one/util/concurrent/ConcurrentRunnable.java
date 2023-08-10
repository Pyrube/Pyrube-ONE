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

/**
 * the <code>ConcurrentRunnable</code> interface provides one method for concurrent use.
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public interface ConcurrentRunnable extends Runnable, Serializable {

	/**
	 * return the result after completion of running. This is called internally. 
	 * This must be called after the run() finishes.
	 * The implementation of this call should just return an object defined 
	 * in its runnable class. This result object should be a user-defined class 
	 * containing result data.
	 * @return
	 */
	public Serializable done();
}
