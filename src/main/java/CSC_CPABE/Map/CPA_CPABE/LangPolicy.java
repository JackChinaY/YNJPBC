package CSC_CPABE.Map.CPA_CPABE;

import CSC_CPABE.Map.CPA_CPABE.Entity.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 主函数调用的全部方法，各个过程具体执行的方法
 */
public class LangPolicy {
    //region //--setup初始化阶段用到的函数--//
    /**---------------------------setup初始化阶段用到的函数---------------------------**/
    /**
     * setup初始化 输出PK、MK
     *
     * @param attributes_U 属性全集
     */
    public static void setup(MK mk, PK pk, String attributes_U, String attributes_Us) {
        //给系统公钥赋值
        //仅对于双线性映射，要使用PBC包装并获得性能，必须设置配对工厂的使用PBC（可能）属性
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        //椭圆类型是Type A，生成 对称-质数阶-双线性群,即G1==G2，返回代数结构,代数结构包含：群、环、场（groups, rings and fields）
        Pairing pairing = PairingFactory.getPairing("a.properties");
        pk.pairing = pairing;
        //产生1个Zr的随机值 主密钥
        mk.x = pairing.getZr().newRandomElement();//随机值
        //给系统公钥赋值
        //公钥中的g，//设定并存储一个生成元。由于椭圆曲线是加法群，所以G群中任意一个元素都可以作为生成元
        pk.g = pairing.getG1().newRandomElement();//生成G1的生成元g
        //公钥中的g2
        pk.g2 = pairing.getG1().newRandomElement();
        //公钥中的Z=e(g^x,g2)
        Element g1 = pk.g.duplicate().powZn(mk.x);
        pk.Z = pairing.pairing(g1, pk.g2).duplicate();
        //将一个字符串解析成字符数组
        ArrayList<String> arrayList_U = parseString2ArrayList(attributes_U);
        System.out.println("集合U大小： " + arrayList_U.size() + "个，即：" + arrayList_U);
        ArrayList<String> arrayList_Us = parseString2ArrayList(attributes_Us);
        System.out.println("集合U`大小：" + arrayList_Us.size() + "个，即：" + arrayList_Us);
        //求U和Us的并集
        ArrayList<String> arrayList_UAndUs = unionArrayList(arrayList_U, arrayList_Us);
        System.out.println("U和U`的并集大小:" + arrayList_UAndUs.size() + "个，即：" + arrayList_UAndUs);
        pk.hList = new HashMap<>();
        //给h0赋值
        pk.hList.put("h0", pairing.getG1().newRandomElement());
        //生成2L-1个随机变量
        for (int i = 0; i < arrayList_UAndUs.size(); i++) {
            pk.hList.put(arrayList_UAndUs.get(i), pairing.getG1().newRandomElement());
        }
        System.out.println("系统主密钥 " + mk.toString());
        System.out.println("系统公钥 " + pk.toString());
        System.out.println("系统公钥中2L个G的随机值如下，共" + pk.hList.size() + "个");
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
//        System.out.print("集合中元素个数：" + arrayList.size());
//        System.out.println("，元素为：" + arrayList);
        return arrayList;
    }

    /**
     * 求两个集合的并集，并且去重复，在attrList1后追加attrList2
     */
    private static ArrayList<String> unionArrayList(ArrayList<String> attrList1, ArrayList<String> attrList2) {
        ArrayList<String> list1 = (ArrayList<String>) attrList1.clone();
        ArrayList<String> list2 = (ArrayList<String>) attrList2.clone();
        list2.removeAll(list1);//去重复
        list1.addAll(list2);//并集
        return list1;
    }

    /**
     * 求两个集合的交集
     */
    private static ArrayList<String> intersectionArrayList(ArrayList<String> attrList1, ArrayList<String> attrList2) {
        ArrayList<String> list1 = (ArrayList<String>) attrList1.clone();
        ArrayList<String> list2 = (ArrayList<String>) attrList2.clone();
        list1.retainAll(list2);//并集
        return list1;
    }

