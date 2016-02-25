package com.dianwoba.forcestaff.message;

import java.io.Serializable;

public class Message implements Serializable {
	private static final long serialVersionUID = 6545363759341191530L;

	private String topic;
	private String appKey;
	private String content;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}