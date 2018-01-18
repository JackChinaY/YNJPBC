package CSC_CPABE.CCA_CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 密文，用户的所有属性封装在TreePolicy中
 */
public class Ciphertext {
    /*
     * A ciphertext. Note that this library only handles encrypting a single
     * group element, so if you want to encrypt something bigger, you will have
     * to use that group element as a symmetric key for hybrid encryption (which
     * you do yourself).
     */
    public Element C0; // G_T,C0=M*Z^s
    public Element C1; // G1,C1=g^s
    public Element C2; // G1,C2=M*e(g,g)^as
    public Element C3; // G1,C3=(d1^c * d2^r * d3)^s，其中c=Hash(T,C0,C1,C2)
    public Element r; // Zr ,r
//    public Element cs; // G_T,`C=M*e(g,g)^as
//    public Element c; // G_1 ,C=h^s
//    public TreePolicy treePolicy;//访问树
}
