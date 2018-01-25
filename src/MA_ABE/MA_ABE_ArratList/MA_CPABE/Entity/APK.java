package MA_ABE.MA_ABE_ArratList.MA_CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Arrays;

/**
 * 每个属性中心的私钥，Authority Public Key
 */
public class APK {
    public Element[] Ti;//G1类型，i=1,2,...,n

    @Override
    public String toString() {
        return "APK{" +
                "Ti=" + Arrays.toString(Ti) +
                '}';
    }
}
