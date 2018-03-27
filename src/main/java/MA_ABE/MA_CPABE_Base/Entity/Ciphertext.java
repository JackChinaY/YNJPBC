package MA_ABE.MA_CPABE_Base.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;

/**
 * 密文
 */
public class Ciphertext {

    public Element E; // G_T类型，E=M*Y0^s
    public Element Eca; // G1类型，Eca=g^s
    public ArrayList<CiphertextComp> Ek; // G1类型，Eki=Tki^s,i属于ACk,对于任意的k,k表示第k个AA参与生成的私钥
//    public Element C0; // G_T,C0=M*Z^s
//    public Element C1; // G1,C1=g^s
//    public Element C2; // G1,C2=M*e(g,g)^as
//    public Element C3; // G1,C3=(d1^c * d2^r * d3)^s，其中c=Hash(T,C0,C1,C2)
//    public Element r; // Zr ,r
//    public Element cs; // G_T,`C=M*e(g,g)^as
//    public Element c; // G_1 ,C=h^s
//    public TreePolicy treePolicy;//访问树
}
