package MA_ABE.MA_CPABE_CSC_CCA.Entity;

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
