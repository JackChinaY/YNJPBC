package Revocatioon.CP_ABE_AL.Entity;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 此类作用，检查属性是否满足，即检查私钥SK中的属性是否满足密文中的访问策略树的门限要求，如果满足，则flag=true，然后计算多项式求得AES种子m
 * 正对一些同时返回boolean 和 Element
 */
public class ElementBoolean {
    /*
     * This class is defined for some classes who return both boolean and
     * Element.
     */
    public Element seed;//计算多项式求得的AES当初加密的种子
    public boolean flag;//检查属性是否满足,如果满足，则flag=true
}
