package com.h.android.encrypt;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MyByte {
    private byte[] data;
    private int writeIndex;
    private static final char[] hexArray = "0123456789abcdef".toCharArray();

    public MyByte(int l) {
        this.data = new byte[l];
        this.writeIndex = 0;
    }

    public MyByte(byte[] bytes) {
        this.data = bytes;
        this.writeIndex = bytes.length - 1;
    }

    public MyByte copy(byte[] src) {
        return this.copy(src, src.length);
    }

    public MyByte copy(byte[] src, int l) {
        return this.copy(src, 0, l);
    }

    public MyByte copy(byte[] src, int s, int l) {
        System.arraycopy(src, s, this.data, this.writeIndex, l);
        this.writeIndex += l - s;
        return this;
    }

    public byte[] getData() {
        return this.data;
    }

    private static byte[] convert(long d, int l) {
        byte[] tar = new byte[l];

        for (int i = 0; i < l; ++i) {
            tar[i] = (byte) ((int) (i < 8 ? d >>> i * 8 & 255L : 0L));
        }

        return tar;
    }

    public static byte[] trim(byte[] src) {
        return trimR(trimL(src));
    }

    public static byte[] trimL(byte[] src) {
        return trimL(src, src.length);
    }

    public static byte[] trimL(byte[] src, int d) {
        int i;
        for (i = 0; i < d && src[i] == 0; ++i) {
            ;
        }

        return copyBytes(src, i, src.length - i);
    }

    public static byte[] trimR(byte[] src) {
        return trimR(src, src.length);
    }

    public static byte[] trimR(byte[] src, int d) {
        int i = src.length - 1;

        for (d = src.length - d; i >= d && src[i] == 0; --i) {
            ;
        }

        return copyBytes(src, i + 1);
    }

    public static byte[] copyBytes(byte[] src) {
        return copyBytes(src, 0, src.length);
    }

    public static byte[] copyBytes(byte[] src, int l) {
        return copyBytes(src, 0, l);
    }

    public static byte[] copyBytes(byte[] src, int s, int l) {
        return (new MyByte(l)).copy(src, s, l).getData();
    }

    public static byte[] copyBytesR(byte[] src, int l) {
        return copyBytes(src, src.length - l, l);
    }

    public static byte[] reverse(byte[] src) {
        byte[] result = new byte[src.length];

        for (int i = 0; i < src.length; ++i) {
            result[i] = src[src.length - i - 1];
        }

        return result;
    }

    public static MyByte builder(int l) {
        return new MyByte(l);
    }

    public static BuildList builder() {
        return new BuildList();
    }

    public static String toHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; ++j) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 15];
        }

        return new String(hexChars);
    }

    public static byte[] fromHex(String s) {
        int l = s.length();
        byte[] bytes = new byte[l / 2];

        for (int i = 0; i < l; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }

        return bytes;
    }

    public static class BuildList {
        List<byte[]> list = new ArrayList();

        public BuildList() {
        }

        public MyByte finish() {
            int l = 0;

            byte[] bytes;
            for (Iterator var2 = this.list.iterator(); var2.hasNext(); l += bytes.length) {
                bytes = (byte[]) var2.next();
            }

            MyByte myByte = new MyByte(l);
            for (int i = 0; i < list.size(); i++) {
                myByte.copy(this.list.get(i));
            }

            return myByte;
        }

        public byte[] getData() {
            return this.finish().getData();
        }

        public BuildList padding() {
            return this.copy((byte) 0);
        }

        public BuildList copy(int d) {
            return this.copy(MyByte.convert((long) d, 4));
        }

        public BuildList copy(long d) {
            return this.copy(d, 8);
        }

        public BuildList copy(long d, int l) {
            return this.copy(MyByte.convert(d, l));
        }

        public BuildList copy(byte[] src) {
            return this.copy(src, src.length);
        }

        public BuildList copy(BigInteger src) {
            return this.copy(src.toByteArray());
        }

//        public MyByte.BuildList copy(List<byte[]> src) {
//            this.copySize((long)src.size());
//            src.forEach(this::copy);
//
//
//
//
//
//            return this;
//        }
//
//        public MyByte.BuildList copy(Map<byte[], Long> src) {
//            this.copySize((long)src.size());
//            src.forEach((k, v) -> {
//                this.copy(k).copy(v);
//            });
//            return this;
//        }

        public BuildList copy(byte[] src, int l) {
            return this.copy(src, 0, l);
        }

        public BuildList copy(byte[] src, int s, int l) {
            byte[] tar = new byte[l];
            System.arraycopy(src, s, tar, 0, l);
            this.list.add(tar);
            return this;
        }

        public BuildList copy(byte d) {
            this.list.add(new byte[]{d});
            return this;
        }

        public BuildList copy(String src) {
            return src != null && src.length() != 0 ? this.copyVector(src.getBytes()) : this.padding();
        }

        public BuildList copyByteString(String s) {
            this.list.add(MyByte.fromHex(s));
            return this;
        }

        public BuildList copyVector(byte[] src) {
            return this.copySize((long) src.length).copy(src, src.length);
        }

        public BuildList copySize(long l) {
            do {
                int b = (int) l & 127;
                l >>= 7;
                b |= (l > 0L ? 1 : 0) << 7;
                this.copy((byte) b);
            } while (l > 0L);

            return this;
        }
    }

    public static byte[] longToBytes(long v) {
        return new byte[]{(byte) ((int) (v >>> 56)), (byte) ((int) (v >>> 48)), (byte) ((int) (v >>> 40)), (byte) ((int) (v >>> 32)), (byte) ((int) (v >>> 24)), (byte) ((int) (v >>> 16)), (byte) ((int) (v >>> 8)), (byte) ((int) v)};
    }

    public static byte[] sha256hash(byte[] orig) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(orig);
        } catch (NoSuchAlgorithmException var3) {
            throw new RuntimeException();
        }
    }
}