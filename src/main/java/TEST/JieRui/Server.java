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
                //调用accept()方法开始监听，等待客户端的连接
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
        ClientSocketMap clientSocketMap = new ClientSocketMap();
        new Thread(clientSocketMap).start();
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
}