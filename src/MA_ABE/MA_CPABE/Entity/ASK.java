package MA_ABE.MA_CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Arrays;

/**
 * 每个属性中心的私钥，Authority Secret Key
 */
public class ASK {
    public String s;//各属性中心的种子
    public int n;//每个属性中心管理的属性的个数n
    public Element[] ti;//Zr类型，i=1,2,...,n

    @Override
    public String toString() {
        return "ASK{" +
                " s=" + s +
                "，n=" + n +
                ", ti=" + Arrays.toString(ti) +
                '}';
    }
}
