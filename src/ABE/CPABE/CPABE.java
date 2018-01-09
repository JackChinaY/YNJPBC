package ABE.CPABE;

import java.lang.reflect.Proxy;
import java.security.NoSuchAlgorithmException;

/**
 * CPABE主函数
 */
public class CPABE implements Ident {
    //变量说明
    // 基本顺序就是 Pairing Field Element
//    private Pairing pairing;//配对
//    private Field G1;//乘法循环群，又称作双线性群，一个集合，里面元素是一个个小集合，小集合中有3个数，前2个的数值非常大，第三个为0，如{78296357617,17401990，0}
//    private Field G2;//乘法循环群，又称作双线性群，一个集合，里面元素是一个个小集合，小集合中有3个数，前2个的数值非常大，第三个为0，如{78296357617,17401990，0}
//    private Field Zr;//指数群，一个集合，里面元素是一个个数，数值非常大，如{2697655,13108697}，如果椭圆类型是type A，则该集合中的元素可以不全是质数
//    private Field GT;//乘法循环群，双线性群，一个集合，里面元素是一个个点对(x,y)，数值非常大，如{x=786897127,y=34822812}
//    private Element MK;//主密钥
//    private Element s;//随机值，用于加密文件
//    private Element g;//G1的生成元
//    private Element PK;//系统公钥
//    private Element PKu;//用户公钥
//    private Element SK;//用户私钥
//    private Element Ys;//加密参数
//    private Element V, T1, T2;
//    private int n = 2;//用户属性的个数
//    private char[] array;//消息字符数组

    private String fileBasePath = "E:/ABE/CPABE/";//文件保存的基础路径
    private String PK_File = "public_key";//系统公钥
    private String MK_File = "master_key";//系统主密钥
    private String SK_File = "private_key";//用户私钥
    private String Message_Original_File = "Message_Original.pdf";//明文
    private String Message_Ciphertext_File = "Message_Ciphertext";//密文
    private String Message_Decrypt_File = "Message_Decrypt.pdf";//解密后的明文
    //学生的属性
    private String student_attr = "objectClass:inetOrgPerson objectClass:organizationalPerson "
            + "sn:student2 cn:student2 uid:student2 userPassword:student2 "
            + "ou:idp o:computer mail:student2@sdu.edu.cn title:student";
    //访问树中的解密策略
    private String policy = "sn:student2 cn:student2 uid:student2 2of3";

    public CPABE() {
    }

