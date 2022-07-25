package com.h.android.encrypt;

import android.text.TextUtils;

import com.h.android.utils.HLog;

import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Created by zhanghongjun on 2018/3/30.
 */

public class EncryptUtil {

    public static String md5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(s.getBytes("utf-8"));
            return toHex(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toHex(byte[] bytes) {
        final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            ret.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            ret.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return ret.toString();
    }

    //生成salt
    public static byte[] makeSalt() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.generateSeed(16);
    }

    /**
     * 计算sha1
     * @param convertme
     * @return
     */
    public static byte[] computeSHA1(byte[] convertme) {
        return computeSHA1(convertme, 0, convertme.length);
    }

    /**
     * 计算sha1
     * @param convertme
     * @param offset
     * @param len
     * @return
     */
    public static byte[] computeSHA1(byte[] convertme, int offset, int len) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(convertme, offset, len);
            return md.digest();
        } catch (Exception e) {
            HLog.INSTANCE.e("",e.getLocalizedMessage());
        }
        return new byte[20];
    }
}
