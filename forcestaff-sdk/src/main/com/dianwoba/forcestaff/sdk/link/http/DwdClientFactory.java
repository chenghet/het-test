package com.dianwoba.forcestaff.sdk.link.http;

import com.dianwoba.forcestaff.sdk.Configuration;

/**
 * Created by het on 2016/4/12.
 */
public class DwdClientFactory {

    private static DwdClientFactory factory;

    private Configuration config;
    private DwdClient client;

    private DwdClientFactory(Configuration config) {
        this.config = config;
    }

    public static DwdClientFactory getDwdClientFactory(Configuration config) {
        return new DwdClientFactory(config);
    }

    public synchronized DwdClient getDwdClient() {
        if (client == null) {
            client = new DwdClient(config);
        }
        return client;
    }
}
