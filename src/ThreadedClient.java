package src;

import java.net.Socket;


public class ThreadedClient {
    public static void main(String[] args) throws Exception {
        Socket s = new Socket("localhost", 12345);
        Demultiplexer m1 = new Demultiplexer(new TaggedConnection(s));
        Demultiplexer m2 = new Demultiplexer(new TaggedConnection(s));
        m1.start();
        m2.start();

        Thread[] threads = {

            new Thread(() -> {
                try  {
                    m1.send(0, ("armando 123").getBytes());
                    Thread.sleep(100);
                    byte[] data = m1.receive(0);
                    System.out.println("(0) Reply Armando: " + new String(data));
                    m1.send(1, ("armando 123").getBytes());
                    Thread.sleep(100);
                    data = m1.receive(1);
                    System.out.println("(1) Reply Armando: " + new String(data));
                    m1.send(2, ("armando;Lisboa-Braga;2022-01-14/2022-01-14").getBytes());
                    Thread.sleep(100);
                    data = m1.receive(2);
                    System.out.println("(2) Reply Armando: " + new String(data));
                }  catch (Exception ignored) {}
            }),
            
            new Thread(() -> {
                try  {
                    m2.send(0, ("stone 123").getBytes());
                    Thread.sleep(100);
                    byte[] data = m2.receive(0);
                    System.out.println("(0) Reply Stone: " + new String(data));
                    m2.send(1, ("stone 123").getBytes());
                    Thread.sleep(100);
                    data = m2.receive(1);
                    System.out.println("(1) Reply Stone: " + new String(data));
                    m2.send(2, ("stone;Lisboa-Braga;2022-01-14/2022-01-14").getBytes());
                    Thread.sleep(100);
                    data = m2.receive(2);
                    System.out.println("(2) Reply Stone: " + new String(data));
                }  catch (Exception ignored) {}
            }),
            /*
            new Thread(() -> {
                try  {
                    // One-way
                    m.send(0, ":-p".getBytes());
                }  catch (Exception ignored) {}
            }),

            new Thread(() -> {
                try  {
                    // Get stream of messages until empty msg
                    m.send(2, "ABCDE".getBytes());
                    for (;;) {
                        byte[] data = m.receive(2);
                        if (data.length == 0)
                            break;
                        System.out.println("(4) From stream: " + new String(data));
                    }
                } catch (Exception ignored) {}
            }),

            new Thread(() -> {
                try  {
                    // Get stream of messages until empty msg
                    m.send(4, "123".getBytes());
                    for (;;) {
                        byte[] data = m.receive(4);
                        if (data.length == 0)
                            break;
                        System.out.println("(5) From stream: " + new String(data));
                    }
                } catch (Exception ignored) {}
            })
            */
        };

        for (Thread t: threads) t.start();
        for (Thread t: threads) t.join();
        m1.close();
        m2.close();
    }
}
