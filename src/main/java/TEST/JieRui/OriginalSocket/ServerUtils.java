package TEST.JieRui.OriginalSocket;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 服务器端常用工具，包含如下方法：
 * int byte byte[] float之间的相互转换、服务器主动给ARM发送的指令、服务器响应ARM指令发送成功接收的标志
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
    public static void writeToARMGetSingleLampErrorState(Socket socket) {
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
     * 服务器发送给ARM的指令：ARM需要上报每个单灯的亮度或故障状态
     */
    public static void writeToARMGetSingleLampBrightnessAndErrorState(String ARM_ID, Socket socket) {
        try {
//            byte[] message = {(byte) 0xfe, 0x01};
            byte[] message = new byte[2 + 2 + ARM_ID.length()];
            //添加开始标志和命令标志 2字节
            message[0] = (byte) 0xfe;
            message[1] = (byte) 0x04;
            //添加DATA的长度，四位保留后两个位  2字节
            byte[] len = ServerUtils.intToByteArray(ARM_ID.length());
//        System.out.println(Arrays.toString(len));
            message[2] = len[2];
            message[3] = len[3];
            //添加ARM的ID  8字节
            byte[] arm = ARM_ID.getBytes();
            for (int i = 0; i < ARM_ID.length(); i++) {
                message[i + 4] = arm[i];
            }
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
    public static void writeToARMGetSingleLampEnergyConsumption(String ARM_ID, Socket socket) {
        try {
//            byte[] message = {(byte) 0xfe, 0x03};
            byte[] message = new byte[2 + 2 + ARM_ID.length()];
            //添加开始标志和命令标志 2字节
            message[0] = (byte) 0xfe;
            message[1] = (byte) 0x04;
            //添加DATA的长度，四位保留后两个位  2字节
            byte[] len = ServerUtils.intToByteArray(ARM_ID.length());
//        System.out.println(Arrays.toString(len));
            message[2] = len[2];
            message[3] = len[3];
            //添加ARM的ID  8字节
            byte[] arm = ARM_ID.getBytes();
            for (int i = 0; i < ARM_ID.length(); i++) {
                message[i + 4] = arm[i];
            }
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
     * 向单个ARM发送控制指令，能控制住该ARM下所有的单灯亮度，可以精确设置每个灯拥有不同的亮度值
     *
     * @param ARM_ID         ARM编号
     * @param singleLampList ARM下所有单灯的编号和亮度值
     * @param socket         通信用的连接
     */
    public boolean setARMSingleLampEachBrightness(String ARM_ID, ArrayList<SingleLamp> singleLampList, Socket socket) {
        byte[] message = new byte[2 + 2 + 8 + 1 + singleLampList.size() * 9];
        //添加开始标志和命令标志 2字节
        message[0] = (byte) 0xfe;
        message[1] = (byte) 0x02;
        //添加DATA的长度，四位保留后两个位  2字节
        byte[] len = ServerUtils.intToByteArray(8 + 1 + singleLampList.size() * 9);
//        System.out.println(Arrays.toString(len));
        message[2] = len[2];
        message[3] = len[3];
        //添加ARM的ID  8字节
        byte[] arm = ARM_ID.getBytes();
        for (int i = 0; i < 8; i++) {
            message[i + 4] = arm[i];
        }
        //添加ARM的管理的单灯数量  1字节
        message[12] = ServerUtils.intToByte(singleLampList.size());
        //添加单灯
        for (int i = 0; i < singleLampList.size(); i++) {
            byte[] singleLamp = singleLampList.get(i).getId().getBytes();
            for (int j = 0; j < 8; j++) {
                message[13 + j + 9 * i] = singleLamp[j];
            }
            message[13 + 9 * (i + 1) - 1] = ServerUtils.intToByte(singleLampList.get(i).getValue());
        }
        //开始发送数据
        try {
            //将字节输出流包装为带缓冲的字节输出流
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedOutputStream.write(message);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 向单个ARM发送控制指令，控制单个ARM上所有单灯的统一亮度（可以理解为打开或关闭ARM，ARM下所有单灯亮度值一样）
     *
     * @param ARM_ID          ARM编号
     * @param brightnessValue 所有单灯的统一亮度值
     * @param socket          通信用的连接
     */
    public boolean setARMSingleLampAllBrightness(String ARM_ID, int brightnessValue, Socket socket) {
        byte[] message = new byte[2 + 2 + 8 + 1];
        //添加开始标志和命令标志 2字节
        message[0] = (byte) 0xfe;
        message[1] = (byte) 0x04;
        //添加DATA的长度，四位保留后两个位  2字节
        byte[] len = ServerUtils.intToByteArray(8 + 1);
//        System.out.println(Arrays.toString(len));
        message[2] = len[2];
        message[3] = len[3];
        //添加ARM的ID  8字节
        byte[] arm = ARM_ID.getBytes();
        for (int i = 0; i < 8; i++) {
            message[i + 4] = arm[i];
        }
        //添加所有单灯的统一亮度值  1字节
        message[12] = ServerUtils.intToByte(brightnessValue);
        //开始发送数据
        try {
            //将字节输出流包装为带缓冲的字节输出流
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedOutputStream.write(message);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 服务器成功接收ARM发过来的数据，给ARM的响应数据
     */
    public static void writeToARMSuccess(Socket socket) {
        try {
            byte[] message = {(byte) 0xfe, 0x00, 0x01, 0x01};
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
     * 服务器失败接收ARM发过来的数据，给ARM的响应数据，让ARM重发
     */
    public static void writeToARMFailAndRepeat(Socket socket) {
        try {
            byte[] message = {(byte) 0xfe, 0x00, 0x01, 0x02};
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
     * 服务器主动给ARM发送指令的调用接口
     *
     * @param ARM_ID         ARM的编号，8位字符串，如：“12345678”
     * @param singleLampList 单灯的集合，8位字符串，如：“12345678”
     */
    public static byte[] Message(String ARM_ID, ArrayList<SingleLamp> singleLampList) {
        byte[] m = new byte[2 + 2 + 8 + 1 + singleLampList.size() * 9];
        //添加开始标志和命令标志 2字节
        m[0] = (byte) 0xfe;
        m[1] = (byte) 0x02;
        //添加DATA的长度，四位保留后两个位  2字节
        byte[] len = intToByteArray(8 + 1 + singleLampList.size() * 9);
//        System.out.println(Arrays.toString(len));
        m[2] = len[2];
        m[3] = len[3];
        //添加ARM的ID  8字节
        byte[] arm = ARM_ID.getBytes();
        for (int i = 0; i < 8; i++) {
            m[i + 4] = arm[i];
        }
        //添加ARM的管理的单灯数量  1字节
        m[12] = intToByte(singleLampList.size());
        //添加单灯
        for (int i = 0; i < singleLampList.size(); i++) {
            byte[] singleLamp = singleLampList.get(i).getId().getBytes();
            for (int j = 0; j < 8; j++) {
                m[13 + j + 9 * i] = singleLamp[j];
            }
            m[13 + 9 * (i + 1) - 1] = intToByte(singleLampList.get(i).getValue());
        }
        return m;
    }
}
