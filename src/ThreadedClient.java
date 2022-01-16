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
                    //Registo
                    m1.send(0, ("armando 123").getBytes());
                    //Thread.sleep(100);
                    byte[] data = m1.receive(0);
                    System.out.println("(0) Reply Armando: " + new String(data));
                    byte[] data7 = m1.receive(0);
                    System.out.println("(0) Reply Armando: " + new String(data7));
                    m1.send(1, ("armando 123").getBytes());
                    //Log In
                    //Thread.sleep(100);
                    byte[] b1 = m1.receive(1);
                    int status = Integer.parseInt(new String(b1));
                    byte[] b2 = m1.receive(1);
                    System.out.println("(1) Reply Armando: " + new String(b2));
                    if (status == 1) {
                        byte[] b3 = m1.receive(1);
                        System.out.println("(1) Reply Armando: " + new String(b3));
                    }
                    Thread.sleep(2000);
                    //Reserva
                    m1.send(2, ("armando;Lisboa-Braga;2022-01-01/2022-01-01").getBytes());
                    byte[] b4 = m1.receive(2);
                    System.out.println("(2) Reply Armando: " + new String(b4));
                    byte[] b5 = m1.receive(2);
                    System.out.println("(2) Reply Armando: " + new String(b5));
                    m1.close();
                }  catch (Exception ignored) {}
            }),
            
            new Thread(() -> {
                try  {
                    Socket s1 = new Socket("localhost", 12345);
                    Demultiplexer m1 = new Demultiplexer(new TaggedConnection(s1));
                    m1.start();
                    //Registo
                    m1.send(0, ("stone 123").getBytes());
                    Thread.sleep(100);
                    byte[] data = m1.receive(0);
                    System.out.println("(0) Reply StOnE: " + new String(data));
                    byte[] data7 = m1.receive(0);
                    System.out.println("(0) Reply StOnE: " + new String(data7));
                    //Log In
                    m1.send(1, ("stone 123").getBytes());
                    Thread.sleep(100);
                    byte[] b1 = m1.receive(1);
                    int status = Integer.parseInt(new String(b1));
                    byte[] b2 = m1.receive(1);
                    System.out.println("(1) Reply StOnE: " + new String(b2));
                    if (status == 1) {
                        byte[] b3 = m1.receive(1);
                        System.out.println("(1) Reply StOnE: " + new String(b3));
                    }
                    // Reserva
                    m1.send(2, ("stone;Lisboa-Braga;2022-01-01/2022-01-01").getBytes());
                    byte[] b4 = m1.receive(2);
                    System.out.println("(2) Reply StOnE: " + new String(b4));
                    byte[] b5 = m1.receive(2);
                    String resp = new String(b5);
                    String[] resp1 = resp.split(" ");
                    String codigo = resp1[resp1.length - 1];
                    System.out.println("(2) Reply StOnE: " + resp);
                    // Cancelar Reserva
                    Thread.sleep(2000);
                    m1.send(4, ("stone " + codigo).getBytes());
                    byte[] b6 = m1.receive(4);
                    int status1 = Integer.parseInt(new String(b1));
                    byte[] b7 = m1.receive(4);

                    if (status1 == 1)
                        System.out.println("(4) Reply St0nE V2: " + new String(b6));
                    else
                        System.out.println("(4) Reply St0nE V2: " + new String(b7));
                    m1.close();
                }  catch (Exception ignored) {}
            }),

            new Thread(() -> {
                try  {
                    Socket s2 = new Socket("localhost", 12345);
                    Demultiplexer m1 = new Demultiplexer(new TaggedConnection(s2));
                    m1.start();
                    // Registo
                    m1.send(0, ("stone 123").getBytes());
                    Thread.sleep(100);
                    byte[] data = m1.receive(0);
                    System.out.println("(0) Reply StOnE V2: " + new String(data));
                    byte[] data7 = m1.receive(0);
                    System.out.println("(0) Reply StOnE V2: " + new String(data7));
                    // Log In
                    m1.send(1, ("stone 123").getBytes());
                    byte[] b1 = m1.receive(1);
                    int status = Integer.parseInt(new String(b1));
                    byte[] b2 = m1.receive(1);
                    System.out.println("(1) Reply StOnE V2: " + new String(b2));
                    if (status == 1) {
                        byte[] b3 = m1.receive(1);
                        System.out.println("(1) Reply StOnE V2: " + new String(b3));
                    }
                    //Reserva
                    m1.send(2, ("stone;Lisboa-Braga;2022-01-01/2022-01-01").getBytes());
                    byte[] b4 = m1.receive(2);
                    System.out.println("(2) Reply StOnE V2: " + new String(b4));
                    byte[] b5 = m1.receive(2);
                    String resp = new String(b5);
                    String[] resp1 = resp.split(" ");
                    String codigo = resp1[resp1.length - 1];
                    System.out.println("(2) Reply StOnE V2: " + resp);
                    // Cancelar Reserva
                    Thread.sleep(2000);
                    m1.send(4, ("stone " + codigo).getBytes());
                    byte[] b6 = m1.receive(4);
                    int status1 = Integer.parseInt(new String(b1));
                    byte[] b7 = m1.receive(4);

                    if (status1 == 1)
                        System.out.println("(4) Reply St0nE V2: " + new String(b6));
                    else
                        System.out.println("(4) Reply St0nE V2: " + new String(b7));
                    m1.close();
                }  catch (Exception ignored) {}
            }),

            new Thread(() -> {
                try  {
                    // ADMIN
                    Socket s2 = new Socket("localhost", 12345);
                    Demultiplexer m1 = new Demultiplexer(new TaggedConnection(s2));
                    m1.start();
                    // Log In
                    m1.send(1, ("admin 123").getBytes());
                    Thread.sleep(100);
                    byte[] b1 = m1.receive(1);
                    int status = Integer.parseInt(new String(b1));
                    byte[] b2 = m1.receive(1);
                    System.out.println("(1) Reply admin: " + new String(b2));
                    if (status == 1) {
                        byte[] b3 = m1.receive(1);
                        System.out.println("(1) Reply admin: " + new String(b3));
                    }
                    Thread.sleep(1000);
                    // Encerramento do Dia
                    m1.send(6, ("").getBytes());
                    byte[] b4 = m1.receive(6);
                    int status1 = Integer.parseInt(new String(b1));
                    byte[] b5 = m1.receive(6);
                    if (status1 == 1)
                    System.out.println("(6) Reply admin: " + new String(b4) + " Dia encerrado com sucesso");
                    else
                        System.out.println("(6) Reply admin: " + new String(b5) + " Dia encerrado sem sucesso");
                    m1.close();
                    }  catch (Exception ignored) {}
            })
        };

        for (Thread t: threads) t.start();
        for (Thread t: threads) t.join();
    }
}
