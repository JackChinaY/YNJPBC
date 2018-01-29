package MA_ABE.MA_CPABE_CSC_CCA.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Map;

/**
 * 用户私钥SK中集合的元素
 */
public class SKComp {
    //以下为生成私钥时用到
//    public ArrayList<Element> Dki;//G1类型,Dk,i=g^(p(i)/tk,i)
    public Map<String, Element> Dki;//G1类型,Dk,i=g^(p(i)/tk,i)

}
