package ABE.CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;

public class TreePolicy {
    /* serialized */

    /* k=1 if leaf, otherwise threshould */
    public int k;//必要属性个数，2of3 k of n
    /* attribute string if leaf, otherwise null */
    public String attr;//单个属性
    public  Element c;	// G_1 only for leaves Cy=g^(qy(0))
    public Element cp;	// G_1 only for leaves Cy`=H(att(y))^(qy(0))
    /* array of BswabePolicy and length is 0 for leaves */
    public TreePolicy[] children;

    //在加密过程中使用
    public Polynomial q;//多项式

    //在解密过程中使用
    public boolean satisfiable;//如果为真，该叶子节点是真正参与了配对
    public int min_leaves;//对于根节点来说，min_leaves是可解密的最小的属性个数，等于上面的k，对于非根节点来说，min_leaves==1说明参与了真正的秘钥解密
    public int decryptAttributeValue;//对于非根节点来说，是解密的属性
    public ArrayList<Integer> minAttrsList;//只用于根节点，保存了可解密的最小的属性群的序号
//    public ArrayList<Integer> satl = new ArrayList<Integer>();
}
