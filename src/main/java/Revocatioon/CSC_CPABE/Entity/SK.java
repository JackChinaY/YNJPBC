package Revocatioon.CSC_CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.Map;

/**
 * 用户私钥SK，用户的所有属性封装在集合中
 */
public class SK {
    public Map<String, SKComp> comps;//SKComp
    public Element Dca; //G_1 Dca=g2^(x-求和(K个xi))
}
