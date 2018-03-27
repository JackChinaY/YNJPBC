package ABE.CPABE;

import java.io.*;

/**
 * 保存和读取本地文件，每个方法都有两个多态实现
 */
public class FileOperation {
    /**
     * 将字节数组byte[]保存为文件
     *
     * @param bfile    字节数组
     * @param filePath 保存的文件路径
     * @param fileName 保存的文件名
     */
    public static void byte2File(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在，不存在就新建文件夹
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 将字节数组byte[]保存为文件
     *
     * @param bfile    字节数组
     * @param PathName 路径和文件名
     */
    public static void byte2File(byte[] bfile, String PathName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
//            File dir = new File(filePath);
//            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在，不存在就新建文件夹
//                dir.mkdirs();
//            }
            file = new File(PathName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 将文件读取，转成byte数组
     *
     * @param filePath 文件路径
     * @param fileName 文件名
     */
    public static byte[] file2byte(String filePath, String fileName) {
        byte[] buffer = null;
        try {
            File file = new File(filePath + "\\" + fileName);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 读取本地文件，转成byte数组
     */
    public static byte[] file2byte(String PathName) {
        byte[] buffer = null;
        try {
            File file = new File(PathName);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 将密文保存到本地
     * 1、首先判断文件是否存在，不存在就新建一个
     * 2、写入文件是以覆盖方式，但函数体内如果多次出现write(),说明本函数体作用域内是追加写入
     * 3、文件不存在会自动创建，存在则会被重写
     *
     * @param encfile 文件保存路径
     * @param cphBuf  密文字节数组
     * @param aesBuf  加密密文字节数组
     */
    public static void Ciphertext2File(String encfile, byte[] cphBuf, byte[] aesBuf) throws IOException {
        int i;
        FileOutputStream os = new FileOutputStream(encfile);
        // 先写入 aes_buf
        for (i = 3; i >= 0; i--)
            os.write(((aesBuf.length & (0xff << 8 * i)) >> 8 * i));
        os.write(aesBuf);
        //再写入 cph_buf
        for (i = 3; i >= 0; i--)
            os.write(((cphBuf.length & (0xff << 8 * i)) >> 8 * i));
        os.write(cphBuf);

        os.close();

    }

    /**
     * 读取本地保存的密文
     */
    public static byte[][] readCiphertextFile(String filePath, String fileName) throws IOException {
        int i, len;
        InputStream is = new FileInputStream(filePath + "\\" + fileName);
        byte[][] res = new byte[2][];
        byte[] aesBuf, cphBuf;

        /* read aes buf */
        len = 0;
        for (i = 3; i >= 0; i--)
            len |= is.read() << (i * 8);
        aesBuf = new byte[len];

        is.read(aesBuf);

        /* read cph buf */
        len = 0;
        for (i = 3; i >= 0; i--)
            len |= is.read() << (i * 8);
        cphBuf = new byte[len];

        is.read(cphBuf);

        is.close();

        res[0] = aesBuf;
        res[1] = cphBuf;
        return res;
    }

    /**
     * 读取本地保存的密文
     */
    public static byte[][] readCiphertextFile(String PathName) throws IOException {
        int i, len;
        InputStream is = new FileInputStream(PathName);
        byte[][] res = new byte[2][];
        byte[] aesBuf, cphBuf;

        /* read aes buf */
        len = 0;
        for (i = 3; i >= 0; i--)
            len |= is.read() << (i * 8);
        aesBuf = new byte[len];

        is.read(aesBuf);

        /* read cph buf */
        len = 0;
        for (i = 3; i >= 0; i--)
            len |= is.read() << (i * 8);
        cphBuf = new byte[len];

        is.read(cphBuf);

        is.close();

        res[0] = aesBuf;
        res[1] = cphBuf;
        return res;
    }
}
