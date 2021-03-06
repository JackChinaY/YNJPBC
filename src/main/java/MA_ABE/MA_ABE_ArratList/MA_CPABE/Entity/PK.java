package MA_ABE.MA_ABE_ArratList.MA_CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;

import java.util.ArrayList;

/**
 * 系统公钥
 */
public class PK {
    public Pairing pairing;
    public Element g;//G1的生成元g
    public Element y0;//Zr类型
    public int K;//属性中心AA的个数
    public ArrayList<PKComp> tk;//PKComp类型，k是AA的下标
    public Element Y0;//系统公钥，GT的,Y0=e(g,g)^y0
//    public Element[] hList;//G1
//    public Element d1;//kexi1,G1类型
//    public Element d2;//kexi2,G1类型
//    public Element d3;//kexi3,G1类型


    @Override
    public String toString() {
        return "PK{" +
//                "pairing=" + pairing +
                " g=" + g +
                ", y0=" + y0 +
                ", K=" + K +
                ", tk=" + tk +
                ", Y0=" + Y0 +
                '}';
    }
}
