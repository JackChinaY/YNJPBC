package Revocatioon.CP_ABE_AL.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 重加密后的密文
 */
public class RCT {

    public Element C;  //C'
    public Element C0; //C0'
    public Element C11; //C1'
    public Map<String, Element> C1 = new HashMap<>();//Ci,1'
    public Map<String, Element> C2 = new HashMap<>();//Ci,2'
    public Map<String, Element> C3 = new HashMap<>();//Ci,3'
    public Element s; // s
}
