package MA_ABE.MA_ABE_ArratList.MA_CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;

/**
 * 用户私钥SK，第i个表示是第i属性中心产生的私钥集合
 */
public class SK {

    public ArrayList<SKComp> comps; // SKComp，第i个表示第第i个AA生成的私钥
    public Element Dca; //G_1 Dca=g^(y0-求和(K个yk,u))
}
