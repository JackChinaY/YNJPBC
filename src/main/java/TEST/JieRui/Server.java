package TEST.JieRui;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Currency;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务器端Socket
 */
public class Server {
    public static ServerSocket serverSocket = null;

    public void init(int port) {
        try {
            //1.创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
            serverSocket = new ServerSocket(port);
//            Socket socket = null;
            //记录客户端的数量
            int count = 0;
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
//                System.out.println("当前客户端的IP：" + socket.getInetAddress() + "：" + socket.getPort());
//                System.out.println("当前客户端的IP：" + address.getHostAddress()+"端口号：" + address.getHostName());
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                    System.out.println("此客户端已关闭连接。");
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

//        for (int i = 0; i < 2; i++) {
//            try {
//                Thread.sleep(5000);
//                for (Map.Entry entry : ClientSocketMap.clientSocketMap.entrySet()) {
//                    byte[] message = {0x00, 0x01, 0x02, 0x03};
//                    ServerUtils.write((Socket) entry.getValue(), message);
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}

/**
 * 客户Socket端集合
 */
class ServerUtils {
    /**
     * 向下位机发送数据
     */
    public static void write(Socket socket, byte[] message) {
        try {
            //PrintWriter pw = new PrintWriter(socket.getOutputStream());//将字节输出流包装为打印流
            //pw.write(message);
            //pw.flush();
            //将字节输出流包装为带缓冲的字节输出流
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedOutputStream.write(message);
            bufferedOutputStream.flush();
//            socket.shutdownOutput();//关闭输出流
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}