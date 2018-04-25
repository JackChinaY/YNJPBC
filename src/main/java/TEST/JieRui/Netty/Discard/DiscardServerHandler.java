package TEST.JieRui.Netty.Discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class DiscardServerHandler extends ChannelHandlerAdapter { // (1)

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        // Discard the received data silently.
        System.out.print("服务器端收到一条数据:");
        ByteBuf in = (ByteBuf) msg;
        try {
//msg.
            System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
//            while (in.isReadable()) { // (1)
//                System.out.print((char) in.readByte());
//                System.out.flush();
//            }
            ctx.write(msg); // (1)
            ctx.flush(); // (2)
        } finally {
//            ReferenceCountUtil.release(msg); // (2)
        }
        System.out.println();
//        ((ByteBuf) msg).release(); // (3)
    }

    /**
     * channelActive()方法将会在连接被建立并且准备进行通信时被调用。
     * 因此让我们在这个方法里完成一个代表当前时间的32位整数消息的构建工作
     * @param ctx
     */
//    @Override
//    public void channelActive(final ChannelHandlerContext ctx) { // (1)
//        final ByteBuf time = ctx.alloc().buffer(4); // (2)
//        time.writeInt(1234);
//        System.out.println(1234);
//        final ChannelFuture f = ctx.writeAndFlush(time); // (3)
//        f.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture future) {
//                assert f == future;
//                ctx.close();
//            }
//        }); // (4)
//    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}