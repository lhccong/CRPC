package com.crpc.core.serialize.cprc;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.List;


/**
 * 各种常见的基础类型转换方法。
 * @author liuhuaicong
 * @date 2023/10/18
 */
public class ByteConvertUtils {

    public static byte[] intToByte(int n) {
        byte[] buf = new byte[4];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) (n >> (8 * i));
        }
        return buf;
    }

    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }


    public static byte[] shortToByte(short n) {
        byte[] buf = new byte[2];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) (n >> (8 * i));
        }
        return buf;
    }


    public static byte[] longToByte(long j) {
        byte[] buf = new byte[8];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (byte) (j >> 8 * i);
        }
        return buf;
    }

    /**
     * 字节数组转换为int数值
     * 十六进制的开头一般都是0x开头
     *
     * @param bytes
     * @return
     */
    public static int byteToInt(byte[] bytes) {
        if (bytes.length != 4) {
            return 0;
        }
        return (bytes[0]) & 0xff | (bytes[1] << 8) & 0xff00 | (bytes[2] << 16) & 0xff0000 | (bytes[3] << 24) & 0xff000000;
    }

    public static short byteToShort(byte[] bytes) {
        if (bytes.length != 2) {
            return 0;
        }
        return (short) ((bytes[0]) & 0xff
                | (bytes[1] << 8) & 0xff00);
    }


    public static long byteToLong(byte[] bytes) {
        if (bytes.length != 8) {
            return 0;
        }
        return ((long) bytes[0]) & 0xff
                | (((long) bytes[1]) << 8 & 0xff00)
                | (((long) bytes[2]) << 16 & 0xff0000)
                | (((long) bytes[3]) << 24 & 0xff000000)
                | (((long) bytes[4]) << 32 & 0xff00000000L)
                | (((long) bytes[5]) << 40 & 0xff0000000000L)
                | (((long) bytes[6]) << 48 & 0xff000000000000L)
                | (((long) bytes[7]) << 56 & 0xff00000000000000L);
    }

    /**
     * 目前只支持传输英文解析
     *
     * @param bytes
     * @return
     */
    public static String byteToString(byte[] bytes) {
        if (bytes.length == 0) {
            return null;
        }
        int len = bytes.length;
        char[] encodeChar = new char[len];
        for (int i = 0; i < bytes.length; i++) {
            encodeChar[i] = (char) bytes[i];
        }
        return String.valueOf(encodeChar);
    }


    /**
     * 目前只支持英文解析
     *
     * @param str
     * @return
     */
    public static byte[] stringToBytes(String str) {
        if (str == null || str.length() == 0) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length()];
        char[] chars = str.toCharArray();
        for (int i = 0; i < bytes.length; i++) {
            int k = chars[i];
            bytes[i] = (byte) k;
        }
        return bytes;
    }

    /**
     * 将链表序列化为字符串存入json文件中
     *
     * @param objList
     * @return
     * @throws IOException
     */
    public static String convertForList(Object objList) {
        return JSON.toJSONString(objList, true);
    }

    /**
     * 将json文件中的内容读取出来，反序列化为链表
     *
     * @param listString
     * @param clazz
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> List<T> convertForListFromFile(String listString, Class<T> clazz) {
        return JSON.parseArray(listString, clazz);
    }
}
