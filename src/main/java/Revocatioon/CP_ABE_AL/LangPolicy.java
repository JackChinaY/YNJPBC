package Revocatioon.CP_ABE_AL;

import Revocatioon.CP_ABE_AL.Entity.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 主函数调用的全部方法，各个过程具体执行的方法
 */
public class LangPolicy {
    /**---------------------------setup初始化阶段用到的函数---------------------------**/
    /**
     * setup初始化 输出PK、MK
     *
     * @param attributes_C 属性中心
     */
    public static void setup(MK mk, PK pk, String thresholds, String[][] attributes_C, ArrayList<AAK> AAKList, String[][] attributes_Us) {
        //仅对于双线性映射，要使用PBC包装并获得性能，必须设置配对工厂的使用PBC（可能）属性
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        //椭圆类型是Type A，生成 对称-质数阶-双线性群,即G1==G2，返回代数结构,代数结构包含：群、环、场（groups, rings and fields）
        Pairing pairing = PairingFactory.getPairing("a.properties");
        /**---------------------------公钥PK---------------------------**/
        //公钥的对运算
        pk.pairing = pairing;
        //公钥中的生成元g，由于椭圆曲线是加法群，所以G群中任意一个元素都可以作为生成元
        pk.g = pairing.getG1().newRandomElement();
        pk.u = pairing.getG1().newRandomElement();
        pk.h = pairing.getG1().newRandomElement();
        pk.w = pairing.getG1().newRandomElement();
        pk.v = pairing.getG1().newRandomElement();
//        System.out.println(pk.g);
        //公钥中的Z=e(g,g)^a
        Element a = pairing.getZr().newRandomElement();
        Element Z = pairing.pairing(pk.g, pk.g);
        pk.e_g_g_a = Z.powZn(a);
        /**---------------------------主密钥MK---------------------------**/
        //产生1个Zr的随机值 主密钥
        mk.a1 = pairing.getZr().newRandomElement();
//        Element q = pairing.getZr().newRandomElement();
        mk.a2 = a.duplicate().sub(mk.a1);
//        mk.a2 = pairing.getZr().newRandomElement();
//        System.out.println("系统公钥 " + pk.toString());
//        System.out.println("系统主密钥 " + mk.toString());
//        System.out.println(pk.g);
//        System.out.println(mk.a1.duplicate().add(mk.a2));
    }

    /**
     * 将一个字符串解析成字符数组，如：将一个字符串，此字符串包含一个人的各种属性，分隔符是空格，最后返回一个数组，数组中包含各个小属性
     * java默认的分隔符是“空格”、“制表符(‘\t’)”、“换行符(‘\n’)”、“回车符(‘\r’)”
     */
    private static ArrayList<String> parseString2ArrayList(String string) {
        ArrayList<String> arrayList = new ArrayList<>();
        //构造一个用来解析str的StringTokenizer对象
        StringTokenizer stringTokenizer = new StringTokenizer(string);//此处按空格划分每个属性
        String token;
        //将各个属性添加到数组中
        while (stringTokenizer.hasMoreTokens()) {
            token = stringTokenizer.nextToken();
            arrayList.add(token);
        }
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

    /**
     * 将单个属性attr哈希到Zr群上,最后返回一个Zr类型的Element元素
     */
    private static Element Hash4Zr(PK pk, byte[] array) throws NoSuchAlgorithmException {
        Pairing pairing = pk.pairing;
        Element hash = pairing.getZr().newElement();
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(array);
        hash.setFromHash(digest, 0, digest.length);
        return hash;
    }

    /**
     * 将一个二维字符串数组的指定行的数据解析成字符串集合
     *
     * @param array 二维数组
     * @param index 指定行，从0开始
     */
    private static ArrayList<String> parseStringArray2ArrayList(String[][] array, int index) {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < array[index].length; i++) {
            arrayList.add(array[index][i]);
        }
        return arrayList;
    }

