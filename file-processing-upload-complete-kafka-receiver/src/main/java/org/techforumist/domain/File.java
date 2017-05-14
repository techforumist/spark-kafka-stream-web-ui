package org.techforumist.domain;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Sarath Muraleedharan
 *
 */
public class File implements Serializable {
	private Long id;
	private String filePath;
	private String status;
	@JsonIgnore
	private Date startTime;
	@JsonIgnore
	private Date endTime;
	@JsonIgnore
	private Date uploadCompleteTime;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getUploadCompleteTime() {
		return uploadCompleteTime;
	}

	public void setUploadCompleteTime(Date uploadCompleteTime) {
		this.uploadCompleteTime = uploadCompleteTime;
	}

	private boolean isLast = false;

	public File() {
	}

	public File(Long id, String filePath) {
		super();
		this.id = id;
		this.filePath = filePath;
	}

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String toString() {
		return "File [id=" + id + ", filePath=" + filePath + "]";
	}

}
