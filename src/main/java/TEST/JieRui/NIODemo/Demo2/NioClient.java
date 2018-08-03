package TEST.JieRui.NIODemo.Demo2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * NIO客户端
 */
public class NioClient {
    public static void main(String[] args) throws IOException {
        for (int i = 0; i < 2; i++) {
            try {
                Thread.sleep(0);
                ClientHandler clientHandler = new ClientHandler("127.0.0.1", 9981, i);
                new Thread(clientHandler).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * 模拟测试客户端
 */
class ClientHandler implements Runnable {
    //管道管理器
    private Selector selector;
    private int num;

    ClientHandler(String serverIp, int port, int num) throws IOException {
        this.num = num;
        //获取socket通道
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        //获得通道管理器
        selector = Selector.open();
        //客户端连接服务器，需要调用channel.finishConnect();才能实际完成连接。
        channel.connect(new InetSocketAddress(serverIp, port));
        //为该通道注册SelectionKey.OP_CONNECT事件
        channel.register(selector, SelectionKey.OP_CONNECT);
    }

    @Override
    public void run() {
        System.out.println("客户端启动");
        //轮询访问selector
        while (true) {
            try {//选择注册过的io操作的事件(第一次为SelectionKey.OP_CONNECT)
                selector.select();
                Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
//                System.out.println(selector.selectedKeys().size());
                while (ite.hasNext()) {
                    SelectionKey key = ite.next();
                    //删除已选的key，防止重复处理
                    ite.remove();
                    if (key.isConnectable()) {
                        SocketChannel channel = (SocketChannel) key.channel();
                        //如果正在连接，则完成连接
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                        }
                        channel.configureBlocking(false);
                        //向服务器发送消息
                        channel.write(ByteBuffer.wrap(String.valueOf(num).getBytes()));

                        //连接成功后，注册接收服务器消息的事件
                        channel.register(selector, SelectionKey.OP_READ);
                        System.out.println("客户端连接成功");
                    } else if (key.isReadable()) { //有可读数据事件
                        SocketChannel channel = (SocketChannel) key.channel();

                        ByteBuffer buffer = ByteBuffer.allocate(10);
                        channel.read(buffer);
                        byte[] data = buffer.array();
                        String message = new String(data);

                        System.out.println("接受了服务器发送的消息, 大小:" + buffer.position() + " 内容: " + message);
                        ByteBuffer outbuffer = ByteBuffer.wrap("xyzw7896.".getBytes());
                        channel.write(outbuffer);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}