    /**
     * Setup 初始化接口， 生成公共参数 PK 和主密钥 MK，并分别存储到 pub_key 和 master_key 对应的文件路径中去。
     */
    @Override
    public void setup() {
        System.out.println("----------------setup系统初始化阶段-------------------");
        LangPolicy.setup(fileBasePath + PK_File, fileBasePath + MK_File);
    }

//    /**
//     * KeyGen 私钥生成算法接口，从 pub_key 和 master_key 指定的文件中分别读取公共参数 PK 和主密钥 MK，
//     * 根据用户的属性串 attr_str，生成用户的私钥 SK 并存储到 private_key 指定的文件中。
//     */
//    @Override
//    public void keygen() {
//        System.out.println("-----------------keygen密钥生成阶段--------------------");
//        byte[] pk_byte, mk_byte, pr_byte;//PK、MK、SK
//        PK pk = new PK();
//        MK mk = new MK();
//        //读取本地的PK文件
//        pk_byte = FileOperation.file2byte(fileBasePath, PK_File);
//        pk = SerializeUtils.unserializePK(pk_byte);
//        //读取本地的MK文件
//        mk_byte = FileOperation.file2byte(fileBasePath, MK_File);
//        mk = SerializeUtils.unserializeMK(pk, mk_byte);
//        //将一个字符串，此字符串包含一个人的各种属性，分隔符是空格，最后返回一个数组，数组中包含各个小属性
//        String[] attr_arr = LangPolicy.parseAttribute(student_attr);
//
////        BswabePrv prv = Bswabe.keygen(pk, mk, attr_arr);
//
//        //计算私钥SK
//        SK sk = new SK();
//        Element g_r, r, b;//g_r表示g^r，r表示Zr的随机值，b表示MK中的b
//        Pairing pairing = pk.pairing;
//        r = pairing.getZr().newRandomElement();
//        b = mk.b.duplicate();//复制
//        //计算g^r
////        g_r = pairing.getG2().newElement();
//        g_r = pk.g.powZn(r).getImmutable();
//        //计算SK中的d，d=(g^r*g^a)^(1/b)
//        sk.d = pairing.getG2().newElement();
//        sk.d = mk.ga.duplicate();
//        sk.d.mul(g_r);
//        b.invert();//取倒数，得到1/b
//        sk.d.powZn(b);
//
//        //计算每个属性，len为用户属性集合的中元素的个数
//        sk.comps = new ArrayList<SKComp>();
//        for (int i = 0; i < attr_arr.length; i++) {
//            SKComp comp = new SKComp();
//            Element h_rj;//哈希函数 H(j)^rj
//            Element rj;//Zr中的随机值 rj
//            //赋值
//            comp.attr = attr_arr[i];
//            comp.d = pairing.getG2().newElement();
//            comp.dj = pairing.getG1().newElement();
//            h_rj = pairing.getG2().newElement();
//            rj = pairing.getZr().newRandomElement();
//            //将单个属性attr哈希到G_1群上
//            MessageDigest md = null;
//            try {
//                md = MessageDigest.getInstance("SHA-1");
//                byte[] digest = md.digest(comp.attr.getBytes());
//                h_rj.setFromHash(digest, 0, digest.length);
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }
//            //计算H(j)^rj
//            h_rj.powZn(rj);
//            //计算Dj=g^r *H(j)^rj
//            comp.d = g_r.duplicate();
//            comp.d.mul(h_rj);
//            //计算Dj`=g^rj
//            comp.dj = pk.g.duplicate();
//            comp.dj.powZn(rj);
//            //添加到集合中
//            sk.comps.add(comp);
//        }
//        //将私钥SK保存到本地文件
//        pr_byte = SerializeUtils.serializeSK(sk);
//        FileOperation.byte2File(pr_byte, fileBasePath, SK_File);
//        System.out.println("用户私钥 D=(g^r*g^a)^(1/b) " + sk.d);
//        System.out.println("用户属性个数 " + sk.comps.size());
//
////        //将byte[] byteArray_G_1哈希到G_1群
////        PKu = pairing.getG1().newElement().setFromHash("IDu".getBytes(), 0, 3).getImmutable(); //从长度为3的Hash值IDu确定用户U产生的公钥Qu
////        SK = PKu.mulZn(MK).getImmutable();
////        System.out.println("用户公钥 PKu=" + PKu);
////        System.out.println("用户私钥 SK=" + SK);
//    }

    /**
     * KeyGen 私钥生成算法接口，从 pub_key 和 master_key 指定的文件中分别读取公共参数 PK 和主密钥 MK，
     * 根据用户的属性串 attr_str，生成用户的私钥 SK 并存储到 private_key 指定的文件中。
     */
    @Override
    public void keygen() {
        System.out.println("-----------------keygen密钥生成阶段--------------------");
        try {
            LangPolicy.keygen(fileBasePath + PK_File, fileBasePath + SK_File, fileBasePath + MK_File, student_attr);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

//    public void encrypt() {
//        System.out.println("-------------------加密阶段----------------------");
//        byte[] pk_byte;//PK
//        PK pk = new PK();
//        //密文实体，包含访问树
//        Ciphertext cph = new Ciphertext();
//        CiphertextAndKey keyCph = new CiphertextAndKey();
//        byte[] messageBuf;//明文的字节数组
//        byte[] ciphertextBuf;//密文的字节数组
//        byte[] aesBuf;//密文的字节数组
//        Element s;//表示Zr的随机值
//        Element m;//表示Zr的随机值
//
//        //读取本地的PK文件
//        pk_byte = FileOperation.file2byte(fileBasePath, PK_File);
//        pk = SerializeUtils.unserializePK(pk_byte);
//
////        keyCph = Bswabe.enc(pk, policy);
//
//        //计算Ciphertext实体
//        Pairing pairing = pk.pairing;
//        s = pairing.getZr().newRandomElement();
//        m = pairing.getGT().newRandomElement();
//        cph.cs = pairing.getGT().newElement();
//        cph.c = pairing.getG1().newElement();
//        try {
//            cph.p = LangPolicy.parsePolicyPostfix(policy);//将字符串表示的策略转换成TreePolicy类实体
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //计算`C=M*e(g,g)^as
//        cph.cs = pk.e_g_ga.duplicate();//e(g,g)^a
//        cph.cs.powZn(s); /* num_exps++; */
//        cph.cs.mul(m); /* num_muls++; */
//        //计算C=h^s
//        cph.c = pk.h.duplicate();
//        cph.c.powZn(s); /* num_exps++; */
//        //填充策略树
//        try {
//            LangPolicy.fillPolicy(cph.p, pk, s);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//        keyCph.cph = cph;
//        keyCph.key = m;
//
//        System.out.println("m = " + m.toString());
//
//        if (cph == null) {
//            System.out.println("Error happed in enc");
//            System.exit(0);
//        }
//        //序列化密文
//        ciphertextBuf = SerializeUtils.serializeCiphertext(cph);
//        //从本地读取明文文件
//        messageBuf = FileOperation.file2byte(fileBasePath, Message_Original_File);
//        try {
//            //使用AES方法进行加密
//            aesBuf = AESCoder.encrypt(m.toBytes(), messageBuf);
//            // 将密文保存到本地
//            FileOperation.Ciphertext2File(fileBasePath + "//" + Message_Ciphertext_File, ciphertextBuf, aesBuf);
//            System.out.println("密文成功生成，已保存到本地！");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        s = Zr.newRandomElement().getImmutable();//随机值
////        Ys = PK.powZn(s);//相乘
////        System.out.println("s=" + s);
////        System.out.println("Y^s=e（g,g）^ys=" + Ys);
////        //对明文进行异或运算加密
////        String message = "Jiangsu University of Science and Technology";
////        array = message.toCharArray(); //获取字符数组
////        byte[] arrayYs = Ys.toBytes(); //获取字符数组
////        for (int i = 0; i < array.length; i++) //遍历字符数组
////        {
////            for (byte x : arrayYs) {
////                array[i] = (char) (array[i] ^ x); //对每个数组元素进行异或运算
////            }
////        }
////        System.out.println("原文：" + message);
////        System.out.println("密文：" + String.valueOf(array));
//
//    }

    /**
     * Encrypt 加密算法从pubfile中读取公共参数，在访问策略policy下将inputfile指定的文件加密为路径为encfile的文件。
     * 其中访问策略是用后序遍历门限编码的字符串。比如访问策略“foo bar fim 2of3 baf 1of2”指定了含有两个门限四个
     * 叶子节点的访问策略，并且拥有属性“baf”或者“foo”、“bar”、“fim”中的至少两个的属性集合满足该访问策略。
     */
    @Override
    public void encrypt() {
        System.out.println("-------------------encrypt加密阶段----------------------");
        try {
            LangPolicy.encrypt(fileBasePath + PK_File, policy, fileBasePath + Message_Original_File, fileBasePath + Message_Ciphertext_File);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * Decrypt 解密算法从 pub_key 指定的文件中读入公共参数，从 private_key 中读入用户私钥，将加密文件 encfile 解密为 decfile。
//     */
//    @Override
//    public void decrypt() {
//        System.out.println("-------------------解密阶段----------------------");
//        byte[] pk_byte;//PK
//        byte[] prv_byte;//SK
//        PK pk = new PK();
//        SK prv;
//        Ciphertext cph;
//
//        byte[] aesBuf, cphBuf;
//        byte[] plt;
//        byte[][] tmp;
//
//        //读取本地的PK文件
//        pk_byte = FileOperation.file2byte(fileBasePath, PK_File);
//        pk = SerializeUtils.unserializePK(pk_byte);
//
//        //读取本地保存的密文
//        try {
//            tmp = FileOperation.readCiphertextFile(fileBasePath, Message_Ciphertext_File);
//            aesBuf = tmp[0];
//            cphBuf = tmp[1];
//            cph = SerializeUtils.unserializeCiphertext(pk, cphBuf);
//
//            //从本地获取SK文件
//            prv_byte = FileOperation.file2byte(fileBasePath, SK_File);
//            prv = SerializeUtils.unserializeSK(pk, prv_byte);
//            //        ElementBoolean beb = Bswabe.dec(pub, prv, cph);
//            //检测SK是否满足密文中的访问策略
//            Element t;
//            Element m;
//            ElementBoolean beb = LangPolicy.dec(pk, prv, cph);
//
////            ElementBoolean beb = new ElementBoolean();
////            m = pk.pairing.getGT().newElement();
////            t = pk.pairing.getGT().newElement();
////
////            LangPolicy.checkSatisfy(cph.p, prv);
////            //不满足的情况
////            if (!cph.p.satisfiable) {
////                System.err.println("sorry，cannot decrypt, attributes in key do not satisfy policy");
////                beb.e = null;
////                beb.b = false;
////                return;
////            }
////            //满足的情况
////            LangPolicy.pickSatisfyMinLeaves(cph.p, prv);
////
////            LangPolicy.decFlatten(t, cph.p, prv, pk);
////
////            m = cph.cs.duplicate();
////            m.mul(t); /* num_muls++; */
////
////            t = pk.pairing.pairing(cph.c, prv.d);
////            t.invert();
////            m.mul(t); /* num_muls++; */
////
////            beb.e = m;
////            beb.b = true;
//
//
//            System.out.println("e = " + beb.e.toString());
//            if (beb.b) {
//                plt = AESCoder.decrypt(beb.e.toBytes(), aesBuf);
//                FileOperation.byte2File(plt, fileBasePath, Message_Decrypt_File);
//                System.out.println("文件解密成功，解密后的文件已保存到本地！ ");
//            } else {
//                System.out.println("文件解密失败！ ");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Decrypt 解密算法从 pub_key 指定的文件中读入公共参数，从 private_key 中读入用户私钥，将加密文件 encfile 解密为 decfile。
     */
    @Override
    public void decrypt() {
        System.out.println("-------------------decrypt解密阶段----------------------");
        try {
            LangPolicy.decrypt(fileBasePath + PK_File, fileBasePath + SK_File, fileBasePath + Message_Ciphertext_File, fileBasePath + Message_Decrypt_File);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主函数
     */
    public static void main(String[] args) {
        CPABE ident = new CPABE();
        // 动态代理，统计各个方法耗时
        Ident identProxy = (Ident) Proxy.newProxyInstance(CPABE.class.getClassLoader(), new Class[]{Ident.class}, new TimeCountProxyHandle(ident));
        identProxy.setup();
        identProxy.keygen();
        identProxy.encrypt();
        identProxy.decrypt();
    }


}
