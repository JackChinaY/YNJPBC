package MA_ABE.MA_CPABE_CSC_CCA.Entity;

/**
 * 每个属性中心的所独有的部件,包含ASK和APK
 */
public class AAK {
    public ASK ask;//属性中心的私钥
    public APK apk;//属性中心的公钥

    @Override
    public String toString() {
        return "AAK{" +
                "ask=" + ask +
                ", apk=" + apk +
                '}';
    }
}
