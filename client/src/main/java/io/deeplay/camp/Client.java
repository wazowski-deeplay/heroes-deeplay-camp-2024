package io.deeplay.camp;

public class Client {
  public static String ipAddr = "localhost";
  public static int port = 9090;

  public static void main(String[] args) {
    new ClientProcess(ipAddr, port);
  }
}
