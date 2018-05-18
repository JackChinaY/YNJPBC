package TEST.JieRui.OriginalSocket;

import java.net.Socket;

public interface SocketRead {
    void readDataFromARM(byte[] readBuffer, int length, Socket socket);
    /**
     * 读取ARM发送过来的数据，数据为该ARM管理的所有单灯的亮度状态
     */
    boolean readBrightnessOfARMAllSingleLamps(Socket socket);
    /**
     * 读取ARM发送过来的数据，数据为该ARM管理的一个或多个单灯的故障状态
     */
    boolean readErrorStateOfARMSingleLamp(Socket socket);
    /**
     * 读取ARM发送过来的数据，数据为该ARM管理的一个或多个单灯的亮度或故障状态
     */
    boolean readBrightnessAndErrorStateOfARMSingleLamp(Socket socket);

    /**
     * 读取ARM发送过来的数据，数据为该ARM管理的一个或多个单灯的能耗
     */
    boolean readEnergyConsumptionOfARMSingleLamp(Socket socket);

    boolean ARMConnectServerAndreadIDOfARM(Socket socket);
}
