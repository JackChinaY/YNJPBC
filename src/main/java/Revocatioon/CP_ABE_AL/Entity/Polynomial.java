package Revocatioon.CP_ABE_AL.Entity;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 多项式
 */
public class Polynomial {
    public int deg;//多项式的阶
    public  Element[] coef; // Zr类型 ,个数为deg+1，存的是多项式的系数
}
