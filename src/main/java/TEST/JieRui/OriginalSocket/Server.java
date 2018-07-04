package TEST.JieRui.OriginalSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务器端Socket
 */
public class Server {
    private ServerSocket serverSocket;
    //线程池
    private ThreadPoolExecutor executor;

    public Server(int port) {
        //创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("***服务器即将启动，等待客户端的连接***");
        /**
         * 启线程池
         * corePoolSize  核心线程数，默认情况下核心线程会一直存活，即使处于闲置状态也不会受存keepAliveTime限制。除非将allowCoreThreadTimeOut设置为true。
         * maximumPoolSize  线程池所能容纳的最大线程数。超过这个数的线程将被阻塞。当任务队列没有设置大小的LinkedBlockingDeque时，这个值无效。
         * keepAliveTime  非核心线程的闲置超时时间，超过这个时间就会被回收。
         * unit  指定keepAliveTime的单位，如TimeUnit.SECONDS。当将allowCoreThreadTimeOut设置为true时对corePoolSize生效。
         * workQueue  线程池中的阻塞任务队列.常用的有三种队列，SynchronousQueue,LinkedBlockingDeque,ArrayBlockingQueue。
         * threadFactory  线程工厂，提供创建新线程的功能。ThreadFactory是一个接口，只有一个方法
         * https://blog.csdn.net/qq_25806863/article/details/71126867
         * 重要说明：当corePoolSize消耗完的时候，新的请求将会阻塞，最大阻塞数量为workQueue的capacity个，
         * 当最大阻塞数量消耗完的时候，线程池将会开启新的线程执行阻塞队列中的请求，开启的新的线程数为maximumPoolSize-corePoolSize个
         */
        executor = new ThreadPoolExecutor(100, 200, 200, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(200));
        //定时清除失效连接
        new Thread(new ClientSocketMap()).start();
    }

    /**
     * 开始监听端口
     */
    public void start() {
        try {
            //循环监听等待客户端的连接
            while (true) {
                //调用accept()方法开始监听，等待客户端的连接，如果没有连接将一直在此阻塞等待
                Socket socket = serverSocket.accept();
//                socket.setKeepAlive(true);//开启保持活动状态的套接字
//                socket.setSoTimeout(5000);//设置超时时间
//                ClientSocketMap.add(socket);
                //创建一个新的线程，使用线程池
                ServerHandler serverHandler = new ServerHandler(socket, executor);
                executor.execute(serverHandler);
//                new Thread(serverThread).start();
                System.out.println("远程客户端的IP：" + socket.getRemoteSocketAddress());
                ClientSocketMap.show(executor);
//                System.out.println("线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" +
//                        executor.getQueue().size() + "，已执行完的任务数目：" + executor.getCompletedTaskCount());
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
        new Server(5001).start();
    }
}