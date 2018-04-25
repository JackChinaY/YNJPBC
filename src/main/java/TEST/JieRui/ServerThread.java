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

    /**
     * 线程执行的操作，响应客户端的请求
     */
    @Override
    public void run() {
//        System.out.println("该客户端是否关闭：" + socket.isClosed());
        while (!socket.isClosed()) {
//            if (socket.isClosed())
//                System.out.println("该客户端已关闭。");
//            else System.out.println("该客户端未关闭。");
//            System.out.println("开始读取客户端的发送的数据：");
            try {
                //读取数据
                byte[] readBuffer = new byte[128];
                int numBytes = 0;// 端口数据长度
                numBytes = socket.getInputStream().read(readBuffer);
                System.out.print("本次数据长度：" + numBytes);
                byte[] hex = new byte[numBytes];
                for (int i = 0; i < numBytes; i++) {
                    hex[i] = readBuffer[i];
                }
                System.out.println(" ，数据：" + bytesToHex(hex));

//                byte[] message = {0x00, 0x01, 0x02, 0x03};
//                ServerUtils.write(socket, message);
                //处理数据
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    try {
                        ClientSocketMap.remove(socket.getRemoteSocketAddress().toString());
                        socket.close();
                        System.out.println("服务器端断开了与此客户端的连接");
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
        for (byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)) + " ");
        }
        return buf.toString();
    }
}