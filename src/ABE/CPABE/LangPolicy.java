package ABE.CPABE;

import ABE.CPABE.Entity.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

/**
 * 主函数调用的全部方法，各个过程具体执行的方法
 */
public class LangPolicy {
    /**---------------------------keygen生成私钥阶段用到的函数---------------------------**/
    /**
     * setup初始化 输出PK、MK
     *
     * @param pkfile PK本地保存的路径和文件名
     * @param mkfile MK本地保存的路径和文件名
     */
    public static void setup(String pkfile, String mkfile) {
        byte[] pk_byte, mk_byte;
        PK pk = new PK();
        MK mk = new MK();
        //给系统公钥赋值
        //仅对于双线性映射，要使用PBC包装并获得性能，必须设置配对工厂的使用PBC（可能）属性
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        //椭圆类型是Type A，生成 对称-质数阶-双线性群,即G1==G2，返回代数结构,代数结构包含：群、环、场（groups, rings and fields）
        Pairing pairing = PairingFactory.getPairing("a.properties");
        pk.pairing = pairing;
        pk.g = pairing.getG1().newRandomElement().getImmutable();//生成G1的生成元g
//        pk.f = pairing.getG1().newRandomElement().getImmutable();
//        pk.h = pairing.getG1().newRandomElement().getImmutable();
//        pk.gp = pairing.getG2().newRandomElement().getImmutable();
        pk.e_g_ga = pairing.getGT().newRandomElement().getImmutable();
        //产生两个Zr的随机值
        Element a, b;
        a = pairing.getZr().newRandomElement().getImmutable();//随机值a
        b = pairing.getZr().newRandomElement().getImmutable();//随机值b
        //给主密钥赋值
        mk.b = b.getImmutable();//随机值
        mk.ga = pk.g.powZn(a).getImmutable();
        //给系统公钥赋值
        pk.h = pk.g.powZn(b).getImmutable();
        pk.e_g_ga = pairing.pairing(pk.g, mk.ga);
        //将系统公钥PK保存到本地文件
        pk_byte = SerializeUtils.serializePK(pk);
        FileOperation.byte2File(pk_byte, pkfile);
        //将主密钥MK保存到本地文件
        mk_byte = SerializeUtils.serializeMK(mk);
        FileOperation.byte2File(mk_byte, mkfile);
        System.out.println("G1的生成元 g=" + pk.g);
        System.out.println("系统主密钥 " + mk.toString());
        System.out.println("系统完整公钥 " + pk.toString());
    }
    /**---------------------------keygen生成私钥阶段用到的函数---------------------------**/
    /**
     * 生成私钥 文件的读取和保存工作 输入PK、MK、ATTR(用户属性)，输出SK
     *
     * @param pkfile   PK本地保存的路径和文件名
     * @param skfile   SK本地保存的路径和文件名
     * @param mkfile   MK本地保存的路径和文件名
     * @param attr_str 用户的属性
     */
    public static void keygen(String pkfile, String skfile, String mkfile, String attr_str) throws NoSuchAlgorithmException {
        byte[] pk_byte, mk_byte, pr_byte;//PK、MK、SK
        PK pk = new PK();
        MK mk = new MK();

        //读取本地的PK文件
        pk_byte = FileOperation.file2byte(pkfile);
        pk = SerializeUtils.unserializePK(pk_byte);

        //读取本地的MK文件
        mk_byte = FileOperation.file2byte(mkfile);
        mk = SerializeUtils.unserializeMK(pk, mk_byte);

        //将一个字符串，此字符串包含一个人的各种属性，分隔符是空格，最后返回一个数组，数组中包含各个小属性
        String[] attr_arr = parseAttribute(attr_str);
        SK sk = keygen(pk, mk, attr_arr);

        //将私钥SK保存到本地文件
        pr_byte = SerializeUtils.serializeSK(sk);
        FileOperation.byte2File(pr_byte, skfile);
        System.out.println("用户私钥 D=(g^r*g^a)^(1/b) " + sk.d);
        System.out.println("用户属性个数 " + sk.comps.size());
    }

