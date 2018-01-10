package TEST;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.Scanner;

public class Test1 {
    //基本顺序就是 Pairing Field Element
    private Pairing pairing;//配对
    private Field G1;//乘法循环群，双线性群一个集合，里面元素是一个个小集合，小集合中有3个数，前2个的数值非常大，第三个为0，如{78296357617,17401990，0}
    private Field G2;//乘法循环群，双线性群一个集合，里面元素是一个个小集合，小集合中有3个数，前2个的数值非常大，第三个为0，如{78296357617,17401990，0}
    private Field Zr;//指数群，一个集合，里面元素是一个个数，数值非常大，如{2697655,13108697,...,45784125}
    private Field GT;//乘法循环群，双线性群，一个集合，里面元素是一个个点对(x,y)，数值非常大，如{x=786897127,y=34822812}
    private Element MK;//主密钥
    private Element r;
    private Element g;//G1的生成元
    private Element PK;//系统公钥
    private Element PKu;//用户公钥
    private Element SK;//用户私钥
    private Element V, T1, T2;

    /**
     * 初始化
     */
    private void init() {
        //仅对于双线性映射，要使用PBC包装并获得性能，必须设置配对工厂的使用PBC（可能）属性
        PairingFactory.getInstance().setUsePBCWhenPossible(true);
        //生成线性对
        pairing = PairingFactory.getPairing("a.properties");
        //将变量r初始化为Zr中的元素,返回代数结构Zr,代数结构包含：群、环、场（groups, rings and fields）
//        Zr = pairing.getZr();
        //创建一个新的未初始化的元素
//        r = Zr.newRandomElement();
//        System.out.println("指数群Zr:" + Zr.getLengthInBytes());
//        for (int i = 0; i < 10; i++) {
//            System.out.println("指数群Zr:" + Zr.newRandomElement());
//        }
        //将变量Ppub，Qu，Su，V初始化为G1中的元素，G1是加法群
        G1 = pairing.getG1();
//        PK = G1.newElement();
//        Zr = pairing.getZr();
//        PK = Zr.newElement();
//        PK.setToOne();
//        PKu.set(PK);
        PK = pairing.getG1().newElement().setFromHash("a".getBytes(), 0, 1).getImmutable();
//        System.out.println("PKu:" + PKu);
//        G1 = pairing.getG2();
//        PK = G1.newRandomElement();
        System.out.println("PK:" + PK);
//        PKu = G1.newElement();
//        SK = G1.newElement();
//        V = G1.newElement();
        //将变量T1，T2V初始化为GT中的元素，GT是乘法群
//        Field GT = pairing.getGT();
//        T1 = GT.newElement();
//        T2 = GT.newElement();
    }

    public static void main(String[] args) {
//        System.out.println("start...");
        Test1 a = new Test1();
        a.init();


//        Zr = pairing.getZr();
//        //创建一个新的未初始化的元素
//        r = Zr.newElement();
//        /* Return Zr */
//        Field Zr = pairing.getZr();
//        int degree = pairing.getDegree();
//        System.out.println("end."+"IDu".getBytes()[0]);
//        for (byte i: "IDu".getBytes()
//             ) {
//            System.out.println(i);
//        }

//        TypeACurveGenerator pg = new TypeACurveGenerator(5, 5);
//        PairingParameters typeAParams = pg.generate();
//        Pairing pairing = PairingFactory.getPairing(typeAParams);
//        Element Z_p = pairing.getZr().newRandomElement().getImmutable();
//        System.out.println(Z_p.toString());
    }

}
