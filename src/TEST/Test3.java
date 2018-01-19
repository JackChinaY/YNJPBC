package TEST;

import ABE.CPABE.FileOperation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Test3 {
    /**
     * 将字节数组byte[]转成16进制字符串
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            String hex = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            stringBuilder.append(hex + " ");
        }
        return stringBuilder.toString();
    }

    public static float getFloat(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

    public static void main(String[] args) {
//        byte[] a = FileOperation.file2byte("E:\\ABE\\CPABE\\public_key");
//        System.out.println(Arrays.toString(a));
//        System.out.println(bytesToHexString(a));
//        byte[] b = {0x00, 0x00, 0x00, -128};
//        System.out.println(getFloat(b,0));
        String a = "1234567";
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
//        String b = md.digest(a.getBytes());
//        System.out.println(b);
    }
}
