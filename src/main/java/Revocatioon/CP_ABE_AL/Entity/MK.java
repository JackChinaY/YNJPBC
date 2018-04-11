package Revocatioon.CP_ABE_AL.Entity;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 系统主密钥
 */
public class MK {
    public Element x; //Z_r的元素
    public Element a1; //Z_r
    public Element a2; //Z_r

    @Override
    public String toString() {
        return "MK{" +
                "x=" + x +
                ", a1=" + a1 +
                ", a2=" + a2 +
                '}';
    }
}
