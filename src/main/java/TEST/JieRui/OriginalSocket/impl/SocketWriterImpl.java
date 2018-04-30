package TEST.JieRui.OriginalSocket.impl;

import TEST.JieRui.OriginalSocket.ServerUtils;
import TEST.JieRui.OriginalSocket.SingleLamp;
import TEST.JieRui.OriginalSocket.SocketWriter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class SocketWriterImpl implements SocketWriter {

    /**
     * 向单个ARM发送控制指令，能控制住该ARM下所有的单灯状态
     */
    @Override
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
