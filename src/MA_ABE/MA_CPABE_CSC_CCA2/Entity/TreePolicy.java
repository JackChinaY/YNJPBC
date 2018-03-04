package MA_ABE.MA_CPABE_CSC_CCA2.Entity;

import it.unisa.dia.gas.jpbc.Element;

import java.util.ArrayList;

public class TreePolicy {
    /* serialized */

    //如果是叶子节点，则 k=1, 如果不是叶子节点，k表示门限值， 比如根节点，2of3 k of n，2表示门限
    public int k;//必要属性个数
    //如果是叶子节点， attr表示属性，如果是非叶子节点, 则为 null
    public String attr;//单个属性
    public  Element c;	// G_1 ，只针对叶子节点， Cy=g^(qy(0))
    public Element cp;	// G_1 ，只针对叶子节点， Cy`=H(att(y))^(qy(0))
    //如果是叶子节点,children集合的长度为0，如果是非叶子节点，children集合的长度不为0
    public TreePolicy[] children;

    //在加密过程中使用，叶子节点和非叶子节点都有
    public Polynomial polynomial;//多项式

    //在解密过程中使用
    public boolean satisfiable;//如果为true，该叶子节点是真正参与了配对
    public int min_leaves;//对于根节点来说，min_leaves是可解密的最小的属性个数，等于上面的k，对于非根节点来说，min_leaves==1说明参与了真正的秘钥解密
    public int decryptAttributeValue;//对于非根节点来说，是解密的属性
    public ArrayList<Integer> minAttrsList;//只用于根节点，保存了可解密的最小的属性群的序号
//    public ArrayList<Integer> satl = new ArrayList<Integer>();
}