    /**---------------------------keygen生成私钥阶段用到的函数---------------------------**/
    /**
     * 生成私钥 文件的读取和保存工作 输入PK、MK、ATTR(用户属性)，输出SK
     *
     * @param mk           PK
     * @param pk           SK
     * @param AAKList      属性全集
     * @param attributes_A 用户的属性
     * @param AID          用户的GUID
     */
    public static SK keygen(MK mk, PK pk, ArrayList<AAK> AAKList, String attributes_A, String AID, String[][] attributes_Us) throws NoSuchAlgorithmException {
        Pairing pairing = pk.pairing;
        //将用户属性解析成字符数组
        ArrayList<String> arrayList_A = parseString2ArrayList(attributes_A);
//        System.out.println("集合A大小：" + arrayList_A.size() + "个，即：" + arrayList_A);
        //产生私钥
        SK sk = new SK();
        //z
        Element Zr_z = pairing.getZr().newRandomElement();
        //赋值
        sk.z = Zr_z.duplicate();
        //求倒数，1/z
        Zr_z.invert();
        /**---------------------------计算K0---------------------------**/
        //g^a1
        Element G1_temp1 = pk.g.duplicate().powZn(mk.a1);
        //r'
        Element Zr_rs = pairing.getZr().newRandomElement();//Zr_rs表示r'
        //w^r'
        Element G1_temp2 = pk.w.duplicate().powZn(Zr_rs);
        //g^a1 * w^r'
        sk.K0 = G1_temp1.mul(G1_temp2).duplicate();
        //(g^a1 * w^r')^(1/z)
        sk.K0.powZn(Zr_z);
//        System.out.println(sk.K0);
        /**---------------------------计算K1---------------------------**/
        //g^r'
        G1_temp1 = pk.g.duplicate().powZn(Zr_rs);
        //(g^r')^(1/z)
        sk.K1 = G1_temp1.powZn(Zr_z).duplicate();
        /**---------------------------计算K2和K3---------------------------**/
        for (int i = 0; i < arrayList_A.size(); i++) {
            //ro'
            Element Zr_ros = pairing.getZr().newRandomElement();
            /**---------------------------计算K2---------------------------**/
            //g^ro'
            G1_temp1 = pk.g.duplicate().powZn(Zr_ros);
            //(g^ro')^(1/z)
            G1_temp1.powZn(Zr_z);
            // s  (g^ro')^(1/z)
            sk.K2.put(arrayList_A.get(i), G1_temp1.duplicate());
            /**---------------------------计算K3---------------------------**/
            //u^so
            G1_temp1 = pk.u.duplicate().powZn(Hash4Zr(pk, arrayList_A.get(i)));
            //u^so * h
            G1_temp1.mul(pk.h);
            //(u^so * h)^ro'
            G1_temp1.powZn(Zr_ros);
            //0
            Element Zr_temp = pairing.getZr().newElement().setToZero();
            //0-r'
            Zr_temp.sub(Zr_rs);
            //v^(-r')
            G1_temp2 = pk.v.duplicate().powZn(Zr_temp);
            //(u^so * h)^ro' * v^(-r')
            G1_temp1.mul(G1_temp2);
            //((u^so * h)^ro' * v^(-r'))^(1/z)
            G1_temp1.powZn(Zr_z);
            sk.K3.put(arrayList_A.get(i), G1_temp1.duplicate());
            System.out.print(arrayList_A.get(i) + ", ");
        }
        System.out.println();
//        System.out.println("密钥SK1生成完毕");
        /**---------------------------计算SK2---------------------------**/
        sk.SK2 = pk.g.duplicate().powZn(mk.a2);
//        System.out.println("密钥SK2生成完毕");
        /**---------------------------计算r---------------------------**/
        sk.r = Zr_rs.duplicate().mul(Zr_z);
        return sk;
    }

