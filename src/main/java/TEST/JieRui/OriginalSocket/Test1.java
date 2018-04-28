package TEST.JieRui.OriginalSocket;

import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Test1 {
    public static void main(String[] args) {
//        66 e6 f0 42
//        byte[] message = {0x66, (byte) 0xe6, (byte) 0xf0, 0x42};
//        byte[] a = {0x00, (byte) 0x00, (byte) 0x00, 0x27};
//        byte[] a = new byte[3];
//        System.out.println(Arrays.toString(a));
//        System.out.println("开始发送");
//        System.out.println(ServerUtils.byteArrayToInt(message, 0));
//        System.out.println((byte) 0xe6);
//        System.out.println(ClientSocketMap.getClientSocketMapSize());
//        byte[] a = ServerUtils.intToByteArray(1223);
//        System.out.println(Arrays.toString(a));
//        System.out.println(ServerUtils.byteArrayToInt(a));
//        byte b = ServerUtils.intToByte(220);
//        System.out.println(b);
//        System.out.println(ServerUtils.byteArrayToIntS(a, 0, 2));
//        System.out.println(ServerUtils.bytesToHex(ServerUtils.intToByteArray(17)));

//        byte[] a = ServerUtils.floatToByteArray(12.023f);
//        System.out.println(Arrays.toString(a));
//        System.out.println(ServerUtils.byteArrayToFloat(a));
//        byte[] b = "100000000011".getBytes();
//        System.out.println(ServerUtils.bytesToHex(b));
////        System.out.println(Arrays.toString(b));
//        System.out.println(new String(b));

        ArrayList<SingleLamp> array = new ArrayList<>();
        array.add(new SingleLamp("10001001", 81));
        array.add(new SingleLamp("10001002", 82));
        array.add(new SingleLamp("10001003", 83));
        array.add(new SingleLamp("10001004", 84));
        System.out.println(ServerUtils.bytesToHex(ServerUtils.Message("12341234", array.size(), array)));
    }
}
