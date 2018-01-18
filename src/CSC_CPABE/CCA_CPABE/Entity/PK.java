package CSC_CPABE.CCA_CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

/**
 * 系统公钥
 */
public class PK {
    public Pairing pairing;
    public Element g;//G1的生成元g
    public Element g2;//G1 g2
    public Element Z;//GT的,Z=e(g1,g2)
    public Element[] hList;//G1

    @Override
    public String toString() {
        return "PK{" +
                "g=" + g +
                "g2=" + g2 +
                ", Z=" + Z +
                '}';
    }
}
