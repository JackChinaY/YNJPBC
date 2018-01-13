package ABE.CPABE;

import ABE.CPABE.Entity.*;
import com.sun.istack.internal.NotNull;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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
        System.out.println("用户属性个数:" + sk.comps.size());
        System.out.println("用户属性:" + attr_str);
    }

    /**
     * 生成私钥 具体计算私钥
     */
    private static SK keygen(PK pk, MK mk, String[] attrs) throws NoSuchAlgorithmException {
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
            //赋值
            comp.attr = attrs[i];
            comp.d = pairing.getG2().newElement();
            comp.dj = pairing.getG1().newElement();
            h_rj = pairing.getG2().newElement();
            rj = pairing.getZr().newRandomElement();
            //将单个属性attr哈希到G_1群上
            h_rj = elementFromString(pk, comp.attr);
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
    private static String[] parseAttribute(String str) {
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
        //加密错误
        if (cph == null) {
            System.err.println("加密过程中出现错误！");
            System.exit(0);
        }
        //加密成功
        System.out.println("AES加密文件的种子：" + m.toString());
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
     * Pick a random group element and encrypt it under the specified access policy. The resulting ciphertext is returned and the Element given as an argument (which need not be initialized) is set to the random group element.
     * <p>
     * After using this function, it is normal to extract the random data in m using the pbc functions element_length_in_bytes and element_to_bytes and use it as a key for hybrid encryption.
     * <p>
     * The policy is specified as a simple string which encodes a postorder traversal of threshold tree defining the access policy. As an example,
     * <p>
     * "foo bar fim 2of3 baf 1of2"
     * <p>
     * specifies a policy with two threshold gates and four leaves. It is not possible to specify an attribute with whitespace in it (although "_" is allowed).
     * <p>
     * Numerical attributes and any other fancy stuff are not supported.
     * <p>
     * Returns null if an error occured, in which case a description can be retrieved by calling bswabe_error().
     */
    public static CiphertextAndKey encrypt(PK pk, String policy) throws Exception {
        CiphertextAndKey keyCph = new CiphertextAndKey();//密文和AES种子
        Ciphertext cph = new Ciphertext();//密文
        Element s;//表示Zr的随机值
        Element m;//表示Zr的随机值，AES种子

        //计算Ciphertext实体
        Pairing pairing = pk.pairing;
        s = pairing.getZr().newRandomElement();
        m = pairing.getGT().newRandomElement();
        cph.cs = pairing.getGT().newElement();
        cph.c = pairing.getG1().newElement();
        cph.treePolicy = parseTreePolicy(policy);//将字符串表示的策略转换成TreePolicy类实体

        //计算cph.cs=`C=M*e(g,g)^as
        cph.cs = pk.e_g_ga.duplicate();//pk.e_g_ga=e(g,g)^a
        cph.cs.powZn(s); // num_exps++
        cph.cs.mul(m); // num_muls++
        //计算cph.c=C=h^s
        cph.c = pk.h.duplicate();
        cph.c.powZn(s); // num_exps++
        //向策略树中添加多项式
        addPolynomialToTreePolicy(cph.treePolicy, pk, s);

        keyCph.cph = cph;
        keyCph.key = m;

        return keyCph;
    }

    /**
     * 将字符串表示的策略转换成TreePolicy类实体，此时对于访问树中各个节点，叶子节点含门限值k、和属性attr，非叶子节点只包含门限值k
     */
    private static TreePolicy parseTreePolicy(String policy) {
        String[] attrAll;//属性集合
        String attr;//临时单个属性变量
        ArrayList<TreePolicy> treePolicys = new ArrayList<TreePolicy>();
        TreePolicy root;
        //将字符串按空格分割成一个个元素，如"sn:student2 cn:student2 uid:student2 2of3"，分给后得到{"sn:student2","cn:student2","uid:student2","2of3"}
        attrAll = policy.split(" ");
        //输出必要属性
        System.out.println("密文中访问树的属性个数：" + (attrAll.length - 1));
        System.out.print("属性：");
        for (int i = 0; i < attrAll.length - 1; i++) {
            System.out.print(attrAll[i] + "  ");
        }
        System.out.println();
        System.out.println("门限规则：" + attrAll[attrAll.length - 1]);

        //分离属性和门限
//        int toks_cnt = attrAll.length;
        for (int index = 0; index < attrAll.length; index++) {
            int i, k, n;
            //临时变量
            attr = attrAll[index];
            //处理属性
            if (!attr.contains("of")) {
                treePolicys.add(createOneNode(1, attr));
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
                    System.err.println("属性转换错误： parsing " + policy + ": trivially satisfied operator " + attr);
                    return null;
                } else if (k > n) {
                    System.err.println("属性转换错误：parsing " + policy + ": unsatisfiable operator " + attr);
                    return null;
                } else if (n == 1) {
                    System.err.println("属性转换错误： parsing " + policy + ": indentity operator " + attr);
                    return null;
                } else if (n > treePolicys.size()) {
                    System.err.println("属性转换错误： parsing " + policy + ": stack underflow at " + attr);
                    return null;
                }
                //构造门限节点 pop n things and fill in children
                treePolicy = createOneNode(k, null);
                treePolicy.children = new TreePolicy[n];
                //依次将属性各节点插入到门限节点下
                for (i = n - 1; i >= 0; i--) treePolicy.children[i] = treePolicys.remove(treePolicys.size() - 1);
                //使得门限节点成为根节点
                treePolicys.add(treePolicy);
            }
        }
        //对策略树特殊情况的报错
        if (treePolicys.size() > 1) {
            System.err.println("属性转换错误： " + policy + ": extra node left on the stack");
            return null;
        } else if (treePolicys.size() < 1) {
            System.err.println("属性转换错误： " + policy + ": empty policy");
            return null;
        }

        root = treePolicys.get(0);
        return root;
    }

    /**
     * 构造一个策略树的节点
     *
     * @param k         门限值  k of n
     * @param attribute 属性
     */
    private static TreePolicy createOneNode(int k, String attribute) {
        TreePolicy treePolicy = new TreePolicy();
        treePolicy.k = k;
        if (attribute != null) {
            treePolicy.attr = attribute;
        } else {
            treePolicy.attr = null;
        }
        treePolicy.polynomial = null;

        return treePolicy;
    }

    /**
     * 向策略树中各个节点添加多项式
     *
     * @param randomValue 表示Zr上的随机值，已有值，即根节点的随机值s，qR(0)=s
     */
    private static void addPolynomialToTreePolicy(TreePolicy treePolicy, PK pk, Element randomValue) throws NoSuchAlgorithmException {
        Element nodeID, temp, hash;//nodeID:节点顺序值, temp:中间临时变量, hash:Hash函数，负责哈希属性值
        Pairing pairing = pk.pairing;
        nodeID = pairing.getZr().newElement();
        temp = pairing.getZr().newElement();
        hash = pairing.getG2().newElement();
        //产生一个k-1阶的随机多项式，k表示门限值，先为根节点产生，再为其他节点产生
        treePolicy.polynomial = createRandomPolynomial(treePolicy.k - 1, randomValue);
        //如果访问树没有叶节点，处理叶子节点
        if (treePolicy.children == null || treePolicy.children.length == 0) {
            treePolicy.c = pairing.getG1().newElement();
            treePolicy.cp = pairing.getG2().newElement();
            //将单个属性attr哈希到G_1或G_2群上
            hash = elementFromString(pk, treePolicy.attr);
            //计算Cy=g^(qy(0))
            treePolicy.c = pk.g.duplicate();
            treePolicy.c.powZn(treePolicy.polynomial.coef[0]);
            //计算Cy`=H(att(y))^(qy(0))
            treePolicy.cp = hash.duplicate();
            treePolicy.cp.powZn(treePolicy.polynomial.coef[0]);
        }
        //处理非叶子节点
        else {
            for (int i = 0; i < treePolicy.children.length; i++) {
                nodeID.set(i + 1);
                //计算多项式，temp是Zr的值，未赋值，空值；polynomial是节点的多项式；nodeNum是Zr的值，赋值子节点的顺序值，如1/2/3
                computePolynomial(temp, treePolicy.polynomial, nodeID);
                //递归循环，先从
                addPolynomialToTreePolicy(treePolicy.children[i], pk, temp);
            }
        }
    }

    /**
     * 对于根节点，deg=k-1,k为门限值（如3of4，k=3），coef系数是k个，比deg多一个，如q(x)=A*x`3+B*x`2+C*x`1+D,polynomial.coef[deg + 1]集合中分别存放着A、B、C、D。
     * 对于叶子节点，deg=0,，coef系数是1个，polynomial.coef[1]集合中存放着Qx(0)=QR(x)=A*x`3+B*x`2+C*x`1+D的值，如Q1(0)=QR(1)=A*1`3+B*1`2+C*1`1+D=A+B+C+D
     * 对于每个节点产生随机多项式，系数的个数比阶数多1，如q(x)=A*x`3+B*x`2+C*x`1+D,ABCD均为系数，阶数为3，系数的个数为4
     * 对于根节点，QR(x)=A*x`3+B*x`2+C*x`1+D,QR(0)=A*0`3+B*0`2+C*0`1+D=D=s,根节点的Polynomial实体类中，阶数为3，系数的个数为4，系数分别为A、B、C、D，其中A、B、C、D均为随机值，其中D也即s
     * 对于叶子节点，Qx(0)=QR(x)=A*x`3+B*x`2+C*x`1+D,x表示对于同一根节点的所有孩子节点的顺序值，从1开始，比如孩子有5个，那么顺序值就是1、2、3、4、5，
     * 对于第1个叶子节点，其Polynomial实体类中，阶数为1，系数的个数为1，系数为Q1(0)=QR(1)=A*1`3+B*1`2+C*1`1+D=A+B+C+D
     * 对于第2个叶子节点，其Polynomial实体类中，阶数为1，系数的个数为1，系数为Q2(0)=QR(2)=A*2`3+B*2`2+C*2`1+D=8A+4B+2C+D
     *
     * @param deg         多项式的阶 deg=k-1,k为门限值; coef系数是k个，比deg多一个，coef系数中，第一个是都是相同的，都是根节点的随机值qR(0)=s,其他系数都是随机值
     * @param randomValue Zr的随机值 s qR(0)=s
     */
    private static Polynomial createRandomPolynomial(int deg, Element randomValue) {
        Polynomial polynomial = new Polynomial();
        polynomial.deg = deg;//阶
        polynomial.coef = new Element[deg + 1];//系数 Ax`3+Bx`2+Cx`1+D ,ABCD均为系数
        //初始化
        for (int i = 0; i < deg + 1; i++)
            polynomial.coef[i] = randomValue.duplicate();
        //第一个元素赋值s
        polynomial.coef[0].set(randomValue);
        //剩余元素赋值随机值
        for (int i = 1; i < deg + 1; i++)
            polynomial.coef[i].setToRandom();
        return polynomial;
    }

    /**
     * 计算各个叶子节点Qx(0)的值
     * 对于叶子节点，Qx(0)=QR(x)=A*x`3+B*x`2+C*x`1+D,x表示对于同一根节点的所有孩子节点的顺序值，从1开始，比如孩子有5个，那么顺序值就是1、2、3、4、5，
     * 对于第1个叶子节点，其Polynomial实体类中，阶数为1，系数的个数为1，系数为Q1(0)=QR(1)=A*1`3+B*1`2+C*1`1+D=A+B+C+D；
     * 对于第2个叶子节点，其Polynomial实体类中，阶数为1，系数的个数为1，系数为Q2(0)=QR(2)=A*2`3+B*2`2+C*2`1+D=8A+4B+2C+D；
     * 对于第3个叶子节点，其Polynomial实体类中，阶数为1，系数的个数为1，系数为Q3(0)=QR(3)=A*3`3+B*3`2+C*3`1+D=27A+9B+3C+D。
     *
     * @param temp       temp是Zr的值，未赋值，空值
     * @param polynomial 节点的多项式
     * @param nodeID     是Zr的值，值为子节点的顺序值，如1、2、3、4、5
     */
    private static void computePolynomial(Element temp, Polynomial polynomial, Element nodeID) {
        Element coef, num;//是Zr的值

        coef = temp.duplicate();//相当于A*3`3
        num = temp.duplicate();//相当于3`3

        temp.setToZero();//0
        num.setToOne();//1
        //循环累加多项式中的各个子项，如Q3(0)=QR(3)=A*3`3+B*3`2+C*3`1+D=27A+9B+3C+D，其中的子项A*3`3、B*3`2、C*3`1、D
        for (int i = 0; i < polynomial.deg + 1; i++) {
            // temp += polynomial.coef[i] * num
            coef = polynomial.coef[i].duplicate();
            coef.mul(num);
            temp.add(coef);

            //对叶子节点的顺序值做阶乘，如Q3(0)=QR(3)=A*3`3+B*3`2+C*3`1+D=27A+9B+3C+D，其中的3
            num.mul(nodeID);
        }
    }

    /**
     * 将单个属性attr哈希到G_1或G_2群上
     */
    private static Element elementFromString(PK pk, String attr) throws NoSuchAlgorithmException {
        Pairing pairing = pk.pairing;
        Element hash = pairing.getG2().newElement();
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(attr.getBytes());
        hash.setFromHash(digest, 0, digest.length);
        return hash;
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

        //读取本地的CT密文文件
        tmp = FileOperation.readCiphertextFile(encfile);
        aesBuf = tmp[0];
        cphBuf = tmp[1];
        cph = SerializeUtils.unserializeCiphertext(pk, cphBuf);

        //读取本地的SK文件
        sk_byte = FileOperation.file2byte(skfile);
        sk = SerializeUtils.unserializeSK(pk, sk_byte);

        //检测SK是否满足密文中的访问策略
        ElementBoolean elementBoolean = decrypt(pk, sk, cph);

        if (elementBoolean.flag) {
            System.out.println("解密后计算出AES种子：" + elementBoolean.seed.toString());
            plt = AESCoder.decrypt(elementBoolean.seed.toBytes(), aesBuf);
            FileOperation.byte2File(plt, decfile);
            System.out.println("文件解密成功，解密后的文件已保存到本地！ ");
        } else {
            System.err.println("解密失败，秘钥中的属性不满足密文中的访问策略！");
        }
    }

    /**
     * 解密 具体计算密文
     * Decrypt the specified ciphertext using the given private key, filling in the provided element m (which need not be initialized) with the result.
     * <p>
     * Returns true if decryption succeeded, false if this key does not satisfy the policy of the ciphertext (in which case m is unaltered).
     */
    private static ElementBoolean decrypt(PK pk, SK sk, Ciphertext ciphertext) {
        Element t;
        Element m;
        ElementBoolean elementBoolean = new ElementBoolean();

        m = pk.pairing.getGT().newElement();
        t = pk.pairing.getGT().newElement();

        //先检查属性是否满足，即检查私钥SK中的属性是否满足密文中的访问策略树的门限要求
        ciphertext.treePolicy = checkSKAttributesSatisfy(ciphertext.treePolicy, sk);
        //不满足的情况
        if (!ciphertext.treePolicy.satisfiable) {
//            System.err.println("解密失败，秘钥中的属性不满足密文中的访问策略！");
            elementBoolean.seed = null;
            elementBoolean.flag = false;
            return elementBoolean;
        }
        //属性满足的情况情况下，开始计算多项式的值
        else {
            ciphertext.treePolicy = pickSatisfyMinLeaves(ciphertext.treePolicy, sk);
            decryptNode(t, ciphertext.treePolicy, sk, pk);//返回t的值，t也是解密根节点的值
            //cph.cs=`C=M*e(g,g)^as
            m = ciphertext.cs.duplicate();
            m.mul(t); //此时t=e(g,g)^rs,m=M*e(g,g)^as*e(g,g)^rs   num_muls++
            //cph.c=C=h^s , sk.d=D=(g^r*g^a)^(1/b)
            t = pk.pairing.pairing(ciphertext.c, sk.d);//此时t=e(h^s,(g^r*g^a)^(1/b))
            t.invert();//求倒数，此时t=1/(e(h^s,(g^r*g^a)^(1/b)))
            m.mul(t); //此时m=(M*e(g,g)^as*e(g,g)^rs)/(e(h^s,(g^r*g^a)^(1/b)))=M   num_muls++

            elementBoolean.seed = m;//m=M
            elementBoolean.flag = true;
            return elementBoolean;
        }
    }

    /**
     * 检查私钥SK中的属性是否满足密文中的访问策略树的门限要求
     */
    private static TreePolicy checkSKAttributesSatisfy(TreePolicy treePolicy, SK sk) {
        String skAttr;

        treePolicy.satisfiable = false;
        //比对叶子节点
        if (treePolicy.children == null || treePolicy.children.length == 0) {
            System.out.print("检查访问树中属性：" + treePolicy.attr);
            for (int i = 0; i < sk.comps.size(); i++) {
                skAttr = sk.comps.get(i).attr;
//                System.out.println("用户SK中的属性：" + skAttr);
                //比对字符串，如果相对返回0
                if (skAttr.compareTo(treePolicy.attr) == 0) {
                    System.out.println("，结果：该用户满足！");
                    treePolicy.satisfiable = true;//如果该叶节点满足，就将叶节点可满足性置为true，并且标出该叶子节点是真正参与了运算
                    treePolicy.decryptAttributeValue = i;
                    break;
                }
            }
        }
        //比对非叶子节点
        else {
            //递归比对叶子节点
            for (int i = 0; i < treePolicy.children.length; i++)
                checkSKAttributesSatisfy(treePolicy.children[i], sk);
            //统计一共有多少个满足条件的叶子节点
            int l = 0;
            for (int i = 0; i < treePolicy.children.length; i++)
                if (treePolicy.children[i].satisfiable)
                    l++;
            //如果满足条件的叶子节点的数量大于整个访问树的门限值，则根节点可满足性置为true
            if (l >= treePolicy.k)
                treePolicy.satisfiable = true;
            System.out.print("用户满足条件的属性个数：" + l);
            System.out.println("，访问树的门限值：" + treePolicy.k);
        }
        return treePolicy;
    }

    /**
     * 标出访问树中哪些叶子节点是真正参与了属性配对，便于接下来计算多项式算出AES的种子
     */
    private static TreePolicy pickSatisfyMinLeaves(TreePolicy treePolicy, SK sk) {
        ArrayList<Integer> leafNodesList = new ArrayList<Integer>();
        //处理叶子节点
        if (treePolicy.children == null || treePolicy.children.length == 0) {
            treePolicy.min_leaves = 1;//真正参与了运算的叶子节点，就将其min_leaves = 1
        }
        //处理非叶子节点
        else {
            int len = treePolicy.children.length;
            //递归操作所有叶子节点，标出哪些叶子节点是真正参与了运算
            for (int i = 0; i < len; i++) {
                if (treePolicy.children[i].satisfiable) {
                    pickSatisfyMinLeaves(treePolicy.children[i], sk);
                }
            }
            //创建len个集合，数量为所有叶子节点数
            for (int i = 0; i < len; i++) {
                leafNodesList.add(i);
            }
            //将所有节点标上序号
            Collections.sort(leafNodesList, new IntegerComparator(treePolicy));
            System.out.println("输出排序");
//            for (int x:leafNodesList) {
//                System.out.println(x);
//            }
            treePolicy.minAttrsList = new ArrayList<Integer>();
            treePolicy.min_leaves = 0;//根节点的最小叶子数先置0
            //处理根节点中的叶子节点，保存哪些属性将会参与到解密
            for (int i = 0; i < len && i < treePolicy.k; i++) {
                int c_i = leafNodesList.get(i); // c[i]
                if (treePolicy.children[c_i].satisfiable) {
                    treePolicy.min_leaves += treePolicy.children[c_i].min_leaves;//累加计算得到根节点可解密的最小的属性个数
                    treePolicy.minAttrsList.add(c_i + 1);//将参与解密的属性的序号保存起来
                }
            }
        }
        return treePolicy;
    }

    /**
     * 给访问树中各个元素表示序号排序用
     */
    public static class IntegerComparator implements Comparator<Integer> {
        TreePolicy policy;

        public IntegerComparator(TreePolicy treePolicy) {
            this.policy = treePolicy;
        }

        /**
         * 若想得到正序，当前值大于后值，返回正整数，当前值小于后值，返回负整数，当前值等于后值，返回0
         * 若想得到倒序，当前值大于后值，返回负整数，当前值小于后值，返回正整数，当前值等于后值，返回0
         * 本例想得到正序
         *
         * @param obj1 前值
         * @param obj2 后值
         * @return
         */
        @Override
        public int compare(Integer obj1, Integer obj2) {
            int k, l;
            k = policy.children[obj1].min_leaves;
            l = policy.children[obj2].min_leaves;
            return k < l ? -1 : k == l ? 0 : 1;//如果k < l，则返回-1，否则再比较k == l，如果k == l，则输出0，否则输出1
        }
    }

    /**
     * 解密节点
     */
    private static void decryptNode(Element elementGT_1, TreePolicy treePolicy, SK sk, PK pk) {
        Element elementZr_1;
        elementZr_1 = pk.pairing.getZr().newElement();
        elementZr_1.setToOne();
        elementGT_1.setToOne();//GT类型，1
        decryptLeafNodeAndNoLeafNode(elementGT_1, elementZr_1, treePolicy, sk, pk);
    }

    /**
     * 解密叶子节点和非叶子节点，先从根节点开始，根节点里面包含递归求解叶子节点
     */
    private static void decryptLeafNodeAndNoLeafNode(Element elementGT_1, Element elementZr_1, TreePolicy treePolicy, SK sk, PK pk) {
        if (treePolicy.children == null || treePolicy.children.length == 0)
            decryptLeafNode(elementGT_1, elementZr_1, treePolicy, sk, pk);
        else
            decryptNoLeafNode(elementGT_1, treePolicy, sk, pk);
    }

    /**
     * 解密非叶子节点，比如根节点
     */
    private static void decryptNoLeafNode(Element elementGT_1, TreePolicy treePolicy, SK sk, PK pk) {
        Element elementZrCoef = pk.pairing.getZr().newElement();
        //用于解密的最小范围的属性的循环
        for (int i = 0; i < treePolicy.minAttrsList.size(); i++) {
            //求解各个叶子节点的拉格朗日插系数 求deta(0) (x-j)/(i-j)
            elementZrCoef = lagrangeCoef(elementZrCoef, treePolicy.minAttrsList, treePolicy.minAttrsList.get(i));
            //求解各个叶子节点的DecryptNode(CT,SK,x)
            decryptLeafNodeAndNoLeafNode(elementGT_1, elementZrCoef, treePolicy.children[treePolicy.minAttrsList.get(i) - 1], sk, pk);
        }
    }

    /**
     * 解密叶子节点
     */
    private static void decryptLeafNode(Element elementGT_1, Element elementZrCoef, TreePolicy treePolicy, SK sk, PK pk) {
        SKComp skComp;
        Element s, t;

        skComp = sk.comps.get(treePolicy.decryptAttributeValue);

        s = pk.pairing.getGT().newElement();
        t = pk.pairing.getGT().newElement();

        s = pk.pairing.pairing(treePolicy.c, skComp.d); //计算e(Di,Cx)  num_pairings++;
        t = pk.pairing.pairing(treePolicy.cp, skComp.dj); //计算e(Di`,Cx`)  num_pairings++;
        t.invert();//此时t=1/e(Di`,Cx`)
        s.mul(t); //此时s=e(Di,Cx)/e(Di`,Cx`)=Fz   num_muls++
        s.powZn(elementZrCoef); //此时s=(e(Di,Cx)/e(Di`,Cx`))^(拉格朗日系数，即deta i,Sx`(0) )  num_exps++

        elementGT_1.mul(s); //此时elementGT_1=e(Di,Cx)/e(Di`,Cx`)  num_muls++
    }


    /**
     * 求解拉格朗日插系数 求deta(0) (x-j)/(i-j)
     */
    private static Element lagrangeCoef(Element elementZr_1, ArrayList<Integer> minAttrsList, int minAttrID) {
        int j, k;
        Element elementZr_temp = elementZr_1.duplicate();

        elementZr_1.setToOne();
        //求循环乘
        for (k = 0; k < minAttrsList.size(); k++) {
            j = minAttrsList.get(k);
            //i==j时，跳过
            if (j == minAttrID)
                continue;
            //求(x-j)
            elementZr_temp.set(-j);//x-j=0-j
            elementZr_1.mul(elementZr_temp); // num_muls++
            //求(i-j)
            elementZr_temp.set(minAttrID - j);
            //求1/(i-j)
            elementZr_temp.invert();
            //求(x-j)/(i-j)
            elementZr_1.mul(elementZr_temp); //num_muls++
        }
        return elementZr_1;
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
