package TEST.JieRui.NIODemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 *
 */
public class NioServer {
    private int port;
    private ByteBuffer echoBuffer = ByteBuffer.allocate(5);

    public NioServer(int port) throws IOException {
        this.port = port;
        go();
    }

    private void go() throws IOException {
        //通道管理器
        Selector selector = Selector.open();
        //获取一个ServerSocket通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        serverSocket.bind(address);
        //将通道管理器与通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，
        //只有当该事件到达时，Selector.select()会返回，否则一直阻塞。
        //https://www.cnblogs.com/gaotianle/p/3325451.html
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Going to listen on " + port);
        //使用轮询访问selector
        while (true) {
            //当有注册的事件到达时，方法返回，否则阻塞。
            int num = selector.select();
//            Set selectedKeys = ;
            //获取selector中的迭代器，选中项为注册的事件
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = (SelectionKey) it.next();
                //客户端请求连接事件
                if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                    ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                    //获得客户端连接通道
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    //在与客户端连接成功后，为客户端通道注册SelectionKey.OP_READ事件。
                    SelectionKey newKey = sc.register(selector, SelectionKey.OP_READ);
                    it.remove();
                    System.out.println("Got connection from " + sc);
                    //有可读数据事件
                } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                    //获取客户端传输数据可读取消息通道
                    SocketChannel sc = (SocketChannel) key.channel();
                    int bytesEchoed = 0;
                    //创建读取数据缓冲器
                    while (true) {
                        echoBuffer.clear();
                        int r = sc.read(echoBuffer);
                        if (r <= 0) {
                            sc.close();
                            break;
                        }
                        echoBuffer.flip();
                        sc.write(echoBuffer);
                        bytesEchoed += r;
                    }
                    System.out.println("Echoed " + bytesEchoed + " from " + sc);
                    it.remove();
                }

            }
            // System.out.println( "going to clear" );
            // selectedKeys.clear();
            // System.out.println( "cleared" );
        }
    }

    static public void main(String args[]) throws Exception {
        int port = 5008;
        new NioServer(port);
    }
}
