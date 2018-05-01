package TEST.JieRui.OriginalSocket;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

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
     * 添加一个新的客户端，如果已存在则将旧的覆盖掉
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
     * 移除所有已存在客户端
     */
    public static void removeAll() {
        for (Map.Entry entry : clientSocketMap.entrySet()) {
            clientSocketMap.remove(entry.getKey());
        }
    }

    /**
     * 移除所有已失效的客户端，目的是为了防止ARM故障断开而导致的连接失效
     */
    public static void removeInvalidation() {
        for (Map.Entry entry : clientSocketMap.entrySet()) {
            if (((Socket) entry.getValue()).isClosed()) {
                clientSocketMap.remove(entry.getKey());
            }
        }
    }

    /**
     * 遍历所有已存在客户端
     */
    public static void show() {
        System.out.println("清理过一遍已失效的客户端，目前还保持连接的客户端数： " + clientSocketMap.size());
//        System.out.println(" 线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" +
//                executor.getQueue().size() + "，已执行完的任务数目：" + executor.getCompletedTaskCount());
//        System.out.println("服务器端还保持连接的客户端有 " + clientSocketMap.size() + " ，分别是：");
//        for (Map.Entry entry : clientSocketMap.entrySet()) {
//            System.out.println(entry.getKey());
//        }
    }

    /**
     * 遍历所有已存在客户端
     */
    public static void show(ThreadPoolExecutor executor) {
        System.out.print("目前还保持连接的客户端数： " + clientSocketMap.size());
        System.out.println(" 线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" +
                executor.getQueue().size() + "，已执行完的任务数目：" + executor.getCompletedTaskCount());
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
        while (true) {
            try {
                Thread.sleep(20000);
                removeInvalidation();
                show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        for (int i = 0; i < 2; i++) {
//            try {
//                Thread.sleep(5000);
//                for (Map.Entry entry : ClientSocketMap.getClientSocketMap().entrySet()) {
//                    byte[] message = {0x00, 0x01, 0x02, 0x03};
//                    ServerUtils.write((Socket) entry.getValue(), message);
//                    System.out.println("给 " + entry.getKey() + " 发送数据成功");
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
