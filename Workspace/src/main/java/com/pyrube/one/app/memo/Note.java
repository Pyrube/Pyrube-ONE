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

package com.pyrube.one.app.memo;

import java.util.Date;

import com.pyrube.one.app.i18n.format.annotations.Converting;
import com.pyrube.one.app.i18n.format.annotations.FormatName;
import com.pyrube.one.app.persistence.Data;

/**
 * <code>Note</code> data.
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class Note extends Data<String> {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 9205099641907174890L;
	/**
	 * this <code>Note</code>
	 */
	private String noteId;
	private String dataType;
	private String dataId;
	private String dataStatus;
	private String eventCode;
	private String noteFrom;
	private String noteTo;
	private String content;
	private String noteStatus;
	@Converting(format = FormatName.LONGTIMESTAMPZ, local = true)
	private Date noteTime;
	@Converting(format = FormatName.LONGTIMESTAMPZ, local = true)
	private Date updateTime;

	/**
	 * constructor
	 */
	public Note() { }
	/**
	 * constructor
	 * @param content String
	 */
	public Note(String content) {
		this.content = content;
	}
	/**
	 * @return the noteId
	 */
	public String getNoteId() {
		return noteId;
	}
	/**
	 * @param noteId the noteId to set
	 */
	public void setNoteId(String noteId) {
		this.noteId = noteId;
	}
	/**
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}
	/**
	 * @param dataType the dataType to set
	 */
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	/**
	 * @return the dataId
	 */
	public String getDataId() {
		return dataId;
	}
	/**
	 * @param dataId the dataId to set
	 */
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	/**
	 * @return the dataStatus
	 */
	public String getDataStatus() {
		return dataStatus;
	}
	/**
	 * @param dataStatus the dataStatus to set
	 */
	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}
	/**
	 * @return the eventCode
	 */
	public String getEventCode() {
		return eventCode;
	}
	/**
	 * @param eventCode the eventCode to set
	 */
	public void setEventCode(String eventCode) {
		this.eventCode = eventCode;
	}
	/**
	 * @return the noteFrom
	 */
	public String getNoteFrom() {
		return noteFrom;
	}
	/**
	 * @param noteFrom the noteFrom to set
	 */
	public void setNoteFrom(String noteFrom) {
		this.noteFrom = noteFrom;
	}
	/**
	 * @return the noteTo
	 */
	public String getNoteTo() {
		return noteTo;
	}
	/**
	 * @param noteTo the noteTo to set
	 */
	public void setNoteTo(String noteTo) {
		this.noteTo = noteTo;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the noteStatus
	 */
	public String getNoteStatus() {
		return noteStatus;
	}
	/**
	 * @param noteStatus the noteStatus to set
	 */
	public void setNoteStatus(String noteStatus) {
		this.noteStatus = noteStatus;
	}
	/**
	 * @return the noteTime
	 */
	public Date getNoteTime() {
		return noteTime;
	}
	/**
	 * @param noteTime the noteTime to set
	 */
	public void setNoteTime(Date noteTime) {
		this.noteTime = noteTime;
	}
	/**
	 * @return the updateTime
	 */
	public Date getUpdateTime() {
		return updateTime;
	}
	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
