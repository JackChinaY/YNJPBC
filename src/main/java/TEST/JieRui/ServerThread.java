package TEST.JieRui;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread {
    // 和本线程相关的Socket
    Socket socket = null;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    //线程执行的操作，响应客户端的请求
    public void run() {
//        System.out.println("该客户端是否关闭：" + socket.isClosed());
//        while (true) {
        while (!socket.isClosed()) {
//            if (socket.isClosed())
//                System.out.println("该客户端已关闭。");
//            else System.out.println("该客户端未关闭。");
//            System.out.println("开始读取客户端的发送的数据：");
            byte[] readBuffer = new byte[1024];
            int numBytes = 0;// 端口数据长度
            try {
                numBytes = socket.getInputStream().read(readBuffer);
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    try {
                        socket.close();
                        System.out.println("服务器端强制关闭了此客户端的连接。");
                        break;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
            System.out.print("本次数据长度：" + numBytes);
            byte[] hex = new byte[numBytes];
            for (int i = 0; i < numBytes; i++) {
                hex[i] = readBuffer[i];
            }
            System.out.println(" ，数据：" + bytesToHex(hex));

//            OutputStream os = null;//字节输出流
//            try {
//                os = socket.getOutputStream();
//                PrintWriter pw = new PrintWriter(os);//将输出流包装为打印流
//                pw.write("用户名：whf;密码：789");
//                pw.flush();
//                socket.shutdownOutput();//关闭输出流
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }
    }

    /**
     * 方法三：
     * byte[] to hex string
     *
     * @param bytes
     * @return
     */
    public String bytesToHex(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)) + " ");
        }
        return buf.toString();
    }
}
//        InputStream is = null;
//        InputStreamReader isr = null;
//        BufferedReader br = null;
//        OutputStream os = null;
//        PrintWriter pw = null;
//        try {
//            //获取输入流，并读取客户端信息
//            is = socket.getInputStream();
//            isr = new InputStreamReader(is);
//            br = new BufferedReader(isr);
//            String info = null;
//            while ((info = br.readLine()) != null) {//循环读取客户端的信息
//                System.out.println("我是服务器，客户端说：" + info);
//            }
//            socket.shutdownInput();//关闭输入流
//            //获取输出流，响应客户端的请求
//            os = socket.getOutputStream();
//            pw = new PrintWriter(os);
//            pw.write("欢迎您！");
//            pw.flush();//调用flush()方法将缓冲输出
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } finally {
//            //关闭资源
//            try {
//                if (pw != null)
//                    pw.close();
//                if (os != null)
//                    os.close();
//                if (br != null)
//                    br.close();
//                if (isr != null)
//                    isr.close();
//                if (is != null)
//                    is.close();
//                if (socket != null)
//                    socket.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }