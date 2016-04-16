package com.dianwoba.forcestaff.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class AuthenticationUtil {

    /**
     * 生成Open API数据签名
     *
     * @param paramMap
     * @param appSecret
     * @return
     */
    public static String sign(Map<String, Object> paramMap, String appSecret) {
        TreeMap<String, Object> map = new TreeMap<String, Object>(paramMap);
        StringBuffer sb = new StringBuffer();
        sb.append(appSecret);
        for (String pName : map.keySet()) {
            sb.append(pName).append(map.get(pName));
        }
        sb.append(appSecret);
        try {
            byte[] sha1Digest = getSHA1Digest(sb.toString());
            return byte2hex(sha1Digest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 进行SHA-1编码
     *
     * @param data
     * @return
     * @throws IOException
     */
    private static byte[] getSHA1Digest(String data) throws IOException {
        byte[] bytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            bytes = md.digest(data.getBytes("UTF-8"));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
        return bytes;
    }

    /**
     * 二进制转十六进制字符串
     *
     * @param bytes
     * @return
     */
    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }
}
