package Revocatioon.CP_ABE_AL;

/**
 * 主函数的接口
 */
public interface Ident {
    void setup();

    void keygen();

    void encrypt();

    void re_encrypt();

    void part_decrypt();

    void decrypt();
}
