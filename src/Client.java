package src;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import src.ui.Menu;

/*
 *  Client:  -
 */

public class Client {
    private static Desmultiplexer desmultiplexer;
    private static Menu menu = new Menu();
    private static Scanner sc = new Scanner(System.in);

    private static void run() {
        menu.message("\n\nBem vindo ao sistema!!!\n");
        homeMenu();
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
            desmultiplexer.send(0, (username + " " + password + " ").getBytes());

            byte[] b1 = desmultiplexer.receive(0);
            int status = Integer.parseInt(new String(b1));
            byte[] b2 = desmultiplexer.receive(0);

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
            desmultiplexer.send(1, (username + " " + password + " ").getBytes());

            byte[] b1 = desmultiplexer.receive(1);
            int status = Integer.parseInt(new String(b1));
            byte[] b2 = desmultiplexer.receive(1);

            if (status == 1) {
                byte[] b3 = desmultiplexer.receive(1);
                String[] tokens = new String(b3).split(" ");

                menu.message("\n" + new String(b2) + "\n");
                return Integer.parseInt(new String(tokens[1]));
            } else
                menu.message("\n" + new String(b2) + "\nErro ao efetuar o login.\n");
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
        return -1;
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
        menu.setHandlers(3, () -> homeMenu());

        menu.run();
    }

    // Método: Criar um novo voo
    private static void createFlight() {
        Thread t = new Thread(() -> {
            try {
                menu.message("\nInsira o local de partida: ");
                String from = sc.nextLine();
                menu.message("Inserir o local de chegada: ");
                String to = sc.nextLine();
                menu.message("Insira o total de lugares no avião: ");
                String seats = sc.nextLine();

                desmultiplexer.send(5, (from + " " + to + " " + seats).getBytes());

                byte[] b1 = desmultiplexer.receive(5);
                int status = Integer.parseInt(new String(b1));
                byte[] b2 = desmultiplexer.receive(5);

                if (status == 1)
                    menu.message("\n" + new String(b2) + "\n");
                else
                    menu.message("\n" + new String(b2) + "\n");
                homeAdminMenu();
            } catch (IOException | InterruptedException e) {
                e.getMessage();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.getMessage();
        }
    }

    // Método: Acabar o dia atual
    private static void endDay() {
        menu.message("\nInsira o ano: ");
        int ano = Integer.parseInt(sc.nextLine());
        menu.message("Insira o mês: ");
        int mes = Integer.parseInt(sc.nextLine());
        menu.message("Insira o dia: ");
        int dia = Integer.parseInt(sc.nextLine());

        try {
            desmultiplexer.send(6, (ano + " " + mes + " " + dia).getBytes());

            byte[] b1 = desmultiplexer.receive(6);
            int status = Integer.parseInt(new String(b1));
            byte[] b2 = desmultiplexer.receive(6);

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
        menu.setHandlers(4, () -> homeMenu());

        menu.run();
    }

    // Método: Criar uma viagem nova pelo cliente
    private static void createTrip() {
        Thread t = new Thread(() -> {
            try {
                menu.message("\nInsira o seu nome de Utilizador: ");
                String user = sc.nextLine();
                menu.message("Insira todas as escalas separadas por '-': ");
                String escalas = sc.nextLine();
                menu.message("Insira um intervalo de datas da separado por '/' (YYYY-MM-DD): ");
                String datas = sc.nextLine();

                desmultiplexer.send(2, (user + ";" + escalas + ";" + datas).getBytes());

                byte[] b1 = desmultiplexer.receive(2);
                int status = Integer.parseInt(new String(b1));
                byte[] b2 = desmultiplexer.receive(2);

                if (status == 1)
                    menu.message("\n" + new String(b2) + "\n");
                else
                    menu.message("\n" + new String(b2) + "\n");
                homeClientMenu();
            } catch (IOException | InterruptedException e) {
                e.getMessage();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.getMessage();
        }
    }

    // Método: Cancelar uma viagem já reservada pelo cliente
    private static void cancelTrip() {
        Thread t = new Thread(() -> {
            try {
                menu.message("\nInsira o seu nome de Utilizador: ");
                String user = sc.nextLine();
                menu.message("Insira o código de reserva: ");
                String code = sc.nextLine();

                desmultiplexer.send(4, (user + " " + code).getBytes());

                byte[] b1 = desmultiplexer.receive(4);
                int status = Integer.parseInt(new String(b1));
                byte[] b2 = desmultiplexer.receive(4);

                if (status == 1)
                    menu.message("\n" + new String(b2) + "\n");
                else
                    menu.message("\n" + new String(b2) + "\n");
                homeClientMenu();
            } catch (IOException | InterruptedException e) {
                e.getMessage();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.getMessage();
        }
    }

    // Método: Obter a lista de todos os voos atuais
    private static void flightList() {
        Thread t = new Thread(() -> {
            try {
                menu.message("\nVoos disponíveis: ");

                desmultiplexer.send(3, (" ").getBytes());

                byte[] b1 = desmultiplexer.receive(3);
                int status = Integer.parseInt(new String(b1));
                byte[] b2 = desmultiplexer.receive(3);

                if (status == 1)
                    menu.message("\n" + new String(b2) + "\n");
                else
                    menu.message("\n" + new String(b2) + "\n");
                homeClientMenu();
            } catch (IOException | InterruptedException e) {
                e.getMessage();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.getMessage();
        }
    }

    // Método: Sair do programa
    private static void exit() {
        menu.message("\nAté uma próxima...");
        menu.setExit(true);
    }

    public static void main(String[] args) throws Exception {
        sc = new Scanner(System.in);
        Socket s = new Socket("localhost", 12345);
        desmultiplexer = new Desmultiplexer(new TaggedConnection(s));
        desmultiplexer.start();
        run();
    }
}
