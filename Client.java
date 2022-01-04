import java.io.IOError;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Client {
    private static Scanner sc;
    private static Demultiplexer m;
    private static Menu menu = new Menu();

    public void run() {
        menu.message("\n\nBem vindo ao sistema!!!\n");
        menuInicial();
    }

    // Método: Menu inicial
    private void menuInicial() {
        // Criar menu
        List<String> options = Arrays.asList(
                "Registar", // 1
                "Login", // 2
                "Sair"); // 3
        menu.setOptions(options);

        // Registar handlers
        menu.setHandlers(1, () -> registar());
        menu.setHandlers(2, () -> login());
        menu.setHandlers(3, () -> sair());

        menu.run();
    }

    // Método: Sair do programa
    public void sair() {
        menu.message("\nAté uma próxima...");
        menu.setExit(true);
    }

    // Método: Menu registar
    private void registar() {
        menu.message("\nID: ");
        String id = sc.nextLine();
        menu.message("Password: ");
        String pw = sc.nextLine();

        int especial = efetuarRegisto(id, pw);
        if (especial == 1)
            menuPrincipalE();
        else if (especial == 0)
            menuPrincipal();
        else
            menuInicial();
    }

    // Método: Menu login
    private void login() {
        menu.message("\nID: ");
        String id = sc.nextLine();
        menu.message("Password: ");
        String pw = sc.nextLine();

        int especial = efetuarLogin(id, pw);
        if (especial == 1)
            menuPrincipal();
        else
            menuPrincipalE();
    }

    // Método: Menu principal de um administrador (Especial)
    public void menuPrincipalE() {
        // Criar menu
        List<String> options = Arrays.asList(
                "Adicionar Voo", // 1
                "Encerrar o dia"); // 2
        menu.setOptions(options);

        // Registar pré-condições

        // Registar handlers
        menu.setHandlers(1, () -> createFlight());
        menu.setHandlers(2, () -> endDay());

        menu.run();
    }

    
    static int efetuarRegisto(String username, String password) {
        int especial = 0;
        m.send(0, (username+" "+password+" ").getBytes());
        byte[] b = m.receive(0);
        int status = Integer.parseInt(new String(b));
        byte[] b1 = m.receive(0);
        byte[] b2 = m.receive(0);
        String[] tokens = new String(b2).split(" ");
        especial = Integer.parseInt(new String(tokens[1]));
        if (status == 1){
            System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
            return especial;
        }
        else menu.message(new String(b1) + "\nRegisto não efetuado.");
        return -1;
    }

    static int efetuarLogin(String username, String password) {
        int especial = 0;
        m.send(1, (username+" "+password+" ").getBytes());
        byte[] b = m.receive(0);
        int status = Integer.parseInt(new String(b));
        byte[] b1 = m.receive(0);
        byte[] b2 = m.receive(0);
        String[] tokens = new String(b2).split(" ");
        especial = Integer.parseInt(new String(tokens[1]));
        if (status == 1){
            System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
            return especial;
        }
        else menu.message(new String(b1) + "\nLogin não efetuado.");
        return -1;
    }

    static void searchFlight() {
        System.out.println("Insira o local de partida: ");
        String from = sc.nextLine();
        System.out.println("Inserir o local de chegada: ");
        String to = sc.nextLine();
        System.out.println("Insira a data Inicio: ");
        String date1 = sc.nextLine();
        System.out.println("Insira a data de Fim: ");
        String date2 = sc.nextLine();

        m.send(2, (from+" "+to+" "+date1+" "+date2).getBytes());
        byte[] b = m.receive(2);
        int status = Integer.parseInt(new String(b));
        byte[] b1 = m.receive(2);
        if (status == 1){
            System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
            userMenu();
        }
        else{
            System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
        }
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
                String from = sc.nextLine();
                System.out.println("Inserir o local de chegada: ");
                String to = sc.nextLine();
                System.out.println("Insira o total de lugares no aviao: ");
                String seats = sc.nextLine();
                m.send(4, (from+" "+to+" "+seats).getBytes());
                byte[] b = m.receive(4);
                int status = Integer.parseInt(new String(b));
                byte[] b1 = m.receive(4);
                if (status == 1){
                    System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
                    userMenu();
                }
                else{
                    System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
                    menuPrincipalE();
                }
            }
            catch (IOException e) {
                e.getMessage();
            }
        });
        t.start();
        t.join();
    }

    static void endDay(){
        System.out.println("Insira o ano a encerrar: ");
        int ano = Integer.parseInt(sc.nextLine());
        System.out.println("Insira o mes a encerrar: ");
        int mes = Integer.parseInt(sc.nextLine());
        System.out.println("Insira o dia a encerrar: ");
        int dia = Integer.parseInt(sc.nextLine());
        m.send(5, (ano+" "+mes+" "+dia).getBytes());
        byte[] b = m.receive(5);
        int status = Integer.parseInt(new String(b));
        byte[] b1 = m.receive(5);
        if (status == 1){
            System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
            menuPrincipalE()
        }
        else{
            System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
            menuPrincipalE();
        }
    static void createTrip() {
        Thread t = new Thread(() -> {
            try{
                System.out.println("Insira o seu nome de Utilizador: ");
                Strin user = scin.nextLine();
                System.out.println("Insira todas as escalas separadas por '-' : ");
                String escalas = scin.nextLine();
                System.out.println("Insira um intervalo de datas da separado por '-' : ");
                String datas = scin.nextLine();
                m.send(5,(user+";"+escalas+";"+datas).getBytes());
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

    static void cancelTrip() {
        Thread t = new Thread(() -> {
            try {
                System.out.println("Insira o seu nome de Utilizador: ");
                Strin user = scin.nextLine();
                System.out.println("Insira o código de reserva: ");
                String code = scin.nextLine();
                m.send(6,(user+" "+code).getBytes());
                byte[] b = m.receive(6); 
                if (excecao==0){
                    System.out.println("\033[1;36m"+ new String(b1)+"\033[0m");
                    userMenu();
                }
                else menu.printExcecao(new String(b1) + "\nErro ao cancelar a reserva.");
            }
            catch (IOException e) {
                e.getMessage();
            }
        });
        t.start();
        t.join();
    }
}
