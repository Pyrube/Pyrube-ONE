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

package com.pyrube.one.util.crypto.base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import com.pyrube.one.util.crypto.CryptoException;

/**
 * Base64 encode/decode. 
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class Base64 {

	/**
	 * encoding mapping, 6-bits-byte to character.
	 *  0 - 25 => 'A' - 'Z'
	 * 26 - 51 => 'a' - 'z'
	 * 52 - 61 => '0' - '9'
	 * 62 => '+'
	 * 63 => '/'
	 * if it is out of this range, mapped to '?'.
	 */
	private static char[] byteToChar = null;

	/**
	 * decoding mapping, character to 6-bits-byte.
	 * 'A' - 'Z' =>  0 - 25
	 * 'a' - 'z' => 26 - 51
	 * '0' - '9' => 52 - 61
	 * '+' => 62
	 * '/' => 63
	 * '=' => 0
	 * if it is out of this range, mapped to -1.
	 */
	private static byte[] charToByte = null;

	/**
	 * encoded string line separator
	 */
	public static final String LINE_SEPARATOR = "\n";

	/** 
	 * default encoded string line length 
	 */
	public static final int BASE64DEFAULTLENGTH = 76;

	/** 
	 * current encoded string line length 
	 */
	static int encLineLength = BASE64DEFAULTLENGTH;

	static {
		byteToChar = new char[256];
		for (int i =  0; i < 26; ++i) byteToChar[i] = (char)('A' + i);
		for (int i = 26; i < 52; ++i) byteToChar[i] = (char)('a' + (i - 26));
		for (int i = 52; i < 62; ++i) byteToChar[i] = (char)('0' + (i - 52));
		byteToChar[62] = '+';
		byteToChar[63] = '/';
		for (int i = 64; i < 256; ++i) byteToChar[i] = '?';

		charToByte = new byte[256];
		for (int i = 0; i < 256; ++i) charToByte[i] = -1;
		for (char c = 'A'; c <= 'Z'; ++c) charToByte[c] = (byte)(c - 'A');
		for (char c = 'a'; c <= 'z'; ++c) charToByte[c] = (byte)(c - 'a' + 26);
		for (char c = '0'; c <= '9'; ++c) charToByte[c] = (byte)(c - '0' + 52);
		charToByte[(int)'+'] = (byte)62;
		charToByte[(int)'/'] = (byte)63;
		charToByte[(int)'='] = (byte)0;
	}

	/**
	 * set encoded string line length
	 */
	public static void setLineLength(int length) {
		encLineLength = length;
	}

	/**
	 * get encoded string line length
	 */
	public static int getLineLength() {
		return(encLineLength);
	}

	/**
	 * decode a base64-encoded byte array
	 *
	 * @param base64 is the encoded base64 characters
	 * @return decoded byte array
	 * @exception CryptoException
	 */
	public static byte[] decode(byte[] base64) throws CryptoException {
		try {
			return(decode(new String(base64, "UTF-8")));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * decode a Base64-encoded string to a byte array
	 *
	 * @param base64 is encoded string
	 * @return decoded byte array
	 * @exception Base64Exception
	 */
	public static byte[] decode(String base64) throws CryptoException {
		try {
			// strip whitespace from anywhere in the string
			StringTokenizer tok = new StringTokenizer(base64, " \n\r\t", false);
			StringBuffer buf = new StringBuffer(base64.length());
			while (tok.hasMoreElements()) buf.append(tok.nextToken());
			base64 = buf.toString();

			int pad = 0;
			for (int i = base64.length() - 1; (i > 0) && (base64.charAt(i) == '='); --i) {
				++pad;
			}

			int length = (base64.length() / 4) * 3 - pad;
			byte[] raw = new byte[length];

			for (int i = 0, rawIndex = 0; i < base64.length(); i += 4, rawIndex += 3) {
				int block = (getValue(base64.charAt(i)) << 18) | 
							(getValue(base64.charAt(i + 1)) << 12) | 
							(getValue(base64.charAt(i + 2)) << 6) | 
							(getValue(base64.charAt(i + 3)));

				for (int j = 2; j >= 0; --j) {
					if ((rawIndex + j) < raw.length) raw[rawIndex + j] = (byte) (block & 0xff);
					block >>= 8;
				}
			}

			return raw;
		} catch (IndexOutOfBoundsException e) {
			throw new CryptoException("global.error.bit-length-illegal" + e.toString());
		}
	}

	/**
	 * encode a byte array in Base64 format without line wrap
	 *
	 * @param rawData is the data array to be encoded
	 */
	public static String encodeNoWrap(byte[] rawData) {
		return(encode(rawData, 0));
	}
	
	/**
	 * encode a byte array in Base64 format and return an optionally wrapped line
	 *
	 * @param rawData is the data array to be encoded
	 * @param outLineLen is the length of output lines. it is rounded by 4.
	 *     No wrapping if it is less than 4 (output just one line). so if don't want lines, then set it to 0.
	 * @return the encoded data
	 */
	public static String encode(byte[] rawData, int outLineLen) {
		// length of encoded string
		int outLen = ((rawData.length + 2) / 3) * 4;

		// adjust length for line terminators
		if (outLineLen >= 4) {
			outLineLen &= 0x0fffffffc;	// round 4
			outLen += (LINE_SEPARATOR.length() * (outLen / outLineLen));	// each line has separator 
		} else {	// disable line wrapping
			outLineLen = Integer.MAX_VALUE;
		}

		StringBuffer encoded = new StringBuffer(outLen);
		int fullBlockLen = rawData.length - (rawData.length % 3);

		// encode full blocks
		for (int i = 0, outPos = 0; i < fullBlockLen; i += 3) {
			encoded.append(encodeBlock(rawData[i], rawData[i + 1], rawData[i + 2]));
			outPos += 4;
			if (outPos >= outLineLen) {
				encoded.append(LINE_SEPARATOR);
				outPos = 0;
			}
		}

		// encode one more block if it exists (it is not full block)
		if (fullBlockLen < rawData.length) {
			encoded.append(encodeBlock(rawData, fullBlockLen));
		}

		return(encoded.toString());
	}

	/**
	 * Encode a byte array and fold lines at the standard 76th character.
	 *
	 * @param rawData is the data array to be encoded
	 * @return the encoded data
	 */
	public static String encode(byte[] rawData) {
		return(encode(rawData, getLineLength()));
	}

	/**
	 * decode the lines from the reader 
	 * @param reader is the reader
	 */
	public static byte[] decode(BufferedReader reader)
		   throws IOException, CryptoException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String line = null;
		while ((line = reader.readLine()) != null) {
			byte[] bytes = decode(line);
			baos.write(bytes);
		}
		return(baos.toByteArray());
	}

	/**
	 * encode a block
	 * @param rawData is the data array to be encoded
	 * @param offset is the start position of the array
	 * @return encoded base64-char array (length is 4)
	*/
	protected static char[] encodeBlock(byte[] rawData, int offset) {
		int slack = (rawData.length - 1) - offset;	// could be 0, 1, 2, 3, 4, ...
		// get block data
		byte b1 = 0, b2 = 0, b3 = 0;
		if (offset < rawData.length) b1 = rawData[offset];
		if ((++offset) < rawData.length) b2 = rawData[offset];
		if ((++offset) < rawData.length) b3 = rawData[offset];
		
		char[] base64 = encodeBlock(b1, b2, b3);

		if (slack < 1) base64[2] = '=';
		if (slack < 2) base64[3] = '=';

		return(base64);
	}

	/**
	 * encode a full Block (3 any bytes to 4 base64-char bytes)
	 *
	 * @param rawData1 is the first byte
	 * @param rawData2 is the second byte
	 * @param rawData3 is the third byte
	 * @return encoded base64-char array (length is 4)
	 */
	protected static char[] encodeBlock(byte rawData1, byte rawData2, byte rawData3) {
		// block data
		int blockData = ((rawData1 & 0xff) << 16) | ((rawData2 & 0xff) << 8) | (rawData3 & 0xff);
		char[] base64 = new char[4];
		for (int i = 3; i >= 0; --i) {
			int sixBit = blockData & 0x3f;
			base64[i] = getChar(sixBit);
			blockData >>= 6;
		}
		return(base64);
	}

	/**
	 * get a base64 character for a six-bit-byte
	 *
	 * @param sixBit is the six-bit-byte
	 * @return the base64 character
	 */
	protected static char getChar(int sixBit) {
		return(byteToChar[sixBit]);
	}

	/**
	 * get six-bit-byte for a base64 character
	 *
	 * @param base64Char is the base64 character
	 * @return integer of the six-bit-byte
	 */
	protected static int getValue(char base64Char) {
		return(charToByte[base64Char]);
	}
}
