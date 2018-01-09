package ABE.CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 多项式
 */
public class Polynomial {
    public int deg;
    /* 系数 coefficients from [0] x^0 to [deg] x^deg */
    public  Element[] coef; /* G_T (of length deg+1) */
}
