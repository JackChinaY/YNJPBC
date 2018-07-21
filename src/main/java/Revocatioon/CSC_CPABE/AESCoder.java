package Revocatioon.CSC_CPABE;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

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
}