package MA_ABE.MA_CPABE_CSC_CPA2.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;

/**
 * 密文，用户的所有属性封装在TreePolicy中
 */
public class Ciphertext {

    public Element C0; // G_T,C0=M*Z^s
    public Element C1; // G_T,C1=g^s
    public Element C2; // G_T,C2=M*e(g,g)^as
    public ArrayList<Element> Ci;//
}
