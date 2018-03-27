package CSC_CPABE.CCA_CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 系统主密钥
 */
public class MK {
    public Element x; //Z_r的元素
//    public Element ga; //G_2的元素，g^a

    @Override
    public String toString() {
        return "MK{" +
                "x=" + x +
//                ", g^a=" + ga +
                '}';
    }
}
