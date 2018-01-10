package ABE.CPABE;

import ABE.CPABE.Entity.*;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.util.ArrayList;

/**
 * 功能：序列化和反序列化一些变量和文件，便于将变量保存到本地，内容以byte二进制形式保存，每个元素为：长度+内容
 * 描述如下：  序列化过程：在字节数组中，先写入元素的长度，再写入元素的内容
 * 描述如下：反序列化过程：向字节数组中，先读取元素的长度，再读取元素的内容
 */
public class SerializeUtils {
    /**-------------------------------将文件保存到本地-------------------------------*/
    /**
     * 序列化公钥 PK
     */
    public static byte[] serializePK(PK pk) {
        ArrayList<Byte> arrlist = new ArrayList<Byte>();

//        serializeString(arrlist, pk.pairingDesc);
        serializeElement(arrlist, pk.g);
        serializeElement(arrlist, pk.h);
//        serializeElement(arrlist, pk.gp);
        serializeElement(arrlist, pk.e_g_ga);

        return Byte_arr2byte_arr(arrlist);
    }

    /**
     * 序列化主密钥 MK
     */
    public static byte[] serializeMK(MK mk) {
        ArrayList<Byte> arrlist = new ArrayList<Byte>();

        serializeElement(arrlist, mk.b);
        serializeElement(arrlist, mk.ga);

        return Byte_arr2byte_arr(arrlist);
    }

    /**
     * 序列化私钥 SK
     */
    public static byte[] serializeSK(SK sk) {
        ArrayList<Byte> arrlist;
        int prvCompsLen, i;

        arrlist = new ArrayList<Byte>();
        prvCompsLen = sk.comps.size();
        serializeElement(arrlist, sk.d);
        serializeUint32(arrlist, prvCompsLen);

        for (i = 0; i < prvCompsLen; i++) {
            serializeString(arrlist, sk.comps.get(i).attr);
            serializeElement(arrlist, sk.comps.get(i).d);
            serializeElement(arrlist, sk.comps.get(i).dj);
        }

        return Byte_arr2byte_arr(arrlist);
    }

    /**
     * 序列化密文
     */
    public static byte[] serializeCiphertext(Ciphertext cph) {
        ArrayList<Byte> arrlist = new ArrayList<Byte>();
        serializeElement(arrlist, cph.cs);
        serializeElement(arrlist, cph.c);
        serializePolicy(arrlist, cph.treePolicy);

        return Byte_arr2byte_arr(arrlist);
    }

    /**
     * 序列化策略
     */
    private static void serializePolicy(ArrayList<Byte> arrlist, TreePolicy p) {
        serializeUint32(arrlist, p.k);

        if (p.children == null || p.children.length == 0) {
            serializeUint32(arrlist, 0);
            serializeString(arrlist, p.attr);
            serializeElement(arrlist, p.c);
            serializeElement(arrlist, p.cp);
        } else {
            serializeUint32(arrlist, p.children.length);
            for (int i = 0; i < p.children.length; i++)
                serializePolicy(arrlist, p.children[i]);
        }
    }

    /**
     * 序列化Element 将Element以字节数组元素的形式写入总的字节数组中
     *
     * @param arrlist 总的字节数组
     * @param e       要写入的元素元素
     */
    private static void serializeElement(ArrayList<Byte> arrlist, Element e) {
        byte[] arr_e = e.toBytes();
        serializeUint32(arrlist, arr_e.length);
        byteArrListAppend(arrlist, arr_e);
    }

    /**
     * 序列化字符串 将String以字节数组元素的形式写入总的字节数组中
     *
     * @param arrlist 总的字节数组
     * @param s       要写入的元素
     */
    private static void serializeString(ArrayList<Byte> arrlist, String s) {
        byte[] b = s.getBytes();
        serializeUint32(arrlist, b.length);
        byteArrListAppend(arrlist, b);
    }


