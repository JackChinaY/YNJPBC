package IBE;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.lang.reflect.Proxy;

/**
 * BasicIdent的基于身份的加密体制是由Boneh和Franklin在《Identity-Based Encryption fromthe Weil Pairing》提出的，算法的加解密过程大家可以自行参考下这篇论文，过程还是比较简单的。
 */
public class IBE implements Ident {
    //基本顺序就是 Pairing Field Element
    private Pairing pairing;//配对
    private Field G1;//乘法循环群，又称作双线性群，一个集合，里面元素是一个个小集合，小集合中有3个数，前2个的数值非常大，第三个为0，如{78296357617,17401990，0}
    private Field G2;//乘法循环群，又称作双线性群，一个集合，里面元素是一个个小集合，小集合中有3个数，前2个的数值非常大，第三个为0，如{78296357617,17401990，0}
    private Field Zr;//指数群，一个集合，里面元素是一个个数，数值非常大，如{2697655,13108697}，如果椭圆类型是type A，则该集合中的元素可以不全是质数
    private Field GT;//乘法循环群，双线性群，一个集合，里面元素是一个个点对(x,y)，数值非常大，如{x=786897127,y=34822812}
    private Element MK;//主密钥
    private Element r;//素数阶，位于a.properties文件中的变量r，是G1和G2的素数阶
    private Element g;//G1的生成元
    private Element PK;//系统公钥
    private Element PKu;//用户公钥
    private Element SK;//用户私钥
    private Element V, T1, T2;
    private char[] array;//消息字符数组

    public IBE() {
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        //仅对于双线性映射，要使用PBC包装并获得性能，必须设置配对工厂的使用PBC（可能）属性
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        //椭圆类型是Type A，生成 对称-质数阶-双线性群,即G1==G2，返回代数结构,代数结构包含：群、环、场（groups, rings and fields）
        pairing = PairingFactory.getPairing("a.properties");
        checkSymmetric(pairing);
        //生成一个指数群
        Zr = pairing.getZr();
        //创建一个新的未初始化的元素
        r = Zr.newElement();
        //将变量Ppub，Qu，Su，V初始化为G1中的元素，G1是加法群
        G1 = pairing.getG1();
        PK = G1.newElement();
        PKu = G1.newElement();
        SK = G1.newElement();
        V = G1.newElement();
        //将变量T1，T2V初始化为GT中的元素，GT是乘法群
        Field GT = pairing.getGT();
        T1 = GT.newElement();
        T2 = GT.newElement();
    }

    /**
     * 判断配对是否为对称配对，不对称则输出错误信息
     *
     * @param pairing
     */
    private void checkSymmetric(Pairing pairing) {
        if (!pairing.isSymmetric()) {
            throw new RuntimeException("密钥不对称!");
        }
    }

    @Override
    public void buildSystem() {
        System.out.println("----------------系统初始化阶段-------------------");
        //创建一个新的随机元素。对getImmutable()函数的解释，Java的运算结果都是产生一个新的Element来存储，所以我们需要把运算结果赋值给一个新的Element； Java在进行相关运算时，参与运算的Element值可能会改变。所以，如果需要在运算过程中保留参与运算的Element值，在存储的时候一定要调用getImmutable()
        MK = Zr.newRandomElement().getImmutable();//随机生成主密钥MK
        g = G1.newRandomElement().getImmutable();// 生成G1的生成元g
        PK = g.mulZn(MK);// 计算系统公钥,执行g和MK之间的乘法运算，其中MK必须是整数mod环的元素（即，对于阶为r的Zr）
        System.out.println("G1的生成元 g=" + g);
        System.out.println("系统主密钥 MK=" + MK);
        System.out.println("系统公钥 PK=" + PK);
    }

    @Override
    public void extractSecretKey() {
        System.out.println("-----------------密钥生成阶段--------------------");
        //将byte[] byteArray_G_1哈希到G_1群 "IDu"是用户的唯一身份ID
        PKu = pairing.getG1().newElement().setFromHash("IDu".getBytes(), 0, 3).getImmutable(); //从长度为3的Hash值IDu确定用户U产生的公钥Qu
        SK = PKu.mulZn(MK).getImmutable();
        System.out.println("用户公钥 PKu=" + PKu);
        System.out.println("用户私钥 SK=" + SK);
    }

    @Override
    public void encrypt() {
        System.out.println("-------------------加密阶段----------------------");
        r = Zr.newRandomElement().getImmutable();//随机值
        V = g.mulZn(r);//相乘
        T1 = pairing.pairing(PKu, PK).getImmutable();// 计算e（PK,PKu），对运算
        T1 = T1.powZn(r).getImmutable();//幂运算T1的r次方
        System.out.println("r=" + r);
        System.out.println("V=" + V);
        System.out.println("T1=e（PKu,PK）^r=" + T1);
        //对明文进行异或运算加密
        String message = "Jiangsu University of Science and Technology";
        array = message.toCharArray(); //获取字符数组
        byte[] arrayT1 = T1.toBytes(); //获取字符数组
        for (int i = 0; i < array.length; i++) //遍历字符数组
        {
            for (byte x : arrayT1) {
                array[i] = (char) (array[i] ^ x); //对每个数组元素进行异或运算
            }
        }
        System.out.println("原文：" + message);
        System.out.println("密文：" + String.valueOf(array));
//        String ciphertext=Message^T1;

    }

    @Override
    public void decrypt() {
        System.out.println("-------------------解密阶段----------------------");
        T2 = pairing.pairing(SK, V).getImmutable();
        System.out.println("e(V,SK)=" + T2);
//        int byt = V.getLengthInBytes();// 求V的字节长度，假设消息长度为128字节
//        System.out.println("文本长度：" + (byt + 128));
        System.out.println("文本长度：" + array.length);
        //对密文进行异或运算解密
        String message = "abc";
        byte[] arrayT2 = T2.toBytes(); //获取字符数组
        for (int i = 0; i < array.length; i++) //遍历字符数组
        {
            for (byte x : arrayT2) {
                array[i] = (char) (array[i] ^ x); //对每个数组元素进行异或运算
            }
        }
        System.out.println("解密后明文：" + String.valueOf(array));
    }

    public static void main(String[] args) {
        IBE ident = new IBE();
        // 动态代理，统计各个方法耗时
        Ident identProxy = (Ident) Proxy.newProxyInstance(IBE.class.getClassLoader(), new Class[]{Ident.class}, new TimeCountProxyHandle(ident));
        identProxy.buildSystem();
        identProxy.extractSecretKey();
        identProxy.encrypt();
        identProxy.decrypt();
    }
}
