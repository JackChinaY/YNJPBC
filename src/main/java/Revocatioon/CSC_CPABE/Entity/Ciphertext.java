package Revocatioon.CSC_CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;

/**
 * 密文，用户的所有属性封装在TreePolicy中
 */
public class Ciphertext {

    public Element C0; // G_T,C0=M*Z^s
    public Element C1; // G_T,C1=g^s
    public Element C2; // G_T,C2=M*e(g,g)^as
    public ArrayList<Element> Ci;//代替原来的C2，个数是AA们的个数
    public Element C3; // G1,C3=(d1^c * d2^r * d3)^s，其中c=Hash(T,C0,C1,C2)
    public Element Cra; // CRA
    public Element r; // Zr ,r

}
