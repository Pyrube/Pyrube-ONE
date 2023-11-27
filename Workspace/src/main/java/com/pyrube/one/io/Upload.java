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

package com.pyrube.one.io;

import java.io.Serializable;

/**
 * Represents a result for uploading. 
 * 
 * @author Aranjuez
 * @version Dec 01, 2009
 * @since Pyrube-ONE 1.0
 */
public class Upload implements Serializable {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 4282740622981602855L;
	/**
	 * uploading status
	 */
	private int status;
	/**
	 * i18n message code
	 */
	private String message;
	/**
	 * file ID
	 */
	private String id;
	/**
	 * file pathname
	 */
	private String pathname;
	/**
	 * file mime
	 */
	private String mime;
	/**
	 * constructor
	 * @param status
	 * @param message
	 */
	private Upload(int status, String message) {
		this.status = status;
		this.message = message;
	}
	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the pathname
	 */
	public String getPathname() {
		return pathname;
	}
	/**
	 * @param pathname the pathname to set
	 */
	public void setPathname(String pathname) {
		this.pathname = pathname;
	}
	/**
	 * @return the mime
	 */
	public String getMime() {
		return mime;
	}
	/**
	 * @param mime the mime to set
	 */
	public void setMime(String mime) {
		this.mime = mime;
	}
	/**
	 * returns this <code>Upload</code> with id
	 * @param id
	 * @return Upload
	 */
	public Upload id(String id) {
		this.setId(id);
		return(this);
	}
	/**
	 * returns this <code>Upload</code> with pathname
	 * @param pathname
	 * @return Upload
	 */
	public Upload pathname(String pathname) {
		this.setPathname(pathname);
		return(this);
	}
	/**
	 * returns this <code>Upload</code> with pathname
	 * @param mime
	 * @return Upload
	 */
	public Upload mime(String mime) {
		this.setMime(mime);
		return(this);
	}
	/**
	 * returns a success upload result
	 * @return Upload
	 */
	public static Upload success() {
		return(new Upload(1,  "message.success.upload-finished"));
	}
	/**
	 * returns an error upload result
	 * @return Upload
	 */
	public static Upload error() {
		return(new Upload(0,  "message.error.upload-failed"));
	}
	/**
	 * returns a empty upload result
	 * @return Upload
	 */
	public static Upload empty() {
		return(new Upload(-1, "message.error.file-empty"));
	}
	/**
	 * returns an oversize upload result
	 * @return Upload
	 */
	public static Upload oversize() {
		return(new Upload(-2, "message.error.upload-oversize"));
	}
	/**
	 * returns an invalid mime upload result
	 * @return Upload
	 */
	public static Upload invalid_mime() {
		return(new Upload(-3, "message.error.mime-invalid"));
	}
}