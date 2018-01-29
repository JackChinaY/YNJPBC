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
    public static void setup(int K, PK pk, String attributes_U, ArrayList<AAK> AAKList) {
        //给系统公钥赋值
        //仅对于双线性映射，要使用PBC包装并获得性能，必须设置配对工厂的使用PBC（可能）属性
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        //椭圆类型是Type A，生成 对称-质数阶-双线性群,即G1==G2，返回代数结构,代数结构包含：群、环、场（groups, rings and fields）
        Pairing pairing = PairingFactory.getPairing("a.properties");
        pk.pairing = pairing;
        //给系统公钥赋值
        //公钥中的生成元g，//设定并存储一个生成元。由于椭圆曲线是加法群，所以G群中任意一个元素都可以作为生成元
        pk.g = pairing.getG1().newRandomElement();
        //公钥中的y0
        pk.y0 = pairing.getZr().newRandomElement();
        //属性中心AA的个数
        pk.K = K;
        //系统公钥，GT的,Y0=e(g,g)^y0
        pk.Y0 = pairing.pairing(pk.g, pk.g).duplicate();
        pk.Y0.powZn(pk.y0);
        //将一个字符串解析成字符数组
        ArrayList<String> arrayList = parseString2ArrayList(attributes_U);
        System.out.println("属性全集U大小： " + arrayList.size() + "个，即：" + arrayList);
        //PKComp类型，k是AA的下标
        pk.tk = new ArrayList<>();
        //对每个PKComp里面生成n个随机变量
        for (int i = 0; i < K; i++) {
            PKComp comp = new PKComp();
            //各属性中心的种子，暂时用i值的MD5算法实现
            comp.s = DigestUtils.md5Hex(String.valueOf(i));
            //属性的个数
            comp.n = arrayList.size();
            //Zr类型，i=1,2,...,n
            comp.ti = new HashMap<>();
//            对ti赋值
            for (int j = 0; j < arrayList.size(); j++) {
                comp.ti.put(arrayList.get(j), pairing.getZr().newRandomElement());
            }
            pk.tk.add(comp);
//            System.out.print((i + 1) + ", ");
        }
//        System.out.println();
        System.out.println("系统公钥 " + pk.toString());
        /**---------------------------为各个属性中心AA生成属性中心私钥ASK和属性中心公钥APK---------------------------**/
        for (int i = 0; i < K; i++) {
            AAK aak = new AAK();
            //生成属性中心私钥ASK
            aak.ask = new ASK();
            aak.ask.s = pk.tk.get(i).s;
            aak.ask.n = arrayList.size();
            aak.ask.ti = new HashMap<>();
            aak.ask.ti = pk.tk.get(i).ti;
            //生成属性中心公钥APK
            aak.apk = new APK();
            aak.apk.Ti = new HashMap<>();
            for (int j = 0; j < arrayList.size(); j++) {
                aak.apk.Ti.put(arrayList.get(j), pk.g.duplicate().powZn(pk.tk.get(i).ti.get(arrayList.get(j))));
            }
            AAKList.add(aak);
//            System.out.println("第" + (i + 1) + "个属性中心的ASK和APK生成成功！");
        }
        System.out.println("各属性中心私钥： " + AAKList);
//        System.out.println("系统公钥中2L个G的随机值如下，共" + pk.hList.length + "个");
//        for (Element s : pk.hList) {
//            System.out.println(s);
//        }
    }

    /**
     * 将一个字符串解析成字符串集合
     * 将一个字符串按分隔符分开，返回字符串类型的数组，如：将一个字符串，此字符串包含一个人的各种属性，分隔符是空格，最后返回一个数组，数组中包含各个小属性
     */
    private static ArrayList<String> parseString2ArrayList(String string) {
        ArrayList<String> arrayList = new ArrayList<>();
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
     * 将一个二维字符串数组的指定行的数据解析成字符串集合
     *
     * @param array 二维数组
     */
    private static ArrayList<String> parseStringArray2ArrayList(String[][] array) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                arrayList.add(array[i][j]);
            }
        }
        return arrayList;
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

    //region //--keygen生成私钥阶段用到的函数--//
    /**---------------------------keygen生成私钥阶段用到的函数---------------------------**/
    /**
     * 生成私钥 文件的读取和保存工作 输入PK、MK、ATTR(用户属性)，输出SK
     *
     * @param pk           公钥
     * @param thresholds   门限值
     * @param AAKList      属性中心AA们
     * @param attributes_A 用户的属性
     */
    public static SK keygen(PK pk, String thresholds, ArrayList<AAK> AAKList, String attributes_A) throws NoSuchAlgorithmException {
        //将各个AA中的门限值解析成字符数组
        ArrayList<String> attrList_thresholds = parseString2ArrayList(thresholds);
        Pairing pairing = pk.pairing;
        Element nodeID = pairing.getZr().newElement();
        Element Zr_temp1 = pairing.getZr().newElement();
        Element Zr_temp2;
        //产生私钥
        SK sk = new SK();
        //给小钥匙赋值属性
        sk.comps = new ArrayList<SKComp>();
        //每个AA产生的私钥，循环的次数是AA的个数
        for (int i = 0; i < AAKList.size(); i++) {
            //根据AA的种子产生一个Zr的值，即p(0)=yk,u
            Element Zr_yku = Hash4Zr(pk, AAKList.get(i).ask.s);
            //构造一个多项式，每个AA都会产生一个多项式，彼此各不相同
            Polynomial polynomial = createRandomPolynomial(Integer.parseInt(attrList_thresholds.get(i)) - 1, Zr_yku);
            //将用户属性解析成字符数组
            ArrayList<String> arrayList_A = parseString2ArrayList(attributes_A);
//            System.out.println("用户属性集合A大小：" + arrayList_A.size() + "个，即：" + arrayList_A);
            //添加一个AA的私钥
            sk.comps.add(new SKComp());
            //AA的Dk,i
            sk.comps.get(i).Dki = new HashMap<>();
            //循环的次数是用户属性的个数
            for (int j = 0; j < arrayList_A.size(); j++) {
                //设置当前属性值
//                nodeID.set(Integer.parseInt(arrayList_A.get(j)));//j的值，j是属性
                nodeID = Hash4Zr(pk, arrayList_A.get(j));//j的值，j是属性
                //计算多项式q(i)的值，Zr_temp=q(i)
                Zr_temp1.setToOne();
                Zr_temp1 = computePolynomial(Zr_temp1, polynomial, nodeID);
                //获取tk,i的值
                Zr_temp2 = AAKList.get(i).ask.ti.get(arrayList_A.get(j)).duplicate();
                //Zr_temp1=1/(tk,i)
                Zr_temp2.invert();
                //q(i)*(1/(tk,i))
                Zr_temp2.mul(Zr_temp1);
                //Dki=g^(q(i)*(1/(tk,i)))
                Element Dki = pk.g.duplicate().powZn(Zr_temp2);
                sk.comps.get(i).Dki.put(arrayList_A.get(j), Dki);
//                System.out.println("第" + (i + 1) + "个属性中心的第" + (j + 1) + "个元素的小钥匙生成成功，元素是：" + arrayList_A.get(j));
            }
        }
        System.out.println("私钥中各个AA的小钥匙到此生成完毕！");
        //CA产生的私钥
        Element Zr_temp3 = pairing.getZr().newElement();
        Zr_temp3.setToZero();
        for (int i = 0; i < AAKList.size(); i++) {
            //Zr_temp3=对K个yk,u求和
            Zr_temp3.add(Hash4Zr(pk, AAKList.get(i).ask.s).duplicate());
        }
        //Zr_temp4 = y0-求和(K个yk,u)
        Element Zr_temp4 = pk.y0.duplicate().sub(Zr_temp3);
        sk.Dca = pk.g.duplicate().powZn(Zr_temp4);
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
        polynomial.coef[0].set(randomValue.duplicate());
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
     * @param pk                        SK
     * @param attributes_S              门限属性
     * @param messageFilePathAndName    明文文件保存的路径和文件名
     * @param ciphertextFilePathAndName 密文文件保存的路径和文件名
     */
    public static Ciphertext encrypt(PK pk, String[][] attributes_S, ArrayList<AAK> AAKList, String messageFilePathAndName, String ciphertextFilePathAndName) throws Exception {
        Pairing pairing = pk.pairing;
        //计算Y0^s，Zr_s=s
        Element Zr_s = pairing.getZr().newRandomElement();
        //GT_temp=Y0^s
        Element GT_temp = pk.Y0.duplicate().powZn(Zr_s);
        //密文实体
        Ciphertext ciphertext = new Ciphertext();
        //表示GT的随机值，AES种子
        Element M = pairing.getGT().newRandomElement();
        //计算E=M*Y0^s
        ciphertext.E = M.duplicate().mul(GT_temp);
        //计算Eca=g^s
        ciphertext.Eca = pk.g.duplicate().powZn(Zr_s);
        /**-----------------------------------接下来求Ek,i-------------------------------**/
        ciphertext.Ek = new ArrayList<CiphertextComp>();
        //循环的次数是AA的个数
        for (int i = 0; i < attributes_S.length; i++) {
            ciphertext.Ek.add(new CiphertextComp());
            ciphertext.Ek.get(i).Eki = new HashMap<>();
            //循环的次数是第i个AA管理的属性的个数
            for (int j = 0; j < attributes_S[i].length; j++) {
                ciphertext.Ek.get(i).Eki.put(attributes_S[i][j], AAKList.get(i).apk.Ti.get(attributes_S[i][j]).duplicate().powZn(Zr_s));
//                System.out.println("第" + (i + 1) + "个属性中心的第" + (j + 1) + "个元素添加成功，元素是：" + attributes_S[i][j]);
            }
        }
        //加密成功
        System.out.println("AES加密文件的种子：" + M);
        /**-----------------------------------接下来开始对明文加密-------------------------------**/
        //从本地读取明文文件
        byte[] messageBuf = FileOperation.file2byte(messageFilePathAndName);
        //明文的字节数组
        //先将明文使用AES方法进行加密
        byte[] aesBuf = AESCoder.encrypt(M.duplicate().toBytes(), messageBuf);
        // 将密文保存到本地
        FileOperation.Ciphertext2File(ciphertextFilePathAndName, aesBuf);
        System.out.println("密文成功生成，已保存到本地！");
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
     * @param attributes_S              门限属性
     * @param thresholds                门限
     * @param ciphertextFilePathAndName 密文
     * @param decryptFilePathAndName    解密后的明文
     */
    public static void decrypt(PK pk, SK sk, Ciphertext ciphertext, String attributes_A, String[][] attributes_S, String thresholds, String ciphertextFilePathAndName, String decryptFilePathAndName) throws Exception {
        Pairing pairing = pk.pairing;
        //将用户属性解析成字符数组
        ArrayList<String> attrList_A = parseString2ArrayList(attributes_A);
        //将各个AA中的门限值解析成字符数组
        ArrayList<String> attrList_thresholds = parseString2ArrayList(thresholds);
        //标志位，用户的属性是否满足门限值
        boolean isSatisfy = true;
        for (int i = 0; i < attributes_S.length; i++) {
            //第i个AA管理的属性
            ArrayList<String> arrayList_AAi = parseStringArray2ArrayList(attributes_S, i);
            //求A和AAi的并集
            ArrayList<String> arrayList_AAndAAi = intersectionArrayList(attrList_A, arrayList_AAi);
            System.out.println("用户和AAi的交集的大小:" + arrayList_AAndAAi.size() + "个，即：" + arrayList_AAndAAi);
            //判断交集中元素个数和该AA的门限值的大小关系
            if (arrayList_AAndAAi.size() < Integer.parseInt(attrList_thresholds.get(i))) {
                isSatisfy = false;
                System.err.println("解密失败，用户的属性不满足第" + (i + 1) + "个属性中心的门限要求！");
                break;
            }
        }
        //如果SK满足密文中的访问策略，则开始解密
        if (isSatisfy) {
            System.out.println("用户属性满足，开始解密...");
            //门限值个必要属性，由用户和AA们的交集前threadhold个属性组成
//            System.exit(0);
            Element GT_temp1 = pairing.getGT().newElement();
            //临时变量，便于求拉格朗日系数
            Element Zr_temp1 = pairing.getZr().newElement();
            GT_temp1.setToOne();
            //用于计算每个AA的对运算
            Element e_Eki_Dki;
            //用于累加各个AA中属性的个数
            //循环的次数是AA的个数
            for (int i = 0; i < attributes_S.length; i++) {
                //获取第i个AA管理的属性
                ArrayList<String> arrayList_Ack = parseStringArray2ArrayList(attributes_S, i);//Ack
                //求Ack和A的并集
                ArrayList<String> arrayList_AckAndA_threadhold = intersectionArrayList(attrList_A, arrayList_Ack);
                System.out.println("集合Ack和A并集的大小:" + arrayList_AckAndA_threadhold.size() + "个，即：" + arrayList_AckAndA_threadhold);
                //循环的次数是交集中元素的个数
                for (int j = 0; j < arrayList_AckAndA_threadhold.size(); j++) {
                    //求e_Eki_Dki
                    e_Eki_Dki = pairing.pairing(ciphertext.Ek.get(i).Eki.get(arrayList_AckAndA_threadhold.get(j)),
                            sk.comps.get(i).Dki.get(arrayList_AckAndA_threadhold.get(j))).duplicate();
                    //求拉格朗日系数 deta(0) (x-j)/(i-j)
                    Zr_temp1 = lagrangeCoef(pk, arrayList_AckAndA_threadhold, arrayList_AckAndA_threadhold.get(j));
                    //乘上拉格朗日系数
                    e_Eki_Dki.powZn(Zr_temp1);
                    GT_temp1.mul(e_Eki_Dki);
                    e_Eki_Dki.setToOne();
                }
            }
            //GT_temp2=Yca^s
            Element GT_temp2 = pairing.pairing(ciphertext.Eca, sk.Dca);
            //GT_temp1=Y0^s
            GT_temp1.mul(GT_temp2);
            //GT_temp1=1/Y0^s
            GT_temp1.invert();
            //AES种子
            Element M = ciphertext.E.duplicate().mul(GT_temp1);
            System.out.println("解密后计算出AES种子：" + M);
            //读取本地的CT密文文件
            byte[] ciphertextFileBuf = FileOperation.file2byte(ciphertextFilePathAndName);//AES文件，密文文件
            byte[] pltBuf = AESCoder.decrypt(M.toBytes(), ciphertextFileBuf);
            //明文
            FileOperation.byte2File(pltBuf, decryptFilePathAndName);
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
    public static void main(String[] args) throws NoSuchAlgorithmException {
//        //仅对于双线性映射，要使用PBC包装并获得性能，必须设置配对工厂的使用PBC（可能）属性
//        PairingFactory.getInstance().setUsePBCWhenPossible(true);
//        //椭圆类型是Type A，生成 对称-质数阶-双线性群,即G1==G2，返回代数结构,代数结构包含：群、环、场（groups, rings and fields）
//        Pairing pairing = PairingFactory.getPairing("a.properties");
//        PK pk = new PK();
//        pk.pairing = pairing;

//读取本地的CT密文文件
        byte[] c = FileOperation.file2byte("E:/ABE/MACPABE/Message_Original.txt");//AES文件，密文文件
        String b = new String(c);
        System.out.println(b);
//        Element g = pairing.getG1().newRandomElement();
//        System.out.println("g:" + g);
//        Element q;
//        q = g.duplicate();
//        System.out.println("q:" + q);
//        Element v = pairing.getG1().newRandomElement();
//        System.out.println("v:" + v);
////        Element s = pairing.getZr().newRandomElement();
////        System.out.println("s:" + s);
//        Element c = g.mul(v).duplicate();
//////        Element g2 = g.duplicate().mul(v);
//////        Element g2 = g.duplicate().powZn(s);
////        Element Z = pairing.pairing(g, v);
//////        g = g.mul(v);
//        System.out.println("c:" + c);
//        System.out.println("g:" + g);
//        System.out.println("v:" + v);
//        System.out.println("q:" + q);
//        Element d = g.mul(v);
//        System.out.println("d:" + d);
//        System.out.println("c:" + c);
//        System.out.println("g:" + g);
//        System.out.println("v:" + v);
////        if (g.isImmutable()) {
////            System.out.println("g不可变");
////        }
//////        if (g2.isImmutable()) {
//////            System.out.println("g2不可变");
//////        }
////        if (v.isImmutable()) {
////            System.out.println("v不可变");
////        }
////        Element g1 = g.getImmutable();
////        System.out.println("g1:" + g1);
////        Element v1 = v.getImmutable();
////        System.out.println("v1:" + v1);
//        Element Z = pairing.pairing(g, v);
//        Element s = pairing.getZr().newRandomElement();
//        Element Zs = Z.powZn(s);
//        Element M1 = pairing.getGT().newRandomElement();
//        System.out.println("M1:" + M1);
//        System.out.println("Zs:" + Zs);
//        Element C0 = M1.mul(Zs);
////        Element Zs1 = Zs.invert();
//        Zs.invert();
//        System.out.println("Zs:" + Zs);
//        Element M2 = C0.mul(Zs);
//        System.out.println("Zs:" + Zs);
//        System.out.println("M2:" + M2);

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
}
