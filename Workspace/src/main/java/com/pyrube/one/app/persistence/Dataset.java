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

package com.pyrube.one.app.persistence;

import java.io.Serializable;
import java.util.List;

import com.pyrube.one.app.persistence.Data;

/**
 * the <code>Dataset</code> contains an array of the <code>Data</code> results.
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class Dataset<D extends Data<?>> implements Serializable {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 6122565406394494144L;

	/**
	 * an array of result
	 */
	private List<D> results;

	/**
	 * constructor
	 */
	public Dataset() {}

	/**
	 * @return the results
	 */
	public List<D> getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(List<D> results) {
		this.results = results;
	}

}