    /**
     * 向字节数组中首先写入元素的长度
     * potential problem: 元素长度必须小于 2^31
     */
    private static void serializeUint32(ArrayList<Byte> arrlist, int k) {
        byte b;
        for (int i = 3; i >= 0; i--) {
            b = (byte) ((k & (0x000000ff << (i * 8))) >> (i * 8));
            arrlist.add(b);
        }
    }

    /**
     * 向字节数组中再写入元素的内容
     */
    public static void byteArrListAppend(ArrayList<Byte> arrlist, byte[] b) {
        int len = b.length;
        for (int i = 0; i < len; i++)
            arrlist.add(b[i]);
    }

    /**
     * 将Byte字节数组以byte字节数组的形式返回
     */
    private static byte[] Byte_arr2byte_arr(ArrayList<Byte> B) {
        int len = B.size();
        byte[] b = new byte[len];

        for (int i = 0; i < len; i++)
            b[i] = B.get(i).byteValue();

        return b;
    }
    /**-------------------------------从本地读取文件-------------------------------**/
    /**
     * 反序列化PK文件
     */
    public static PK unserializePK(byte[] b) {
        PK pk = new PK();
        int offset = 0;

//        StringBuffer sb = new StringBuffer("");
//        offset = unserializeString(b, offset, sb);
//        pk.pairingDesc = sb.substring(0);

//		CurveParameters params = new DefaultCurveParameters()
//				.load(new ByteArrayInputStream(pub.pairingDesc.getBytes()));
//		pub.p = PairingFactory.getPairing(params);
        pk.pairing = PairingFactory.getPairing("a.properties");
        Pairing pairing = pk.pairing;
        pk.g = pairing.getG1().newElement();
        pk.h = pairing.getG1().newElement();
//        pk.gp = pairing.getG2().newElement();
        pk.e_g_ga = pairing.getGT().newElement();

        offset = unserializeElement(b, offset, pk.g);
        offset = unserializeElement(b, offset, pk.h);
//        offset = unserializeElement(b, offset, pk.gp);
        offset = unserializeElement(b, offset, pk.e_g_ga);

        return pk;
    }

    /**
     * 反序列化MK文件
     */
    public static MK unserializeMK(PK pub, byte[] b) {
        int offset = 0;
        MK mk = new MK();

        mk.b = pub.pairing.getZr().newElement();
        mk.ga = pub.pairing.getG2().newElement();

        offset = unserializeElement(b, offset, mk.b);
        offset = unserializeElement(b, offset, mk.ga);

        return mk;
    }

    /**
     * 反序列化密文Ciphertext
     *
     * @param pub
     * @param cphBuf
     * @return
     */
    public static Ciphertext unserializeCiphertext(PK pub, byte[] cphBuf) {
        Ciphertext cph = new Ciphertext();
        int offset = 0;
        int[] offset_arr = new int[1];

        cph.cs = pub.pairing.getGT().newElement();
        cph.c = pub.pairing.getG1().newElement();

        offset = SerializeUtils.unserializeElement(cphBuf, offset, cph.cs);
        offset = SerializeUtils.unserializeElement(cphBuf, offset, cph.c);

        offset_arr[0] = offset;
        cph.treePolicy = SerializeUtils.unserializeTreePolicy(pub, cphBuf, offset_arr);
        offset = offset_arr[0];

        return cph;
    }

