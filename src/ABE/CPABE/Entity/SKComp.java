package ABE.CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;
/**
 *  用户私钥SK中集合的元素these actually get serialized
 */
public class SKComp {
    //以下为生成私钥时用到
    public String attr; //单个属性
    public Element d;   //G_2  Dj=g^r *H(j)^rj
    public Element dj;	 //G_2  Dj`=g^rj

    // 以下为解密时用到
    public int used;
    public Element z;	//G_1
    public Element zp;	//G_1
}
