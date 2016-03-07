package com.dianwoba.forcestaff.message;

import com.dianwoba.forcestaff.endpoint.Endpoint;

public abstract class MessageSourceFactory {
	public abstract MessageSource getMessageSource(Endpoint endpoint);
}