    /**
     * 生成私钥 具体计算私钥
     */
    public static SK keygen(PK pk, MK mk, String[] attrs) throws NoSuchAlgorithmException {
        SK sk = new SK();
        Element g_r, r, b;//g_r表示g^r，r表示Zr的随机值，b表示MK中的b
        //计算私钥SK
        Pairing pairing = pk.pairing;
        r = pairing.getZr().newRandomElement();
        b = mk.b.duplicate();//复制
        //计算g^r
        g_r = pk.g.duplicate();
        g_r.powZn(r);
        //计算SK中的d，d=(g^r*g^a)^(1/b)
        sk.d = mk.ga.duplicate();
        sk.d.mul(g_r);
        b.invert();//取倒数，得到1/b
        sk.d.powZn(b);

        //计算每个属性，len为用户属性集合的中元素的个数
        sk.comps = new ArrayList<SKComp>();
        for (int i = 0; i < attrs.length; i++) {
            SKComp comp = new SKComp();
            Element h_rj;//哈希函数 H(j)^rj
            Element rj;//Zr中的随机值 rj
//            //赋值
            comp.attr = attrs[i];
            comp.d = pairing.getG2().newElement();
            comp.dj = pairing.getG1().newElement();
            h_rj = pairing.getG2().newElement();
            rj = pairing.getZr().newRandomElement();
            //将单个属性attr哈希到G_1群上
            elementFromString(h_rj, comp.attr);
            rj.setToRandom();
            //计算H(j)^rj
            h_rj.powZn(rj);
            //计算Dj=g^r *H(j)^rj
            comp.d = g_r.duplicate();
            comp.d.mul(h_rj);
            comp.dj = pk.g.duplicate();
            comp.dj.powZn(rj);
            //添加到集合中
            sk.comps.add(comp);
        }
        return sk;
    }

    /**
     * 将一个字符串按分隔符分开，返回字符串类型的数组。
     * 如将一个字符串，此字符串包含一个人的各种属性，分隔符是空格，最后返回一个数组，数组中包含各个小属性
     */
    public static String[] parseAttribute(String str) {
        ArrayList<String> str_arr = new ArrayList<String>();
        //构造一个用来解析str的StringTokenizer对象。java默认的分隔符是“空格”、“制表符(‘\t’)”、“换行符(‘\n’)”、“回车符(‘\r’)”
        StringTokenizer st = new StringTokenizer(str);//此处按空格划分每个属性
        String token;
        String res[];
        int len;

        while (st.hasMoreTokens()) {
            token = st.nextToken();
            if (token.contains(":")) {
                str_arr.add(token);
            } else {
                System.out.println("Some error happens in the input attribute");
                System.exit(0);
            }
        }

        Collections.sort(str_arr, new SortByAlphabetic());

        len = str_arr.size();
        res = new String[len];
        for (int i = 0; i < len; i++)
            res[i] = str_arr.get(i);
        return res;
    }

