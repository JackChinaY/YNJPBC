package CSC_CPABE.Map.CCA_CPABE;

import CSC_CPABE.Map.CCA_CPABE.Entity.Ciphertext;
import CSC_CPABE.Map.CCA_CPABE.Entity.MK;
import CSC_CPABE.Map.CCA_CPABE.Entity.PK;
import CSC_CPABE.Map.CCA_CPABE.Entity.SK;

import java.lang.reflect.Proxy;

/**
 * 定长密文CP-ABE之CCA_CPABE(选择密文攻击安全的CPABE)，使用了Map来保存属性
 */
public class CCA_CPABE implements Ident {
    private String fileBasePath = "E:/ABE/CCACPABE/";//文件保存的基础路径
    private String PK_File = "public_key";//系统公钥
    private String MK_File = "master_key";//系统主密钥
    private String SK_File = "private_key";//用户私钥
    private String Message_Original_File = "Message_Original.txt";//明文
    private String Message_Ciphertext_File = "Message_Ciphertext";//密文
    private String Message_Decrypt_File = "Message_Decrypt.txt";//解密后的明文
    //    //属性全集U，共L个元素
//    private String attributes_U = "university:jkd college:jsj major:major1 type:student2 title:student";
//    //用户的属性
//    private String attributes_user = "university:jkd college:jsj major:major1 title:student";
//    //属性全集U`，共L-1个元素，是U的子集
//    private String attributes_Us = "university:jkd college:jsj major:major1 title:student";
//    //属性集OMG，共L-t个元素，是U`的子集
//    private String attributes_OMG = "university:jkd college:jsj major:major1";
//    //访问树中的解密策略 S
//    private String policy_S = "university:jkd college:jsj major:major1";

    //属性全集U，共L个元素
    private String attributes_U = "1 2 3 4 5 6 7 8 9 10 a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11 a12 a13 a14 a15 a16 a21 a22 a23 a24 a25 a26 a31 a32 a33 a34 a35 a36 a41 a42 a43 a44 a45 a46 a55 a66 a77 a88 a99 a100";
    //    private String attributes_U = "1 2 3 4 5 6";
    //用户的属性
    private String attributes_A = "1 2 3 4 5 6 7 8 9";
    //    private String attributes_A = "1 2 7";
    //属性全集U`，共L-1个元素，是U的子集
    private String attributes_Us = "11 12 13 14 15 16 17 18 19 b1 b2 b3 b4 b5 b6 b7 b8 b9 b10 b11 b12 b13 b14 b15 b16 b21 b22 b23 b24 b25 b26 b31 b32 b33 b34 b35 b36 b41 b42 b43 b44 b45 b46 b55 b66 b77 b88 b99 b100";
    //    private String attributes_Us = "11 12 13 14 15";
    //属性集OMG，共L-t个元素，是U`的子集
    private String attributes_OMG = "11 12 13 14 15 16 17 b1 b2 b3 b4 b5 b6 b7 b8 b9 b10 b11 b12 b13 b14 b15 b16 b21 b22 b23 b24 b25 b26 b31 b32 b33 b34 b35 b36 b41 b42 b43 b44 b45 b46 b55 b66 b77 b88 b99 b100";
    //    private String attributes_OMG = "11 12 13";
    //访问树中的解密策略 S
    private String attributes_S = "1 2 3 4 5 6";
    //门限t
    private int threshold = 3;

    private MK mk = new MK();
    private PK pk = new PK();
    private SK sk = new SK();
    private Ciphertext ciphertext = new Ciphertext();

    /**
     * Setup 初始化接口， 生成公共参数 PK 和主密钥 MK，并分别存储到 pub_key 和 master_key 对应的文件路径中去。
     */
    @Override
    public void setup() {
        System.out.println("----------------setup系统初始化阶段-------------------");
        LangPolicy.setup(mk, pk, attributes_U, attributes_Us);
    }

    /**
     * KeyGen 私钥生成算法接口，从 pub_key 和 master_key 指定的文件中分别读取公共参数 PK 和主密钥 MK，
     * 根据用户的属性串 attr_str，生成用户的私钥 SK 并存储到 private_key 指定的文件中。
     */
    @Override
    public void keygen() {
        System.out.println("-----------------keygen密钥生成阶段--------------------");
        try {
            sk = LangPolicy.keygen(mk, pk, attributes_U, attributes_Us, attributes_A);
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
            ciphertext = LangPolicy.encrypt(mk, pk, attributes_U, attributes_OMG, attributes_S, threshold, fileBasePath + Message_Original_File, fileBasePath + Message_Ciphertext_File);
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
            LangPolicy.decrypt(pk, sk, ciphertext, attributes_A, attributes_OMG, attributes_S, threshold, fileBasePath + Message_Ciphertext_File, fileBasePath + Message_Decrypt_File);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主函数
     */
    public static void main(String[] args) {
        CCA_CPABE ident = new CCA_CPABE();
        // 动态代理，统计各个方法耗时
        Ident identProxy = (Ident) Proxy.newProxyInstance(CCA_CPABE.class.getClassLoader(), new Class[]{Ident.class}, new TimeCountProxyHandle(ident));
        identProxy.setup();
        identProxy.keygen();
        identProxy.encrypt();
        identProxy.decrypt();
    }
}
