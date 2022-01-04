import java.io.IOError;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Scanner scin;
    private static Demultiplexer m;

    
    static void efetuarRegisto() throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                System.out.println("Insira um nome de Utilizador: ");
                String user = scin.nextLine();
                System.out.println("Inserir password: ");
                String pass = scin.nextLine();

                m.send(0, (user+" "+pass+" ").getBytes());
                byte[] b = m.receive(0);
                int excecao = Integer.parseInt(new String(b));
                byte[] b1 = m.receive(0);
                if (excecao==0){
                    System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
                    userID = user;
                    userMenu();
                }
                else menu.printExcecao(new String(b1) + "\nRegisto não efetuado.");

            } catch (NullPointerException | IOException | InterruptedException e) {
                menu.printExcecao(e.getMessage());
            }
            catch (InputMismatchException valIncorretos) {
                menu.printExcecao("Valor não é um número inteiro!");
            }
        });
        t.start();
        t.join();
    }

    static void searchFlight() {
        Thread t = new Thread(()  -> {
            try {
                System.out.println("Insira o local de partida: ");
                String from = scin.nextLine();
                System.out.println("Inserir o local de chegada: ");
                String to = scin.nextLine();

                m.send(2, (from+" "+to+" ").getBytes());
                byte[] b = m.receive(2);
                int excecao = Integer.parseInt(new String(b));
                byte[] b1 = m.receive(2);
                if (excecao==0){
                    System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
                    userMenu();
                }
                else menu.printExcecao(new String(b1) + "\nErro ao procurar voo.");
            }
            catch (IOException e) {
                e.getMessage();
            }
        });
        t.start();
        t.join();
    }

    static void flightList() {
        Thread t = new Thread(() -> {
            try {
                System.out.println("Voos disponíveis: ");
                m.send(3, (" ").getBytes());
                byte[] b = m.receive(3);
                int excecao = Integer.parseInt(new String(b));
                byte[] b1 = m.receive(3);
                if (excecao==0){
                    System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
                    userMenu();
                }
                else menu.printExcecao(new String(b1) + "\nErro ao procurar a lista de voos.");
            }
            catch (IOException e) {
                e.getMessage();
            }
        });
        t.start();
        t.join();
    }

    static boolean createFlight() {
        Thread t = new Thread(() -> {
            try{
                System.out.println("Insira o local de partida: ");
                String from = scin.nextLine();
                System.out.println("Inserir o local de chegada: ");
                String to = scin.nextLine();
                System.out.println("Insira o total de lugares no aviao: ");
                String seats = scin.nextLine();
                m.send(4, (from+" "+to+" "+seats).getBytes());
                byte[] b = m.receive(4);
                int excecao = Integer.parseInt(new String(b));
                byte[] b1 = m.receive(4);
                if (excecao==0){
                    System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
                    userMenu();
                }
                else menu.printExcecao(new String(b1) + "\nErro ao criar um voo.");
            }
            catch (IOException e) {
                e.getMessage();
            }
        });
        t.start();
        t.join();
    }

    static void createTrip() {
        Thread t = new Thread(() -> {
            try{
                System.out.println("Insira todas as escalas separadas por '-' : ");
                String escalas = scin.nextLine();
                System.out.println("Insira um intervalo de datas da separado por '-' : ");
                String datas = scin.nextLine();
                m.send(5,(escalas+";"+datas).getBytes());
                byte[] b = m.receive(5);
                if (excecao==0){
                    System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
                    userMenu();
                }
                else menu.printExcecao(new String(b1) + "\nErro ao criar uma viagem com escalas.");
            }
            catch (IOException e) {
                e.getMessage();
            }
        });
        t.start();
        t.join();
    }
}
