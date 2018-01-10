package ABE.CPABE.Entity;

import it.unisa.dia.gas.jpbc.Element;

/**
 * 密文，用户的所有属性封装在TreePolicy中
 */
public class Ciphertext {
    /*
     * A ciphertext. Note that this library only handles encrypting a single
     * group element, so if you want to encrypt something bigger, you will have
     * to use that group element as a symmetric key for hybrid encryption (which
     * you do yourself).
     */
    public Element cs; // G_T,`C=M*e(g,g)^as
    public Element c; // G_1 ,C=h^s
    public TreePolicy treePolicy;//访问树
}
