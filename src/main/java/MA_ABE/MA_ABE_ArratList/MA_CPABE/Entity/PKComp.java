package MA_ABE.MA_ABE_ArratList.MA_CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Arrays;

/**
 * 系统公钥中的部件
 */
public class PKComp {
    public String s;//各属性中心的种子
    public int n;//每个属性中心管理的属性的个数n
    public Element[] ti;//Zr类型，i=1,2,...,n

    @Override
    public String toString() {
        return "PKComp{" +
                " s=" + s +
                "，n=" + n +
                ", ti=" + Arrays.toString(ti) +
                '}';
    }
}
