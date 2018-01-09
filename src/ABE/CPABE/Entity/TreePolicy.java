package ABE.CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;

public class TreePolicy {
    /* serialized */

    /* k=1 if leaf, otherwise threshould */
    public int k;//必要属性个数，2of3 k of n
    /* attribute string if leaf, otherwise null */
    public String attr;//单个属性
    public  Element c;			/* G_1 only for leaves */
    public Element cp;		/* G_1 only for leaves */
    /* array of BswabePolicy and length is 0 for leaves */
    public TreePolicy[] children;

    /* only used during encryption */
    public Polynomial q;

    /* only used during decription */
    public boolean satisfiable;
    public int min_leaves;
    public int attri;
    public ArrayList<Integer> satl = new ArrayList<Integer>();
}
