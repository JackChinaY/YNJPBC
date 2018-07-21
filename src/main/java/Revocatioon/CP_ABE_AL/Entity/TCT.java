package Revocatioon.CP_ABE_AL.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * 部分解密文件
 */
public class TCT {
    public Element C;//C'
    public Element B;
    public Map<String, Element> Bi = new HashMap<>();
    public Element D;
    public Element E;
    public Element F;
}
