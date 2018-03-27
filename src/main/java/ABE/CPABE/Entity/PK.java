package ABE.CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
/**
 * 系统公钥
 */
public class PK {
//    public String pairingDesc;
    public Pairing pairing;
    public Element g;//G1的生成元g
    public Element h;//G1的元素，g^b
//    public Element f;//G1的
//    public Element gp;//G2的
    public Element e_g_ga;//GT的,e(g,g)^a

    @Override
    public String toString() {
        return "PK{" +
//                "pairingDesc='" + pairingDesc + '\'' +
//                ", pairing=" + pairing +
                " g=" + g +
                ", h=" + h +
//                ", f=" + f +
//                ", gp=" + gp +
                ", e(g,g)a=" + e_g_ga +
                '}';
    }
}
