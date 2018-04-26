package TEST.JieRui;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务器端Socket
 */
public class Server {
    public static ServerSocket serverSocket = null;

    public void init(int port) {
        try {
            //创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
            serverSocket = new ServerSocket(port);
            System.out.println("***服务器即将启动，等待客户端的连接***");
            //循环监听等待客户端的连接
            while (true) {
                //调用accept()方法开始监听，等待客户端的连接，如果没有连接将一直在此阻塞等待
                Socket socket = serverSocket.accept();
                ClientSocketMap.add(socket);
                //创建一个新的线程
                ServerThread serverThread = new ServerThread(socket);
                new Thread(serverThread).start();
                System.out.println("远程客户端的IP：" + socket.getRemoteSocketAddress());
                ClientSocketMap.show();
//                System.out.println("当前客户端的IP：" + address.getHostAddress()+"端口号：" + address.getHostName());
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                try {
                    //如果服务器端出现问题，则关闭服务器端，并释放掉所有资源
                    serverSocket.close();
                    ClientSocketMap.removeAll();
                    System.out.println("此服务端已关闭连接");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        ClientSocketMap clientSocketMap = new ClientSocketMap();
//        new Thread(clientSocketMap).start();
        new Server().init(5001);
    }
}

/**
 * 服务器端常用工具
 */
class ServerUtils {
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
     * int转byte[4]，大端顺序
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
}