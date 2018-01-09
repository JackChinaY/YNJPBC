package ABE.CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;

/**
 * 用户私钥SK
 */
public class SK {

    public Element d; //G_2 D=(g^r*g^a)^(1/b)
    public ArrayList<SKComp> comps; // SKComp
}
