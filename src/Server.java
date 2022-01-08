package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.*;

import src.business.Model;

/*
 *  Server:  -
 */

public class Server {
    private static Model model;

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);
        model = new Model();

        while (true) {
            Socket socket = ss.accept();
            TaggedConnection connection = new TaggedConnection(socket);

            Runnable worker = () -> {
                try {
                    while (true) {
                        TaggedConnection.Frame frame = connection.receive();
                        String data = new String(frame.data);
                        try {
                            if (frame.tag == 0) { // Registo
                                String id = model.createUser(data);
                                int special = model.getUser(id).getSpecial(); // Possivelmente nao deveria ser assim,
                                                                              // mas para ja fica
                                System.out.println("Replying to: " + id);
                                if (id == null) {
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro ao registar!").getBytes());
                                } else {
                                    connection.send(frame.tag, String.valueOf(1).getBytes());
                                    connection.send(frame.tag, "Registo Concluído!!".getBytes());
                                    connection.send(frame.tag, ("Especial: " + special).getBytes());
                                }
                            } else if (frame.tag == 1) { // Login
                                String[] tokens = data.split(" ");
                                if (model.userLogin(tokens[0], tokens[1])) {
                                    int special = model.getUser(tokens[0]).getSpecial(); // Possivelmente nao deveria ser assim, mas para ja fica
                                    System.out.println("Replying to: " + data);
                                    connection.send(frame.tag, String.valueOf(1).getBytes());
                                    connection.send(frame.tag, ("Login Concluído!!").getBytes());
                                    connection.send(frame.tag, ("Especial: " + special).getBytes());
                                } else {
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro ao fazer login!").getBytes());
                                }
                            } else if (frame.tag == 2) { // Pedir Voo (Nao esta completo, o utilizador tem de dar todas as escalas)
                                String[] tokens = data.split(" ");
                                boolean r = model.searchFlight(tokens[0], tokens[1], tokens[2], tokens[3]);
                                if (r) {
                                    connection.send(frame.tag, String.valueOf(1).getBytes());
                                    connection.send(frame.tag, ("Voo Encontrado").getBytes());
                                } else {
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro ao encontrar voo").getBytes());
                                }
                            } else if (frame.tag == 3) { // Pedir Lista Voos
                                String allflights = model.allFlightsToString();
                                if (allflights != null) {
                                    connection.send(frame.tag, String.valueOf(1).getBytes());
                                    connection.send(frame.tag, allflights.getBytes());
                                } else {
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro ao pedir lista de voos!").getBytes());
                                }
                            }
                            else if (frame.tag == 4) { // Fazer uma reserva de voos em escala
                                String[] tokens = data.split(";"); // Username;Porto-London-Tokyo;Data1-Data2
                                String[] dests = tokens[1].split("-");
                                String[] dates = tokens[2].split("-");
                                List<String> destinations = new ArrayList<>();
                                for(String dest : dests) destinations.add(dest);
                                String code = model.createTrip(tokens[0],destinations, dates[1], dates[2]);
                                if(code != null) {
                                    connection.send(frame.tag, String.valueOf(1).getBytes());
                                    connection.send(frame.tag,("Reserva adicionada com o código: " + code).getBytes());
                                }
                                else{
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro ao fazer uma reserva!").getBytes());
                                }
                            }
                            else if (frame.tag == 5) { // Cancelar uma reserva
                                String tokens[] = data.split(" ");
                                boolean r = model.cancelTrip(tokens[0], tokens[1]);
                                if(r){
                                    connection.send(frame.tag, String.valueOf(1).getBytes());
                                    connection.send(frame.tag, ("Reserva com o código: " + tokens[1] + " cancelada.").getBytes());
                                }
                                else{
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro ao cancelar reserva").getBytes());
                                }
                            }
                            else if (frame.tag == 6) { // Adicionar Voo
                                String[] tokens = data.split(" ");
                                boolean r = model.createFlight(tokens[0], tokens[1], tokens[2], tokens[3]);
                                if (r) {
                                    connection.send(frame.tag, String.valueOf(1).getBytes());
                                    connection.send(frame.tag, ("Novo voo adicionado!").getBytes());
                                } else {
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro ao criar voo!").getBytes());
                                }
                            }
                            else if (frame.tag == 7) { // Encerrar dia
                                String[] tokens = data.split(" ");
                                LocalDate date = LocalDate.of(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]),
                                        Integer.parseInt(tokens[2]));
                                if (data != null && date.isAfter(LocalDate.now())) {
                                    if (model.addNewBlockedDate(date)) {
                                        connection.send(frame.tag, String.valueOf(1).getBytes());
                                        connection.send(frame.tag,
                                                ("Data de encerramento adicionada com sucesso!").getBytes());
                                    }
                                } else {
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro ao adicionar data de encerramento!").getBytes());
                                }
                            }
                        }
                        catch (IOException e){
                            e.getMessage();
                        }
                    }
                } catch (IOException e) {
                    e.getMessage();
                }
            };
            new Thread(worker).start();
        }
    }
}
