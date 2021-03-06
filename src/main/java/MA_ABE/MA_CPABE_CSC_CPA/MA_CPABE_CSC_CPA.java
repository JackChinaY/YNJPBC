package MA_ABE.MA_CPABE_CSC_CPA;

import MA_ABE.MA_CPABE_CSC_CPA.Entity.*;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.UUID;

/**
 * 多属性中心_定长密文_CP-ABE_CPA
 * (选择明文攻击安全)
 */
public class MA_CPABE_CSC_CPA implements Ident {
    private String fileBasePath = "E:/ABE/CPACPABE/";//文件保存的基础路径
    private String PK_File = "public_key";//系统公钥
    private String MK_File = "master_key";//系统主密钥
    private String SK_File = "private_key";//用户私钥
    private String Message_Original_File = "Message_Original.txt";//明文
    private String Message_Ciphertext_File = "Message_Ciphertext";//密文
    private String Message_Decrypt_File = "Message_Decrypt.txt";//解密后的明文

    //用户的属性
//    private String attributes_A = "1 2 3 4 5 6 7 8";
    private String attributes_A = "A B C D E F G H I";
    //    private String attributes_A = "1 2 3";

    //访问树中的解密策略 C，集合的大小就是属性中心AA的个数
    private String[][] attributes_C = {{"A", "B", "C"}, {"D", "E", "F"}, {"G", "H", "I"}};
    //private String[][] attributes_C = {{"1", "2", "3"}, {"4", "5", "6"}, {"7", "8", "9"}};
    //用户的GUID
    private String AID = UUID.randomUUID().toString();
    //各个AA中的门限值
    private String thresholds = "2 3 3";
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
        LangPolicy.setup(mk, pk, thresholds, attributes_C, AAKList);
    }

    /**
     * KeyGen 私钥生成算法接口，从 pub_key 和 master_key 指定的文件中分别读取公共参数 PK 和主密钥 MK，
     * 根据用户的属性串 attr_str，生成用户的私钥 SK 并存储到 private_key 指定的文件中。
     */
    @Override
    public void keygen() {
        System.out.println("-----------------keygen密钥生成阶段--------------------");
        try {
            sk = LangPolicy.keygen(mk, pk, AAKList, attributes_A, AID);
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
            ciphertext = LangPolicy.encrypt(pk, AAKList, fileBasePath + Message_Original_File, fileBasePath + Message_Ciphertext_File);
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
            LangPolicy.decrypt(pk, sk, ciphertext, attributes_A, attributes_C, thresholds, fileBasePath + Message_Ciphertext_File, fileBasePath + Message_Decrypt_File);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主函数
     */
    public static void main(String[] args) {
        MA_CPABE_CSC_CPA ident = new MA_CPABE_CSC_CPA();
        // 动态代理，统计各个方法耗时
        Ident identProxy = (Ident) Proxy.newProxyInstance(MA_CPABE_CSC_CPA.class.getClassLoader(), new Class[]{Ident.class}, new TimeCountProxyHandle(ident));
        identProxy.setup();
        identProxy.keygen();
        identProxy.encrypt();
        identProxy.decrypt();
    }
}
