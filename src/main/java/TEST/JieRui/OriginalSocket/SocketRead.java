package TEST.JieRui.OriginalSocket;

import java.net.Socket;

public interface SocketRead {
    void readDataFromARM(byte[] readBuffer, int length, Socket socket);

    void readBrightnessOfARMAllSingleLamps(Socket socket);

    void readErrorStateOfARMSingleLamp(Socket socket);
}
