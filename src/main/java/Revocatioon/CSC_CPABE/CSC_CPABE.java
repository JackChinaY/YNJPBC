package Revocatioon.CSC_CPABE;

import Revocatioon.CSC_CPABE.Entity.*;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * 单属性中心_定长密文_CP-ABE_CCA  (选择密文攻击安全) 准备改成属性可撤销
 */
public class CSC_CPABE implements Ident {
    private String fileBasePath = "E:/ABE/CPACPABE/";//文件保存的基础路径
    private String PK_File = "public_key";//系统公钥
    private String MK_File = "master_key";//系统主密钥
    private String SK_File = "private_key";//用户私钥
    private String Message_Original_File = "Message_Original.txt";//明文
    private String Message_Ciphertext_File = "Message_Ciphertext";//密文
    private String Message_Decrypt_File = "Message_Decrypt.txt";//解密后的明文

    //用户的属性
//    private String attributes_A = "1 2 3 4 5 6 7 8";
//    private String attributes_A = "A B C D E F G H I";
    //用户属性
    private String attributes_A = "A B C H I";
    //被撤销的用户属性
//    private String attributes_RA = "B C";
    private String attributes_RA = "B";
//    private String attributes_RA = "";

    //属性中心管理的属性
    private String attributes_AA = "A B C D E F G H I";
    //    private String[][] attributes_C = {{"A", "B", "C"}, {"D", "E", "F"}, {"G", "H", "I"}};
    //    private String[][] attributes_C = {{"1", "2", "3"}, {"4", "5", "6"}, {"7", "8", "9"}};
    //各个AA中的门限值
    private int threshold = 3;
    //用户的GUID
    private String AID = UUID.randomUUID().toString();
    private MK mk = new MK();
    private PK pk = new PK();
    private SK sk = new SK();
    private Ciphertext ciphertext = new Ciphertext();
    private AAK AA = new AAK();//AAK类型，所有属性中心

    /**
     * Setup 初始化接口， 生成公共参数 PK 和主密钥 MK，并分别存储到 pub_key 和 master_key 对应的文件路径中去。
     */
    @Override
    public void setup() {
//        System.out.println(String.format("%40s", "-----------------setup系统初始化阶段---------------------"));
        System.out.println("-----------------setup系统初始化阶段------------------");
        LangPolicy.setup(mk, pk, threshold, attributes_AA, AA);
    }

    /**
     * KeyGen 私钥生成算法接口，从 pub_key 和 master_key 指定的文件中分别读取公共参数 PK 和主密钥 MK，
     * 根据用户的属性串 attr_str，生成用户的私钥 SK 并存储到 private_key 指定的文件中。
     */
    @Override
    public void keygen() {
        System.out.println("-----------------keygen密钥生成阶段-------------------");
        try {
            sk = LangPolicy.keygen(mk, pk, AA, attributes_A, attributes_RA, AID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypt 加密算法
     */
    @Override
    public void encrypt() {
        System.out.println("-------------------encrypt加密阶段---------------------");
        try {
            ciphertext = LangPolicy.encrypt(pk, AA, fileBasePath + Message_Original_File, fileBasePath + Message_Ciphertext_File);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重加密算法
     */
    @Override
    public void re_encrypt() {
        System.out.println("----------------re_encrypt重加密阶段-------------------");
        try {
            LangPolicy.re_encrypt(pk, AA, sk, attributes_RA, ciphertext, fileBasePath + Message_Original_File, fileBasePath + Message_Ciphertext_File);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Decrypt 解密算法从 pub_key 指定的文件中读入公共参数，从 private_key 中读入用户私钥，将加密文件 encfile 解密为 decfile。
     */
    @Override
    public void decrypt() {
        System.out.println("------------------decrypt解密阶段----------------------");
        try {
            LangPolicy.decrypt(pk, sk, ciphertext, attributes_A, attributes_AA, attributes_RA, AA, threshold, fileBasePath + Message_Ciphertext_File, fileBasePath + Message_Decrypt_File);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 主函数
     */
    public static void main(String[] args) {
        CSC_CPABE ident = new CSC_CPABE();
        // 动态代理，统计各个方法耗时
        Ident identProxy = (Ident) Proxy.newProxyInstance(CSC_CPABE.class.getClassLoader(), new Class[]{Ident.class}, new TimeCountProxyHandle(ident));
        identProxy.setup();
        identProxy.keygen();
        identProxy.encrypt();
        identProxy.re_encrypt();
        identProxy.decrypt();
    }
}
