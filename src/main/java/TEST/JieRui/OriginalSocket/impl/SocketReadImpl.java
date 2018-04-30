package TEST.JieRui.OriginalSocket.impl;

import TEST.JieRui.OriginalSocket.ServerUtils;
import TEST.JieRui.OriginalSocket.SocketRead;

import java.net.Socket;

public class SocketReadImpl implements SocketRead {
    private byte[] tempBytesArray;// 每当端口有数据时就加入到此数组中
    private volatile int count;// 数据长度
    private volatile int position;// 当前处理的数据在数组中的位置

    public SocketReadImpl() {
        this.tempBytesArray = new byte[1024];
        this.count = 0;
        this.position = 0;
    }

    @Override
    public void readDataFromARM(byte[] readBuffer, int length, Socket socket) {
        //出错时清零
        if (count + length >= tempBytesArray.length || count < position || count - position > 100) {
            count = 0;// 计数从头开始
            position = 0;
        } else {
            //将每次接收到的数据放入到缓存中
            for (int w = 0; w < length; w++) {
                tempBytesArray[count] = readBuffer[w];
                count++;
//                     System.out.print("  " + readBuffer[w]);
            }
            if (count - position >= 9) {
                //判断前三位是FE 02 命令
                if (tempBytesArray[position] == (byte) 0xfe && tempBytesArray[position + 1] == 0x02) {
                    System.out.println("ARM上传了信息，指令是02");
                    readBrightnessOfARMAllSingleLamps(socket);
                } else if (tempBytesArray[position] == 0xfe && tempBytesArray[position + 1] == 0x03) {
                    System.out.println("ARM上传了信息，指令是03");
                    readErrorStateOfARMSingleLamp(socket);
                    byte[] message = {0x02};
                    ServerUtils.write(socket, message);
                    count = 0;
                    position = 0;
                }
            }
            System.out.println("count:" + count + " ，position:" + position);
        }
    }

    /**
     * 读取ARM发送过来的数据，数据为该ARM管理的所有单灯的亮度状态
     */
    @Override
    public void readBrightnessOfARMAllSingleLamps(Socket socket) {
        int len = ServerUtils.byteArrayToIntS(tempBytesArray, position + 2, 2);
        System.out.println("  DATA长度 " + len);
        //如果本条命令完整上传完毕
        if (count - position == 4 + len) {
            System.out.print("ARM编号 " + ServerUtils.byteArrayToString(tempBytesArray, position + 4, 8));
            int lenth = ServerUtils.byteToInt(tempBytesArray[position + 2 + 2 + 8]);
            System.out.println("  ARM管理的单灯数量： " + lenth);
            for (int j = 0; j < lenth; j++) {
                System.out.print("单灯编号 " + ServerUtils.byteArrayToString(tempBytesArray, position + 2 + 2 + 8 + 1 + 9 * j, 8));
                System.out.println("  单灯亮度： " + ServerUtils.byteToInt(tempBytesArray[position + 2 + 2 + 8 + 1 + 9 * (j + 1) - 1]));
            }
            byte[] message = {0x02};
            ServerUtils.write(socket, message);
            count = 0;
            position = 0;
        }

        System.out.println("count:" + count + " ，position:" + position);
    }

    /**
     * 读取ARM发送过来的数据，数据为该ARM管理的部分单灯的故障状态
     */
    @Override
    public void readErrorStateOfARMSingleLamp(Socket socket) {
        //判断前三位是Fint("ARM上传了信息，指令是02");
        int len = ServerUtils.byteArrayToIntS(tempBytesArray, position + 2, 2);
        System.out.println("  DATA长度 " + len);
        //如果本条命令完整上传完毕
        if (count - position == 4 + len) {
            System.out.print("ARM编号 " + ServerUtils.byteArrayToString(tempBytesArray, position + 4, 8));
            int lenth = ServerUtils.byteToInt(tempBytesArray[position + 2 + 2 + 8]);
            System.out.println("  ARM管理的单灯数量： " + lenth);
            for (int j = 0; j < lenth; j++) {
                System.out.print("单灯编号 " + ServerUtils.byteArrayToString(tempBytesArray, position + 2 + 2 + 8 + 1 + 9 * j, 8));
                System.out.println("  单灯亮度： " + ServerUtils.byteToInt(tempBytesArray[position + 2 + 2 + 8 + 1 + 9 * (j + 1) - 1]));
            }
            byte[] message = {0x02};
            ServerUtils.write(socket, message);
            count = 0;
            position = 0;
        }
    }
}
