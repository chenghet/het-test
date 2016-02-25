package com.dianwoba.forcestaff.endpoint;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Message implements Serializable {
	private static final long serialVersionUID = 6545363759341191530L;
	
	private String topic;
	private String pubAppKey;
	private Date pubTime;
	private Date outgoingTime;
	private String content;
	private Map<?, ?> contentMap;

	public String getTopic() {
		return this.topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getPubAppKey() {
		return this.pubAppKey;
	}

	public void setPubAppKey(String pubAppKey) {
		this.pubAppKey = pubAppKey;
	}

	public Date getPubTime() {
		return this.pubTime;
	}

	public void setPubTime(Date pubTime) {
		this.pubTime = pubTime;
	}

	public Date getOutgoingTime() {
		return this.outgoingTime;
	}

	public void setOutgoingTime(Date outgoingTime) {
		this.outgoingTime = outgoingTime;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<?, ?> getContentMap() {
		return this.contentMap;
	}

	public void setContentMap(Map<?, ?> contentMap) {
		this.contentMap = contentMap;
	}
}