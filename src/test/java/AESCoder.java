import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * 对文件进行AES加密
 * AES 高级加密标准（Advanced Encryption Standard），是一种区块加密标准。
 * 这个标准用来替代原先的DES，已经被多方分析且广为全世界所使用。
 */
public class AESCoder {
    /**
     * 由种子产生128位的随机秘钥
     *
     * @param seed 种子
     */
    private static byte[] getRawKey(byte[] seed) throws Exception {
        // 创建AES的Key生产者
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        //SecureRandom是生成安全随机数序列，seed是种子，只要种子相同，序列就一样，所以解密只要有seed就行
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        // 利用安全随机数序列初始化出128位的key生产者
        kgen.init(128, sr); // 192 and 256 bits may not be available
        //产生AES的Key
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    /**
     * AES加密
     */
    public static byte[] encrypt(byte[] seed, byte[] plaintext) throws Exception {
        //由种子产生128位的随机秘钥
        byte[] raw = getRawKey(seed);
        //转换为AES专用密钥
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        // 初始化为加密模式的密码器
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        // 加密
        byte[] encrypted = cipher.doFinal(plaintext);
        return encrypted;
    }

    /**
     * AES解密
     */
    public static byte[] decrypt(byte[] seed, byte[] ciphertext) throws Exception {
        //由种子产生128位的随机秘钥
        byte[] raw = getRawKey(seed);
        //转换为AES专用密钥
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        // 初始化为解密模式的密码器
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        // 解密
        try {
            byte[] decrypted = cipher.doFinal(ciphertext);
            return decrypted;
        } catch (Exception e) {
            System.err.println("解密失败，AES种子不对！");
            System.exit(0);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
//        Long beginTime3 = System.currentTimeMillis();
        byte[] seed = "1234567890".getBytes();
        int n = 1;
        List<Long> a = new ArrayList<>();
        List<Long> b = new ArrayList<>();
        List<Integer> c = new ArrayList<>();
        for (int i = 1; i < 31; i++) {
            c.add(n);
            byte[] M = new byte[n];
            n = n * 2;
            for (int j = 0; j < M.length; j++) {
                M[j] = 0x18;
            }
            Long beginTime = System.currentTimeMillis();
            byte[] CT = encrypt(seed, M);
            Long endTime = System.currentTimeMillis() - beginTime;
            System.out.print("加密耗时:" + endTime + "ms ");

            Long beginTime2 = System.currentTimeMillis();
            decrypt(seed, CT);
            Long endTime2 = System.currentTimeMillis() - beginTime2;
            System.out.print("解密耗时:" + endTime2 + "ms ");
            System.out.print("明文大小:" + M.length + "字节 ");
            System.out.println("密文大小:" + CT.length + "字节");
            a.add(endTime);
            b.add(endTime2);

        }
        for (int i = 0; i < c.size(); i++) {
            System.out.println(c.get(i));
        }
        for (int i = 0; i < a.size(); i++) {
            System.out.println(a.get(i));
        }
        for (int i = 0; i < b.size(); i++) {
            System.out.println(b.get(i));
        }
//        Long endTime3 = System.currentTimeMillis() - beginTime3;
//        System.out.println("文件耗时:" + endTime3 + "ms");


//        System.out.println("明文大小:" + M.length + "字节");
//        System.out.println("密文大小:" + CT.length + "字节");
    }
//    Long beginTime3 = System.currentTimeMillis();
//    byte[] seed = "1234567890".getBytes();
//    byte[] M = new byte[500360000];
//        for (int i = 0; i < seed.length; i++) {
//        M[i] = 0x18;
//    }
//    Long endTime3 = System.currentTimeMillis() - beginTime3;
//        System.out.println("文件耗时:" + endTime3 + "ms");
//
//    Long beginTime = System.currentTimeMillis();
//    byte[] CT = encrypt(seed, M);
//    Long endTime = System.currentTimeMillis() - beginTime;
//        System.out.println("加密耗时:" + endTime + "ms");
//
//    Long beginTime2 = System.currentTimeMillis();
//    decrypt(seed, CT);
//    Long endTime2 = System.currentTimeMillis() - beginTime2;
//        System.out.println("解密耗时:" + endTime2 + "ms");
//
//        System.out.println("明文大小:" + M.length + "字节");
//        System.out.println("密文大小:" + CT.length + "字节");
}