    /**
     * 将单个属性attr哈希到G_1或G_2群上
     */
    private static Element Hash4G1(PK pk, String attr) throws NoSuchAlgorithmException {
        Pairing pairing = pk.pairing;
        Element hash = pairing.getG1().newElement();
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(attr.getBytes());
        hash.setFromHash(digest, 0, digest.length);
        return hash;
    }

    /**
     * 将单个属性attr哈希到Zr群上,最后返回一个Zr类型的Element元素
     */
    private static Element Hash4Zr(PK pk, String attr) throws NoSuchAlgorithmException {
        Pairing pairing = pk.pairing;
        Element hash = pairing.getZr().newElement();
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(attr.getBytes());
        hash.setFromHash(digest, 0, digest.length);
        return hash;
    }
    //endregion

    //region //--keygen生成私钥阶段用到的函数--//
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
        System.out.println("集合U大小：" + arrayList_U.size() + "个，即：" + arrayList_U);
        //构造一个多项式
        Polynomial polynomial = createRandomPolynomial(arrayList_U.size() - 1, mk.x);
        Pairing pairing = pk.pairing;
        Element Zr_temp = pairing.getZr().newElement();
        Element nodeID = pairing.getZr().newElement();
        Element Zr_r = pairing.getZr().newElement();
        Element G1_temp1;
        Element G1_temp2;
        //将一个字符串解析成字符数组
        ArrayList<String> arrayList_A = parseString2ArrayList(attributes_A);
        System.out.println("集合A大小：" + arrayList_A.size() + "个，即：" + arrayList_A);
        ArrayList<String> arrayList_Us = parseString2ArrayList(attributes_Us);
        System.out.println("集合U`大小：" + arrayList_Us.size() + "个，即：" + arrayList_Us);
        //求Us和A的并集
        ArrayList<String> arrayList_AAndUs = unionArrayList(arrayList_A, arrayList_Us);
        System.out.println("A和U`的并集大小:" + arrayList_AAndUs.size() + "个，即：" + arrayList_AAndUs);
        //产生私钥
        SK sk = new SK();
        //给小钥匙赋值属性
        sk.comps = new HashMap<>();
        System.out.println("私钥中共有" + arrayList_AAndUs.size() + "个小钥匙！");
        //产生私钥中每把小钥匙
        for (int i = 0; i < arrayList_AAndUs.size(); i++) {
            SKComp comp = new SKComp();
            comp.attr = arrayList_AAndUs.get(i);
            //计算ai=(g2^q(i))*((h0*hi)^ri)
            nodeID = Hash4Zr(pk, arrayList_AAndUs.get(i));//i的值，i是属性
            //Zr_temp=q(i)
            Zr_temp = computePolynomial(Zr_temp, polynomial, nodeID);
            //G1_temp1=g2^q(i)
            G1_temp1 = pk.g2.duplicate().powZn(Zr_temp);
            //G1_temp2=h0*hi
            G1_temp2 = pk.hList.get("h0").duplicate().mul(pk.hList.get(arrayList_AAndUs.get(i)));//h0*hi
            Zr_r.setToRandom();//ri
            G1_temp2.powZn(Zr_r.duplicate());//(h0*hi)^ri
            comp.a = G1_temp1.mul(G1_temp2).duplicate();//ai=(g2^q(i))*((h0*hi)^ri)
            //计算b=g^ri
            comp.b = pk.g.duplicate().powZn(Zr_r);//b=g^ri
            //将pk中的h0---h2L-1复制到comp.hList
            comp.hList = new HashMap<>();//2L-1个
            for (Map.Entry<String, Element> entry : pk.hList.entrySet()) {
                comp.hList.put(entry.getKey(), entry.getValue().duplicate());
            }
            //剔除h0
            comp.hList.remove("h0");
//            System.out.println("pk.hList个数：" + pk.hList.size());
//            System.out.println("comp.hList个数：" + comp.hList.size());
            //计算Ci,1-----Ci,2L-1
            for (Map.Entry<String, Element> entry : comp.hList.entrySet()) {
                entry.getValue().powZn(Zr_r);
            }
            sk.comps.put(arrayList_AAndUs.get(i), comp);
            System.out.print((i + 1) + ", ");
//            System.out.println("第" + (i + 1) + "个小钥匙生成成功！")
//            System.out.println("pk.hList0：" + pk.hList.get("h0"));
//            System.out.println("pk.hList：" + pk.hList.get("1"));
//            System.out.println("comp.hList：" + comp.hList.get("1"));
        }
        System.out.println();

//        System.out.println("用户私钥 D=(g^r*g^a)^(1/b) " + sk.d);
//        System.out.println("用户私钥中属性个数:" + sk.comps.size());
//        System.out.println("用户属性:" + attr_str);
        System.out.println("系统主密钥 " + mk.toString());
        System.out.println("系统公钥 " + pk.toString());
        return sk;
    }

    /**
     * 对于根节点，deg=k-1,k为门限值（如3of4，k=3），coef系数是k个，比deg多一个，如q(x)=A*x`3+B*x`2+C*x`1+D,polynomial.coef[deg + 1]集合中分别存放着A、B、C、D。
     * 对于叶子节点，deg=0,，coef系数是1个，polynomial.coef[1]集合中存放着Qx(0)=QR(x)=A*x`3+B*x`2+C*x`1+D的值，如Q1(0)=QR(1)=A + B*1`1 + C*1`2 + D*1`3=A+B+C+D；
     * 对于每个节点产生随机多项式，系数的个数比阶数多1，如q(x)=A*x`3+B*x`2+C*x`1+D,ABCD均为系数，阶数为3，系数的个数为4
     * 对于根节点，QR(x)=A + B*x`1 + C*x`2 + D*x`3,QR(0)=A + B*0`1 + C*0`2 + D*0`3=A=s,根节点的Polynomial实体类中，阶数为3，系数的个数为4，系数分别为A、B、C、D，其中A、B、C、D均为随机值，其中D也即s
     * 对于叶子节点，Qx(0)=QR(x)=A + B*x`1 + C*x`2 + D*x`3,x表示对于同一根节点的所有孩子节点的顺序值，从1开始，比如孩子有5个，那么顺序值就是1、2、3、4、5，
     * 对于第1个叶子节点，其Polynomial实体类中，阶数为1，系数的个数为1，系数为Q1(0)=QR(1)=A + B*1`1 + C*1`2 + D*1`3=A+B+C+D；
     * 对于第2个叶子节点，其Polynomial实体类中，阶数为1，系数的个数为1，系数为Q2(0)=QR(2)=A + B*2`1 + C*2`2 + D*2`3=A+2B+4C+8D；
     *
     * @param deg         多项式的阶 deg=k-1,k为门限值; coef系数是k个，比deg多一个，coef系数中，第一个是都是相同的，都是根节点的随机值qR(0)=s,其他系数都是随机值
     * @param randomValue Zr的随机值 s qR(0)=s
     */
    private static Polynomial createRandomPolynomial(int deg, Element randomValue) {
        Polynomial polynomial = new Polynomial();
        polynomial.deg = deg;//阶
        polynomial.coef = new Element[deg + 1];//系数 A + B*x`1 + C*x`2 + D*x`3 , A B C D均为系数
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
     * 计算各个叶子节点q(x)的值
     * 对于叶子节点，Qx(0)=QR(x)=A + B*x`1 + C*x`2 + D*x`3 ,x表示对于同一根节点的所有孩子节点的顺序值，从1开始，比如孩子有5个，那么顺序值就是1、2、3、4、5，
     * 对于第1个叶子节点，其Polynomial实体类中，阶数为1，系数的个数为1，系数为Q1(0)=QR(1)=A + B*1`1 + C*1`2 + D*1`3=A+B+C+D；
     * 对于第2个叶子节点，其Polynomial实体类中，阶数为1，系数的个数为1，系数为Q2(0)=QR(2)=A + B*2`1 + C*2`2 + D*2`3=A+2B+4C+8D；
     * 对于第3个叶子节点，其Polynomial实体类中，阶数为1，系数的个数为1，系数为Q3(0)=QR(3)=A + B*3`1 + C*3`2 + D*3`3=A+3B+9C+27D。
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
        //循环累加多项式中的各个子项，如Q3(0)=QR(3)=A + B*3`1 + C*3`2 + D*3`3 = A+3B+9C+27D，其中的子项A、B*3`1、C*3`2、D*3`3
        for (int i = 0; i < polynomial.deg + 1; i++) {
            // temp += polynomial.coef[i] * num
            coef = polynomial.coef[i].duplicate();
            coef.mul(num);
            Zr_temp.add(coef);

            //对叶子节点的顺序值做阶乘，如Q3(0)=QR(3)=A + B*3`1 + C*3`2 + D*3`3 = A+3B+9C+27D，其中的3
            num.mul(nodeID);
        }
        return Zr_temp;
    }
    //endregion

    // region //--encrypt加密阶段用到的函数--//
    /**---------------------------encrypt加密阶段用到的函数---------------------------**/
    /**
     * 加密 文件的读取和保存工作
     *
     * @param mk                        PK
     * @param pk                        SK
     * @param attributes_U              属性全集
     * @param attributes_OMG            属性全集
     * @param attributes_S              门限属性
     * @param threshold                 门限
     * @param messageFilePathAndName    明文
     * @param ciphertextFilePathAndName 密文
     */
    public static Ciphertext encrypt(MK mk, PK pk, String attributes_U, String attributes_OMG, String attributes_S, int threshold, String messageFilePathAndName, String ciphertextFilePathAndName) throws Exception {
        Pairing pairing = pk.pairing;
        //表示GT的随机值，AES种子
        Element M = pairing.getGT().newRandomElement();
        //计算Z^s
        Element Zr_s = pairing.getZr().newRandomElement();
        Element GT_temp = pk.Z.duplicate().powZn(Zr_s);
//        System.out.println(GT_temp);
        //密文实体
        Ciphertext ciphertext = new Ciphertext();
        //计算C0=M*Z^s
        ciphertext.C0 = M.duplicate().mul(GT_temp);

//        System.out.println(ciphertext.C0);
        //计算C1=g^s
        ciphertext.C1 = pk.g.duplicate().powZn(Zr_s);
        //计算C2
        //将一个字符串解析成字符数组
//        ArrayList<String> arrayList_U = parseString2ArrayList(attributes_U);
        ArrayList<String> attrList_S = parseString2ArrayList(attributes_S);
        ArrayList<String> attrList_OMG = parseString2ArrayList(attributes_OMG);
        //求S和OMG的并集
        ArrayList<String> attrList_SAndOMG = unionArrayList(attrList_S, attrList_OMG);
        System.out.println("集合S和OMG并集的大小:" + attrList_SAndOMG.size() + "个，即：" + attrList_SAndOMG);
        Element G1_temp1 = pairing.getG1().newElement();
        G1_temp1.setToOne();
        //循环乘hj
//        Element G1_temp2;
        for (int i = 0; i < attrList_SAndOMG.size(); i++) {
            G1_temp1.mul(pk.hList.get(attrList_SAndOMG.get(i)));
        }
        //G1_temp1=h0*(循环乘hj)
        G1_temp1.mul(pk.hList.get("h0"));
        //计算C2=(h0*(循环乘hj))^s
        ciphertext.C2 = G1_temp1.powZn(Zr_s).duplicate();
        //加密成功
        System.out.println("AES加密文件的种子：" + M);
        //从本地读取明文文件
        byte[] messageBuf = FileOperation.file2byte(messageFilePathAndName);
        //明文的字节数组
        //先将明文使用AES方法进行加密
        byte[] aesBuf = AESCoder.encrypt(M.duplicate().toBytes(), messageBuf);
        // 将密文保存到本地
        FileOperation.Ciphertext2File(ciphertextFilePathAndName, aesBuf);
        System.out.println("密文成功生成，已保存到本地！");
//        System.out.println(GT_temp);
//        GT_temp = GT_temp.invert();
//        System.out.println(GT_temp);
//        Element MM = ciphertext.C0.duplicate().mul(GT_temp);
//        System.out.println(ciphertext.C0);
//        System.out.println(GT_temp);
//        System.out.println("临时算得AES加密文件的种子：" + MM);
        System.out.println("系统主密钥 " + mk.toString());
        System.out.println("系统公钥 " + pk.toString());
        return ciphertext;
    }
    //endregion

    // region //--decrypt解密阶段用到的函数--//
    /**---------------------------decrypt解密阶段用到的函数---------------------------**/
    /**
     * 解密 文件的读取和保存工作
     *
     * @param pk                        SK
     * @param attributes_A              用户属性
     * @param policy_S                  门限属性
     * @param threshold                 门限
     * @param ciphertextFilePathAndName 密文
     * @param decryptFilePathAndName    解密后的明文
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
        System.out.println("集合A和S并集的大小:" + arrayList_AAndS.size() + "个，即：" + arrayList_AAndS);
        ArrayList<String> arrayList_As = new ArrayList<>();
        //检测SK是否满足密文中的访问策略
        if (arrayList_AAndS.size() < threshold) {
            System.err.println("解密失败，秘钥中的属性不满足密文中的访问策略！");
        } else {
            //求集合A`，共t个
            for (int i = 0; i < threshold; i++) {
                arrayList_As.add(arrayList_AAndS.get(i));
            }
            System.out.println("集合A`的大小:" + arrayList_As.size() + "个，即：" + arrayList_As);
            //求A`和OMG的并集
            ArrayList<String> arrayList_AsAndOMG = unionArrayList(arrayList_As, attrList_OMG);
            System.out.println("集合A`和OMG并集的大小:" + arrayList_AsAndOMG.size() + "个，即：" + arrayList_AsAndOMG);
            //求S和OMG的并集
            ArrayList<String> arrayList_SAndOMG = unionArrayList(attrList_S, attrList_OMG);
            System.out.println("集合S和OMG并集的大小:" + arrayList_SAndOMG.size() + "个，即：" + arrayList_SAndOMG);
            /**-----------------------------------接下来求D1-------------------------------**/
            //接下来求D1
            Element G1_temp1 = pairing.getG1().newElement();
            Element G1_temp2 = pairing.getG1().newElement();
            //临时变量，便于求拉格朗日系数
            Element Zr_temp1 = pairing.getZr().newElement();
            G1_temp1.setToOne();
            G1_temp2.setToOne();
            //最外层连乘，处理i
            for (int i = 0; i < arrayList_AsAndOMG.size(); i++) {
                //最内层连乘，处理j
                for (int j = 0; j < arrayList_SAndOMG.size(); j++) {
                    //i不等于j时
                    if (!arrayList_SAndOMG.get(j).equals(arrayList_AsAndOMG.get(i))) {
                        G1_temp1.mul(sk.comps.get(arrayList_AsAndOMG.get(i)).hList.get(arrayList_SAndOMG.get(j)));
                    }
                }
                //乘上ai
                G1_temp1.mul(sk.comps.get(arrayList_AsAndOMG.get(i)).a);
                //求拉格朗日系数 deta(0) (x-j)/(i-j)
                Zr_temp1 = lagrangeCoef(pk, arrayList_AsAndOMG, arrayList_AsAndOMG.get(i));
                //乘上拉格朗日系数
                G1_temp1.powZn(Zr_temp1);
                G1_temp2.mul(G1_temp1);
                G1_temp1.setToOne();
            }
            D1 = G1_temp2.duplicate();
            /**-----------------------------------接下来求D2-------------------------------**/
            //接下来求D2
            G1_temp1.setToOne();
            G1_temp2.setToOne();
            Zr_temp1.setToOne();
            //最外层连乘，处理i
            for (int i = 0; i < arrayList_AsAndOMG.size(); i++) {
                //乘上bi
                G1_temp1.mul(sk.comps.get(arrayList_AsAndOMG.get(i)).b);
                //求拉格朗日系数 deta(0) (x-j)/(i-j)
                Zr_temp1 = lagrangeCoef(pk, arrayList_AsAndOMG, arrayList_AsAndOMG.get(i));
                //乘上拉格朗日系数
                G1_temp1.powZn(Zr_temp1);
                G1_temp2.mul(G1_temp1);
                G1_temp1.setToOne();
            }
            D2 = G1_temp2.duplicate();
            //计算M
            Element e_C2_D2 = pairing.pairing(ciphertext.C2, D2).duplicate();
            Element e_C1_D1 = pairing.pairing(ciphertext.C1, D1).duplicate();
            //AES种子
            Element M = ciphertext.C0.duplicate().mul(e_C2_D2);
            e_C1_D1.invert();
            M.mul(e_C1_D1);
            System.out.println("解密后计算出AES种子：" + M);
            //读取本地的CT密文文件
            byte[] ciphertextFileBuf = FileOperation.file2byte(ciphertextFilePathAndName);//AES文件，密文文件
            byte[] pltBuf = AESCoder.decrypt(M.toBytes(), ciphertextFileBuf);
            //明文
            FileOperation.byte2File(pltBuf, decryptFilePathAndName);
            System.out.println("文件解密成功，解密后的文件已保存到本地！ ");
        }
    }

    /**
     * 求解拉格朗日插系数 求deta(0) (x-j)/(i-j)，即x=0，j=currentAttr，i=attrsList.get(i)
     * 求p(0),对于n-1阶多项式来说，必须知道至少n个点才能求出，比如求p(0),对于4阶多项式来说，必须知道至少4个点才能求出p(0)的值
     * 处理属性时，需要先将属性哈希到Zr群上，得到一个大质数
     *
     * @param attrsList   属性集合
     * @param currentAttr 当前属性
     */
    private static Element lagrangeCoef(PK pk, ArrayList<String> attrsList, String currentAttr) throws NoSuchAlgorithmException {
        //运算过程中的中间结果
        Element Zr_temp1 = pk.pairing.getZr().newElement();
        //返回的结果
        Element Zr_result = pk.pairing.getZr().newElement();
        Zr_result.setToOne();
        //循环次数是集合的元素个数
        for (int i = 0; i < attrsList.size(); i++) {
            //i==currentAttr时，跳过
            if (attrsList.get(i).equals(currentAttr)) {
                continue;
            }
            //Zr_temp1=x-i=0-i
            Zr_temp1.setToZero();
            Zr_temp1.sub(Hash4Zr(pk, attrsList.get(i)));
            Zr_result.mul(Zr_temp1);
            //Zr_temp1=currentAttr-i
            Zr_temp1.set(Hash4Zr(pk, currentAttr).duplicate().sub(Hash4Zr(pk, attrsList.get(i))));
            //Zr_temp1=1/(currentAttr-i)
            Zr_temp1.invert();
            //Zr_result=(0-i)/(currentAttr-i)
            Zr_result.mul(Zr_temp1);
        }
        return Zr_result;
    }
    //endregion


    /**
     * 测试用
     */
    public static void main(String[] args) {
        //仅对于双线性映射，要使用PBC包装并获得性能，必须设置配对工厂的使用PBC（可能）属性
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        //椭圆类型是Type A，生成 对称-质数阶-双线性群,即G1==G2，返回代数结构,代数结构包含：群、环、场（groups, rings and fields）
        Pairing pairing = PairingFactory.getPairing("a.properties");
        Element g = pairing.getG1().newRandomElement();
        System.out.println("g:" + g);
        Element q;
        q = g.duplicate();
        System.out.println("q:" + q);
        Element v = pairing.getG1().newRandomElement();
        System.out.println("v:" + v);
//        Element s = pairing.getZr().newRandomElement();
//        System.out.println("s:" + s);
        Element c = g.mul(v).duplicate();
////        Element g2 = g.duplicate().mul(v);
////        Element g2 = g.duplicate().powZn(s);
//        Element Z = pairing.pairing(g, v);
////        g = g.mul(v);
        System.out.println("c:" + c);
        System.out.println("g:" + g);
        System.out.println("v:" + v);
        System.out.println("q:" + q);
        Element d = g.mul(v);
        System.out.println("d:" + d);
        System.out.println("c:" + c);
        System.out.println("g:" + g);
        System.out.println("v:" + v);
//        if (g.isImmutable()) {
//            System.out.println("g不可变");
//        }
////        if (g2.isImmutable()) {
////            System.out.println("g2不可变");
////        }
//        if (v.isImmutable()) {
//            System.out.println("v不可变");
//        }
//        Element g1 = g.getImmutable();
//        System.out.println("g1:" + g1);
//        Element v1 = v.getImmutable();
//        System.out.println("v1:" + v1);
        Element Z = pairing.pairing(g, v);
        Element s = pairing.getZr().newRandomElement();
        Element Zs = Z.powZn(s);
        Element M1 = pairing.getGT().newRandomElement();
        System.out.println("M1:" + M1);
        System.out.println("Zs:" + Zs);
        Element C0 = M1.mul(Zs);
//        Element Zs1 = Zs.invert();
        Zs.invert();
        System.out.println("Zs:" + Zs);
        Element M2 = C0.mul(Zs);
        System.out.println("Zs:" + Zs);
        System.out.println("M2:" + M2);

//        Element Z = pairing.pairing(g, v).duplicate();
//        Element s = pairing.getZr().newRandomElement();
//        Element Zs = Z.duplicate().powZn(s).duplicate();
//        Element M = pairing.getGT().newRandomElement();
//        System.out.println("M:" + M);
//        Element C0 = M.duplicate().mul(Zs).duplicate();
//        Element Zs1 = Zs.duplicate().invert();
//        Element M1 = C0.duplicate().mul(Zs1.duplicate()).duplicate();
//        System.out.println("M1:" + M1);


//        Element g = pairing.getG1().newRandomElement();
//        Element v = pairing.getG1().newRandomElement();
//        Element Z = pairing.pairing(g, v).duplicate();
//        Element s = pairing.getZr().newRandomElement();
//        Element Zs = Z.duplicate().powZn(s).duplicate();
//        Element M = pairing.getGT().newRandomElement();
//        System.out.println("M:" + M);
//        Element C0 = M.duplicate().mul(Zs).duplicate();
//        Element Zs1 = Zs.duplicate().invert();
//        Element M1 = C0.duplicate().mul(Zs1.duplicate()).duplicate();
//        System.out.println("M1:" + M1);

//        Element M;
//        Element M1;
//        Element M2;
//
//        Element a = pairing.getZr().newRandomElement();
//        Element b = pairing.getZr().newRandomElement();
//        System.out.println("a:" + a);
//        System.out.println("b:" + b);
//
//        Element g = pairing.getG1().newRandomElement();
//        System.out.println("g:" + g);
//        Element v = pairing.getG1().newRandomElement();
//        System.out.println("v:" + v);
//        Element ga = g.getImmutable().powZn(a).getImmutable();
//        System.out.println("g:" + g);
////        Element gb = g.powZn(b).getImmutable();
////        M1 = pairing.pairing(ga, gb).getImmutable();
////        System.out.println("M1:e(g^a,g^b):" + M1);
////
////        M = pairing.pairing(g, g).getImmutable();
////        System.out.println("M:e(g,g):" + M);
//        Element ab = a.getImmutable().mul(b).getImmutable();
//        System.out.println("ab:" + ab);
////        M2 = M.powZn(ab).getImmutable();
////        System.out.println("M2:e(g,g)^ab:" + M2);
//
//        Element vb = v.getImmutable().powZn(b).getImmutable();
//        Element M3 = pairing.pairing(ga, vb).getImmutable();
//        System.out.println("e(g^a,v^b):" + M3);
//
//        Element M4 = pairing.pairing(g, v).getImmutable();
////        System.out.println("e(g,v):" + M4);
////        System.out.println("ab:" + ab);
//
//        Element M5 = M4.powZn(ab).getImmutable();
//        System.out.println("e(g,v)^ab:" + M5);


    }
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
