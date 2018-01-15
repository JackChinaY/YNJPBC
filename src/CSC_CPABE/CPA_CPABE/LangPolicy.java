package CSC_CPABE.CPA_CPABE;

import ABE.CPABE.AESCoder;
import CSC_CPABE.CPA_CPABE.Entity.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * 主函数调用的全部方法，各个过程具体执行的方法
 */
public class LangPolicy {
    //region /**---------------------------setup初始化阶段用到的函数---------------------------**/
    /**---------------------------setup初始化阶段用到的函数---------------------------**/
    /**
     * setup初始化 输出PK、MK
     *
     * @param attributes_U 属性全集
     */
    public static void setup(MK mk, PK pk, String attributes_U) {
        //给系统公钥赋值
        //仅对于双线性映射，要使用PBC包装并获得性能，必须设置配对工厂的使用PBC（可能）属性
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        //椭圆类型是Type A，生成 对称-质数阶-双线性群,即G1==G2，返回代数结构,代数结构包含：群、环、场（groups, rings and fields）
        Pairing pairing = PairingFactory.getPairing("a.properties");
        pk.pairing = pairing;
        //产生1个Zr的随机值 主密钥
        mk.x = pairing.getZr().newRandomElement().getImmutable();//随机值
        //给系统公钥赋值
        //公钥中的g
        pk.g = pairing.getG1().newRandomElement().getImmutable();//生成G1的生成元g
        //公钥中的g2
        pk.g2 = pairing.getG1().newRandomElement().getImmutable();
        //公钥中的Z
        Element g1 = pk.g.powZn(mk.x).getImmutable();
        pk.Z = pairing.pairing(g1, pk.g2).getImmutable();
        //将一个字符串解析成字符数组
        ArrayList<String> arrayList = parseString2ArrayList(attributes_U);
        pk.hList = new Element[arrayList.size() * 2];
        for (int i = 0; i < pk.hList.length; i++) {
            pk.hList[i] = pairing.getG1().newRandomElement().getImmutable();
        }
        System.out.println("系统主密钥 " + mk.toString());
        System.out.println("系统公钥 " + pk.toString());
        System.out.println("系统公钥中2L个G的随机值如下，共" + pk.hList.length + "个");
//        for (Element s : pk.hList) {
//            System.out.println(s);
//        }
    }

    /**
     * 将一个字符串解析成字符数组
     * 将一个字符串按分隔符分开，返回字符串类型的数组，如：将一个字符串，此字符串包含一个人的各种属性，分隔符是空格，最后返回一个数组，数组中包含各个小属性
     */
    private static ArrayList<String> parseString2ArrayList(String string) {
        ArrayList<String> arrayList = new ArrayList<String>();
        //构造一个用来解析str的StringTokenizer对象。java默认的分隔符是“空格”、“制表符(‘\t’)”、“换行符(‘\n’)”、“回车符(‘\r’)”
        StringTokenizer stringTokenizer = new StringTokenizer(string);//此处按空格划分每个属性
        String token;
        int len;
        //将各个属性添加到数组中
        while (stringTokenizer.hasMoreTokens()) {
            token = stringTokenizer.nextToken();
//            if (token.contains(":")) {
            arrayList.add(token);
//            } else {
//                System.out.println("用户的属性输入有误！");
//                System.exit(0);
//            }
        }
        System.out.print("属性集合中元素个数：" + arrayList.size());
        System.out.println("，元素为：" + arrayList);
        return arrayList;
    }

    /**
     * 求两个集合的并集，并且去重复，在attrList1后追加attrList2
     */
    private static ArrayList<String> unionArrayList(ArrayList<String> attrList1, ArrayList<String> attrList2) {
        ArrayList<String> list1 = attrList1;
        ArrayList<String> list2 = attrList2;
        list2.removeAll(list1);//去重复
        list1.addAll(list2);//并集
        return list1;
    }

