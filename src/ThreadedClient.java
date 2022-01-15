package src;

import java.net.Socket;


public class ThreadedClient {
    public static void main(String[] args) throws Exception {

        Thread[] threads = {

            new Thread(() -> {
                try  {
                    Socket s = new Socket("localhost", 12345);
                    Demultiplexer m1 = new Demultiplexer(new TaggedConnection(s));
                    m1.start();
                    m1.send(0, ("armando 123").getBytes());
                    Thread.sleep(100);
                    byte[] data = m1.receive(0);
                    System.out.println("(0) Reply Armando: " + new String(data));
                    byte[] data7 = m1.receive(0);
                    System.out.println("(0) Reply Armando: " + new String(data7));
                    m1.send(1, ("armando 123").getBytes());
                    Thread.sleep(100);
                    byte[] b1 = m1.receive(1);
                    int status = Integer.parseInt(new String(b1));
                    byte[] b2 = m1.receive(1);
                    System.out.println("(0) Reply Armando: " + new String(b2));
                    if (status == 1) {
                        byte[] b3 = m1.receive(1);
                        System.out.println("(0) Reply Armando: " + new String(b3));
                    }
                    m1.send(2, ("armando;Lisboa-Braga;2022-01-15/2022-01-16").getBytes());
                    byte[] b4 = m1.receive(2);
                    System.out.println("(0) Reply Armando: " + new String(b4));
                    byte[] b5 = m1.receive(2);
                    System.out.println("(0) Reply Armando: " + new String(b5));
                    m1.close();
                }  catch (Exception ignored) {}
            }),
            
            new Thread(() -> {
                try  {
                    Socket s1 = new Socket("localhost", 12345);
                    Demultiplexer m1 = new Demultiplexer(new TaggedConnection(s1));
                    m1.start();
                    m1.send(0, ("stone 123").getBytes());
                    Thread.sleep(100);
                    byte[] data = m1.receive(0);
                    System.out.println("(0) Reply StOnE: " + new String(data));
                    byte[] data7 = m1.receive(0);
                    System.out.println("(0) Reply StOnE: " + new String(data7));
                    m1.send(1, ("stone 123").getBytes());
                    Thread.sleep(100);
                    byte[] b1 = m1.receive(1);
                    int status = Integer.parseInt(new String(b1));
                    byte[] b2 = m1.receive(1);
                    System.out.println("(0) Reply StOnE: " + new String(b2));
                    if (status == 1) {
                        byte[] b3 = m1.receive(1);
                        System.out.println("(0) Reply StOnE: " + new String(b3));
                    }
                    m1.send(2, ("stone;Lisboa-Braga;2022-01-14/2022-01-14").getBytes());
                    byte[] b4 = m1.receive(2);
                    System.out.println("(0) Reply StOnE: " + new String(b4));
                    byte[] b5 = m1.receive(2);
                    System.out.println("(0) Reply StOnE: " + new String(b5));
                    m1.close();
                }  catch (Exception ignored) {}
            }),

            new Thread(() -> {
                try  {
                    Socket s2 = new Socket("localhost", 12345);
                    System.out.println("HHere\n");
                    Demultiplexer m1 = new Demultiplexer(new TaggedConnection(s2));
                    m1.start();
                    m1.send(0, ("stone 123").getBytes());
                    Thread.sleep(100);
                    byte[] data = m1.receive(0);
                    System.out.println("(0) Reply StOnE: " + new String(data));
                    byte[] data7 = m1.receive(0);
                    System.out.println("(0) Reply StOnE: " + new String(data7));
                    m1.send(1, ("stone 123").getBytes());
                    Thread.sleep(100);
                    byte[] b1 = m1.receive(1);
                    int status = Integer.parseInt(new String(b1));
                    byte[] b2 = m1.receive(1);
                    System.out.println("(0) Reply StOnE: " + new String(b2));
                    if (status == 1) {
                        byte[] b3 = m1.receive(1);
                        System.out.println("(0) Reply StOnE: " + new String(b3));
                    }
                    m1.send(2, ("stone;Lisboa-Braga;2022-01-14/2022-01-14").getBytes());
                    byte[] b4 = m1.receive(2);
                    System.out.println("(0) Reply StOnE: " + new String(b4));
                    byte[] b5 = m1.receive(2);
                    System.out.println("(0) Reply StOnE: " + new String(b5));
                    m1.close();
                }  catch (Exception ignored) {}
            }),
        };

        for (Thread t: threads) t.start();
        for (Thread t: threads) t.join();
    }
}
