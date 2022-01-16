package src;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import src.business.Model;

/*
 *  Server:  -
 */

public class Server {
    private static Model model;
    private static int j = 0;

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);
        model = new Model();

        while (true) {
            Socket socket = ss.accept();
            j++;

            TaggedConnection connection = new TaggedConnection(socket);

            Runnable worker = () -> {
                try {
                    while (true) {
                        TaggedConnection.Frame frame = connection.receive();
                        String data = new String(frame.data);
                        try {
                            if (frame.tag == 0) { // Registo
                                String id = model.createUser(data);
                                System.out.println("Replying to: " + id);
                                if (id == null) {
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro ao registar!").getBytes());
                                } else {
                                    connection.send(frame.tag, String.valueOf(1).getBytes());
                                    connection.send(frame.tag, "Registo Concluído!!".getBytes());
                                }
                            } else if (frame.tag == 1) { // Login
                                String[] tokens = data.split(" ");
                                if (model.userLogin(tokens[0], tokens[1])) {
                                    model.setLoggedIn(tokens[0]);
                                    int special = model.getSpecial(tokens[0]);
                                    System.out.println("Replying to: " + tokens[0]);
                                    connection.send(frame.tag, String.valueOf(1).getBytes());
                                    connection.send(frame.tag, ("Login realizado com sucesso").getBytes());
                                    connection.send(frame.tag, ("Especial: " + special).getBytes());
                                } else {
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro no login").getBytes());
                                }
                            }
                            else if (frame.tag == 2) { // Fazer uma reserva de voos em escala
                                String[] tokens = data.split(";");
                                String[] dests = tokens[1].split("-");
                                String[] dates = tokens[2].split("/");
                                List<String> destinations = new ArrayList<>();
                                for(String dest : dests) destinations.add(dest); 
                                String code = model.createTrip(tokens[0],destinations, dates[0], dates[1]);
                                if(code != null) {
                                    connection.send(frame.tag, String.valueOf(1).getBytes());
                                    connection.send(frame.tag,("Reserva adicionada com o código: " + code).getBytes());
                                }
                                else
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro ao reservar voo").getBytes());
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
                            else if (frame.tag == 4) { // Cancelar uma reserva
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
                            else if (frame.tag == 5) { // Adicionar Voo
                                String[] tokens = data.split(" ");
                                boolean r = model.createFlight(tokens[0], tokens[1], tokens[2]);
                                if(r){
                                    connection.send(frame.tag, String.valueOf(1).getBytes());
                                    connection.send(frame.tag, ("Novo voo adicionado!").getBytes());
                                } else {
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro ao criar voo!").getBytes());
                                }
                            }
                            else if (frame.tag == 6) { // Encerrar dia
                                if(model.endingDay()){
                                    connection.send(frame.tag, String.valueOf(1).getBytes());
                                    connection.send(frame.tag, ("Dia encerrado com sucesso!").getBytes());
                                }
                                else{
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro ao encerrar o dia!").getBytes());
                                }
                            }
                            else if (frame.tag == 7) {
                                if(model.logout(data)) {
                                    connection.send(frame.tag, String.valueOf(1).getBytes());
                                    connection.send(frame.tag, ("Logout com sucesso").getBytes());
                                }
                                else{
                                    connection.send(frame.tag, String.valueOf(-1).getBytes());
                                    connection.send(frame.tag, ("Erro a fazer Logout").getBytes());
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
