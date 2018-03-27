package MA_ABE.MA_CPABE_CSC_CPA2.Entity;


/**
 * 每个属性中心的所独有的部件,包含ASK和APK
 */
public class AAK {
    public int threshold;//属性中心的门限值
    public String s;//属性中心的种子
    public APK apk;//属性中心的公钥
    public int number;//U的个数，也是AA掌管的属性的个数
    @Override
    public String toString() {
        return "AAK {" +
                " threshold=" + threshold +
                "， s=" + s +
                "， apk=" + apk +
                '}';
    }
}
