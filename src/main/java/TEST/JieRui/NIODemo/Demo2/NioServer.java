package TEST.JieRui.NIODemo.Demo2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * NIO服务器端
 * NIO最重要的组成部分
 * 通道 Channels 是一个对象，可以通过它读取和写入数据
 * 缓冲区 Buffers 是一个对象， 它包含一些要写入或者刚读出的数据
 * 选择器 Selectors 是Java NIO中能够检测一到多个NIO通道，并能够知晓通道是否为诸如读写事件做好准备的组件
 * https://www.cnblogs.com/gaotianle/p/3325451.html
 */
public class NioServer {
    //通道管理器
    private Selector selector;

    //获取一个ServerSocket通道，并初始化通道
    private NioServer(int port) throws IOException {
        //获取一个ServerSocket通道
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(port));
        //获取通道管理器
        selector = Selector.open();
        //将通道管理器与ServerSocket通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件，
        // 意思是在通过Selector监听此Channel是对什么事件感兴趣只有当该事件到达时，Selector.select()会返回，否则一直阻塞。
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void listen() throws IOException {
        System.out.println("服务器端启动成功");
        //使用轮询访问selector
        while (true) {
            //当有注册的事件到达时，方法返回，否则阻塞
            selector.select();
            //获取selector中的迭代器，选中项为注册的事件
            Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
            System.out.println(selector.selectedKeys().size());
            while (ite.hasNext()) {
                SelectionKey key = ite.next();
                //删除已选key，防止重复处理
                ite.remove();
                //客户端请求连接事件
                if (key.isAcceptable()) {
                    //先拿到这个SelectionKey里面的ServerSocketChannel
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
                    //获得客户端连接通道
                    SocketChannel channel = serverSocketChannel.accept();
                    channel.configureBlocking(false);
                    //向客户端发消息
                    channel.write(ByteBuffer.wrap(new String("abcd1234").getBytes()));
                    //在与客户端连接成功后，为客户端通道注册SelectionKey.OP_READ事件,如果你对不止一种事件感兴趣，那么可以用“位或”操作符将常量连接起来，如下：int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE
                    channel.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端请求连接事件");
                } else if (key.isReadable()) {//有可读数据事件
                    //获取客户端传输数据可读取消息通道
                    SocketChannel channel = (SocketChannel) key.channel();
                    //创建读取数据缓冲器
                    ByteBuffer buffer = ByteBuffer.allocate(10);
                    int read = channel.read(buffer);
                    byte[] data = buffer.array();
                    String message = new String(data);

                    System.out.println("接受了客户端(" + channel.socket().getRemoteSocketAddress() + ")发送的消息, 大小:" + buffer.position() + " 内容: " + message);
//                    ByteBuffer outbuffer = ByteBuffer.wrap(("server.".concat(msg)).getBytes());
//                    channel.write(outbuffer);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new NioServer(9981).listen();
    }
}
