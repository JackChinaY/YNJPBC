package TEST.JieRui.OriginalSocket;

import java.net.Socket;

public interface SocketRead {
    void readDataFromARM(byte[] readBuffer, int length, Socket socket);
    /**
     * 读取ARM发送过来的数据，数据为该ARM管理的所有单灯的亮度状态
     */
    void readBrightnessOfARMAllSingleLamps(Socket socket);
    /**
     * 读取ARM发送过来的数据，数据为该ARM管理的一个或多个单灯的故障状态
     */
    void readErrorStateOfARMSingleLamp(Socket socket);
    /**
     * 读取ARM发送过来的数据，数据为该ARM管理的一个或多个单灯的能耗
     */
    void readEnergyConsumptionOfARMSingleLamp(Socket socket);
}
