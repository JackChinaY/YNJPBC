package TEST.JieRui;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户Socket端集合
 */
public class ClientSocketMap implements Runnable {
    private static Map<String, Socket> clientSocketMap = new ConcurrentHashMap<>();

    public static Map<String, Socket> getClientSocketMap() {
        return clientSocketMap;
    }

    public static int getClientSocketMapSize() {
        return clientSocketMap.size();
    }

    /**
     * 添加一个新的客户端
     */
    public static void add(Socket socket) {
        clientSocketMap.put(socket.getRemoteSocketAddress().toString(), socket);
    }

    /**
     * 移除一个已存在客户端
     */
    public static void remove(String IP) {
        for (Map.Entry entry : clientSocketMap.entrySet()) {
            if (entry.getKey().equals(IP)) {
                clientSocketMap.remove(IP);
                System.out.println("服务器端断开了与客户端 " + IP + " 的连接");
            }
        }
    }

    /**
     * 遍历所有已存在客户端
     */
    public static void show() {
        System.out.println("目前还保持连接的客户端数： " + clientSocketMap.size());
//        System.out.println("服务器端还保持连接的客户端有 " + clientSocketMap.size() + " ，分别是：");
//        for (Map.Entry entry : clientSocketMap.entrySet()) {
//            System.out.println(entry.getKey());
//        }
    }

    /**
     * 线程测试
     */
    @Override
    public void run() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 2; i++) {
            try {
                Thread.sleep(5000);
                for (Map.Entry entry : ClientSocketMap.getClientSocketMap().entrySet()) {
                    byte[] message = {0x00, 0x01, 0x02, 0x03};
                    ServerUtils.write((Socket) entry.getValue(), message);
                    System.out.println("给 " + entry.getKey() + " 发送数据成功");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
