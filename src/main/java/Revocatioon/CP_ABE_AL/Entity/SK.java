package Revocatioon.CP_ABE_AL.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户私钥SK，用户的所有属性封装在集合中
 */
public class SK {
    public Map<String, SKComp> comps;//SKComp
    public Element Dca; //G_1 Dca=g2^(x-求和(K个xi))
    ///////////////////////////////////////
    public Element K0; //G_1
    public Element K1; //G_1
    public Map<String, Element> K2 = new HashMap<>();
    public Map<String, Element> K3 = new HashMap<>();
    public Element SK2; //G_1 SK2=g^(a2)
}
