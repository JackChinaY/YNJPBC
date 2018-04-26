package TEST.JieRui;

import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;
import java.util.Map;

public class Test1 {
    public static void main(String[] args) {
//        66 e6 f0 42
        byte[] message = {0x66, (byte) 0xe6, (byte) 0xf0, 0x42};
//        System.out.println("开始发送");
//        System.out.println(ServerUtils.byteArrayToInt(message, 0));
//        System.out.println((byte) 0xe6);
//        System.out.println(ClientSocketMap.getClientSocketMapSize());
        byte[] a = ServerUtils.intToByteArray(1223);
        System.out.println(Arrays.toString(a));
        System.out.println(ServerUtils.byteArrayToInt(a));
//        byte b = ServerUtils.intToByte(220);
//        System.out.println(b);
//        System.out.println(ServerUtils.byteToInt(b));

//        byte[] a = ServerUtils.floatToByteArray(12.023f);
//        System.out.println(Arrays.toString(a));
//        System.out.println(ServerUtils.byteArrayToFloat(a));
    }
}