    /**
     * 多项式q(x)=A + B*x`1 + C*x`2 + D*x`3,阶为3，系数为4个（coef=k=deg-1）
     * 门限值k=4时，阶就为3，此时需要4个点才能计算出拉格朗日插值多项式f(0)的值
     *
     * @param deg         多项式的阶，门限值k减1,即deg=k-1
     * @param randomValue Zr的随机值 s q(0)的值，即A的值
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

    /**---------------------------encrypt加密阶段用到的函数---------------------------**/
    /**
     * 加密 文件的读取和保存工作
     *
     * @param pk         SK
     * @param AAKList    属性中心们
     * @param MPathName  明文
     * @param CTPathName 密文
     */
    public static Ciphertext encrypt(PK pk, ArrayList<AAK> AAKList, String attributes_A, String[][] attributes_Us, int[][] matrix, String MPathName, String CTPathName) throws Exception {
        //将用户属性解析成字符数组
        ArrayList<String> arrayList_A = parseString2ArrayList(attributes_A);
        Pairing pairing = pk.pairing;
        //表示GT的随机值，AES种子
        Element M = pairing.getGT().newRandomElement();
        //Zr_s=s
        Element Zr_s = pairing.getZr().newRandomElement();
        //密文实体
        Ciphertext ciphertext = new Ciphertext();
        /**---------------------求s----------------------**/
        ciphertext.s = Zr_s.duplicate();
        /**---------------------求C----------------------**/
        //GT_temp=(e(g,g)^a)^s
        Element GT_temp = pk.e_g_g_a.duplicate().powZn(Zr_s);
        //C=M*(e(g,g)^a)^s
        ciphertext.C = M.duplicate().mul(GT_temp);
        /**---------------------求C0----------------------**/
        //C0=g^s
        ciphertext.C0 = pk.g.duplicate().powZn(Zr_s);
        /**---------------------求Ci,1、Ci,2、Ci,3----------------------**/
        //ti
        Element ti = pairing.getZr().newElement();
        //l*n的矩阵M
        Element[][] matixElement = new Element[matrix.length][matrix[0].length];
        //给矩阵赋初值
        for (int i = 0; i < matixElement.length; i++) {
            for (int j = 0; j < matixElement[0].length; j++) {
                if (matrix[i][j] == 0) matixElement[i][j] = pairing.getZr().newElement().setToZero();
                else if (matrix[i][j] == 1) matixElement[i][j] = pairing.getZr().newElement().setToOne();
            }
        }
        //向量v
        Element[] v = new Element[matrix[0].length];
        for (int i = 0; i < v.length; i++) {
            v[i] = pairing.getZr().newElement();
        }
        Element Zr_temp1 = pairing.getZr().newElement();
        Element Zr_temp2 = pairing.getZr().newElement().setToOne();
        Element G1_temp1;
        Element G1_temp2;
        //循环次数是AA的个数
        for (int i = 0; i < matrix.length; i++) {
            /**---------------------求Ci,1----------------------**/
            //ti
            ti.setToRandom();
            //lanmda
            for (int j = 0; j < v.length; j++) {
                Zr_temp1 = matixElement[i][j].duplicate().mul(v[j]);
                Zr_temp2.add(Zr_temp1);
            }
            //w^lanmda
            G1_temp1 = pk.w.duplicate().powZn(Zr_temp2);
            //v^ti
            G1_temp2 = pk.v.duplicate().powZn(ti);
            ciphertext.C1.put(arrayList_A.get(i), G1_temp1.mul(G1_temp2).duplicate());
            /**---------------------求Ci,2----------------------**/
            //构造一个多项式
//            Polynomial polynomial = createRandomPolynomial(AAKList.get(i).number - 1, p0);
            G1_temp1 = pk.u.duplicate().mul(pk.h);
            Zr_temp1.setToZero();
            Zr_temp1.sub(ti);
            G1_temp1.powZn(Zr_temp1);
            ciphertext.C2.put(arrayList_A.get(i), G1_temp1.duplicate());
            /**---------------------求Ci,3----------------------**/
            G1_temp1 = pk.g.duplicate().powZn(ti);
            ciphertext.C3.put(arrayList_A.get(i), G1_temp1.duplicate());
        }
        /**-----------------------------------接下来开始对明文加密-------------------------------**/
        //加密成功
        System.out.println("AES加密文件的种子：" + M);
        //从本地读取明文文件
        byte[] messageBuf = FileOperation.file2byte(MPathName);
        System.out.println("明文大小：" + messageBuf.length);
        //先将明文使用AES方法进行加密
        byte[] aesBuf = AESCoder.encrypt(M.duplicate().toBytes(), messageBuf);
        // 将密文保存到本地
        FileOperation.Ciphertext2File(CTPathName, aesBuf);
        System.out.println("密文大小：" + aesBuf.length);
        System.out.println("密文成功生成，已保存到本地！");
        return ciphertext;
    }

