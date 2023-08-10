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

import java.security.MessageDigest;

import com.pyrube.one.util.crypto.base64.Base64;

/**
 * This is an one-way password encryption. <br>
 * If you want use it in Oracle database, please follow the steps to
 * load this class and its dependent classes into Oracle database and register its methods as Oracle functions. <br>
 * <pre>
 * <ol>
 *   <li> create crypto.jar:
 *     jar cvf pwd.jar com/pyrube/one/util/crypto/base64/*.class com/pyrube/one/util/crypto/PwdEncryptor.class
 *   </li>
 *   <li> load classes using Oracle command:
 *     loadjava -user usr/pwd@dbserver pwd.jar
 *   </li>
 *   <li> create oracle top-level functions for the methods.
 *     CREATE OR REPLACE FUNCTION ENCRYPT_PASSWORD(A_PWD VARCHAR2) RETURN VARCHAR2
 *     AS LANGUAGE JAVA
 *     NAME 'com.pyrube.one.util.crypto.PwdEncryptor.encrypt(java.lang.String) return java.lang.String';
 *
 *     CREATE OR REPLACE FUNCTION COMPARE_PASSWORD(A_PWD VARCHAR2, A_PWD_E VARCHAR2) RETURN NUMBER
 *     AS LANGUAGE JAVA
 *     NAME 'com.pyrube.one.util.crypto.PwdEncryptor.compare(java.lang.String, java.lang.String) return int';
 *   </li>
 *   <li>
 * --For user checking.
 * create or replace procedure USER_CHECK(userCode VARCHAR2,
                                       userPwd VARCHAR2,
                                       result out INT,
                                       pwdExpiryDate out DATE, 
                                       pwdHistory out VARCHAR2,
                                       numLogonTry out INT,
                                       userStatus out VARCHAR2,
                                       dataActive out VARCHAR2)
   is

       flag INT :=0;
       encPwd VARCHAR2(40) :='';
    begin
        begin
            select USER_PWD,PWD_EXPIRY_DATE,PWD_HISTORY,NUM_LOGON_TRY,USER_STATUS,DATA_ACTIVE into encPwd,pwdExpiryDate,pwdHistory,numLogonTry,userStatus,dataActive from TCDA_USER where USER_CODE=userCode;
        exception
            when no_data_found then
                result :=2;
                return;
        end;
        --if compare_password(userPwd,encPwd)=0 then
        --    result :=0;
        --else
        --    result :=1;
        --end if;
        result:=compare_password(userPwd,encPwd);
    end;
 *   </li>
 * </ol>
 * </pre>
 *
 * this class needs package com.pyrube.one.util.crypto.base64 .
 *
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class PwdEncryptor {
	/**
	 * encrypted password flag. a string starting with this flag is an encrypted password.
	 */
	private static final String FLAG = "{#}";	// for Version 2 using SHA-256 (256 bits)
	
	// for Version 1 using SHA-1 (160 bits) to support old password
	private static final String FLAG_V1 = "{@}";

	/**
	 * constructor
	 */
	private PwdEncryptor() {}

	/**
	 * encrypt password using default encrypt key.
	 * @param clearPwd is the password to be encrypted
	 * @return the encrypted password.
	 */
	public static String encrypt(String clearPwd) {
		return(encrypt(clearPwd, (byte)(System.currentTimeMillis() & 0xFF)));
	}
	/**
	 * To be compatible with db2, we provide the method to encrypt password
	 * @param clearPwd
	 * @param enPwd, the returned encrypted password.
	 * DB2 procedure defination is:
	 * <pre>
	 * db2 -td@ -f ENCRYPTPASSWORD.sql
	 * put pwd.jar into %DB2PATH%/lib/FUNCTION/JAR/
	 *  or put pwd.jar into %SYSTEM_DRIVER%/Documents and Settings/xxxuser/function/JAR/xxxuser folder for windows os.
		CREATE PROCEDURE ENCRYPTPASSWORD (IN CLSPWD VARCHAR(30), OUT ENPWD VARCHAR(60) )
			NOT DETERMINISTIC
			LANGUAGE Java
			EXTERNAL NAME 'com.pyrube.one.util.pwd.PwdEncryptor.encrypt'
			FENCED
			THREADSAFE
			PARAMETER STYLE JAVA
			NO DBINFO
			PROGRAM TYPE SUB
		@
	 * </pre>
	 */
	public static void encrypt(String clearPwd, String[] enPwd) {
		enPwd[0] = encrypt(clearPwd, (byte)(System.currentTimeMillis() & 0xFF));
	}
	
	/**
	 * encrypt password using given encrypt key and iterations.
	 * @param clearPwd is the password to be encrypted
	 * @param encKey the key
	 * @return the encrypted password. maximum length is 44 + 3 = 47
	 */
	private static String encrypt(String clearPwd, byte encKey) {
		try{
			if (clearPwd == null) return("");
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = clearPwd.getBytes("UTF-8");
			// number of iterations in [1, 4]
			int iterations = ((int) encKey & 0x3) + 1;
			for (int i = 0; i < iterations; ++i) {
				md.update(bytes);
				md.update(encKey);
				md.update(bytes);
				bytes = md.digest();
			}
			byte[] res = new byte[bytes.length + 1];
			System.arraycopy(bytes, 0, res, 1, bytes.length);
			res[0] = encKey;
			return(FLAG + Base64.encodeNoWrap(res));
		} catch(Throwable e) {
			return("");
		}
	}
	
	/**
	 * encrypt password using given encrypt key using version 1 (SHA-1).
	 * This is to support verifying old password.
	 * @param clearPwd is the password to be encrypted
	 * @param encKey the key
	 * @return the encrypted password. maximum length is 28 + 3 = 31
	 */
	private static String encryptV1(String clearPwd, byte encKey) {
		try{
			if (clearPwd == null) return("");
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] bytes = clearPwd.getBytes();
			md.update(bytes);
			md.update(encKey);
			md.update(bytes);

			bytes = md.digest();
			byte[] res = new byte[bytes.length + 1];
			System.arraycopy(bytes, 0, res, 1, bytes.length);
			res[0] = encKey;
			return(FLAG_V1 + Base64.encodeNoWrap(res));
		} catch(Throwable e) {
			return("");
		}
	}

	/**
	 * compare two passwords.
	 * @param clearPwd is the password to be checked.
	 * @param encPwd is the encrypted password that the clearPwd is compared to.
	 * @return 0 if they are same, or 1 if they are not same.
	 */
	public static int compare(String clearPwd, String encPwd) {
		try {
			if (encPwd == null) {
				return(1);
			} else if (encPwd.startsWith(FLAG) && encPwd.length() > FLAG.length()) {
				// encPwd is encrypted
				byte[] bytes = Base64.decode(encPwd.substring(FLAG.length()));
				if (bytes == null || bytes.length == 0) return(1);
				return(encPwd.equals(encrypt(clearPwd, bytes[0])) ? 0 : 1);
			} else if (encPwd.startsWith(FLAG_V1) && encPwd.length() > FLAG_V1.length()) {
				// encPwd is encrypted using V1
				byte[] bytes = Base64.decode(encPwd.substring(FLAG_V1.length()));
				if (bytes == null || bytes.length == 0) return(1);
				return(encPwd.equals(encryptV1(clearPwd, bytes[0])) ? 0 : 1);
			} else {
				// encPwd is not encrypted
				return((clearPwd != null && clearPwd.equals(encPwd)) ? 0 : 1);
			}
		} catch (Throwable e) {
			return(1);
		}
	}
	
	/**
	 * To be compatible with db2, we provide the method to compare password for security.
	 * @param clearPwd
	 * @param encPwd
	 * @param retCode the return code for compare password.
	 * <pre>
	 * db2 -td@ -f COMPAREPASSWORD.sql
	 * put pwd.jar into %DB2PATH%/lib/FUNCTION/JAR/
		CREATE PROCEDURE COMPAREPASSWORD(IN CLSPWD VARCHAR(60), IN ENPWD VARCHAR(60), OUT INT)
			NOT DETERMINISTIC
			LANGUAGE Java
			EXTERNAL NAME 'com.pyrube.one.util.crypto.PwdEncryptor.compare'
			FENCED
			THREADSAFE
			PARAMETER STYLE JAVA
			NO DBINFO
			PROGRAM TYPE SUB
	 * @throws Throwable 
	 * Attention: if you are using db2 in linux os,  
	 * you must drop the method above the method (comparePassword(String clearPwd, String encPwd))
	 * and then rebuild the pwd.jar
	 * 
	 * </pre>
	 */
	public static void compare(String clearPwd, String encPwd, int[] retCode) throws Throwable {
		try {
			retCode[0] = compare(clearPwd, encPwd);
		} catch (Throwable e) {
			retCode[0] = 1;
			throw e;
		}
	}
	
	/**
	 * test
	 */
	public static void main(String[] argv) {
		try {
			java.io.PrintStream ps = System.out;
			String pwd = null;
			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
			if (argv.length > 0) {
				pwd = argv[0];
			} else {
				ps.println("please enter your password:");
				pwd = reader.readLine();
			}
			String encPwd = encrypt(pwd);
			ps.println("encrypted password: " + encPwd);

			ps.println("please re-enter your password:");
			String pwd2 = reader.readLine();
			int c = compare(pwd2, encPwd);
			ps.println("password is " + ((c == 0) ? "correct" : "incorrect"));
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}