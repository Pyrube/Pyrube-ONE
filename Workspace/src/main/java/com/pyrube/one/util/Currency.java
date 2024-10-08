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
/**
 * Represents a currency. Currencies are identified by their ISO 4217 currency
 * codes. Visit the <a href="http://www.iso.org/iso/home/standards/currency_codes.htm">
 * ISO web site</a> for more information.
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class Currency {
	/**
	 * ISO 4217 currency code for this currency.
	 */
	private String code;
	/**
	 * The precision of this currency
	 */
	private Integer precision;
	/**
	 * The scale of this currency
	 * @deprecated use precision instead
	 */
	private Integer scale;
	/**
	 * The calculation basis
	 */
	private String calcBasis;
	
	/**
	 * Constructor
	 * @param code
	 */
	public Currency(String code) {
		this(code, Integer.MIN_VALUE, null);
	}
	
	/**
	 * Constructor
	 * @param code
	 * @param precision
	 */
	public Currency(String code, Integer precision) {
		this(code, precision, null);
	}
	
	/**
	 * Constructor
	 * @param code
	 * @param precision
	 * @param calcBasis
	 */
	public Currency(String code, Integer precision, String calcBasis) {
		this.code = code;
		this.precision = precision;
		this.scale = precision;
		this.calcBasis = calcBasis;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the precision
	 */
	public Integer getPrecision() {
		return precision;
	}

	/**
	 * @param precision the precision to set
	 */
	public void setPrecision(Integer precision) {
		this.precision = precision;
	}

	/**
	 * @return the scale
	 * @deprecated
	 */
	public Integer getScale() {
		return scale;
	}

	/**
	 * @param scale the scale to set
	 * @deprecated
	 */
	public void setScale(Integer scale) {
		this.scale = scale;
	}

	/**
	 * @return the calcBasis
	 */
	public String getCalcBasis() {
		return calcBasis;
	}

	/**
	 * @param calcBasis the calcBasis to set
	 */
	public void setCalcBasis(String calcBasis) {
		this.calcBasis = calcBasis;
	}
	
	@Override
	public String toString() {
		return ("Currency: {" +
				"code: " + this.code + "," +
				"scale: " + this.scale + "," +
				"calcBasis: " + this.calcBasis +
				"}");
	}
}