    /**
     * 反序列化访问树T
     *
     * @param pk
     * @param arr
     * @param offset
     * @return
     */
    private static TreePolicy unserializeTreePolicy(PK pk, byte[] arr, int[] offset) {
        int i;
        int n;
        TreePolicy p = new TreePolicy();
        p.k = unserializeUint32(arr, offset[0]);
        offset[0] += 4;
        p.attr = null;

        /* children */
        n = unserializeUint32(arr, offset[0]);
        offset[0] += 4;
        if (n == 0) {
            p.children = null;

            StringBuffer sb = new StringBuffer("");
            offset[0] = unserializeString(arr, offset[0], sb);
            p.attr = sb.substring(0);

            p.c = pk.pairing.getG1().newElement();
            p.cp = pk.pairing.getG1().newElement();

            offset[0] = unserializeElement(arr, offset[0], p.c);
            offset[0] = unserializeElement(arr, offset[0], p.cp);
        } else {
            p.children = new TreePolicy[n];
            for (i = 0; i < n; i++)
                p.children[i] = unserializeTreePolicy(pk, arr, offset);
        }

        return p;
    }


    /**
     * 反序列化私钥SK
     */
    public static SK unserializeSK(PK pk, byte[] b) {
        SK prv = new SK();
        int i, offset, len;
        offset = 0;

        prv.d = pk.pairing.getG2().newElement();
        offset = unserializeElement(b, offset, prv.d);

        prv.comps = new ArrayList<SKComp>();
        len = unserializeUint32(b, offset);
        offset += 4;

        for (i = 0; i < len; i++) {
            SKComp c = new SKComp();

            StringBuffer sb = new StringBuffer("");
            offset = unserializeString(b, offset, sb);
            c.attr = sb.substring(0);

            c.d = pk.pairing.getG2().newElement();
            c.dj = pk.pairing.getG2().newElement();

            offset = unserializeElement(b, offset, c.d);
            offset = unserializeElement(b, offset, c.dj);

            prv.comps.add(c);
        }

        return prv;
    }

    /**
     * 反序列化Element，将结果元素赋值给e
     *
     * @param arr    要反序列化的整个字节数组
     * @param offset 偏移量
     * @param e      要反序列化的结果元素
     * @return 下一个元素的起始位置
     */
    private static int unserializeElement(byte[] arr, int offset, Element e) {
        int len;//将要读取的元素的长度
        byte[] e_byte;//为将要读取的元素申请临时空间

        len = unserializeUint32(arr, offset);
        e_byte = new byte[(int) len];
        offset += 4;//+4的作用是：该元素的长度是用int类型保存的，占4字节，在byte[]在4字节空间，所以在此4字节之后才是该元素的内容
        for (int i = 0; i < len; i++)
            e_byte[i] = arr[offset + i];
        e.setFromBytes(e_byte);//将结果元素赋值给e

        return (int) (offset + len);
    }


    /**
     * 反序列化String
     * Usage:
     * <p>
     * StringBuffer sb = new StringBuffer("");
     * <p>
     * offset = unserializeString(arr, offset, sb);
     * <p>
     * String str = sb.substring(0);
     */
    private static int unserializeString(byte[] arr, int offset, StringBuffer sb) {
        int i;
        int len;
        byte[] str_byte;

        len = unserializeUint32(arr, offset);
        offset += 4;
        str_byte = new byte[len];
        for (i = 0; i < len; i++)
            str_byte[i] = arr[offset + i];

        sb.append(new String(str_byte));
        return offset + len;
    }

    /**
     * 获取将要读取的元素的长度
     * 调用此方法后必须进行offset+=4
     * <<  : 左移运算符，num << 1,相当于num乘以2
     * “|”表示的是或运算，即两个二进制数同位中，只要有一个为1则结果为1，若两个都为1其结果也为1
     *
     * @param arr    要反序列化的字节数组
     * @param offset 偏移量
     *               Usage:You have to do offset+=4 after call this method
     */
    private static int unserializeUint32(byte[] arr, int offset) {
        int r = 0;
        for (int i = 3; i >= 0; i--)
            r |= (byte2int(arr[offset++])) << (i * 8);
        return r;
    }

    /**
     * 字节转int类型，java中byte范围-128 ~ 127，闭区间
     */
    private static int byte2int(byte b) {
        if (b >= 0)
            return b;
        return (256 + b);
    }

}
