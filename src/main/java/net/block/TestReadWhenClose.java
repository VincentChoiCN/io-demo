package net.block;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class TestReadWhenClose {

  static CountDownLatch serverStartLatch = new CountDownLatch(1);

  static class Client extends Thread {
    public void run() {
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

    public void run() {

      try {
        ServerSocket ss = new ServerSocket();
        ss.bind(new InetSocketAddress("0.0.0.0", 7777));
        serverStartLatch.countDown();
        Socket s = ss.accept();
        OutputStream out = s.getOutputStream();
        byte[] bytes = new byte[10];
        new Random().nextBytes(bytes);
        long size = 0;
        out.write(bytes);
        size += bytes.length;
        System.out.println("send " + size);
        Thread.sleep(1000);  // 1s后退出线程，模拟对端死掉
      } catch (Exception e) {

      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    Server server = new Server();
    server.start();

    serverStartLatch.await();
    Thread.sleep(100);
    Client client = new Client();
    client.start();
  }
}
