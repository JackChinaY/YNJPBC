package MA_ABE.MA_CPABE_CSC_CPA.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Map;

/**
 * 每个属性中心的私钥，Authority Public Key
 */
public class APK {
    public Map<String, Element> Hi;//G1类型，i=1,2,...,n

    @Override
    public String toString() {
        return "APK{" +
                "Hi=" + Hi +
                '}';
    }
}
