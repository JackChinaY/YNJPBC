package ABE.CPABE.Entity;

import ABE.CPABE.Entity.Ciphertext;
import it.unisa.dia.gas.jpbc.Element;
/**
 * 密文和秘钥
 */
public class CiphertextAndKey {
    /*
     * This class is defined for some classes who return both cph and key.
     */
    public Ciphertext cph;
    public Element key;
}
