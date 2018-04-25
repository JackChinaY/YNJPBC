package TEST.JieRui;

import java.io.*;
import java.net.Socket;

/**
 * 客户端处理线程，每个客户端对应一个线程
 */
public class ServerThread implements Runnable {
    // 和本线程相关的Socket
    Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

//    private byte[] tempAll = new byte[10];// 每当端口有数据时就加入到此数组中
//    private volatile int i = 0;// 负责tempAll数组的移位工作
//    private volatile int p = 0;// 工作指针
//    // 第一步 上位机发送：fe 68 11 00 00 0b b9 d5 16
//    byte[] swjOutPut1 = {0x00, 0x68, 0x11, 0x00};
//    // 第二步 下位机发送：FE 68 20 00 00 0b b9 e4 16 (发送密码)
//    byte[] xwjInPut = {0x01, 0x68, 0x11, 0x00};
//    // 第三步 上位机发送: fe 68 22 00 00 0b ba e7 16 (密码比对成功)

    /**
     * 线程执行的操作，响应客户端的请求
     */
    @Override
    public void run() {

//        System.out.println("该客户端是否关闭：" + socket.isClosed());
        while (!socket.isClosed()) {
//
            try {
                //读取数据
                byte[] readBuffer = new byte[128];//临时缓存数组
                int numBytes = 0;// 数据长度
                numBytes = socket.getInputStream().read(readBuffer);
                //用于测试输出
                byte[] hex = new byte[numBytes];
                for (int i = 0; i < numBytes; i++) {
                    hex[i] = readBuffer[i];
                }
                System.out.print("下位机 " + socket.getRemoteSocketAddress() + " 发送数据长度：" + numBytes);
                System.out.println(" ，数据：" + bytesToHex(hex));
                //解析数据
//                for (int w = 0; w < numBytes; w++) {
//                    tempAll[i] = readBuffer[w];
//                    i++;
//                    // System.out.print("  " + readBuffer[w]);
//                }
//                if (i - p >= 9) {
//                    // // 判断前三位是FE 68 20 命令
//                    if (tempAll[p] == xwjInPut[0] && tempAll[p + 1] == xwjInPut[1] && tempAll[p + 2] == xwjInPut[2]) {
//                        p = p + 9;
//                    }
//                }


//                byte[] message = {0x00, 0x01, 0x02, 0x03};
//                ServerUtils.write(socket, message);
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    try {
                        ClientSocketMap.remove(socket.getRemoteSocketAddress().toString());
                        socket.close();
//                        System.out.println("服务器端断开了与此客户端的连接");
                        ClientSocketMap.show();
                        break;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
        }
    }

    /**
     * 字节数组转16进制的字符串
     */
    public String bytesToHex(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)) + " ");
        }
        return buf.toString();
    }
}