    /**
     * 求两个集合的交集
     */
    private static ArrayList<String> intersectionArrayList(ArrayList<String> attrList1, ArrayList<String> attrList2) {
        ArrayList<String> list1 = attrList1;
        ArrayList<String> list2 = attrList2;
        list1.retainAll(list2);//并集
        return list1;
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
    //endregion

    //region /**---------------------------keygen生成私钥阶段用到的函数---------------------------**/
    /**---------------------------keygen生成私钥阶段用到的函数---------------------------**/
    /**
     * 生成私钥 文件的读取和保存工作 输入PK、MK、ATTR(用户属性)，输出SK
     *
     * @param mk            PK
     * @param pk            SK
     * @param attributes_U  属性全集
     * @param attributes_Us 属性
     * @param attributes_A  用户的属性
     */
    public static SK keygen(MK mk, PK pk, String attributes_U, String attributes_Us, String attributes_A) throws NoSuchAlgorithmException {
        //将一个字符串解析成字符数组
        ArrayList<String> arrayList_U = parseString2ArrayList(attributes_U);
        //构造一个多项式
        Polynomial polynomial = createRandomPolynomial(arrayList_U.size() - 1, mk.x);
        Pairing pairing = pk.pairing;
        Element Zr_temp = pairing.getZr().newElement();
        Element nodeID = pairing.getZr().newElement();
        Element Zr_r = pairing.getZr().newElement();
        Element G1_temp1 = pairing.getG1().newElement();
        Element G1_temp2 = pairing.getG1().newElement();
        //将一个字符串解析成字符数组
        ArrayList<String> arrayList_A = parseString2ArrayList(attributes_A);
        ArrayList<String> arrayList_Us = parseString2ArrayList(attributes_Us);
        //求Us和A的并集
        ArrayList<String> arrayList_AAndUs = unionArrayList(arrayList_A, arrayList_Us);
        System.out.println("A和U`的并集:" + arrayList_AAndUs);
        //产生私钥
        SK sk = new SK();
        //给小钥匙赋值属性
        sk.comps = new ArrayList<SKComp>();
        System.out.println("私钥中共有" + arrayList_AAndUs.size() + "个小钥匙！");
        //产生私钥中每把小钥匙
        for (int i = 0; i < arrayList_AAndUs.size(); i++) {
            SKComp comp = new SKComp();
            comp.attr = arrayList_AAndUs.get(i);
            //计算ai=(g2^q(i))*((h0*hi)^ri)
//            System.out.println(arrayList_AAndUs.get(i));
//            System.out.println(Integer.getInteger(arrayList_AAndUs.get(i)));
            nodeID.set(Integer.parseInt(arrayList_AAndUs.get(i)));//i的值，i是属性
            Zr_temp = computePolynomial(Zr_temp, polynomial, nodeID);//q(i)
            G1_temp1.powZn(Zr_temp);//g2^q(i)
            G1_temp2 = pk.hList[0].mul(pk.hList[i]).getImmutable();//h0*hi
            Zr_r.setToRandom();//ri
            G1_temp2.powZn(Zr_r);//(h0*hi)^ri
            comp.a = G1_temp1.mul(G1_temp2);//ai=(g2^q(i))*((h0*hi)^ri)
            //计算b=g^ri
            comp.b = pk.g.powZn(Zr_r).getImmutable();//b=g^ri
            //计算Ci,1-----Ci,2L-1
            comp.hList = new Element[2 * arrayList_U.size() - 1];//2L-1个
            for (int j = 0; j < comp.hList.length; j++) {
                comp.hList[j] = pk.hList[i + 1].powZn(Zr_r).getImmutable();
            }
            sk.comps.add(comp);
            System.out.println("第" + (i + 1) + "个小钥匙生成成功！");
        }

//        System.out.println("用户私钥 D=(g^r*g^a)^(1/b) " + sk.d);
//        System.out.println("用户私钥中属性个数:" + sk.comps.size());
//        System.out.println("用户属性:" + attr_str);
        return sk;
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
     * 计算各个叶子节点(x)的值
     * 对于叶子节点，Qx(0)=QR(x)=A*x`3+B*x`2+C*x`1+D,x表示对于同一根节点的所有孩子节点的顺序值，从1开始，比如孩子有5个，那么顺序值就是1、2、3、4、5，
     * 对于第1个叶子节点，其Polynomial实体类中，阶数为1，系数的个数为1，系数为Q1(0)=QR(1)=A*1`3+B*1`2+C*1`1+D=A+B+C+D；
     * 对于第2个叶子节点，其Polynomial实体类中，阶数为1，系数的个数为1，系数为Q2(0)=QR(2)=A*2`3+B*2`2+C*2`1+D=8A+4B+2C+D；
     * 对于第3个叶子节点，其Polynomial实体类中，阶数为1，系数的个数为1，系数为Q3(0)=QR(3)=A*3`3+B*3`2+C*3`1+D=27A+9B+3C+D。
     *
     * @param Zr_temp    temp是Zr的值，未赋值，空值
     * @param polynomial 节点的多项式
     * @param nodeID     是Zr的值，值为子节点的顺序值，如1、2、3、4、5
     */
    private static Element computePolynomial(Element Zr_temp, Polynomial polynomial, Element nodeID) {
        Element coef, num;//是Zr的值

        coef = Zr_temp.duplicate();//相当于A*3`3
        num = Zr_temp.duplicate();//相当于3`3

        Zr_temp.setToZero();//0
        num.setToOne();//1
        //循环累加多项式中的各个子项，如Q3(0)=QR(3)=A*3`3+B*3`2+C*3`1+D=27A+9B+3C+D，其中的子项A*3`3、B*3`2、C*3`1、D，计算时是逆序的，即QR(3)=D+C*3`1+B*3`2+A*3`3
        for (int i = 0; i < polynomial.deg + 1; i++) {
            // temp += polynomial.coef[i] * num
            coef = polynomial.coef[i].duplicate();
            coef.mul(num);
            Zr_temp.add(coef);

            //对叶子节点的顺序值做阶乘，如Q3(0)=QR(3)=A*3`3+B*3`2+C*3`1+D=27A+9B+3C+D，其中的3
            num.mul(nodeID);
        }
        return Zr_temp;
    }
    //endregion

    // region /**---------------------------encrypt加密阶段用到的函数---------------------------**/
    /**---------------------------encrypt加密阶段用到的函数---------------------------**/
    /**
     * 加密 文件的读取和保存工作
     *
     * @param mk                        PK
     * @param pk                        SK
     * @param attributes_U              属性全集
     * @param policy_S                  门限属性
     * @param threshold                 门限
     * @param messageFilePathAndName    明文
     * @param ciphertextFilePathAndName 密文
     */
    public static Ciphertext encrypt(MK mk, PK pk, String attributes_U, String attributes_OMG, String policy_S, int threshold, String messageFilePathAndName, String ciphertextFilePathAndName) throws Exception {
        Pairing pairing = pk.pairing;
        //表示GT的随机值，AES种子
        Element M = pairing.getGT().newRandomElement();
        //计算Z^s
        Element Zr_s = pairing.getZr().newRandomElement();
        Element GT_temp = pk.Z.powZn(Zr_s).getImmutable();
        //密文实体
        Ciphertext ciphertext = new Ciphertext();
        //计算C0=M*Z^s
        ciphertext.C0 = M.mul(GT_temp).getImmutable();
        //计算C1=g^s
        ciphertext.C1 = pk.g.powZn(Zr_s).getImmutable();
        //计算C2
        //将一个字符串解析成字符数组
        ArrayList<String> arrayList_U = parseString2ArrayList(attributes_U);
        ArrayList<String> attrList_OMG = parseString2ArrayList(attributes_OMG);
        ArrayList<String> attrList_S = parseString2ArrayList(policy_S);
        //求S和OMG的并集
        ArrayList<String> list = unionArrayList(attrList_S, attrList_OMG);
        System.out.println("S和OMG的并集:" + list);
        Element G1_temp1 = pairing.getG1().newElement();
        G1_temp1.setToOne();
        //循环乘hj
        Element G1_temp2 = pairing.getG1().newElement();
        for (int i = 0; i < list.size(); i++) {
            G1_temp1.mul(pk.hList[Integer.parseInt(list.get(i))]);
        }
        //G1_temp1=h0*(循环乘hj)
        G1_temp1.mul(pk.hList[0]);
        //计算C2=(h0*(循环乘hj))^s
        ciphertext.C2 = G1_temp1.powZn(Zr_s);
        //加密成功
        System.out.println("AES加密文件的种子：" + M);
        //从本地读取明文文件
        byte[] messageBuf = FileOperation.file2byte(messageFilePathAndName);
        ;//明文的字节数组
        //先将明文使用AES方法进行加密
        byte[] aesBuf = AESCoder.encrypt(M.toBytes(), messageBuf);
        // 将密文保存到本地
        FileOperation.Ciphertext2File(ciphertextFilePathAndName, aesBuf);
        System.out.println("密文成功生成，已保存到本地！");
        return ciphertext;
    }
    //endregion

    // region /**---------------------------decrypt解密阶段用到的函数---------------------------**/
    /**---------------------------decrypt解密阶段用到的函数---------------------------**/
    /**
     * 解密 文件的读取和保存工作
     *
     * @param pk                        SK
     * @param attributes_A              用户属性
     * @param policy_S                  门限属性
     * @param threshold                 门限
     * @param ciphertextFilePathAndName 密文
     * @param ciphertextFilePathAndName 解密后的明文
     */
    public static void decrypt(PK pk, SK sk, Ciphertext ciphertext, String attributes_A, String attributes_OMG, String policy_S, int threshold, String ciphertextFilePathAndName, String decryptFilePathAndName) throws Exception {
        Pairing pairing = pk.pairing;
        //解密用的D1和D2
        Element D1;
        Element D2 = pairing.getGT().newElement();
        D2.setToOne();
        //将一个字符串解析成字符数组
        ArrayList<String> attrList_A = parseString2ArrayList(attributes_A);
        ArrayList<String> attrList_OMG = parseString2ArrayList(attributes_OMG);
        ArrayList<String> attrList_S = parseString2ArrayList(policy_S);
        //求S和OMG的并集
        ArrayList<String> arrayList_AAndS = intersectionArrayList(attrList_A, attrList_S);
        System.out.println("A和S的并集:" + arrayList_AAndS);
        ArrayList<String> arrayList_As = new ArrayList<String>();
        if (arrayList_AAndS.size() < threshold) {
            System.err.println("解密失败，秘钥中的属性不满足密文中的访问策略！");
        } else {
            //求集合A`，共t个
            for (int i = 0; i < threshold; i++) {
                arrayList_As.add(arrayList_AAndS.get(i));
            }
            //求A`和OMG的并集
            ArrayList<String> arrayList_AsAndOMG = unionArrayList(arrayList_As, attrList_OMG);
            System.out.println("A`和OMG的并集:" + arrayList_AsAndOMG);
            //求S和OMG的并集
            ArrayList<String> arrayList_SAndOMG = unionArrayList(attrList_S, attrList_OMG);
            System.out.println("S和OMG的并集:" + arrayList_SAndOMG);
            //接下来求D1
            Element G1_temp1 = pairing.getG1().newElement();
            //临时变量，便于求拉格朗日系数
            Element Zr_temp1 = pairing.getZr().newElement();
            G1_temp1.setToOne();
            //最外层连乘，处理i
            for (int i = 0, j = 0; i < arrayList_AsAndOMG.size(); j++) {
                if (arrayList_AsAndOMG.get(i).equals(sk.comps.get(j).attr)) {
                    //最内层连乘，处理j
                    for (int k = 0; k < arrayList_SAndOMG.size(); k++) {
                        if (!arrayList_SAndOMG.get(k).equals(arrayList_AsAndOMG.get(i))) {
                            int temp1 = Integer.parseInt(arrayList_SAndOMG.get(k)) - 1;
                            System.out.println("temp1：" + temp1);
                            Element temp2 = sk.comps.get(j).hList[temp1];
                            System.out.println("temp2：" + temp2);
                            G1_temp1.mul(temp2);
//                            G1_temp1.mul(sk.comps.get(j).hList[Integer.parseInt(arrayList_SAndOMG.get(k)) - 1]);
                        }
                    }
                    //乘上ai
                    G1_temp1.mul(sk.comps.get(j).a);
                    //求拉格朗日系数 deta(0) (x-j)/(i-j)
                    Zr_temp1 = lagrangeCoef(Zr_temp1, arrayList_AsAndOMG, sk.comps.get(j).attr);
                    //乘上拉格朗日系数
                    G1_temp1.powZn(Zr_temp1);
                    i++;
                } else {
                }
            }
            D1 = G1_temp1.getImmutable();
            //接下来求D2
            G1_temp1.setToOne();
            Zr_temp1.setToOne();
            //最外层连乘，处理i
            for (int i = 0, j = 0; i < arrayList_AsAndOMG.size(); j++) {
                if (arrayList_AsAndOMG.get(i).equals(sk.comps.get(j).attr)) {
                    //乘上bi
                    G1_temp1.mul(sk.comps.get(j).b);
                    //求拉格朗日系数 deta(0) (x-j)/(i-j)
                    Zr_temp1 = lagrangeCoef(Zr_temp1, arrayList_AsAndOMG, sk.comps.get(j).attr);
                    //乘上拉格朗日系数
                    G1_temp1.powZn(Zr_temp1);
                    i++;
                } else {
                }
            }
            D2 = G1_temp1.getImmutable();
            //计算M
            Element e_C2_D2 = pairing.pairing(ciphertext.C2, D2).getImmutable();
            Element e_C1_D1 = pairing.pairing(ciphertext.C1, D1).getImmutable();
            e_C1_D1.invert();
            e_C2_D2.mul(e_C1_D1);
            //AES种子
            Element M = ciphertext.C0.mul(e_C2_D2).getImmutable();
            System.out.println("解密后计算出AES种子：" + M);
        }


//        byte[] aesBuf, cphBuf;//AES文件，密文文件
//        byte[] plt;//明文
//        byte[] sk_byte;//私钥
//        byte[] pk_byte;//公钥
//        byte[][] tmp;//用于临时储存AES文件，密文文件
//        Ciphertext cph;//密文策略
//        SK sk;
//        PK pk;
//
//        //读取本地的PK文件
//        pk_byte = FileOperation.file2byte(pkfile);
//        pk = SerializeUtils.unserializePK(pk_byte);
//
//        //读取本地的CT密文文件
//        tmp = FileOperation.readCiphertextFile(encfile);
//        aesBuf = tmp[0];
//        cphBuf = tmp[1];
//        cph = SerializeUtils.unserializeCiphertext(pk, cphBuf);
//
//        //读取本地的SK文件
//        sk_byte = FileOperation.file2byte(skfile);
//        sk = SerializeUtils.unserializeSK(pk, sk_byte);
//
//        //检测SK是否满足密文中的访问策略
//        ElementBoolean elementBoolean = decrypt(pk, sk, cph);
//
//        if (elementBoolean.flag) {
//            System.out.println("解密后计算出AES种子：" + elementBoolean.seed.toString());
//            plt = AESCoder.decrypt(elementBoolean.seed.toBytes(), aesBuf);
//            FileOperation.byte2File(plt, decfile);
//            System.out.println("文件解密成功，解密后的文件已保存到本地！ ");
//        } else {
//            System.err.println("解密失败，秘钥中的属性不满足密文中的访问策略！");
//        }
    }

    /**
     * 求解拉格朗日插系数 求deta(0) (x-j)/(i-j)，即x=0,i=minAttrID,j=minAttrsList.get(k)
     */
    private static Element lagrangeCoef(Element elementZr_1, ArrayList<String> attrsList, String currentAttr) {
        String j;
        Element elementZr_temp = elementZr_1.duplicate();
        System.out.println("待解密叶子总数：" + attrsList.size());
        System.out.println("此时解密叶子节点序号：" + currentAttr);
        elementZr_1.setToOne();
        //求循环乘
        for (int k = 0; k < attrsList.size(); k++) {
            j = attrsList.get(k);
            //i==j时，跳过
            if (j == currentAttr)
                continue;
            //求(x-j)
            elementZr_temp.set(-Integer.parseInt(j));//x-j=0-j
            elementZr_1.mul(elementZr_temp); // num_muls++
            //求(i-j) TODO
            System.out.println(Integer.parseInt(currentAttr) - Integer.parseInt(j));
            elementZr_temp.set(Integer.parseInt(currentAttr) - Integer.parseInt(j));
            //求1/(i-j)
            elementZr_temp.invert();
            //求(x-j)/(i-j)
            elementZr_1.mul(elementZr_temp); //num_muls++
        }
        return elementZr_1;
    }

    //endregion


//    /**
//     * 求两个集合的并集，并且去重复
//     */
//    public static ArrayList<String> unionList(String[] attrList1, String[] attrList2) throws Exception {
//        ArrayList<String> list1 = new ArrayList<String>();
//        ArrayList<String> list2 = new ArrayList<String>();
//        Arrays.asList(attrList1);//String[]转ArrayList
//        Arrays.asList(attrList2);
//        list2.removeAll(list1);//去重复
//        list1.addAll(list2);//并集
//        return list1;
//    }


//    /**
//     * 将一个字符串按分隔符分开，返回字符串类型的数组。
//     * 如：将一个字符串，此字符串包含一个人的各种属性，分隔符是空格，最后返回一个数组，数组中包含各个小属性
//     */
//    private static String[] parseAttribute(String str) {
//        ArrayList<String> str_arr = new ArrayList<String>();
//        //构造一个用来解析str的StringTokenizer对象。java默认的分隔符是“空格”、“制表符(‘\t’)”、“换行符(‘\n’)”、“回车符(‘\r’)”
//        StringTokenizer st = new StringTokenizer(str);//此处按空格划分每个属性
//        String token;
//        String res[];
//        int len;
//        //将各个属性添加到数组中
//        while (st.hasMoreTokens()) {
//            token = st.nextToken();
//            if (token.contains(":")) {
//                str_arr.add(token);
//            } else {
//                System.out.println("用户的属性输入有误！");
//                System.exit(0);
//            }
//        }
//        System.out.println("属性集合中元素个数：" + str_arr.size());
//        System.out.println("属性集合：" + str_arr);
//        //将集合赋值到数组中
//        len = str_arr.size();
//        res = new String[len];
//        for (int i = 0; i < len; i++)
//            res[i] = str_arr.get(i);
//        return res;
//    }
//    /**
//     * 将字符串表示的策略转换成TreePolicy类实体，此时对于访问树中各个节点，叶子节点含门限值k、和属性attr，非叶子节点只包含门限值k
//     */
//    private static TreePolicy parseTreePolicy(String policy) {
//        String[] attrAll;//属性集合
//        String attr;//临时单个属性变量
//        ArrayList<TreePolicy> treePolicys = new ArrayList<TreePolicy>();
//        TreePolicy root;
//        //将字符串按空格分割成一个个元素，如"sn:student2 cn:student2 uid:student2 2of3"，分给后得到{"sn:student2","cn:student2","uid:student2","2of3"}
//        attrAll = policy.split(" ");
//        //输出必要属性
//        System.out.println("密文中访问树的属性个数：" + (attrAll.length - 1));
//        System.out.print("属性：");
//        for (int i = 0; i < attrAll.length - 1; i++) {
//            System.out.print(attrAll[i] + "  ");
//        }
//        System.out.println();
//        System.out.println("门限规则：" + attrAll[attrAll.length - 1]);
//
//        //分离属性和门限
////        int toks_cnt = attrAll.length;
//        for (int index = 0; index < attrAll.length; index++) {
//            int i, k, n;
//            //临时变量
//            attr = attrAll[index];
//            //处理属性
//            if (!attr.contains("of")) {
//                treePolicys.add(createOneNode(1, attr));
//            }
//            //处理门限 2of3
//            else {
//                TreePolicy treePolicy;
//                // 剥离 k of n
//                String[] k_n = attr.split("of");
//                k = Integer.parseInt(k_n[0]);//必要属性个数，2of3，如2
//                n = Integer.parseInt(k_n[1]);//属性个数，2of3，如3
//                //对门限值特殊情况的报错
//                if (k < 1) {
//                    System.err.println("属性转换错误： parsing " + policy + ": trivially satisfied operator " + attr);
//                    return null;
//                } else if (k > n) {
//                    System.err.println("属性转换错误：parsing " + policy + ": unsatisfiable operator " + attr);
//                    return null;
//                } else if (n == 1) {
//                    System.err.println("属性转换错误： parsing " + policy + ": indentity operator " + attr);
//                    return null;
//                } else if (n > treePolicys.size()) {
//                    System.err.println("属性转换错误： parsing " + policy + ": stack underflow at " + attr);
//                    return null;
//                }
//                //构造门限节点 pop n things and fill in children
//                treePolicy = createOneNode(k, null);
//                treePolicy.children = new TreePolicy[n];
//                //依次将属性各节点插入到门限节点下
//                for (i = n - 1; i >= 0; i--) treePolicy.children[i] = treePolicys.remove(treePolicys.size() - 1);
//                //使得门限节点成为根节点
//                treePolicys.add(treePolicy);
//            }
//        }
//        //对策略树特殊情况的报错
//        if (treePolicys.size() > 1) {
//            System.err.println("属性转换错误： " + policy + ": extra node left on the stack");
//            return null;
//        } else if (treePolicys.size() < 1) {
//            System.err.println("属性转换错误： " + policy + ": empty policy");
//            return null;
//        }
//
//        root = treePolicys.get(0);
//        return root;
//    }
//
//    /**
//     * 构造一个策略树的节点
//     *
//     * @param k         门限值  k of n
//     * @param attribute 属性
//     */
//    private static TreePolicy createOneNode(int k, String attribute) {
//        TreePolicy treePolicy = new TreePolicy();
//        treePolicy.k = k;
//        if (attribute != null) {
//            treePolicy.attr = attribute;
//        } else {
//            treePolicy.attr = null;
//        }
//        treePolicy.polynomial = null;
//
//        return treePolicy;
//    }
//
//    /**
//     * 向策略树中各个节点添加多项式
//     *
//     * @param randomValue 表示Zr上的随机值，已有值，即根节点的随机值s，qR(0)=s
//     */
//    private static void addPolynomialToTreePolicy(TreePolicy treePolicy, PK pk, Element randomValue) throws NoSuchAlgorithmException {
//        Element nodeID, temp, hash;//nodeID:节点顺序值, temp:中间临时变量, hash:Hash函数，负责哈希属性值
//        Pairing pairing = pk.pairing;
//        nodeID = pairing.getZr().newElement();
//        temp = pairing.getZr().newElement();
//        hash = pairing.getG2().newElement();
//        //产生一个k-1阶的随机多项式，k表示门限值，先为根节点产生，再为其他节点产生
//        treePolicy.polynomial = createRandomPolynomial(treePolicy.k - 1, randomValue);
//        //如果访问树没有叶节点，处理叶子节点
//        if (treePolicy.children == null || treePolicy.children.length == 0) {
//            treePolicy.c = pairing.getG1().newElement();
//            treePolicy.cp = pairing.getG2().newElement();
//            //将单个属性attr哈希到G_1或G_2群上
//            hash = elementFromString(pk, treePolicy.attr);
//            //计算Cy=g^(qy(0))
//            treePolicy.c = pk.g.duplicate();
//            treePolicy.c.powZn(treePolicy.polynomial.coef[0]);
//            //计算Cy`=H(att(y))^(qy(0))
//            treePolicy.cp = hash.duplicate();
//            treePolicy.cp.powZn(treePolicy.polynomial.coef[0]);
//        }
//        //处理非叶子节点
//        else {
//            for (int i = 0; i < treePolicy.children.length; i++) {
//                nodeID.set(i + 1);
//                //计算多项式，temp是Zr的值，未赋值，空值；polynomial是节点的多项式；nodeNum是Zr的值，赋值子节点的顺序值，如1/2/3
//                computePolynomial(temp, treePolicy.polynomial, nodeID);
//                //递归循环，先从
//                addPolynomialToTreePolicy(treePolicy.children[i], pk, temp);
//            }
//        }
//    }
//


//
//    /**---------------------------decrypt解密阶段用到的函数---------------------------**/
//    /**
//     * 解密 文件的读取和保存工作
//     *
//     * @param pkfile  PK本地保存的路径和文件名
//     * @param skfile  SK本地保存的路径和文件名
//     * @param encfile 加密后密文的本地保存的路径和文件名
//     * @param decfile 解密后明文的本地保存的路径和文件名
//     */
//    public static void decrypt(String pkfile, String skfile, String encfile, String decfile) throws Exception {
//        byte[] aesBuf, cphBuf;//AES文件，密文文件
//        byte[] plt;//明文
//        byte[] sk_byte;//私钥
//        byte[] pk_byte;//公钥
//        byte[][] tmp;//用于临时储存AES文件，密文文件
//        Ciphertext cph;//密文策略
//        SK sk;
//        PK pk;
//
//        //读取本地的PK文件
//        pk_byte = FileOperation.file2byte(pkfile);
//        pk = SerializeUtils.unserializePK(pk_byte);
//
//        //读取本地的CT密文文件
//        tmp = FileOperation.readCiphertextFile(encfile);
//        aesBuf = tmp[0];
//        cphBuf = tmp[1];
//        cph = SerializeUtils.unserializeCiphertext(pk, cphBuf);
//
//        //读取本地的SK文件
//        sk_byte = FileOperation.file2byte(skfile);
//        sk = SerializeUtils.unserializeSK(pk, sk_byte);
//
//        //检测SK是否满足密文中的访问策略
//        ElementBoolean elementBoolean = decrypt(pk, sk, cph);
//
//        if (elementBoolean.flag) {
//            System.out.println("解密后计算出AES种子：" + elementBoolean.seed.toString());
//            plt = AESCoder.decrypt(elementBoolean.seed.toBytes(), aesBuf);
//            FileOperation.byte2File(plt, decfile);
//            System.out.println("文件解密成功，解密后的文件已保存到本地！ ");
//        } else {
//            System.err.println("解密失败，秘钥中的属性不满足密文中的访问策略！");
//        }
//    }
//
//    /**
//     * 解密 具体计算密文
//     * Decrypt the specified ciphertext using the given private key, filling in the provided element m (which need not be initialized) with the result.
//     * <p>
//     * Returns true if decryption succeeded, false if this key does not satisfy the policy of the ciphertext (in which case m is unaltered).
//     */
//    private static ElementBoolean decrypt(PK pk, SK sk, Ciphertext ciphertext) {
//        Element t;
//        Element m;
//        ElementBoolean elementBoolean = new ElementBoolean();
//
//        m = pk.pairing.getGT().newElement();
//        t = pk.pairing.getGT().newElement();
//
//        //先检查属性是否满足，即检查私钥SK中的属性是否满足密文中的访问策略树的门限要求
//        ciphertext.treePolicy = checkSKAttributesSatisfy(ciphertext.treePolicy, sk);
//        //不满足的情况
//        if (!ciphertext.treePolicy.satisfiable) {
////            System.err.println("解密失败，秘钥中的属性不满足密文中的访问策略！");
//            elementBoolean.seed = null;
//            elementBoolean.flag = false;
//            return elementBoolean;
//        }
//        //属性满足的情况情况下，开始计算多项式的值
//        else {
//            ciphertext.treePolicy = pickSatisfyMinLeaves(ciphertext.treePolicy, sk);
//            decryptNode(t, ciphertext.treePolicy, sk, pk);//返回t的值，t也是解密根节点的值
//            //cph.cs=`C=M*e(g,g)^as
//            m = ciphertext.cs.duplicate();
//            m.mul(t); //此时t=e(g,g)^rs,m=M*e(g,g)^as*e(g,g)^rs   num_muls++
//            //cph.c=C=h^s , sk.d=D=(g^r*g^a)^(1/b)
//            t = pk.pairing.pairing(ciphertext.c, sk.d);//此时t=e(h^s,(g^r*g^a)^(1/b))
//            t.invert();//求倒数，此时t=1/(e(h^s,(g^r*g^a)^(1/b)))
//            m.mul(t); //此时m=(M*e(g,g)^as*e(g,g)^rs)/(e(h^s,(g^r*g^a)^(1/b)))=M   num_muls++
//
//            elementBoolean.seed = m;//m=M
//            elementBoolean.flag = true;
//            return elementBoolean;
//        }
//    }
//
//    /**
//     * 检查私钥SK中的属性是否满足密文中的访问策略树的门限要求，
//     * 如果该叶节点满足，就将叶节点可满足性置为true
//     */
//    private static TreePolicy checkSKAttributesSatisfy(TreePolicy treePolicy, SK sk) {
//        String skAttr;
//
//        treePolicy.satisfiable = false;
//        //比对叶子节点
//        if (treePolicy.children == null || treePolicy.children.length == 0) {
//            System.out.print("检查访问树中属性：" + treePolicy.attr);
//            for (int i = 0; i < sk.comps.size(); i++) {
//                skAttr = sk.comps.get(i).attr;
////                System.out.println("用户SK中的属性：" + skAttr);
//                //比对字符串，如果相等返回0
//                if (skAttr.compareTo(treePolicy.attr) == 0) {
//                    System.out.println("，结果：该用户满足！");
//                    treePolicy.satisfiable = true;//如果该叶节点满足，就将叶节点可满足性置为true，并且标出该叶子节点是真正参与了运算
//                    treePolicy.decryptAttributeValue = i;
//                    break;
//                }
//            }
//        }
//        //比对非叶子节点
//        else {
//            //递归比对叶子节点
//            for (int i = 0; i < treePolicy.children.length; i++) {
//                checkSKAttributesSatisfy(treePolicy.children[i], sk);
//            }
//            //统计一共有多少个满足条件的叶子节点
//            int l = 0;
//            for (int i = 0; i < treePolicy.children.length; i++) {
//                if (treePolicy.children[i].satisfiable)
//                    l++;
//            }
//            //如果满足条件的叶子节点的数量大于整个访问树的门限值，则根节点可满足性置为true
//            if (l >= treePolicy.k)
//                treePolicy.satisfiable = true;
//            System.out.print("用户满足条件的属性个数：" + l);
//            System.out.println("，访问树的门限值：" + treePolicy.k);
//        }
//        return treePolicy;
//    }
//
//    /**
//     * 标出访问树中哪些叶子节点是真正参与了属性配对，便于接下来计算多项式算出AES的种子
//     */
//    private static TreePolicy pickSatisfyMinLeaves(TreePolicy treePolicy, SK sk) {
//        ArrayList<Integer> leafNodesList = new ArrayList<Integer>();
//        //处理叶子节点
//        if (treePolicy.children == null || treePolicy.children.length == 0) {
//            treePolicy.min_leaves = 1;//真正参与了运算的叶子节点，就将其min_leaves = 1
//        }
//        //处理非叶子节点，即处理根节点
//        else {
//            int len = treePolicy.children.length;
//            //递归操作所有叶子节点，标出哪些叶子节点是真正参与了运算
//            for (int i = 0; i < len; i++) {
//                if (treePolicy.children[i].satisfiable) {
//                    pickSatisfyMinLeaves(treePolicy.children[i], sk);
//                }
//            }
//            //创建根节点下所有叶子节点数个集合，数量为所有叶子节点数
//            for (int i = 0; i < len; i++) {
//                leafNodesList.add(i);
//            }
//            //将所有叶子节点标上序号
////            Collections.sort(leafNodesList, new IntegerComparator(treePolicy));
////            System.out.println("输出排序");
////            for (int x:leafNodesList) {
////                System.out.println(x);
////            }
//            treePolicy.minAttrsList = new ArrayList<Integer>();
//            treePolicy.min_leaves = 0;//根节点的最小叶子数先置0
//            //处理根节点中的叶子节点，保存哪些属性将会参与到解密
////            for (int i = 0; i < len && i < treePolicy.k; i++) {
//            for (int i = 0; i < len; i++) {
//                int c_i = leafNodesList.get(i); // c[i]
//                //向最小属性集合中添加的叶子节点序号，序号从1开始
//                if (treePolicy.children[c_i].satisfiable) {
//                    treePolicy.min_leaves += treePolicy.children[c_i].min_leaves;//累加计算得到根节点可解密的最小的属性个数
//                    treePolicy.minAttrsList.add(c_i + 1);//将参与解密的属性的序号保存起来，加1的目的是：for循环是从0开始的，但叶节点的顺序是从1开始的，minAttrsList集合中保存着叶节点的顺序，所以加1
//                    System.out.println("向最小属性集合中添加的叶子节点序号：" + (c_i + 1));
//                }
//            }
//        }
//        return treePolicy;
//    }
//
//    /**
//     * 给访问树中各个元素表示序号排序用
//     */
//    public static class IntegerComparator implements Comparator<Integer> {
//        TreePolicy policy;
//
//        public IntegerComparator(TreePolicy treePolicy) {
//            this.policy = treePolicy;
//        }
//
//        /**
//         * 若想得到正序，当前值大于后值，返回正整数，当前值小于后值，返回负整数，当前值等于后值，返回0
//         * 若想得到倒序，当前值大于后值，返回负整数，当前值小于后值，返回正整数，当前值等于后值，返回0
//         * 本例想得到正序
//         *
//         * @param obj1 前值
//         * @param obj2 后值
//         * @return
//         */
//        @Override
//        public int compare(Integer obj1, Integer obj2) {
//            int k, l;
//            k = policy.children[obj1].min_leaves;
//            l = policy.children[obj2].min_leaves;
//            return k < l ? -1 : k == l ? 0 : 1;//如果k < l，则返回-1，否则再比较k == l，如果k == l，则输出0，否则输出1
//        }
//    }
//
//    /**
//     * 解密节点
//     */
//    private static void decryptNode(Element elementGT_1, TreePolicy treePolicy, SK sk, PK pk) {
//        Element elementZr_1;
//        elementZr_1 = pk.pairing.getZr().newElement();
//        elementZr_1.setToOne();
//        elementGT_1.setToOne();//GT类型，1
//        decryptLeafNodeAndNoLeafNode(elementGT_1, elementZr_1, treePolicy, sk, pk);
//    }
//
//    /**
//     * 解密叶子节点和非叶子节点，先从根节点开始，根节点里面包含递归求解叶子节点
//     */
//    private static void decryptLeafNodeAndNoLeafNode(Element elementGT_1, Element elementZr_1, TreePolicy treePolicy, SK sk, PK pk) {
//        if (treePolicy.children == null || treePolicy.children.length == 0)
//            decryptLeafNode(elementGT_1, elementZr_1, treePolicy, sk, pk);
//        else
//            decryptNoLeafNode(elementGT_1, treePolicy, sk, pk);
//    }
//
//    /**
//     * 解密非叶子节点，从根节点开始
//     */
//    private static void decryptNoLeafNode(Element elementGT_1, TreePolicy treePolicy, SK sk, PK pk) {
//        Element elementZrCoef = pk.pairing.getZr().newElement();
//        //用于解密的最小范围的属性的循环
//        for (int i = 0; i < treePolicy.minAttrsList.size(); i++) {
//            //求解各个叶子节点的拉格朗日插系数 求deta(0) (x-j)/(i-j)
//            elementZrCoef = lagrangeCoef(elementZrCoef, treePolicy.minAttrsList, treePolicy.minAttrsList.get(i));
//            //求解各个叶子节点的DecryptNode(CT,SK,x)
//            decryptLeafNodeAndNoLeafNode(elementGT_1, elementZrCoef, treePolicy.children[treePolicy.minAttrsList.get(i) - 1], sk, pk);
//        }
//    }
//
//    /**
//     * 解密叶子节点
//     */
//    private static void decryptLeafNode(Element elementGT_1, Element elementZrCoef, TreePolicy treePolicy, SK sk, PK pk) {
//        SKComp skComp;
//        Element up, down;//临时中间计算变量 GT类型 up是分子 down是分母
//
//        skComp = sk.comps.get(treePolicy.decryptAttributeValue);
//
//        up = pk.pairing.getGT().newElement();
//        down = pk.pairing.getGT().newElement();
//
//        up = pk.pairing.pairing(treePolicy.c, skComp.d); //计算e(Di,Cx)  num_pairings++;
//        down = pk.pairing.pairing(treePolicy.cp, skComp.dj); //计算e(Di`,Cx`)  num_pairings++;
//        down.invert();//此时down=1/e(Di`,Cx`)
//        up.mul(down); //此时up=e(Di,Cx)/e(Di`,Cx`)=Fz   num_muls++
//        up.powZn(elementZrCoef); //此时up=(e(Di,Cx)/e(Di`,Cx`))^(拉格朗日系数，即deta i,Sx`(0) )  num_exps++
//
//        elementGT_1.mul(up); //此时elementGT_1=e(Di,Cx)/e(Di`,Cx`)  num_muls++
//    }
//
//

//    /**
//     * 测试用
//     */
//    public static void main(String[] args) {
//        String attr = "objectClass:inetOrgPerson objectClass:organizationalPerson "
//                + "sn:student2 cn:student2 uid:student2 userPassword:student2 "
//                + "ou:idp o:computer mail:student2@sdu.edu.cn title:student";
//        String[] arr = parseAttribute(attr);
//        for (int i = 0; i < arr.length; i++)
//            System.out.println(arr[i]);
//    }
//
//    /**
//     * 将字节数组byte[]转成16进制字符串
//     */
//    public static String bytesToHexString(byte[] src) {
//        StringBuilder stringBuilder = new StringBuilder("");
//        if (src == null || src.length <= 0) {
//            return null;
//        }
//        for (int i = 0; i < src.length; i++) {
//            String hex = Integer.toHexString(src[i] & 0xFF).toUpperCase();
//            if (hex.length() == 1) {
//                hex = '0' + hex;
//            }
//            stringBuilder.append(hex + " ");
//        }
//        return stringBuilder.toString();
//    }
}
