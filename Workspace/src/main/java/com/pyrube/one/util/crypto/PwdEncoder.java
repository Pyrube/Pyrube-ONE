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

package com.pyrube.one.util.crypto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import com.pyrube.one.util.crypto.base64.Base64;

/**
 * Password utility to encode password in order to put 
 * the password in config files. The encoded password can be decoded.
 * The password must be printable ASCII characters. <br>
 *
 * And support Triple-DES (Data Encryption Standard) algorithm
 * JCE1_2_2 is needed. JDK1.4.2 and later JDK should include this JCE version. <br>
 * 
 * Random prefix is added so that the encoded 
 * password is different at different time even for same password. <br>
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */ 

public class PwdEncoder {
    /**
     * flag
     */
    private static final String FLAG = "{xor}";

	/**
	 * whether output debug info
	 */
	private static boolean debugMode = false;
	
	/**
	 * key
	 */
	private static final byte[] DESKEY = {
		(byte)0x3D, (byte)0x9D, (byte)0x94, (byte)0x1F, 
		(byte)0xC1, (byte)0x31, (byte)0xFD, (byte)0xAB, 
		(byte)0xC2, (byte)0x8F, (byte)0xF2, (byte)0x89, 
		(byte)0xB6, (byte)0xA1, (byte)0xC2, (byte)0xFB, 
		(byte)0x75, (byte)0x1F, (byte)0x26, (byte)0xEF, 
		(byte)0x32, (byte)0x19, (byte)0xDF, (byte)0xAE };
	  	
	/**
	 * random
	 */
	private static Random rnd = new Random(System.currentTimeMillis());

	/**
	 * constructor
	 */
	private PwdEncoder() {}
	
	/**
	 * encode clear password
	 * @param clearPwd is the clear password
	 */
	public static String encode(char[] clearPwd) {
		if (clearPwd == null) clearPwd = new char[0];
		return(FLAG + Base64.encode(encrypt(clearPwd)));
	}

	/**
	 * decode encoded password.
	 * @param encPwd is the encoded password
	 * @return decoded password. if encPwd is not encoded, then return it self.
	 */
	public static char[] decode(String encPwd) {
		char[] pwd = new char[0];
		try {
			if (encPwd == null || encPwd.length() == 0) return(pwd);
			if (isEncoded(encPwd)) {	// encoded password
				pwd = decrypt(Base64.decode(encPwd.substring(FLAG.length())));
			} else {	// not encoded password
				pwd = encPwd.toCharArray();
			}
			return(pwd);
		} catch (Exception e) {
			if (debugMode) e.printStackTrace();
			return(pwd);
		}
	}

	/**
	 * check if the password is encoded or not
	 */
	public static boolean isEncoded(String pwd) {
		if (pwd != null && pwd.startsWith(FLAG) && pwd.length() > FLAG.length()) return(true);
		return(false);
	}

	/**
	 * encrypt password. It adds random prefix chars so that encrypted 
	 * password is different even for same password.
	 * 
	 * @param clearPwd the clear password in char array. 
	 *        it must not be null. it could be empty array (length is 0).
	 * @return encrypted password in byte array. its length is longer 
	 *         than clearPwd. It returns empty array if error occurs.
	 */
	private static byte[] encrypt(char[] clearPwd) {
		// get number of prefix chars (1 to 9)
		int numPrefix = rnd.nextInt(9) + 1;
		byte[] enc = new byte[numPrefix + 1 + clearPwd.length];
		enc[0] = (byte) numPrefix;
		for (int i = 1; i <= numPrefix; ++i) {
			// get random number 1 to 254
			enc[i] = (byte)(rnd.nextInt(254) + 1);
		}
		for (int i = 0, k = 1; i < clearPwd.length; ++i) {
			enc[numPrefix + 1 + i] = (byte) (clearPwd[i] ^ (int)enc[k]);
			++k;
			if (k > numPrefix) k = 1;
		}
		try{
			Cipher cipher = createCipher(Cipher.ENCRYPT_MODE);
		  	byte[] cipherText = cipher.doFinal(enc);
		 	return(cipherText);
		} catch (Exception e) {
			if (debugMode) e.printStackTrace();
			return (new byte[0]);
		}
	}

	/**
	 * decrypt password
	 * 
	 * @param encPwd the encrypted password in byte array
	 * @return decrypted password in char array or empty array if error occurs
	 */
	private static char[] decrypt(byte[] encPwd) {
		char[] dec = null;
		try{	
			Cipher cipher = createCipher(Cipher.DECRYPT_MODE);
			byte[] clearPwd = cipher.doFinal(encPwd);

			// get the number of random prefix chars and remove them
			int sPos = 0;
			if (clearPwd[0] > 0 && clearPwd[0] < 10) {
				sPos = clearPwd[0] + 1;
			}
			
			// remove the 0s at the end
			int ePos = clearPwd.length - 1;
			while (ePos >= 0 && clearPwd[ePos] == 0) {
				--ePos;
			}
			
			if (sPos > ePos) {
				// empty password
				dec = new char[0];
			} else {
				dec = new char[ePos - sPos + 1];
				if (sPos > 0) {
					for (int i = sPos, k = 1; i <= ePos; ++i) {
						dec[i - sPos] = (char)(clearPwd[i] ^ clearPwd[k]);
						++k;
						if (k >= sPos) k = 1;
					}
				} else {
					for (int i = sPos; i <= ePos; ++i) {
						dec[i - sPos] = (char)(clearPwd[i]);
					}
				}
			}
			
			return(dec);
		} catch (Exception e) {
			if (debugMode) e.printStackTrace();
			return(new char[0]);
		}
	}
	
	/**
	 * create cipher
	 * @param mode Cipher.DECRYPT_MODE or Cipher.ENCRYPT_MODE
	 * @return
	 * @throws Exception
	 */
	private static Cipher createCipher(int mode) throws Exception {
		Cipher cipher = Cipher.getInstance("DESede");
		DESedeKeySpec keySpec = new DESedeKeySpec(DESKEY);
		SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("DESede");
		SecretKey sKey = secretKeyFactory.generateSecret(keySpec);
		cipher.init(mode, sKey);
		return(cipher);
	}
	
	/**
	 * set debug mode
	 * @param debug true or false
	 */
	public static void setDebugMode(boolean debug) {
		debugMode = debug;
	}
	/**
	 * main entry. utility to encode password. <br>
	 * usage: java -classpath ptiutils.jar com.pyrube.one.util.pwd.PwdEncoder [-debug] [password]
	 */
	public static void main(String[] argv) {
		System.out.println("password encoding utility.");
		try {
			String pwd = null;
			if (argv.length > 0) {
				if (argv[0].equalsIgnoreCase("-debug")) {
					setDebugMode(true);
				} else {
					pwd = argv[0];
				}
				if (pwd == null && argv.length > 1) {
					pwd = argv[1];
				}
			}
			if (pwd == null) {
				System.out.println("please enter password:");
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				pwd = reader.readLine();
			} else {
				if (debugMode) System.out.println("password: " + pwd);
			}
			String encPwd = encode(pwd.toCharArray());
			System.out.println("encoded password:");
			System.out.println(encPwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
