package MA_ABE.MA_CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 密文和AES种子
 */
public class CiphertextAndKey {
    /*
     * This class is defined for some classes who return both cph and key.
     */
    public Ciphertext cph;//密文
    public Element key;//AES种子，GT类型
}
