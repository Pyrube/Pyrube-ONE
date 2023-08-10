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

package com.pyrube.one.util.math;

import java.math.BigDecimal;

import com.pyrube.one.app.Apps;

/**
 * the <code>Arith</code> contains various methods for manipulating numbers.
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class Arith {

	/**
	 * sums the augend plus(+) all addends
	 * @param augend BigDecimal
	 * @param addends BigDecimal[]
	 * @return BigDecimal
	 */
	public static BigDecimal sum(BigDecimal augend, BigDecimal...addends) {
		if (addends == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		if (addends.length < 1) {
			throw new IllegalArgumentException("Wrong number of arguments (given: " + addends.length + ", excepted at least: 1).");
		}
		if (augend == null) augend = BigDecimal.ZERO;
		BigDecimal sum = BigDecimal.ZERO.add(augend);
		for (BigDecimal addend : addends) {
			if (addend == null) addend = BigDecimal.ZERO;
			sum = sum.add(addend);
		}
		return(sum);
	}

	/**
	 * differes the minuend minus(-) the subtrahend
	 * @param minuend BigDecimal
	 * @param subtrahend BigDecimal
	 * @return BigDecimal
	 */
	public static BigDecimal differe(BigDecimal minuend, BigDecimal subtrahend) {
		if (minuend    == null) minuend    = BigDecimal.ZERO;
		if (subtrahend == null) subtrahend = BigDecimal.ZERO;
		return(minuend.subtract(subtrahend));
	}

	/**
	 * returns the product of the multiplicand times(*) all multipliers
	 * @param multiplicand BigDecimal
	 * @param multipliers BigDecimal[]
	 * @return BigDecimal
	 */
	public static BigDecimal product(BigDecimal multiplicand, BigDecimal...multipliers) {
		if (multipliers == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		if (multipliers.length < 1) {
			throw new IllegalArgumentException("Wrong number of arguments (given: " + multipliers.length + ", excepted at least: 1).");
		}
		if (multiplicand == null) multiplicand = BigDecimal.ZERO;
		BigDecimal product = BigDecimal.ONE.multiply(multiplicand);
		for (BigDecimal multiplier : multipliers) {
			if (multiplier == null) multiplier = BigDecimal.ZERO;
			product = product.multiply(multiplier);
		}
		return(product);
	}

	/**
	 * returns the quotient of the dividend over(/) the divisor
	 * @param dividend BigDecimal
	 * @param divisor BigDecimal
	 * @return BigDecimal
	 */
	public static BigDecimal quotient(BigDecimal dividend, BigDecimal divisor) {
		return(quotient(dividend, divisor, 32));
	}

	/**
	 * returns the quotient of the dividend over(/) the divisor
	 * @param dividend BigDecimal
	 * @param divisor BigDecimal
	 * @param scale int
	 * @return BigDecimal
	 */
	public static BigDecimal quotient(BigDecimal dividend, BigDecimal divisor, int scale) {
		if (dividend == null) dividend = BigDecimal.ZERO;
		if (divisor  == null) divisor  = BigDecimal.ZERO;
		return(dividend.divide(divisor, scale, BigDecimal.ROUND_HALF_UP));
	}

	/**
	 * compares the two numbers
	 * @param val1 Number
	 * @param val2 Number
	 * @return -1, 0, or 1 as val1 Number is numerically
	 *         less than, equal to, or greater than val2
	 */
	public static int compare(Number val1, Number val2) {
		if (val1 == null) val1 = BigDecimal.ZERO;
		if (val2 == null) val2 = BigDecimal.ZERO;
		if (!(val1 instanceof BigDecimal)) val1 = new BigDecimal(val1.toString());
		if (!(val2 instanceof BigDecimal)) val2 = new BigDecimal(val2.toString());
		return(((BigDecimal) val1).compareTo((BigDecimal) val2));
	}

	/**
	 * does rounding with the default scale and rounding mode (ROUND_HALF_UP)
	 * @param val BigDecimal
	 * @return BigDecimal
	 */
	public static BigDecimal round(BigDecimal val) {
		return(round(val, 32));
	}

	/**
	 * does rounding with a given scale and the default rounding mode (ROUND_HALF_UP)
	 * @param val BigDecimal
	 * @return BigDecimal
	 */
	public static BigDecimal round(BigDecimal val, int scale) {
		return(round(val, scale, BigDecimal.ROUND_HALF_UP));
	}

	/**
	 * does rounding with a given scale and rounding mode
	 * @param val BigDecimal
	 * @return BigDecimal
	 */
	public static BigDecimal round(BigDecimal val, int scale, int roundingMode) {
		if (val == null) val = BigDecimal.ZERO;
		return(val.divide(BigDecimal.ONE, scale, roundingMode));
	}
	
	public static void main(String[] args) {
		System.out.println(Arith.sum(new BigDecimal(1), new BigDecimal(2)));
		System.out.println(Arith.sum(new BigDecimal(1), new BigDecimal(2), new BigDecimal(3.0)));
		System.out.println(Apps.a.decimal.ZERO.is.lt(123.45));
	}
}