    /**
     * 重加密
     *
     * @param pk             系统公钥
     * @param old_ciphertext 密文
     * @param attributes_RL  撤销的属性集合
     * @param sk             私钥
     * @param MPathName      明文
     * @param CTPathName     密文
     */
    public static RCT re_encrypt(PK pk, Ciphertext old_ciphertext, SK sk, String attributes_RL, int[][] matrix, String MPathName, String CTPathName) throws Exception {
        Pairing pairing = pk.pairing;
        //表示GT的随机值，AES种子
        Element M = pairing.getGT().newRandomElement();
        //将用户撤销的属性解析成字符数组
        ArrayList<String> arrayList_RL = parseString2ArrayList(attributes_RL);
        //密文实体
        RCT rct = new RCT();
        /**--------------------------------如果无属性撤销---------------------------------**/
        if (arrayList_RL.size() == 0) {
            System.out.println("无属性被撤销");
            //k
            Element k = pairing.getZr().newRandomElement();
            /**---------------------求C'----------------------**/
            //C'=C=M*(e(g,g)^a)^s
            rct.C = old_ciphertext.C.duplicate();
            /**---------------------求C0'----------------------**/
            //C0'=C0=g^s
            rct.C0 = old_ciphertext.C0.duplicate();
            /**---------------------求C1'----------------------**/
            //1/k
            Element k_invert = k.duplicate().invert();
            //C11=C1'=C0^(1/k)=(g^s)^(1/k)
            rct.C11 = old_ciphertext.C0.duplicate().powZn(k_invert);
//            System.out.println(rct.C11);
            /**---------------------求Ci,1'、Ci,2'、Ci,3'----------------------**/
            //Ci,1'=Ci,1 * (v^k)
            for (Map.Entry<String, Element> entry : old_ciphertext.C1.entrySet()) {
                rct.C1.put(entry.getKey(), entry.getValue().duplicate().mul(pk.v.duplicate().powZn(k)));
            }
            /**---------------------求Ci,2'----------------------**/
            //0
            Element Zr_temp = pairing.getZr().newElement().setToZero();
            //0-k=-k
            Zr_temp.sub(k);
            //Ci,2'=Ci,2 * (v^k)
            for (Map.Entry<String, Element> entry : old_ciphertext.C2.entrySet()) {
                //u * h
                Element G1_temp1 = pk.u.duplicate().mul(pk.h);
                //(u * h)^(-k)
                G1_temp1.powZn(Zr_temp);
                //Ci,2'=(u * h)^(-ti)*(u * h)^(-k)
                rct.C2.put(entry.getKey(), entry.getValue().duplicate().mul(G1_temp1));
            }
            /**---------------------求Ci,3'----------------------**/
            //Ci,3'=Ci,3 * (g^k)
            for (Map.Entry<String, Element> entry : old_ciphertext.C3.entrySet()) {
                rct.C3.put(entry.getKey(), entry.getValue().duplicate().mul(pk.g.duplicate().powZn(k)));
            }
            /**---------------------更新相应的授权密钥为SK2'----------------------**/
            //SK2'=SK2^k
            sk.SK2.powZn(k);
//            System.out.println(sk.SK2);
            /**---------------------更新s----------------------**/
            rct.s = old_ciphertext.s.duplicate();
        }
        /**--------------------------------如果存在被撤销的属性---------------------------------**/
        else {
            System.out.println("被撤销的属性个数：" + arrayList_RL.size() + " ,分别是： " + attributes_RL);
            //v
            Element v = pairing.getZr().newRandomElement();
            rct.Vx = v.duplicate();
            //1/v
            Element v_invert = v.invert();
            //k
            Element k = pairing.getZr().newRandomElement();
            /**---------------------求C'----------------------**/
            //C'=C=M*(e(g,g)^a)^s
            rct.C = old_ciphertext.C.duplicate();
            /**---------------------求C0'----------------------**/
            //C0'=C0=g^s
            rct.C0 = old_ciphertext.C0.duplicate();
            /**---------------------求C1'----------------------**/
            //1/k
            Element k_invert = k.duplicate().invert();
            //C11=C1'=C0^(1/k)=(g^s)^(1/k)
            rct.C11 = old_ciphertext.C0.duplicate().powZn(k_invert);
//            System.out.println(rct.C11);
            /**---------------------求Ci,1'----------------------**/
            //Ci,1'=Ci,1 * (v^k)
            for (Map.Entry<String, Element> entry : old_ciphertext.C1.entrySet()) {
                rct.C1.put(entry.getKey(), entry.getValue().duplicate().mul(pk.v.duplicate().powZn(k)));
            }
            /**---------------------求Ci,2'----------------------**/
            //0
            Element Zr_temp = pairing.getZr().newElement().setToZero();
            //0-k=-k
            Zr_temp.sub(k);
            //Ci,2'=Ci,2 * (v^k)
            for (Map.Entry<String, Element> entry : old_ciphertext.C2.entrySet()) {
                //u * h
                Element G1_temp1 = pk.u.duplicate().mul(pk.h);
                //(u * h)^(-k)
                G1_temp1.powZn(Zr_temp);
                //Ci,2'=(u * h)^(-ti)*(u * h)^(-k)
                rct.C2.put(entry.getKey(), entry.getValue().duplicate().mul(G1_temp1));
            }
            /**---------------------求Ci,3'----------------------**/
            boolean flag = false;
            //Ci,3'=Ci,3 * (g^k) TODO
            for (Map.Entry<String, Element> entry : old_ciphertext.C3.entrySet()) {
                for (int i = 0; i < arrayList_RL.size(); i++) {
                    if ((entry.getKey()).equals(arrayList_RL.get(i))) {
                        flag = true;
                        break;
                    }
                }
                //(g^ti) * (g^k)
                Element C3 = entry.getValue().duplicate().mul(pk.g.duplicate().powZn(k));
                //如果p(i)==x
                if (flag) {
                    //((g^ti) * (g^k))^(1/v)
                    C3.powZn(v_invert);
                }
                rct.C3.put(entry.getKey(), C3);
                flag = false;
            }
            /**---------------------更新相应的授权密钥为SK2'----------------------**/
            //SK2'=SK2^k
            sk.SK2.powZn(k);
//            System.out.println(sk.SK2);
            /**---------------------更新s----------------------**/
            rct.s = old_ciphertext.s.duplicate();
        }
        return rct;
    }

