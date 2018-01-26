package TEST;

import ABE.CPABE.FileOperation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
        long begin = System.currentTimeMillis();


//        String[][] attributes_C = {{"1", "2", "3"}, {"4", "5", "6"}, {"7", "8", "9", "10"}};
//        ArrayList<String> a = new ArrayList<>();
//        a.add("1");
//        a.add("2");
//        a.add("3");
////        a.
////        System.out.println(a.indexOf("2"));
//        Map<String, String> b = new HashMap<>();
//        b.put("1","qw");
//        b.put("2","qe");
//        b.put("3","qr");
        String buffer = null;
        assert buffer != null :"12223";
        System.out.println("67");
//        byte[] a = FileOperation.file2byte("E:\\ABE\\CPABE\\public_key");
//        System.out.println(Arrays.deepToString(attributes_C));
//        System.out.println(bytesToHexString(a));
//        byte[] b = {0x00, 0x00, 0x00, -128};
//        System.out.println(getFloat(b,0));
//        String a = "1234567";
//        MessageDigest md = null;
//        try {
//            md = MessageDigest.getInstance("SHA-1");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        String b = md.digest(a.getBytes());
//        System.out.println(b);
        long end = System.currentTimeMillis();
        System.out.println("耗时:" + (end - begin) + "ms");
    }
}
