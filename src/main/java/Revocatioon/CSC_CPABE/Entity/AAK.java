package Revocatioon.CSC_CPABE.Entity;


import it.unisa.dia.gas.jpbc.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * 每个属性中心的所独有的部件,包含ASK和APK
 */
public class AAK {
    public int threshold;//属性中心的门限值
    public String s;//属性中心的种子
    public Map<String, Element> Hi = new HashMap<>();//G1类型，i=1,2,...,n

    @Override
    public String toString() {
        return "AAK {" +
                " threshold=" + threshold +
                "， s=" + s +
                '}';
    }
}
