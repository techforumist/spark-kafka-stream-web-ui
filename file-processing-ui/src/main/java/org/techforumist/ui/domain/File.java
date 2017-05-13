package org.techforumist.ui.domain;

import java.io.Serializable;

public class File implements Serializable {
	private Long id;
	private String filePath;

	public File() {
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
