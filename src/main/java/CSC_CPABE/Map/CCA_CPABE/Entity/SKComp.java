package CSC_CPABE.Map.CCA_CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Map;

/**
 *  用户私钥SK中集合的元素
 */
public class SKComp {
    //以下为生成私钥时用到
    public String attr; //单个属性
    public Element a;   //G_2  ai=(g2^q(i))*(h0hi) ^ri
    public Element b;	 //G_2  bi=g^ri
//    public Element[] hList;//GT的,Z=e(g1,g2)
    public Map<String, Element> hList;//G1类型
    // 以下为解密时用到
    public int used;
    public Element z;	//G_1
    public Element zp;	//G_1
}