    /**
     * 集合排序函数
     */
    public static class SortByAlphabetic implements Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            if (s1.compareTo(s2) >= 0)
                return 1;
            return 0;
        }
    }

    /**
     * 将字符串表示的策略转换成TreePolicy类实体
     */
    public static TreePolicy parsePolicyPostfix(String s) throws Exception {
        String[] attrAll;//属性集合
        String attr;//临时单个属性变量
        ArrayList<TreePolicy> treePolicys = new ArrayList<TreePolicy>();
        TreePolicy root;
        //将字符串按空格分割成一个个元素，如"sn:student2 cn:student2 uid:student2 2of3"，分给后得到{"sn:student2","cn:student2","uid:student2","2of3"}
        attrAll = s.split(" ");
        //分离属性和门限
//        int toks_cnt = attrAll.length;
        for (int index = 0; index < attrAll.length; index++) {
            int i, k, n;
            //临时变量
            attr = attrAll[index];
            //处理属性
            if (!attr.contains("of")) {
                treePolicys.add(baseNode(1, attr));
            }
            //处理门限 2of3
            else {
                TreePolicy treePolicy;
                // 剥离 k of n
                String[] k_n = attr.split("of");
                k = Integer.parseInt(k_n[0]);//必要属性个数，2of3，如2
                n = Integer.parseInt(k_n[1]);//属性个数，2of3，如3
                //对门限值特殊情况的报错
                if (k < 1) {
                    System.out.println("error parsing " + s + ": trivially satisfied operator " + attr);
                    return null;
                } else if (k > n) {
                    System.out.println("error parsing " + s + ": unsatisfiable operator " + attr);
                    return null;
                } else if (n == 1) {
                    System.out.println("error parsing " + s + ": indentity operator " + attr);
                    return null;
                } else if (n > treePolicys.size()) {
                    System.out.println("error parsing " + s + ": stack underflow at " + attr);
                    return null;
                }

                //构造门限节点 pop n things and fill in children
                treePolicy = baseNode(k, null);
                treePolicy.children = new TreePolicy[n];

                for (i = n - 1; i >= 0; i--)
                    treePolicy.children[i] = treePolicys.remove(treePolicys.size() - 1);

                /* push result */
                treePolicys.add(treePolicy);
            }
        }
        //对策略树特殊情况的报错
        if (treePolicys.size() > 1) {
            System.out.println("error parsing " + s
                    + ": extra node left on the stack");
            return null;
        } else if (treePolicys.size() < 1) {
            System.out.println("error parsing " + s + ": empty policy");
            return null;
        }

        root = treePolicys.get(0);
        return root;
    }

    /**
     * 构造一个策略树的节点
     */
    public static TreePolicy baseNode(int k, String s) {
        TreePolicy p = new TreePolicy();
        p.k = k;
        if (!(s == null))
            p.attr = s;
        else
            p.attr = null;
        p.q = null;

        return p;
    }

    /**
     * 填充策略树
     */
    public static void fillPolicy(TreePolicy p, PK pub, Element e) throws NoSuchAlgorithmException {
        int i;
        Element r, t, h;
        Pairing pairing = pub.pairing;
        r = pairing.getZr().newElement();
        t = pairing.getZr().newElement();
        h = pairing.getG2().newElement();

        p.q = randPoly(p.k - 1, e);

        if (p.children == null || p.children.length == 0) {
            p.c = pairing.getG1().newElement();
            p.cp = pairing.getG2().newElement();

            elementFromString(h, p.attr);
            p.c = pub.g.duplicate();
            ;
            p.c.powZn(p.q.coef[0]);
            p.cp = h.duplicate();
            p.cp.powZn(p.q.coef[0]);
        } else {
            for (i = 0; i < p.children.length; i++) {
                r.set(i + 1);
                evalPoly(t, p.q, r);
                fillPolicy(p.children[i], pub, t);
            }
        }
    }

    /**
     * 随机策略
     */
    public static Polynomial randPoly(int deg, Element zeroVal) {
        int i;
        Polynomial q = new Polynomial();
        q.deg = deg;
        q.coef = new Element[deg + 1];

        for (i = 0; i < deg + 1; i++)
            q.coef[i] = zeroVal.duplicate();

        q.coef[0].set(zeroVal);

        for (i = 1; i < deg + 1; i++)
            q.coef[i].setToRandom();

        return q;
    }

    /**
     * 将单个属性attr哈希到G_1群上
     */
    public static void elementFromString(Element h, String s) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(s.getBytes());
        h.setFromHash(digest, 0, digest.length);
    }

    /**
     * 评价策略树
     */
    public static void evalPoly(Element r, Polynomial q, Element x) {
        int i;
        Element s, t;

        s = r.duplicate();
        t = r.duplicate();

        r.setToZero();
        t.setToOne();

        for (i = 0; i < q.deg + 1; i++) {
            /* r += q->coef[i] * t */
            s = q.coef[i].duplicate();
            s.mul(t);
            r.add(s);

            /* t *= x */
            t.mul(x);
        }
    }

    /**---------------------------encrypt加密阶段用到的函数---------------------------**/
    /**
     * 加密 文件的读取和保存工作
     *
     * @param pkfile    PK本地保存的路径和文件名
     * @param policy    访问树策略
     * @param inputfile 明文本地保存的路径和文件名
     * @param encfile   加密后密文的本地保存的路径和文件名，密文分两个部分，前半部分是用了AES方法加密的文件，后半部分是访问策略（访问树）
     */
    public static void encrypt(String pkfile, String policy, String inputfile, String encfile) throws Exception {
        byte[] pk_byte;//PK
        PK pk = new PK();
        //密文实体，包含访问树
        Ciphertext cph;
        CiphertextAndKey keyCph;
        byte[] messageBuf;//明文的字节数组
        byte[] ciphertextBuf;//密文的字节数组
        byte[] aesBuf;//密文的字节数组
        Element m;//表示Zr的随机值

        //读取本地的PK文件
        pk_byte = FileOperation.file2byte(pkfile);
        pk = SerializeUtils.unserializePK(pk_byte);
        //计算Ciphertext实体
        keyCph = encrypt(pk, policy);
        cph = keyCph.cph;
        m = keyCph.key;
        System.out.println("AES加密文件的种子：" + m.toString());

        if (cph == null) {
            System.err.println("加密过程中出现错误！");
            System.exit(0);
        }

        //从本地读取明文文件
        messageBuf = FileOperation.file2byte(inputfile);
        //先将明文使用AES方法进行加密
        aesBuf = AESCoder.encrypt(m.toBytes(), messageBuf);
        //序列化密文
        ciphertextBuf = SerializeUtils.serializeCiphertext(cph);
        // 将密文保存到本地
        FileOperation.Ciphertext2File(encfile, ciphertextBuf, aesBuf);
        System.out.println("密文成功生成，已保存到本地！");
    }

    /**
     * 加密密文和key 具体计算密文
     * Pick a random group element and encrypt it under the specified access
     * policy. The resulting ciphertext is returned and the Element given as an
     * argument (which need not be initialized) is set to the random group
     * element.
     * <p>
     * After using this function, it is normal to extract the random data in m
     * using the pbc functions element_length_in_bytes and element_to_bytes and
     * use it as a key for hybrid encryption.
     * <p>
     * The policy is specified as a simple string which encodes a postorder
     * traversal of threshold tree defining the access policy. As an example,
     * <p>
     * "foo bar fim 2of3 baf 1of2"
     * <p>
     * specifies a policy with two threshold gates and four leaves. It is not
     * possible to specify an attribute with whitespace in it (although "_" is
     * allowed).
     * <p>
     * Numerical attributes and any other fancy stuff are not supported.
     * <p>
     * Returns null if an error occured, in which case a description can be
     * retrieved by calling bswabe_error().
     */
    public static CiphertextAndKey encrypt(PK pk, String policy) throws Exception {
        CiphertextAndKey keyCph = new CiphertextAndKey();
        Ciphertext cph = new Ciphertext();
        Element s;//表示Zr的随机值
        Element m;//表示Zr的随机值

        //计算Ciphertext实体
        Pairing pairing = pk.pairing;
        s = pairing.getZr().newRandomElement();
        m = pairing.getGT().newRandomElement();
        cph.cs = pairing.getGT().newElement();
        cph.c = pairing.getG1().newElement();
        cph.p = parsePolicyPostfix(policy);//将字符串表示的策略转换成TreePolicy类实体

        //计算cph.cs=`C=M*e(g,g)^as
        cph.cs = pk.e_g_ga.duplicate();//pk.e_g_ga=e(g,g)^a
        cph.cs.powZn(s); // num_exps++
        cph.cs.mul(m); // num_muls++
        //计算cph.c=C=h^s
        cph.c = pk.h.duplicate();
        cph.c.powZn(s); // num_exps++
        //填充策略树
        fillPolicy(cph.p, pk, s);

        keyCph.cph = cph;
        keyCph.key = m;

        return keyCph;
    }

    /**---------------------------decrypt解密阶段用到的函数---------------------------**/
    /**
     * 解密 文件的读取和保存工作
     *
     * @param pkfile  PK本地保存的路径和文件名
     * @param skfile  SK本地保存的路径和文件名
     * @param encfile 加密后密文的本地保存的路径和文件名
     * @param decfile 解密后明文的本地保存的路径和文件名
     */
    public static void decrypt(String pkfile, String skfile, String encfile, String decfile) throws Exception {
        byte[] aesBuf, cphBuf;//AES文件，密文文件
        byte[] plt;//明文
        byte[] sk_byte;//私钥
        byte[] pk_byte;//公钥
        byte[][] tmp;//用于临时储存AES文件，密文文件
        Ciphertext cph;//密文策略
        SK sk;
        PK pk;

        //读取本地的PK文件
        pk_byte = FileOperation.file2byte(pkfile);
        pk = SerializeUtils.unserializePK(pk_byte);

        //读取本地保存的密文
        tmp = FileOperation.readCiphertextFile(encfile);
        aesBuf = tmp[0];
        cphBuf = tmp[1];
        cph = SerializeUtils.unserializeCiphertext(pk, cphBuf);

        //从本地获取SK文件
        sk_byte = FileOperation.file2byte(skfile);
        sk = SerializeUtils.unserializeSK(pk, sk_byte);
        //检测SK是否满足密文中的访问策略
        ElementBoolean beb = decrypt(pk, sk, cph);

        if (beb.b) {
            System.out.println("解密后计算出AES种子：" + beb.e.toString());
            plt = AESCoder.decrypt(beb.e.toBytes(), aesBuf);
            FileOperation.byte2File(plt, decfile);
            System.out.println("文件解密成功，解密后的文件已保存到本地！ ");
        } else {
            System.err.println("解密失败，秘钥中的属性不满足密文中的访问策略！");
        }
    }

    /**
     * 解密 具体计算密文
     * Decrypt the specified ciphertext using the given private key, filling in
     * the provided element m (which need not be initialized) with the result.
     * <p>
     * Returns true if decryption succeeded, false if this key does not satisfy
     * the policy of the ciphertext (in which case m is unaltered).
     */
    public static ElementBoolean decrypt(PK pk, SK sk, Ciphertext cph) {
        Element t;
        Element m;
        ElementBoolean beb = new ElementBoolean();

        m = pk.pairing.getGT().newElement();
        t = pk.pairing.getGT().newElement();

        //检测SK是否满足密文中的访问策略
        checkSatisfy(cph.p, sk);
        //不满足的情况
        if (!cph.p.satisfiable) {
//            System.err.println("解密失败，秘钥中的属性不满足密文中的访问策略！");
            beb.e = null;
            beb.b = false;
            return beb;
        }
        //满足的情况
        pickSatisfyMinLeaves(cph.p, sk);

        decFlatten(t, cph.p, sk, pk);
        //计算cph.cs=`C=M*e(g,g)^as
        m = cph.cs.duplicate();
        m.mul(t); // num_muls++ ，此时t=e(g,g)^rs
        //cph.c=C=h^s , sk.d=D=(g^r*g^a)^(1/b)
        t = pk.pairing.pairing(cph.c, sk.d);//此时t=e(h^s,(g^r*g^a)^(1/b))
        t.invert();//求倒数，此时t=1/(e(h^s,(g^r*g^a)^(1/b)))
        m.mul(t); // num_muls++

        beb.e = m;//m=M
        beb.b = true;

        return beb;
    }

    /**
     * 检测SK是否满足密文中的访问策略
     */
    public static void checkSatisfy(TreePolicy p, SK sk) {
        int i, l;
        String prvAttr;

        p.satisfiable = false;
        if (p.children == null || p.children.length == 0) {
            for (i = 0; i < sk.comps.size(); i++) {
                prvAttr = sk.comps.get(i).attr;
                // System.out.println("prvAtt:" + prvAttr);
                // System.out.println("p.attr" + p.attr);
                if (prvAttr.compareTo(p.attr) == 0) {
                    // System.out.println("=staisfy=");
                    p.satisfiable = true;
                    p.attri = i;
                    break;
                }
            }
        } else {
            for (i = 0; i < p.children.length; i++)
                checkSatisfy(p.children[i], sk);

            l = 0;
            for (i = 0; i < p.children.length; i++)
                if (p.children[i].satisfiable)
                    l++;

            if (l >= p.k)
                p.satisfiable = true;
        }
    }

    /**
     * 选择满足条件的最小的叶子
     */
    public static void pickSatisfyMinLeaves(TreePolicy p, SK sk) {
        int i, k, l, c_i;
        int len;
        ArrayList<Integer> c = new ArrayList<Integer>();

        if (p.children == null || p.children.length == 0)
            p.min_leaves = 1;
        else {
            len = p.children.length;
            for (i = 0; i < len; i++)
                if (p.children[i].satisfiable)
                    pickSatisfyMinLeaves(p.children[i], sk);

            for (i = 0; i < len; i++)
                c.add(new Integer(i));

            Collections.sort(c, new IntegerComparator(p));

            p.satl = new ArrayList<Integer>();
            p.min_leaves = 0;
            l = 0;

            for (i = 0; i < len && l < p.k; i++) {
                c_i = c.get(i).intValue(); /* c[i] */
                if (p.children[c_i].satisfiable) {
                    l++;
                    p.min_leaves += p.children[c_i].min_leaves;
                    k = c_i + 1;
                    p.satl.add(new Integer(k));
                }
            }
        }
    }

    /**
     * 排序用
     */
    public static class IntegerComparator implements Comparator<Integer> {
        TreePolicy policy;

        public IntegerComparator(TreePolicy p) {
            this.policy = p;
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            int k, l;

            k = policy.children[o1.intValue()].min_leaves;
            l = policy.children[o2.intValue()].min_leaves;

            return k < l ? -1 : k == l ? 0 : 1;
        }
    }

    /**
     * 解密根节点
     */
    public static void decFlatten(Element r, TreePolicy p, SK prv, PK pub) {
        Element one;
        one = pub.pairing.getZr().newElement();
        one.setToOne();
        r.setToOne();

        decNodeFlatten(r, one, p, prv, pub);
    }

    /**
     * 解密非叶子节点
     */
    public static void decNodeFlatten(Element r, Element exp, TreePolicy p, SK prv, PK pub) {
        if (p.children == null || p.children.length == 0)
            decLeafFlatten(r, exp, p, prv, pub);
        else
            decInternalFlatten(r, exp, p, prv, pub);
    }

    /**
     * 解密叶子节点
     */
    public static void decLeafFlatten(Element r, Element exp, TreePolicy p, SK prv, PK pub) {
        SKComp c;
        Element s, t;

        c = prv.comps.get(p.attri);

        s = pub.pairing.getGT().newElement();
        t = pub.pairing.getGT().newElement();

        s = pub.pairing.pairing(p.c, c.d); /* num_pairings++; */
        t = pub.pairing.pairing(p.cp, c.dj); /* num_pairings++; */
        t.invert();
        s.mul(t); /* num_muls++; */
        s.powZn(exp); /* num_exps++; */

        r.mul(s); /* num_muls++; */
    }

    /**
     * 解密内部节点
     */
    public static void decInternalFlatten(Element r, Element exp, TreePolicy p, SK prv, PK pub) {
        int i;
        Element t, expnew;

        t = pub.pairing.getZr().newElement();
        expnew = pub.pairing.getZr().newElement();

        for (i = 0; i < p.satl.size(); i++) {
            lagrangeCoef(t, p.satl, (p.satl.get(i)).intValue());
            expnew = exp.duplicate();
            expnew.mul(t);
            decNodeFlatten(r, expnew, p.children[p.satl.get(i) - 1], prv, pub);
        }
    }

    public static void lagrangeCoef(Element r, ArrayList<Integer> s, int i) {
        int j, k;
        Element t;
        t = r.duplicate();

        r.setToOne();
        for (k = 0; k < s.size(); k++) {
            j = s.get(k).intValue();
            if (j == i)
                continue;
            t.set(-j);
            r.mul(t); /* num_muls++; */
            t.set(i - j);
            t.invert();
            r.mul(t); /* num_muls++; */
        }
    }

    /**
     * 测试用
     */
    public static void main(String[] args) {
        String attr = "objectClass:inetOrgPerson objectClass:organizationalPerson "
                + "sn:student2 cn:student2 uid:student2 userPassword:student2 "
                + "ou:idp o:computer mail:student2@sdu.edu.cn title:student";
        String[] arr = parseAttribute(attr);
        for (int i = 0; i < arr.length; i++)
            System.out.println(arr[i]);
    }

    /**
     * 将字节数组byte[]转成16进制字符串
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            String hex = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            stringBuilder.append(hex + " ");
        }
        return stringBuilder.toString();
    }

}
