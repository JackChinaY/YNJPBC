package TEST.JieRui.OriginalSocket.impl;

import TEST.JieRui.OriginalSocket.ServerUtils;
import TEST.JieRui.OriginalSocket.SocketRead;

import java.net.Socket;

public class SocketReadImpl implements SocketRead {
    private static int cacheLenth = 1024 * 2;//缓存数组的长度
    private byte[] tempBytesArray;// 每当端口有数据时就加入到此数组中
    private int count;// 数据长度
    private int position;// 当前处理的数据在数组中的位置

    public SocketReadImpl() {
        this.tempBytesArray = new byte[cacheLenth];
        this.count = 0;
        this.position = 0;
    }

    /**
     * 读取ARM发过来的数据
     *
     * @param readBuffer 本次接收的字节数组
     * @param length     本次接收的字节数组的长度
     * @param socket     本次socket
     */
    @Override
    public void readDataFromARM(byte[] readBuffer, int length, Socket socket) {
        //出错时清零
        if (count + length >= tempBytesArray.length || count < position || count - position > (0.5 * cacheLenth)) {
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
                //判断ARM发送的数据是何种数据
                if (tempBytesArray[position] == (byte) 0xfe && tempBytesArray[position + 1] == 0x11) {
//                    System.out.println("ARM上传了信息，指令是02");
                    //0x11 是ARM被动上报每个单灯的数据
                    readBrightnessOfARMAllSingleLamps(socket);
                    count = 0;
                    position = 0;
                } else if (tempBytesArray[position] == 0xfe && tempBytesArray[position + 1] == 0x12) {
//                    System.out.println("ARM上传了信息，指令是03");
                    //0x12 是AARM被动上报每个单灯的状态数据（故障等信息）
                    readErrorStateOfARMSingleLamp(socket);
//                    byte[] message = {0x02};
//                    ServerUtils.write(socket, message);
                } else if (tempBytesArray[position] == 0xfe && tempBytesArray[position + 1] == 0x13) {
//                    System.out.println("ARM上传了信息，指令是03");
                    //0x12 是ARM被动上报每个单灯的能耗数据
                    readErrorStateOfARMSingleLamp(socket);
//                    byte[] message = {0x02};
//                    ServerUtils.write(socket, message);
                } else if (tempBytesArray[position] == (byte) 0xfe && tempBytesArray[position + 1] == 0x21) {
                    //0x21 是ARM主动上报每个单灯的数据
                    if (readBrightnessOfARMAllSingleLamps(socket)) {
                        ServerUtils.writeToARMSuccess(socket);
                    } else {
                        ServerUtils.writeToARMFailAndRepeat(socket);
                    }
                } else if (tempBytesArray[position] == 0xfe && tempBytesArray[position + 1] == 0x22) {
                    //0x22 是ARM主动上报每个单灯的状态数据（故障等信息）
                    if (readErrorStateOfARMSingleLamp(socket)) {
                        ServerUtils.writeToARMSuccess(socket);
                    } else {
                        ServerUtils.writeToARMFailAndRepeat(socket);
                    }
                } else if (tempBytesArray[position] == 0xfe && tempBytesArray[position + 1] == 0x23) {
                    //0x23 是ARM主动上报每个单灯的能耗数据
                    if (readErrorStateOfARMSingleLamp(socket)) {
                        ServerUtils.writeToARMSuccess(socket);
                    } else {
                        ServerUtils.writeToARMFailAndRepeat(socket);
                    }
                }
            }
//            System.out.println("count:" + count + " ，position:" + position);
        }
    }

    /**
     * 读取ARM发送过来的数据，数据为该ARM管理的所有单灯的亮度状态
     */
    @Override
    public boolean readBrightnessOfARMAllSingleLamps(Socket socket) {
        int len = ServerUtils.byteArrayToIntS(tempBytesArray, position + 2, 2);
//        System.out.println("  DATA长度 " + len);
        //如果本条命令完整上传完毕
        if (count - position == 4 + len) {
//            System.out.print("ARM编号 " + ServerUtils.byteArrayToString(tempBytesArray, position + 4, 8));
            int lenth = ServerUtils.byteToInt(tempBytesArray[position + 2 + 2 + 8]);
//            System.out.println("  ARM管理的单灯数量： " + lenth);
            for (int j = 0; j < lenth; j++) {
//                System.out.print("单灯编号 " + ServerUtils.byteArrayToString(tempBytesArray, position + 2 + 2 + 8 + 1 + 9 * j, 8));
//                System.out.println("  单灯亮度： " + ServerUtils.byteToInt(tempBytesArray[position + 2 + 2 + 8 + 1 + 9 * (j + 1) - 1]));
            }
            count = 0;
            position = 0;
            return true;
        }
        return false;

//        System.out.println("count:" + count + " ，position:" + position);
    }

    /**
     * 读取ARM发送过来的数据，数据为该ARM管理的一个或多个单灯的故障状态
     */
    @Override
    public boolean readErrorStateOfARMSingleLamp(Socket socket) {
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
                System.out.println("  单灯故障： " + ServerUtils.byteToInt(tempBytesArray[position + 2 + 2 + 8 + 1 + 9 * (j + 1) - 1]));
            }
            count = 0;
            position = 0;
            return true;
        }
        return false;
    }

    /**
     * 读取ARM发送过来的数据，数据为该ARM管理的一个或多个单灯的能耗
     */
    @Override
    public boolean readEnergyConsumptionOfARMSingleLamp(Socket socket) {
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
                System.out.println("  单灯能耗： " + ServerUtils.byteToInt(tempBytesArray[position + 2 + 2 + 8 + 1 + 9 * (j + 1) - 1]));
            }
            count = 0;
            position = 0;
            return true;
        }
        return false;
    }
}
