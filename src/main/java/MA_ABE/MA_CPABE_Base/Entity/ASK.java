package MA_ABE.MA_CPABE_Base.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Map;

/**
 * 每个属性中心的私钥，Authority Secret Key
 */
public class ASK {
    public String s;//各属性中心的种子，由序号的MD5值算出，是32位的字符串，之后哈希到Zr上，得到一个Zr类型的Element
    public int n;//每个属性中心管理的属性的个数n
//    public Element[] ti;
    public Map<String, Element> ti;//Zr类型，i=1,2,...,n
    @Override
    public String toString() {
        return "ASK{" +
                " s=" + s +
                "，n=" + n +
                ", ti=" + ti +
                '}';
    }
}