    /**
     * 将多个Element元素整合到一个byte数组中
     */
    private static byte[] Element2ByteArray(Element... args) {
        //先求最终拼接的数组总长度
        int lengthAll = 0;
        for (Element element : args) {
            lengthAll += element.toBytes().length;
        }
        //开辟一个数组
        byte[] array = new byte[lengthAll];
        //开始拼接，elementByteLength是每个元素的字节长度
        int elementByteLength = 0;
        for (Element element : args) {
            System.arraycopy(element.toBytes(), 0, array, elementByteLength, element.toBytes().length);
            elementByteLength += element.toBytes().length;
        }
        return array;
    }
    /**---------------------------part_decrypt部分解密阶段用到的函数---------------------------**/
    /**
     * 部分解密
     *
     * @param attributes_A  用户属性
     * @param attributes_RL 用户撤销的属性
     * @param thresholds    门限
     * @param CTPathName    密文
     * @param DPathName     解密后的明文
     */
    public static TCT part_decrypt(PK pk, MK mk, SK sk, RCT rct, String attributes_A, String attributes_RL, ArrayList<AAK> AAKList, String thresholds, String[][] attributes_Us, String CTPathName, String DPathName) throws Exception {
        Pairing pairing = pk.pairing;
        //将字符串解析成字符数组
        ArrayList<String> attrList_A = parseString2ArrayList(attributes_A);
        //将用户撤销的属性解析成字符数组
        ArrayList<String> arrayList_RL = parseString2ArrayList(attributes_RL);
        //将各个AA中的门限值解析成字符数组
        ArrayList<String> arrayList_thresholds = parseString2ArrayList(thresholds);
        TCT tct = new TCT();
        /**--------------------------------如果无属性撤销---------------------------------**/
        if (arrayList_RL.size() == 0) {
            /**---------------------求C'----------------------**/
            tct.C = rct.C.duplicate();
            /**---------------------求B----------------------**/
            Element B = pairing.pairing(pk.g, pk.w);
            //B=e(g,w)^rs
            B.powZn(sk.r.duplicate().mul(rct.s));
            tct.B = B;
            //重新计算B
            /**---------------------求D----------------------**/
//        System.out.println(sk.K0);
            //D=e(C0',K0)
            Element D = pairing.pairing(rct.C0, sk.K0);
            tct.D = D;
            /**---------------------求E----------------------**/
//        System.out.println(sk.SK2);
//        System.out.println(rct.C11);
            //E=e(SK2',C1')
            Element E = pairing.pairing(sk.SK2, rct.C11);
            tct.E = E;

//        System.out.println(E);
//        Element a = pairing.pairing(pk.g, pk.g);
//        Element b =mk.a2.mul(rct.s);
//        a.powZn(b);
//        System.out.println(a);System.out.println(pk.g);
            /**---------------------求F----------------------**/
            //-B
            Element _B = B.duplicate().invert();
            //F=D/B
            tct.F = D.duplicate().mul(_B);

//        System.out.println(tct.F);
//        Element z_invert = sk.z.duplicate().invert();
//        Element a = pairing.pairing(pk.g, pk.g);
//        Element b =mk.a1.mul(rct.s);
//        b.mul(z_invert);
//        a.powZn(b);
//        System.out.println(a);
        }
        /**--------------------------------如果有属性被撤销---------------------------------**/
        else {
            /**---------------------求C'----------------------**/
            tct.C = rct.C.duplicate();
            /**---------------------求B----------------------**/
            Element B = pairing.pairing(pk.g, pk.w);
            //B=e(g,w)^rs
            B.powZn(sk.r.duplicate().mul(rct.s));
            tct.B = B;
            /**---------------------求Bi----------------------**/
            boolean flag = false;
            for (int i = 0; i < attrList_A.size(); i++) {

                for (int j = 0; j < arrayList_RL.size(); j++) {
                    if (attrList_A.get(i).equals(arrayList_RL.get(j))) {
                        flag = true;
                        break;
                    }
                }
                //如果p(i)==x
                if (flag) {

                }
                //如果p(i)不等于x
                else {
                    //B1=e(Ci,1',Ki,1)
                    Element B1 = pairing.pairing(rct.C1.get(attrList_A.get(i)), sk.K1);
                    //B2=e(Ci,2',Ki,2)
                    Element B2 = pairing.pairing(rct.C2.get(attrList_A.get(i)), sk.K2.get(attrList_A.get(i)));
                    //B3=e(Ci,3',Ki,3)
                    Element B3 = pairing.pairing(rct.C3.get(attrList_A.get(i)), sk.K3.get(attrList_A.get(i)));
                    //B1=e(Ci,1',Ki,1)*e(Ci,2',Ki,2)
                    B1.mul(B2);
                    //B1=e(Ci,1',Ki,1)*e(Ci,2',Ki,2)*e(Ci,3',Ki,3)
                    B1.mul(B3);
                    tct.Bi.put(attrList_A.get(i), B1.duplicate());
                }
                flag = false;
            }
            //重新计算B
            /**---------------------求D----------------------**/
//        System.out.println(sk.K0);
            //D=e(C0',K0)
            Element D = pairing.pairing(rct.C0, sk.K0);
            tct.D = D;
            /**---------------------求E----------------------**/
//        System.out.println(sk.SK2);
//        System.out.println(rct.C11);
            //E=e(SK2',C1')
            Element E = pairing.pairing(sk.SK2, rct.C11);
            tct.E = E;

//        System.out.println(E);
//        Element a = pairing.pairing(pk.g, pk.g);
//        Element b =mk.a2.mul(rct.s);
//        a.powZn(b);
//        System.out.println(a);System.out.println(pk.g);
            /**---------------------求F----------------------**/
            //-B
            Element _B = B.duplicate().invert();
            //F=D/B
            tct.F = D.duplicate().mul(_B);

//        System.out.println(tct.F);
//        Element z_invert = sk.z.duplicate().invert();
//        Element a = pairing.pairing(pk.g, pk.g);
//        Element b =mk.a1.mul(rct.s);
//        b.mul(z_invert);
//        a.powZn(b);
//        System.out.println(a);

        }
        return tct;
    }
    /**---------------------------decrypt解密阶段用到的函数---------------------------**/
    /**
     * 解密
     *
     * @param pk            SK
     * @param attributes_A  用户属性
     * @param attributes_RL 用户撤销的属性
     * @param thresholds    门限
     * @param CTPathName    密文
     * @param DPathName     解密后的明文
     */
    public static void decrypt(PK pk, SK sk, TCT tct, RCT rct, String attributes_A, String attributes_RL, ArrayList<AAK> AAKList, String thresholds, String[][] attributes_Us, String CTPathName, String DPathName) throws Exception {
        Pairing pairing = pk.pairing;
        //将字符串解析成字符数组
        ArrayList<String> attrList_A = parseString2ArrayList(attributes_A);
        //将用户撤销的属性解析成字符数组
        ArrayList<String> arrayList_RL = parseString2ArrayList(attributes_RL);
        //将各个AA中的门限值解析成字符数组
        ArrayList<String> arrayList_thresholds = parseString2ArrayList(thresholds);
        //明文
        Element M;
        /**--------------------------------如果无属性撤销---------------------------------**/
        if (arrayList_RL.size() == 0) {
            /**---------------------求E*F^z----------------------**/
            //E*F^z
            Element EFz = tct.E.duplicate().mul(tct.F.duplicate().powZn(sk.z));
            //1/(E*F^z)
            EFz.invert();
            /**---------------------求明文M----------------------**/
            M = tct.C.duplicate().mul(EFz);
        }
        /**--------------------------------如果有属性被撤销---------------------------------**/
        else {
            /**---------------------求剩余的Bi----------------------**/
            boolean flag = false;
            for (int i = 0; i < attrList_A.size(); i++) {

                for (int j = 0; j < arrayList_RL.size(); j++) {
                    if (attrList_A.get(i).equals(arrayList_RL.get(j))) {
                        flag = true;
                        break;
                    }
                }
                //如果p(i)==x
                if (flag) {
                    //B1=e(Ci,1',Ki,1)
                    Element B1 = pairing.pairing(rct.C1.get(attrList_A.get(i)), sk.K1);
                    //B2=e(Ci,2',Ki,2)
                    Element B2 = pairing.pairing(rct.C2.get(attrList_A.get(i)), sk.K2.get(attrList_A.get(i)));
                    //Ki,3=(Ki,3)^Vx
                    sk.K3.get(attrList_A.get(i)).powZn(rct.Vx);
                    //B3=e(Ci,3',(Ki,3)^Vx)
                    Element B3 = pairing.pairing(rct.C3.get(attrList_A.get(i)), sk.K3.get(attrList_A.get(i)));
                    //B1=e(Ci,1',Ki,1)*e(Ci,2',Ki,2)
                    B1.mul(B2);
                    //B1=e(Ci,1',Ki,1)*e(Ci,2',Ki,2)*e(Ci,3',(Ki,3)^Vx)
                    B1.mul(B3);
                    tct.Bi.put(attrList_A.get(i), B1.duplicate());
                }
                //如果p(i)不等于x
                else {
                }
                flag = false;
            }
            /**---------------------求E*F^z----------------------**/
            //E*F^z
            Element EFz = tct.E.duplicate().mul(tct.F.duplicate().powZn(sk.z));
            //1/(E*F^z)
            EFz.invert();
            /**---------------------求明文M----------------------**/
            M = tct.C.duplicate().mul(EFz);

        }
        System.out.println("解密后计算出AES种子：" + M);
        //读取本地的CT密文文件
        byte[] ciphertextFileBuf = FileOperation.file2byte(CTPathName);//AES文件，密文文件
        byte[] pltBuf = AESCoder.decrypt(M.toBytes(), ciphertextFileBuf);
        //明文
        FileOperation.byte2File(pltBuf, DPathName);
        System.out.println("文件解密成功，解密后的文件已保存到本地！");
    }

    /**
     * 求解拉格朗日插系数 求deta(0) (x-j)/(i-j)，即x=0，j=currentAttr，i=attrsList.get(i)
     * 求p(0),对于n-1阶多项式来说，必须知道至少n个点才能求出，比如求p(0),对于4阶多项式来说，必须知道至少4个点才能求出p(0)的值
     * 处理属性时，需要先将属性哈希到Zr群上，得到一个大质数
     *
     * @param attrsList   属性集合
     * @param currentAttr 当前属性
     */
    private static Element lagrangeCoefficient(PK pk, ArrayList<String> attrsList, String currentAttr) throws NoSuchAlgorithmException {
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

    /**
     * 测试用的主函数
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
    }

}
