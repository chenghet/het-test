package com.dianwoba.forcestaff.sdk.util;

import com.dianwoba.forcestaff.sdk.link.LinkException;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by het on 2016/4/10.
 */
public class HttpClientUtil {

    private static HttpClient httpClient;
    private static Thread connectionMonitorThread;

    public synchronized static HttpClient getHttpClient() {
        if (httpClient == null) {
            final PoolingHttpClientConnectionManager mgr = new PoolingHttpClientConnectionManager();
            mgr.setMaxTotal(200);
            mgr.setDefaultMaxPerRoute(50);
            mgr.setValidateAfterInactivity(500);
            httpClient = HttpClients.custom().setConnectionManager(mgr).build();
            // 启动链接检测线程
            connectionMonitorThread = new Thread() {
                @Override
                public void run() {
                    while (!isInterrupted()) {
                        try {
                            synchronized (this) {
                                wait(5000);
                                mgr.closeExpiredConnections();
                                mgr.closeIdleConnections(30, TimeUnit.SECONDS);
                            }
                        } catch (InterruptedException e) {
                            // ternimal
                        }
                    }
                }
            };
            connectionMonitorThread.setDaemon(true);
            connectionMonitorThread.start();
        }
        return httpClient;
    }

    public static void closeClient(HttpClient httpClient) {
        HttpClientUtils.closeQuietly(httpClient);
    }

    public static String post(String uri, Map<String, Object> paramMap) {
        HttpClient client = getHttpClient();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (paramMap != null) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                params.add(pair);
            }
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, Charset.forName("utf-8"));

        try {
            HttpPost post = new HttpPost(new URI(uri));
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity());
            }
            throw new LinkException(String.format("Execute HTTP POST request failed! status=%s", statusCode));
        } catch (Exception e) {
            throw new LinkException("Execute HTTP POST request failed!", e);
        }
    }

    public static String get(String uri, Map<String, Object> paramMap) {
        HttpClient client = getHttpClient();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (paramMap != null) {
            for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry.getValue().toString());
                params.add(pair);
            }
        }

        try {
            String paramString = EntityUtils.toString(new UrlEncodedFormEntity(params));
            String suri = uri;
            if (StringUtils.isNotBlank(paramString)) {
                if (uri.contains("?")) {
                    if (uri.endsWith("&")) {
                        suri += paramString;
                    } else {
                        suri += "&" + paramString;
                    }
                } else {
                    suri += "?" + paramString;
                }
            }
            HttpGet get = new HttpGet(new URI(suri));
            HttpResponse response = client.execute(get);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity());
            }
            throw new LinkException(String.format("Execute HTTP GET request failed! status=%s", statusCode));
        } catch (Exception e) {
            throw new LinkException("Execute HTTP GET request failed!", e);
        }

    }

    public static void main(String[] args) throws Exception {
        System.out.println(get("http://www.baidu.com/", null));
    }
}
