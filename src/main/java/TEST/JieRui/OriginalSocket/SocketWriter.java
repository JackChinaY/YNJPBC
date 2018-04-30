package TEST.JieRui.OriginalSocket;

import java.net.Socket;
import java.util.ArrayList;

public interface SocketWriter {
    boolean writerToARM(String ARM_ID, ArrayList<SingleLamp> singleLampList, Socket socket);
}
