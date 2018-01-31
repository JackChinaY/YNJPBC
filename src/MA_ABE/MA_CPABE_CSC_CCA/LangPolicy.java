package MA_ABE.MA_CPABE_CSC_CCA;

import MA_ABE.MA_CPABE_CSC_CCA.Entity.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import org.apache.commons.codec.digest.DigestUtils;

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
     * @param attributes_C 属性中心
     */
    public static void setup(MK mk, PK pk, String thresholds, String[][] attributes_C, ArrayList<AAK> AAKList) {
        //仅对于双线性映射，要使用PBC包装并获得性能，必须设置配对工厂的使用PBC（可能）属性
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        //椭圆类型是Type A，生成 对称-质数阶-双线性群,即G1==G2，返回代数结构,代数结构包含：群、环、场（groups, rings and fields）
        Pairing pairing = PairingFactory.getPairing("a.properties");
        //公钥的对运算
        pk.pairing = pairing;
        //产生1个Zr的随机值 主密钥
        mk.x = pairing.getZr().newRandomElement();
        //公钥中的生成元g，由于椭圆曲线是加法群，所以G群中任意一个元素都可以作为生成元
        pk.g = pairing.getG1().newRandomElement();
        //公钥中的g2
        pk.g2 = pairing.getG1().newRandomElement();
        //公钥中的Z=e(g^x,g2)
        Element g1 = pk.g.duplicate().powZn(mk.x);
        pk.Z = pairing.pairing(g1, pk.g2).duplicate();
        //公钥中的h0
        pk.h0 = pairing.getG1().newRandomElement();
        //公钥中的kexi 1-3
        pk.d1 = pairing.getG1().newRandomElement();
        pk.d2 = pairing.getG1().newRandomElement();
        pk.d3 = pairing.getG1().newRandomElement();
        System.out.println("系统主密钥 " + mk.toString());
        System.out.println("系统公钥 " + pk.toString());
        //将各个AA中的门限值解析成字符数组
        ArrayList<String> arrayList_thresholds = parseString2ArrayList(thresholds);
        /**---------------------------为各个属性中心AA生成属性中心私钥ASK和属性中心公钥APK---------------------------**/
        for (int i = 0; i < attributes_C.length; i++) {
            AAK aak = new AAK();
            //各当前的属性中心的门限值赋值
            aak.threshold = Integer.parseInt(arrayList_thresholds.get(i));
            aak.s = DigestUtils.md5Hex(String.valueOf(i));
            //生成属性中心公钥APK
            aak.apk = new APK();
            aak.apk.Hi = new HashMap<>();
            //为属性中心的每个属性生成G1随机值
            for (int j = 0; j < attributes_C[i].length; j++) {
                aak.apk.Hi.put(attributes_C[i][j], pairing.getG1().newRandomElement());
            }
            AAKList.add(aak);
//            System.out.println("第" + (i + 1) + "个属性中心的APK生成成功！");
        }
        System.out.println("各属性中心私钥： " + AAKList);
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
    //endregion

    //region //--keygen生成私钥阶段用到的函数--//
    /**---------------------------keygen生成私钥阶段用到的函数---------------------------**/
    /**
     * 生成私钥 文件的读取和保存工作 输入PK、MK、ATTR(用户属性)，输出SK
     *
     * @param mk           PK
     * @param pk           SK
     * @param AAKList      属性全集
     * @param attributes_A 用户的属性
     */
    public static SK keygen(MK mk, PK pk, ArrayList<AAK> AAKList, String attributes_A) throws NoSuchAlgorithmException {
        Pairing pairing = pk.pairing;
        //将用户属性解析成字符数组
        ArrayList<String> arrayList_A = parseString2ArrayList(attributes_A);
        System.out.println("集合A大小：" + arrayList_A.size() + "个，即：" + arrayList_A);
        Element Zr_temp = pairing.getZr().newElement();
        Element nodeID = pairing.getZr().newElement();
        Element Zr_r = pairing.getZr().newElement();
        Element G1_temp1;
        Element G1_temp2;
        //产生私钥
        SK sk = new SK();
        //给小钥匙赋值属性
        sk.comps = new HashMap<>();
        System.out.println("私钥中共有" + arrayList_A.size() + "个小钥匙！");
        //每个AA产生的私钥，循环的次数是AA的个数
        for (int i = 0; i < AAKList.size(); i++) {
            //根据AA的种子产生一个Zr的值
            Element p0 = Hash4Zr(pk, AAKList.get(i).s);
//            System.out.println("AA: " + p0);
            //构造一个多项式
            Polynomial polynomial = createRandomPolynomial(AAKList.get(i).threshold - 1, p0);
            //对单个属性中心遍历
            for (Map.Entry<String, Element> entry : AAKList.get(i).apk.Hi.entrySet()) {
                //对用户属性遍历
                for (String str : arrayList_A) {
                    //如果属性中心的属性和用于属性相同，就新建一个SKi
                    if (entry.getKey().equals(str)) {
                        SKComp comp = new SKComp();
                        comp.attr = str;
                        //计算ai=(g2^q(i))*((h0*hi)^ri)
                        nodeID = Hash4Zr(pk, str);//将当前属性哈希到Zr上
                        //Zr_temp=q(i)
                        Zr_temp = computePolynomial(Zr_temp, polynomial, nodeID);
                        //G1_temp1=g2^q(i)
                        G1_temp1 = pk.g2.duplicate().powZn(Zr_temp);
                        //G1_temp2=h0*hi
                        G1_temp2 = pk.h0.duplicate().mul(entry.getValue());
                        Zr_r.setToRandom();//ri
                        //G1_temp2=(h0*hi)^ri
                        G1_temp2.powZn(Zr_r);
                        //ai=(g2^q(i))*((h0*hi)^ri)
                        comp.a = G1_temp1.mul(G1_temp2);
                        //计算b=g^ri
                        comp.b = pk.g.duplicate().powZn(Zr_r);
                        //将pk中的h0---h2L-1复制到comp.hList
                        comp.hList = new HashMap<>();//2L-1个
                        //将和用户属性一样的属性值赋值过去, TODO 此处是创新点1,减少h的个数
                        for (Map.Entry<String, Element> entry2 : AAKList.get(i).apk.Hi.entrySet()) {
//                            for (String str2 : arrayList_A) {
//                                if (entry2.getKey().equals(str2)) {
                            comp.hList.put(entry2.getKey(), entry2.getValue().duplicate());
//                                    break;
//                                }
//                            }
                        }
                        //计算Ci,1-----Ci,2L-1
                        for (Map.Entry<String, Element> entry3 : comp.hList.entrySet()) {
                            entry3.getValue().powZn(Zr_r);
                        }
                        sk.comps.put(str, comp);
                        System.out.print(str + ", ");
                        break;
                    }
                }
            }
        }
        System.out.println();
        System.out.println("私钥中各个AA的小钥匙到此生成完毕！");
        //CA产生的私钥
        Element Zr_temp3 = pairing.getZr().newElement();
        Zr_temp3.setToZero();
        for (int i = 0; i < AAKList.size(); i++) {
            //Zr_temp3=对K个s求和
            Zr_temp3.add(Hash4Zr(pk, AAKList.get(i).s).duplicate());
        }
        //Zr_temp4 = x-求和(K个xi)
        Element Zr_temp4 = mk.x.duplicate().sub(Zr_temp3);
//        System.out.println("4  " + Zr_temp4);
        sk.Dca = pk.g2.duplicate().powZn(Zr_temp4);
        System.out.println("私钥中CA的小钥匙生成成功！");
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
     * @param pk         SK
     * @param AAKList    属性中心们
     * @param MPathName  明文
     * @param CTPathName 密文
     */
    public static Ciphertext encrypt(PK pk, ArrayList<AAK> AAKList, String MPathName, String CTPathName) throws Exception {
        Pairing pairing = pk.pairing;
        //表示GT的随机值，AES种子
        Element M = pairing.getGT().newRandomElement();
        //Zr_s=s
        Element Zr_s = pairing.getZr().newRandomElement();
        //GT_temp=Z^s
        Element GT_temp = pk.Z.duplicate().powZn(Zr_s);
        //密文实体
        Ciphertext ciphertext = new Ciphertext();
        /**-----------------------------------接下来求C0-------------------------------**/
        //计算C0=M*Z^s
        ciphertext.C0 = M.duplicate().mul(GT_temp);
        /**-----------------------------------接下来求C1-------------------------------**/
        //计算C1=g^s
        ciphertext.C1 = pk.g.duplicate().powZn(Zr_s);
//        //计算C2
//        Element G1_temp1 = pairing.getG1().newElement();
//        G1_temp1.setToOne();
//        //循环乘hj
//        for (int i = 0; i < AAKList.size(); i++) {
//            for (Map.Entry<String, Element> entry : AAKList.get(i).apk.Hi.entrySet()) {
//                G1_temp1.mul(entry.getValue());
//            }
//        }
//        //G1_temp1=h0*(循环乘hj)
//        G1_temp1.mul(pk.h0);
//        //计算C2=(h0*(循环乘hj))^s
//        ciphertext.C2 = G1_temp1.powZn(Zr_s).duplicate();
        /**-----------------------------------接下来求Ci-------------------------------**/
        ciphertext.Ci = new ArrayList<>();
        Element G1_temp1 = pairing.getG1().newElement();
        G1_temp1.setToOne();
        //循环次数是AA的个数
        for (int i = 0; i < AAKList.size(); i++) {
            //G1_temp1=循环乘hj
            for (Map.Entry<String, Element> entry : AAKList.get(i).apk.Hi.entrySet()) {
                G1_temp1.mul(entry.getValue());
            }
            //G1_temp1=h0*(循环乘hj)
            G1_temp1.mul(pk.h0);
            //Ci=(h0*(循环乘hj))^s
            ciphertext.Ci.add(G1_temp1.powZn(Zr_s).duplicate());
            G1_temp1.setToOne();
//            Element e_C2_D2 = pairing.pairing(pk.g.duplicate().powZn(Hash4Zr(pk, AAKList.get(i).s).duplicate()), pk.g2.duplicate().powZn(Zr_s)).duplicate();
//            System.out.println("e_C2_D2 " + e_C2_D2);
        }
        /**-----------------------------------接下来求C3-------------------------------**/
        //计算C3=C3=(d1^c * d2^r * d3)^s，其中c=Hash(T,C0,C1,C2)
        G1_temp1.setToOne();
        //将C0C1C2转换成字节数组 TODO 如果属性中心个数增加了，此处需要收到增加参数
        byte[] byteArray = Element2ByteArray(ciphertext.C0, ciphertext.C1, ciphertext.Ci.get(0), ciphertext.Ci.get(1), ciphertext.Ci.get(2));
        //求哈希值
        Element c = Hash4Zr(pk, byteArray);
        //G1D1=d1^c
        Element G1D1 = pk.d1.duplicate().powZn(c);
        //Zr的随机值r
        Element Zr_r = pairing.getZr().newRandomElement();
        //赋值r
        ciphertext.r = Zr_r.duplicate();
        //G1_temp1=1*d1^c
        G1_temp1.mul(G1D1);
        //G1D2=d2^r
        Element G1D2 = pk.d2.duplicate().powZn(Zr_r);//G1D2=d2^r
        //G1_temp1=1 * d1^c * d2^r
        G1_temp1.mul(G1D2);
        //G1_temp1=1 * d1^c * d2^r * d3
        G1_temp1.mul(pk.d3.duplicate());
        //G1_temp1=( 1 * d1^c * d2^r * d3 )^s
        G1_temp1.powZn(Zr_s);
        //C3=( 1 * d1^c * d2^r * d3 )^s
        ciphertext.C3 = G1_temp1.duplicate();
        /**-----------------------------------接下来开始对明文加密-------------------------------**/
        //加密成功
        System.out.println("AES加密文件的种子：" + M);
        //从本地读取明文文件
        byte[] messageBuf = FileOperation.file2byte(MPathName);
        //明文的字节数组
        //先将明文使用AES方法进行加密
        byte[] aesBuf = AESCoder.encrypt(M.duplicate().toBytes(), messageBuf);
        // 将密文保存到本地
        FileOperation.Ciphertext2File(CTPathName, aesBuf);
        System.out.println("密文成功生成，已保存到本地！");
        return ciphertext;
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
    //endregion

    // region //--decrypt解密阶段用到的函数--//
    /**---------------------------decrypt解密阶段用到的函数---------------------------**/
    /**
     * 解密 文件的读取和保存工作
     *
     * @param pk           SK
     * @param attributes_A 用户属性
     * @param attributes_S 属性中心
     * @param thresholds   门限
     * @param CTPathName   密文
     * @param DPathName    解密后的明文
     */
    public static void decrypt(PK pk, SK sk, Ciphertext ciphertext, String attributes_A, String[][] attributes_S, ArrayList<AAK> AAKList, String thresholds, String CTPathName, String DPathName) throws Exception {
        Pairing pairing = pk.pairing;
        //将字符串解析成字符数组
        ArrayList<String> attrList_A = parseString2ArrayList(attributes_A);
        //将各个AA中的门限值解析成字符数组
        ArrayList<String> arrayList_thresholds = parseString2ArrayList(thresholds);
        /**-----------------------------------接下来验证用户属性-------------------------------**/
        //标志位，用户的属性是否满足门限值
        boolean isSatisfy = true;
        for (int i = 0; i < attributes_S.length; i++) {
            //第i个AA管理的属性
            ArrayList<String> arrayList_AAi = parseStringArray2ArrayList(attributes_S, i);
            //求A和AAi的并集
            ArrayList<String> arrayList_AAndAAi = intersectionArrayList(attrList_A, arrayList_AAi);
//            System.out.println("用户和AAi的交集的大小:" + arrayList_AAndAAi.size() + "个，即：" + arrayList_AAndAAi);
            //判断交集中元素个数和该AA的门限值的大小关系
            if (arrayList_AAndAAi.size() < Integer.parseInt(arrayList_thresholds.get(i))) {
                isSatisfy = false;
                System.err.println("解密失败，用户的属性不满足第" + (i + 1) + "个属性中心的门限要求！");
                break;
            }
        }
        /**------------------------------接下来验证两个参数，第一个参数e(g,C2)--------------------------**/
        //循环乘hj
        for (int i = 0; i < AAKList.size(); i++) {
            Element G1_temp3 = pairing.getG1().newElement();
            G1_temp3.setToOne();
            //G1_temp1=循环乘hj
            for (Map.Entry<String, Element> entry : AAKList.get(i).apk.Hi.entrySet()) {
                G1_temp3.mul(entry.getValue());
            }
            //G1_temp1=h0*(循环乘hj)
            G1_temp3.mul(pk.h0);
            Element e_g_C2_1 = pairing.pairing(pk.g, ciphertext.Ci.get(i));
            Element e_g_C2_2 = pairing.pairing(ciphertext.C1, G1_temp3);
            if (e_g_C2_1.isEqual(e_g_C2_2)) {
                System.out.println("第一个条件满足，关于第" + (i + 1) + "个属性中心！");
            } else {
                System.err.println("第一个条件不满足，程序退出！");
                System.exit(0);
            }
        }
        /**-----------------------------------接下来验证第二个参数e(g,C3)-------------------------------**/
        Element G1_temp4 = pairing.getG1().newElement();
        G1_temp4.setToOne();
        //计算C3=C3=(d1^c * d2^r * d3)^s，其中c=Hash(T,C0,C1,C2)
        //将C0C1C2转换成字节数组，TODO 如果属性中心个数增加了，此处需要收到增加参数
        byte[] byteArray = Element2ByteArray(ciphertext.C0, ciphertext.C1, ciphertext.Ci.get(0), ciphertext.Ci.get(1), ciphertext.Ci.get(2));
        //求哈希值
        Element c = Hash4Zr(pk, byteArray);
        //G1D1=d1^c
        Element G1D1 = pk.d1.duplicate().powZn(c);
        //G1_temp4=1*d1^c
        G1_temp4.mul(G1D1);
        //G1D2=d2^r
        Element G1D2 = pk.d2.duplicate().powZn(ciphertext.r);//G1D2=d2^r
        //G1_temp4=1 * d1^c * d2^r
        G1_temp4.mul(G1D2);
        //G1_temp4=1 * d1^c * d2^r * d3
        G1_temp4.mul(pk.d3.duplicate());
        //G1_temp1=( 1 * d1^c * d2^r * d3 )^s
        Element e_g_C3_1 = pairing.pairing(pk.g, ciphertext.C3).duplicate();
        Element e_g_C3_2 = pairing.pairing(ciphertext.C1, G1_temp4).duplicate();
        if (e_g_C3_1.isEqual(e_g_C3_2)) {
            System.out.println("第二个条件满足！");
        } else {
            System.err.println("第二个条件不满足，程序退出！");
            System.exit(0);
        }
        /**------------------------如果SK满足密文中的访问策略，则开始解密-----------------------**/
        if (isSatisfy) {
            System.out.println("用户属性满足，开始解密...");
            //解密用的D1和D2
            Element D1;
            Element D2 = pairing.getGT().newElement();
            D2.setToOne();
            //接下来求D1
            Element G1_temp1 = pairing.getG1().newElement();
            Element G1_temp2 = pairing.getG1().newElement();
            Element GT_temp = pairing.getGT().newElement();
            //临时变量，便于求拉格朗日系数
            Element Zr_temp1 = pairing.getZr().newElement();
            G1_temp1.setToOne();
            G1_temp2.setToOne();
            GT_temp.setToOne();
            for (int k = 0; k < attributes_S.length; k++) {
                //第i个AA管理的属性
                ArrayList<String> arrayList_AAi = parseStringArray2ArrayList(attributes_S, k);
                //求A和AAi的并集
                ArrayList<String> arrayList_AAndAAi = intersectionArrayList(attrList_A, arrayList_AAi);
                ArrayList<String> arrayList_As = new ArrayList<>();
                //求集合A`，共threshold个
                for (int i = 0; i < Integer.parseInt(arrayList_thresholds.get(k)); i++) {
                    arrayList_As.add(arrayList_AAndAAi.get(i));
                }
                System.out.print("集合A`的大小:" + arrayList_As.size() + "个，即：" + arrayList_As);
                System.out.println("， 集合AAi的大小:" + arrayList_AAi.size() + "个，即：" + arrayList_AAi);
                /**-----------------------------------接下来求D1-------------------------------**/
                //最外层连乘，处理i
                for (int i = 0; i < arrayList_As.size(); i++) {
                    //最内层连乘，处理j，G1_temp1=循环乘Ci,j
                    for (int j = 0; j < arrayList_AAi.size(); j++) {
                        //i不等于j时
                        if (!arrayList_AAi.get(j).equals(arrayList_As.get(i))) {
                            G1_temp1.mul(sk.comps.get(arrayList_As.get(i)).hList.get(arrayList_AAi.get(j)));
                        }
                    }
                    //G1_temp1=ai*(循环乘Ci,j)
                    G1_temp1.mul(sk.comps.get(arrayList_As.get(i)).a);
                    //求拉格朗日系数 Zr_temp1=deta(0) (x-j)/(i-j)
                    Zr_temp1 = lagrangeCoefficient(pk, arrayList_As, arrayList_As.get(i));
                    //G1_temp1=ai*(循环乘Ci,j)^deta(0)
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
                for (int i = 0; i < arrayList_As.size(); i++) {
                    //G1_temp1=bi
                    G1_temp1.mul(sk.comps.get(arrayList_As.get(i)).b);
                    //求拉格朗日系数 Zr_temp1=deta(0) (x-j)/(i-j)
                    Zr_temp1 = lagrangeCoefficient(pk, arrayList_As, arrayList_As.get(i));
                    //G1_temp1=bi^deta(0)
                    G1_temp1.powZn(Zr_temp1);
                    G1_temp2.mul(G1_temp1);
                    G1_temp1.setToOne();
                }
                D2 = G1_temp2.duplicate();
                //计算M
                Element e_C2_D2 = pairing.pairing(ciphertext.Ci.get(k), D2).duplicate();
                Element e_C1_D1 = pairing.pairing(ciphertext.C1, D1).duplicate();
                //e_C2_D2=1/e_C2_D2
                e_C2_D2.invert();
                //e_C1_D1=e_C1_D1/e_C2_D2=e(g,g2^s)^xi
                e_C1_D1.mul(e_C2_D2);
                //GT_temp=e(g,g2^s)^(x1+x2+x3)
                GT_temp.mul(e_C1_D1);
                //累加置1
                G1_temp2.setToOne();
            }
            //GT_temp2=e(g,g2^s)^x-(x1+x2+x3)
            Element GT_temp2 = pairing.pairing(ciphertext.C1, sk.Dca);
            //GT_temp=e(g^x,g2^s)=Z^s
            GT_temp.mul(GT_temp2);
            //GT_temp=1/Z^s
            GT_temp.invert();
            //AES种子
            Element M = ciphertext.C0.duplicate().mul(GT_temp);
            System.out.println("解密后计算出AES种子：" + M);
            //读取本地的CT密文文件
            byte[] ciphertextFileBuf = FileOperation.file2byte(CTPathName);//AES文件，密文文件
            byte[] pltBuf = AESCoder.decrypt(M.toBytes(), ciphertextFileBuf);
            //明文
            FileOperation.byte2File(pltBuf, DPathName);
            System.out.println("文件解密成功，解密后的文件已保存到本地！");
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
    //endregion

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
