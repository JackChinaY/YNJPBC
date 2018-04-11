package Revocatioon.CP_ABE_AL.Entity;

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
    public Element h0;//G1
    //    public Map<String, Element> hList;//G1类型
    public Element d1;//kexi1,G1类型
    public Element d2;//kexi2,G1类型
    public Element d3;//kexi3,G1类型
    public Element u;//G1
    public Element h;//G1
    public Element w;//G1
    public Element v;//G1
    public Element e_g_g_a;//GT e(g,g)^a

    @Override
    public String toString() {
        return "PK{" +
                "g=" + g +
                ", u=" + u +
                ", h=" + h +
                ", w=" + w +
                ", v=" + v +
                ", e_g_g_a=" + e_g_g_a +
                '}';
    }
}
