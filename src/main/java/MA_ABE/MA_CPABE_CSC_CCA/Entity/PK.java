package MA_ABE.MA_CPABE_CSC_CCA.Entity;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.Map;

/**
 * 系统公钥
 */
public class PK {
    public Pairing pairing;
    public Element g;//G1的生成元g
    public Element g2;//G1 g2
    public Element Z;//GT的,Z=e(g1,g2)
    public Element h0;//G1
//    public Map<String, Element> hList;//G1类型
    public Element d1;//kexi1,G1类型
    public Element d2;//kexi2,G1类型
    public Element d3;//kexi3,G1类型

    @Override
    public String toString() {
        return "PK{" +
                "g=" + g +
                ", g2=" + g2 +
                ", Z=" + Z +
                ", h0=" + h0 +
                ", d1=" + d1 +
                ", d2=" + d2 +
                ", d3=" + d3 +
                '}';
    }
}
