package com.bluesky.jeecg.webserver;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;

public class HttpServer {

  /**
   * web_root是html文件夹的地址
   */
  public static final String WEB_ROOT =
    System.getProperty("user.dir") + File.separator  + "webroot";

  //关闭命令
  private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

  // 关闭的状态
  private boolean shutdown = false;

  public static void main(String[] args) {
    HttpServer server = new HttpServer();
    server.await();
  }

  public void await() {
    ServerSocket serverSocket = null;
    int port = 8080;
    try {
      serverSocket =  new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
    }
    catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    // 循环等待请求
    while (!shutdown) {
      Socket socket = null;
      InputStream input = null;
      OutputStream output = null;
      try {
        socket = serverSocket.accept(); //等待socket打开
        input = socket.getInputStream();
        output = socket.getOutputStream();

        // 创建一个请求处理
        Request request = new Request(input);
        request.parse();

        // 创建返回对象
        Response response = new Response(output);
        response.setRequest(request);
        response.sendStaticResource();

        //关闭一个连接
        socket.close();

        //检查是否是关闭命令
        shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
      }
      catch (Exception e) {
        e.printStackTrace();
        continue;
      }
    }
  }
}
