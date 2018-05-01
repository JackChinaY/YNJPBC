package TEST.JieRui.OriginalSocket;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * 模拟测试客户端
 */
public class Client {
    public static void main(String[] args) {
        ArrayList<SingleLamp> array = new ArrayList<>();
        array.add(new SingleLamp("10001001", 81));
        array.add(new SingleLamp("10001002", 82));
        array.add(new SingleLamp("10001003", 83));
        array.add(new SingleLamp("10001004", 84));
        double ARM_ID = 12345001;
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(50);
                ClientHandler clientHandler = new ClientHandler(String.valueOf(ARM_ID++), array);
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
    static double count = 0;
    double size = 0;
    String ARM_ID;
    ArrayList<SingleLamp> singleLampList;

    public ClientHandler(String ARM_ID, ArrayList<SingleLamp> singleLampList) {
        this.ARM_ID = ARM_ID;
        this.singleLampList = singleLampList;
    }

    @Override
    public void run() {
        //1.创建客户端Socket，指定服务器地址和端口
        Socket socket = null;
        try {
            socket = new Socket("localhost", 5001);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                size++;
                if (size == 5) {
                    if (!socket.isClosed()) {
                        socket.shutdownOutput();
                        socket.close();
                        break;
//                        Thread.sleep(5000);
//                        socket = new Socket("localhost", 5001);
                    }

                }
                Thread.sleep(200);
                writerToARM(ARM_ID, singleLampList, socket);
                System.out.println(socket.getLocalAddress() + ":" + socket.getLocalPort() + " 成功发送给服务器一次数据，总发送次数：" + count++);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 向单个ARM发送控制指令，能控制住该ARM下所有的单灯状态
     */
    public boolean writerToARM(String ARM_ID, ArrayList<SingleLamp> singleLampList, Socket socket) {
        byte[] message = new byte[2 + 2 + 8 + 1 + singleLampList.size() * 9];
        //添加开始标志和命令标志 2字节
        message[0] = (byte) 0xfe;
        message[1] = (byte) 0x02;
        //添加DATA的长度，四位保留后两个位  2字节
        byte[] len = ServerUtils.intToByteArray(8 + 1 + singleLampList.size() * 9);
//        System.out.println(Arrays.toString(len));
        message[2] = len[2];
        message[3] = len[3];
        //添加ARM的ID  8字节
        byte[] arm = ARM_ID.getBytes();
        for (int i = 0; i < 8; i++) {
            message[i + 4] = arm[i];
        }
        //添加ARM的管理的单灯数量  1字节
        message[12] = ServerUtils.intToByte(singleLampList.size());
        //添加单灯
        for (int i = 0; i < singleLampList.size(); i++) {
            byte[] singleLamp = singleLampList.get(i).getId().getBytes();
            for (int j = 0; j < 8; j++) {
                message[13 + j + 9 * i] = singleLamp[j];
            }
            message[13 + 9 * (i + 1) - 1] = ServerUtils.intToByte(singleLampList.get(i).getState());
        }
        //开始发送数据
        try {
            //将字节输出流包装为带缓冲的字节输出流
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedOutputStream.write(message);
            bufferedOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
