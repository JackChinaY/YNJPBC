package MA_ABE.MA_CPABE_CSC_CCA2;

import MA_ABE.MA_CPABE_CSC_CCA2.Entity.*;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.UUID;

/**
 * 多属性中心_定长密文_CP-ABE_CCA
 * (选择密文攻击安全)
 */
public class MA_CPABE_CSC_CCA implements Ident {
    private String fileBasePath = "E:/ABE/CPACPABE/";//文件保存的基础路径
    private String PK_File = "public_key";//系统公钥
    private String MK_File = "master_key";//系统主密钥
    private String SK_File = "private_key";//用户私钥
    private String Message_Original_File = "Message_Original.txt";//明文
    private String Message_Ciphertext_File = "Message_Ciphertext";//密文
    private String Message_Decrypt_File = "Message_Decrypt.txt";//解密后的明文

    //用户的属性
//    private String attributes_A = "1 2 3 4 5 6 7 8";
    private String attributes_A = "a1 a2 a3 a4 b1 b2 b3 c1 c2";
    //    private String attributes_A = "1 2 3";

    //访问树中的解密策略 C，集合的大小就是属性中心AA的个数
//    private String[][] attributes_C = {{"A", "B", "C", "X1", "X2", "X3", "X4", "X5", "X6", "X7", "X8", "X9"}, {"D", "E", "F", "Y1", "Y2", "Y3", "Y4", "Y5"}, {"G", "H", "I", "Z1", "Z2", "Z3"}};
//    private String[][] attributes_C = {
//            {"a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "a10", "a11", "a12", "a13", "a14", "a15", "a16", "a17", "a18", "a19", "a20", "a21", "a22", "a23", "a24", "a25"},
//            {"b1", "b2", "b3", "b4", "b5", "b6", "b7", "b8", "b9", "b10", "b11", "b12", "b13", "b14", "b15", "b16", "b17", "b18", "b19", "b20", "b21", "b22", "b23", "b24", "b25"}};
//    private String[][] attributes_C = {{"a1", "a2", "a3"}, {"b1", "b2", "b3"}, {"c1", "c2", "c3"}};
    private String[][] attributes_C = {
            {"a1", "a2", "a3", "a4", "a5", "a6", "a7", "a8", "a9", "a10", "a11", "a12", "a13", "a14", "a15", "a16", "a17"},
            {"b1", "b2", "b3", "b4", "b5", "b6", "b7", "b8", "b9", "b10", "b11", "b12", "b13", "b14", "b15", "b16", "b17"},
            {"c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "c10", "c11", "c12", "c13", "c14", "c15", "c16"}};
    //    private String[][] attributes_C = {{"X1", "X2", "X3", "X4", "X5", "X6", "X7", "X8"}, {"D", "E", "F", "Y1", "Y2", "Y3", "Y4", "Y5"}};
//    private String[][] attributes_C = {{"X1", "X2", "X3", "X4", "A4"}, { "X5", "X6","X7", "X8", "BA"}, { "D", "E", "F","Y1"}, { "Y2", "Y3", "Y4", "Y5"}};
//    private String[][] attributes_C = {{"X1", "X2"}, {"X4", "A4"}, {"X6", "X7"}, {"BA", "D"}, {"X8", "E"}, {"F", "Y1"}, {"A3", "A7"}, {"X3", "X5"}, {"A2", "A6"}, {"B4", "Y4"}};
//        private String[][] attributes_C = {{"1", "2", "3"}, {"4", "5", "6"}, {"7", "8", "9"}};
    // 属性全集U`，共L-1个元素，是U的子集，又称AA的伪属性
//    private String[][] attributes_Us = {{"AA", "AB", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9"}, {"BA", "BB", "B1", "B2", "B3", "B4", "B5"}, {"CA", "CB", "C1", "C2", "C3"}};
//    private String[][] attributes_Us = {{"aa1", "aa2", "aa3", "aa4", "aa5", "aa6", "aa7", "aa8", "aa9", "aa10", "aa11", "aa12", "aa13", "aa14", "aa15", "aa16", "aa17", "aa18", "aa19", "aa20", "aa21", "aa22", "aa23", "aa24"},
//            {"bb1", "bb2", "bb3", "bb4", "bb5", "bb6", "bb7", "bb8", "bb9", "bb10", "bb11", "bb12", "bb13", "bb14", "bb15", "bb16", "bb17", "bb18", "bb19", "bb20", "bb21", "bb22", "bb23", "bb24"}};
//    private String[][] attributes_Us = {{"aa1", "aa2"}, {"bb1", "bb2"}, {"cc1", "cc2"}};
    private String[][] attributes_Us = {
            {"aa1", "aa2", "aa3", "aa4", "aa5", "aa6", "aa7", "aa8", "aa9", "aa10", "aa11", "aa12", "aa13", "aa14", "aa15", "aa16"},
            {"bb1", "bb2", "bb3", "bb4", "bb5", "bb6", "bb7", "bb8", "bb9", "bb10", "bb11", "bb12", "bb13", "bb14", "bb15", "bb16"},
            {"cc1", "cc2", "cc3", "cc4", "cc5", "cc6", "cc7", "cc8", "cc9", "cc10", "cc11", "cc12", "cc13", "cc14", "cc15"}};
    //    private String[][] attributes_Us = {{"A1", "A2", "A3", "A4", "A5", "A6", "A7",}, {"BA", "BB", "B1", "B2", "B3", "B4", "B5"}};
//    private String[][] attributes_Us = {{"A1", "A2", "A3"}, { "A5","A6", "A7"}, { "BB", "B1","B2"}, {"B3", "B4", "B5"}};
//    private String[][] attributes_Us = {{"A1"}, {"A5"}, {"BB"}, {"B3"}, {"Y3"}, {"B5"}, {"Y5"}, {"Y2"}, {"B2"}, {"B1"}};
    //各个AA中的门限值
    private String thresholds = "1 1 1";
    //用户的GUID
    private String AID = UUID.randomUUID().toString();
    private MK mk = new MK();
    private PK pk = new PK();
    private SK sk = new SK();
    private Ciphertext ciphertext = new Ciphertext();
    public ArrayList<AAK> AAKList = new ArrayList<>();//AAK类型，所有属性中心

    /**
     * Setup 初始化接口， 生成公共参数 PK 和主密钥 MK，并分别存储到 pub_key 和 master_key 对应的文件路径中去。
     */
    @Override
    public void setup() {
        System.out.println("----------------setup系统初始化阶段-------------------");
        LangPolicy.setup(mk, pk, thresholds, attributes_C, AAKList, attributes_Us);
    }

    /**
     * KeyGen 私钥生成算法接口，从 pub_key 和 master_key 指定的文件中分别读取公共参数 PK 和主密钥 MK，
     * 根据用户的属性串 attr_str，生成用户的私钥 SK 并存储到 private_key 指定的文件中。
     */
    @Override
    public void keygen() {
        System.out.println("-----------------keygen密钥生成阶段--------------------");
        try {
            sk = LangPolicy.keygen(mk, pk, AAKList, attributes_A, AID, attributes_Us);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypt 加密算法从pubfile中读取公共参数，在访问策略policy下将inputfile指定的文件加密为路径为encfile的文件。
     * 其中访问策略是用后序遍历门限编码的字符串。比如访问策略“foo bar fim 2of3 baf 1of2”指定了含有两个门限四个
     * 叶子节点的访问策略，并且拥有属性“baf”或者“foo”、“bar”、“fim”中的至少两个的属性集合满足该访问策略。
     */
    @Override
    public void encrypt() {
        System.out.println("-------------------encrypt加密阶段----------------------");
        try {
            ciphertext = LangPolicy.encrypt(pk, AAKList, attributes_C, attributes_Us, fileBasePath + Message_Original_File, fileBasePath + Message_Ciphertext_File);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Decrypt 解密算法从 pub_key 指定的文件中读入公共参数，从 private_key 中读入用户私钥，将加密文件 encfile 解密为 decfile。
     */
    @Override
    public void decrypt() {
        System.out.println("-------------------decrypt解密阶段----------------------");
        try {
            LangPolicy.decrypt(pk, sk, ciphertext, attributes_A, attributes_C, AAKList, thresholds, attributes_Us, fileBasePath + Message_Ciphertext_File, fileBasePath + Message_Decrypt_File);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主函数
     */
    public static void main(String[] args) {
        MA_CPABE_CSC_CCA ident = new MA_CPABE_CSC_CCA();
        // 动态代理，统计各个方法耗时
        Ident identProxy = (Ident) Proxy.newProxyInstance(MA_CPABE_CSC_CCA.class.getClassLoader(), new Class[]{Ident.class}, new TimeCountProxyHandle(ident));
        identProxy.setup();
        identProxy.keygen();
        identProxy.encrypt();
        identProxy.decrypt();
    }
}
