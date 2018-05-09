package TEST.JieRui.OriginalSocket;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 服务器端常用工具，int byte byte[] float之间的相互转换
 */
public class ServerUtils {
    /**
     * 向下位机发送数据
     */
    public static void write(Socket socket, byte[] message) {
        try {
            //将字节输出流包装为带缓冲的字节输出流
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedOutputStream.write(message);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            System.err.println("数据发送失败，连接已断开");
            e.printStackTrace();
        }
    }

    /**
     * 服务器发送给ARM的指令：ARM需要上报每个单灯的亮度
     */
    public static void writeToARMGetSingleLampBrightness(Socket socket) {
        try {
            byte[] message = {(byte) 0xfe, 0x01};
            //将字节输出流包装为带缓冲的字节输出流
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedOutputStream.write(message);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            System.err.println("数据发送失败，连接已断开");
            e.printStackTrace();
        }
    }

    /**
     * 服务器发送给ARM的指令：ARM需要上报每个单灯的状态（故障等信息）
     */
    public static void writeToARMGetSingleLampState(Socket socket) {
        try {
            byte[] message = {(byte) 0xfe, 0x02};
            //将字节输出流包装为带缓冲的字节输出流
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedOutputStream.write(message);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            System.err.println("数据发送失败，连接已断开");
            e.printStackTrace();
        }
    }

    /**
     * 服务器发送给ARM的指令：ARM需要上报每个单灯的能耗
     */
    public static void writeToARMGetSingleLampEnergyConsumption(Socket socket) {
        try {
            byte[] message = {(byte) 0xfe, 0x03};
            //将字节输出流包装为带缓冲的字节输出流
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedOutputStream.write(message);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            System.err.println("数据发送失败，连接已断开");
            e.printStackTrace();
        }
    }
    /**
     * 服务器成功接收ARM发过来的数据，给ARM的响应数据
     */
    public static void writeToARMSuccess(Socket socket) {
        try {
            byte[] message = {(byte) 0xfe, 0x01};
            //将字节输出流包装为带缓冲的字节输出流
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedOutputStream.write(message);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            System.err.println("数据发送失败，连接已断开");
            e.printStackTrace();
        }
    }
    /**
     * “大端顺序”，高位在前，低位在后，这句话的意思是说对于整数0x11223344
     * byte[0]保存0x11，byte[1]保存0x22，byte[2]保存0x33，byte[3]保存0x44
     * byte 范围-128~127的有符号字节,read方法返回的0~255的无符号字节
     */

    /**
     * int转成byte
     */
    public static byte intToByte(int x) {
        return (byte) x;
    }

    /**
     * byte转成int
     */
    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    /**
     * int转byte[4]，大端顺序 17 会转换成 00 00 00 11
     */
    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    /**
     * byte[4]转成int，大端顺序
     */
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16 | (b[0] & 0xFF) << 24;
    }

    /**
     * byte[4]转成int，大端顺序，从index位开始的连续4位
     */
    public static int byteArrayToInt(byte[] b, int index) {
        return b[index + 3] & 0xFF | (b[index + 2] & 0xFF) << 8 | (b[index + 1] & 0xFF) << 16 | (b[index + 0] & 0xFF) << 24;
    }

    /**
     * byte[4]转成int，大端顺序，从index位开始的连续4位，针对本项目的特别修改，发送过来的数据是 12 34，但本函数要把它当做00 00 12 34
     */
    public static int byteArrayToIntS(byte[] b, int offset, int length) {
        return b[offset + 1] & 0xFF | (b[offset + 0] & 0xFF) << 8 | (0x00 & 0xFF) << 16 | (0x00 & 0xFF) << 24;
    }

    /**
     * byte数组转成int，小端顺序
     * 高位在后，低位在前
     * 通过byte数组取得float ,将从byte数组的第index位起的4个字节整体转换成float型数据
     * 66 e6 f0 42=120.45
     */
//    public static float byteArrayToInt(byte[] b, int index) {
//        int l;
//        l = b[index + 0];
//        l &= 0xff;
//        l |= ((long) b[index + 1] << 8);
//        l &= 0xffff;
//        l |= ((long) b[index + 2] << 16);
//        l &= 0xffffff;
//        l |= ((long) b[index + 3] << 24);
//        return Float.intBitsToFloat(l);
//    }


    /**
     * float转成byte[4]，大端顺序
     */
    public static byte[] floatToByteArray(float f) {
        int intbits = Float.floatToIntBits(f);//将float里面的二进制串解释为int整数
        return intToByteArray(intbits);
    }

    /**
     * byte[4]转成float，大端顺序
     */
    public static float byteArrayToFloat(byte[] arr) {
        return Float.intBitsToFloat(byteArrayToInt(arr));
    }

    /**
     * byte[]转成String
     */
    public static String byteArrayToString(byte[] arr, int offset, int length) {
        byte[] a = new byte[length];
        for (int i = 0; i < length; i++) {
            a[i] = arr[i + offset];
        }
        return new String(a);
    }

    /**
     * LRC校验 异或值
     *
     * @param buffer 待校验字节数组
     * @param offset 偏移量
     * @param length 校验长度
     * @return 返回值是一个字节对象
     */
    public static byte getLRCHash(byte[] buffer, int offset, int length) {
        //计算LRC校验码
        byte xorResult = buffer[offset];
        // 求xor校验和，注意：XOR运算从第二元素开始
        for (int i = offset + 1; i < offset + length; i++) {
            xorResult ^= buffer[i];
        }
        return xorResult;//LRC校验码
    }

    /**
     * 字节数组转16进制的字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)) + " ");
        }
        return buf.toString();
    }

    /**
     * 字节数组转16进制的字符串
     */
    public static byte[] Message(String ARM_ID, int singleLampCount, ArrayList<SingleLamp> singleLampList) {
        byte[] m = new byte[2 + 2 + 8 + 1 + singleLampCount * 9];
        //添加开始标志和命令标志 2字节
        m[0] = (byte) 0xfe;
        m[1] = (byte) 0x02;
        //添加DATA的长度，四位保留后两个位  2字节
        byte[] len = intToByteArray(8 + 1 + singleLampCount * 9);
//        System.out.println(Arrays.toString(len));
        m[2] = len[2];
        m[3] = len[3];
        //添加ARM的ID  8字节
        byte[] arm = ARM_ID.getBytes();
        for (int i = 0; i < 8; i++) {
            m[i + 4] = arm[i];
        }
        //添加ARM的管理的单灯数量  1字节
        m[12] = intToByte(singleLampCount);
        //添加单灯
        for (int i = 0; i < singleLampList.size(); i++) {
            byte[] singleLamp = singleLampList.get(i).getId().getBytes();
            for (int j = 0; j < 8; j++) {
                m[13 + j + 9 * i] = singleLamp[j];
            }
            m[13 + 9 * (i + 1) - 1] = intToByte(singleLampList.get(i).getState());
        }
        return m;
    }
}
