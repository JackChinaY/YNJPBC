package MA_ABE.MA_CPABE_CSC_CCA.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Map;

/**
 * 每个属性中心的私钥，Authority Public Key
 */
public class APK {
    //    public Element[] Ti;
    public Map<String, Element> Ti;//G1类型，i=1,2,...,n

    @Override
    public String toString() {
        return "APK{" +
                "Ti=" + Ti +
                '}';
    }
}
