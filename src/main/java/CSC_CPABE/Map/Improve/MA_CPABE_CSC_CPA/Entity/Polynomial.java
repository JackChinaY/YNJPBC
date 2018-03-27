package CSC_CPABE.Map.Improve.MA_CPABE_CSC_CPA.Entity;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 多项式
 */
public class Polynomial {
    public int deg;//多项式的阶
    /* 系数 coefficients from [0] x^0 to [deg] x^deg */
    public  Element[] coef; // Zr类型 ,个数为deg+1
}
