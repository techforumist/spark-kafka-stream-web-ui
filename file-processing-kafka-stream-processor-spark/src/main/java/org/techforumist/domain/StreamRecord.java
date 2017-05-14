package org.techforumist.domain;

/**
 * @author Sarath Muraleedharan
 *
 */
public class StreamRecord {
	private Long id;
	private String data;

	public StreamRecord() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "StreamRecord [id=" + id + ", data=" + data + "]";
	}

}
