package Revocatioon.CSC_CPABE;

/**
 * 主函数的接口
 */
public interface Ident {
    void setup();

    void keygen();

    void encrypt();

    void re_encrypt();

    void decrypt();
}
