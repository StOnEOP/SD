package src;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import src.ui.Menu;

/*
 *  Client:  - Classe que possui todos os menus que serão utilizados para mostrar o programa aos Clientes
 */

public class Client {
    private static Demultiplexer demultiplexer;
    private static Menu menu = new Menu();
    private static Scanner sc = new Scanner(System.in);

    private static String idU; // Variavel do username do Cliente

    private static void run() {
        try {
            menu.message("\n\nBem vindo ao sistema!!!\n");
            homeMenu();
            demultiplexer.close();
        } catch (IOException e) {
        }
    }

    // Método: Menu inicial
    private static void homeMenu() {
        // Criar menu
        List<String> options = Arrays.asList(
                "Registar", // 1
                "Login", // 2
                "Sair"); // 3
        menu.setOptions(options);

        // Registar handlers
        menu.setHandlers(1, () -> signupMenu());
        menu.setHandlers(2, () -> loginMenu());
        menu.setHandlers(3, () -> exit());

        menu.run();
    }

    // Método: Menu registar
    private static void signupMenu() {
        menu.message("\nID: ");
        String id = sc.nextLine();
        menu.message("Password: ");
        String pw = sc.nextLine();

        signup(id, pw);
        homeMenu();
    }

    // Método: Efetua o registo do utilizador
    private static void signup(String username, String password) {
        try {
            demultiplexer.send(0, (username + " " + password + " ").getBytes());

            byte[] b1 = demultiplexer.receive(0);
            int status = Integer.parseInt(new String(b1));
            byte[] b2 = demultiplexer.receive(0);

            if (status == 1)
                menu.message("\n" + new String(b2) + "\n");
            else
                menu.message("\n" + new String(b2) + "\nRegisto não efetuado.\n");
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
    }

    // Método: Menu login
    private static void loginMenu() {
        menu.message("\nID: ");
        String id = sc.nextLine();
        menu.message("Password: ");
        String pw = sc.nextLine();

        int isAdmin = login(id, pw);
        if (isAdmin == 1)
            homeAdminMenu();
        else if (isAdmin == 0)
            homeClientMenu();
        else
            homeMenu();
    }

    // Método: Efetua a autenticação do utilizador
    private static int login(String username, String password) {
        try {
            demultiplexer.send(1, (username + " " + password + " ").getBytes());

            byte[] b1 = demultiplexer.receive(1);
            int status = Integer.parseInt(new String(b1));
            byte[] b2 = demultiplexer.receive(1);

            if (status == 1) {
                idU = username;
                byte[] b3 = demultiplexer.receive(1);
                String[] tokens = new String(b3).split(" ");

                menu.message("\n" + new String(b2) + "\n");
                return Integer.parseInt(new String(tokens[1]));
            } else
                menu.message("\n" + new String(b2) + "\n");
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
        return -1;
    }

    // Método: Termina a sessão do utilizador
    private static void logout() {
        try {
            demultiplexer.send(7, (idU).getBytes());

            byte[] b1 = demultiplexer.receive(7);
            int status = Integer.parseInt(new String(b1));
            byte[] b2 = demultiplexer.receive(7);

            if (status == 1) {
                menu.message("\n" + new String(b2) + "\n");
            } else
                menu.message("\n" + new String(b2) + "\n");
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
        homeMenu();
    }

    // Método: Menu principal do administrador
    private static void homeAdminMenu() {
        // Criar menu
        List<String> options = Arrays.asList(
                "Adicionar Voo", // 1
                "Encerrar o dia", // 2
                "Terminar sessão"); // 3
        menu.setOptions(options);

        // Registar handlers
        menu.setHandlers(1, () -> createFlight());
        menu.setHandlers(2, () -> endDay());
        menu.setHandlers(3, () -> logout());

        menu.run();
    }

    // Método: Criar um novo voo
    private static void createFlight() {
        try {
            menu.message("\nInsira o local de partida: ");
            String from = sc.nextLine();
            menu.message("Inserir o local de chegada: ");
            String to = sc.nextLine();
            menu.message("Insira o total de lugares no avião: ");
            String seats = sc.nextLine();

            demultiplexer.send(5, (from + ";" + to + ";" + seats).getBytes());

            byte[] b1 = demultiplexer.receive(5);
            int status = Integer.parseInt(new String(b1));
            byte[] b2 = demultiplexer.receive(5);

            if (status == 1)
                menu.message("\n" + new String(b2) + "\n");
            else
                menu.message("\n" + new String(b2) + "\n");
            homeAdminMenu();
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
    }

    // Método: Acabar o dia atual
    private static void endDay() {
        try {
            demultiplexer.send(6, ("").getBytes());

            byte[] b1 = demultiplexer.receive(6);
            int status = Integer.parseInt(new String(b1));
            byte[] b2 = demultiplexer.receive(6);

            if (status == 1)
                menu.message("\n" + new String(b2) + "\n");
            else
                menu.message("\n" + new String(b2) + "\n");
            homeAdminMenu();
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
    }

    // Método: Menu principal do cliente
    private static void homeClientMenu() {
        // Criar menu
        List<String> options = Arrays.asList(
                "Reservar uma viagem", // 1
                "Cancelar reserva", // 2
                "Lista de voos", // 3
                "Terminar sessão"); // 4
        menu.setOptions(options);

        // Registar handlers
        menu.setHandlers(1, () -> createTrip());
        menu.setHandlers(2, () -> cancelTrip());
        menu.setHandlers(3, () -> flightList());
        menu.setHandlers(4, () -> logout());

        menu.run();
    }

    // Método: Criar uma viagem nova pelo cliente
    private static void createTrip() {
        menu.message("Insira todas as escalas separadas por '-': ");
        String escalas = sc.nextLine();
        menu.message("Insira um intervalo de datas da separado por '/' (YYYY-MM-DD): ");
        String datas = sc.nextLine();

        if ((!escalas.contains("-")) && (!datas.contains("/"))) {
            menu.message("\nParâmetros Errados\n");
            homeClientMenu();
        }

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(3000);
                demultiplexer.send(2, (idU + ";" + escalas + ";" + datas).getBytes());
                byte[] b1 = demultiplexer.receive(2);
                int status = Integer.parseInt(new String(b1));
                byte[] b2 = demultiplexer.receive(2);

                if (status == 1)
                    menu.message("\n" + new String(b2) + "\n");
                else
                    menu.message("\n" + new String(b2) + "\n");
            } catch (IOException | InterruptedException e) {
                e.getMessage();
            }
        });
        t.start();
        homeClientMenu();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.getMessage();
        }
    }

    // Método: Cancelar uma viagem já reservada pelo cliente
    private static void cancelTrip() {
        try {
            menu.message("Insira o código de reserva: ");
            String code = sc.nextLine();

            demultiplexer.send(4, (idU + " " + code).getBytes());

            byte[] b1 = demultiplexer.receive(4);
            int status = Integer.parseInt(new String(b1));
            byte[] b2 = demultiplexer.receive(4);

            if (status == 1)
                menu.message("\n" + new String(b2) + "\n");
            else
                menu.message("\n" + new String(b2) + "\n");
            homeClientMenu();
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
    }

    // Método: Obter a lista de todos os voos atuais
    private static void flightList() {
        try {
            menu.message("\n***** Lista de voos *****");
            demultiplexer.send(3, (" ").getBytes());
            byte[] b1 = demultiplexer.receive(3);
            int status = Integer.parseInt(new String(b1));
            byte[] b2 = demultiplexer.receive(3);
            if (status == 1)
                menu.message("\n" + new String(b2) + "\n");
            else
                menu.message("\n" + new String(b2) + "\n");
            homeClientMenu();
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
    }

    // Método: Sair do programa
    private static void exit() {
        menu.message("\nAté uma próxima...\n");
        menu.setExit(true);
    }

    public static void main(String[] args) throws Exception {
        sc = new Scanner(System.in);
        Socket s = new Socket("localhost", 12345);
        demultiplexer = new Demultiplexer(new TaggedConnection(s));
        demultiplexer.start();
        run();
    }
}
