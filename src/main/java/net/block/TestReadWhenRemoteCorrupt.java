package net.block;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


/**
 * 优先运行server进程，然后执行client，等待client输出一次信息之后，手动杀掉server，观察client read函数行为。
 */
public class TestReadWhenRemoteCorrupt {

  static class Client extends Thread {
    public static void main(String[] args) {
      try {
        Socket s = new Socket();
        s.connect(new InetSocketAddress("127.0.0.1", 7777));
        InputStream is = s.getInputStream();
        byte[] bytes = new byte[1024];
        while (true) {
          int readCount = is.read(bytes);
          System.out.println("readCount = " + readCount + " content = " + new String(bytes, 0, readCount));
          for (int i = 0; i < bytes.length; i++) {
            bytes[i] = 0;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  static class Server extends Thread {

    public static void main(String[] args) {
      try {
        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress("0.0.0.0", 7777));
        Socket s = ss.accept();
        OutputStream out = s.getOutputStream();
        byte[] bytes = new byte[10];
        new Random().nextBytes(bytes);
        long size = 0;
        out.write(bytes);
        size += bytes.length;
        System.out.println("send " + size);
        Thread.sleep(1000000);  // 模拟server常驻
      } catch (Exception e) {

      }
    }
  }

}
