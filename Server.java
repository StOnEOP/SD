import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class Server {
    private static Model model;
    private static Map<String,TaggedConnection> tg = new HashMap<>();

    public static void main(String[] args) throws Exception {
        ServerSocket ss = new ServerSocket(12345);
        model = new Model();

        while(true) {
            Socket socket = ss.accept();
            TaggedConnection connection = new TaggedConnection(socket);

            Runnable worker = () -> {
                try {
                    while (true) {
                            int flag = 0;
                            TaggedConnection.Frame frame = connection.receive();
                            String data = new String(frame.data);
                        try {
                            if (frame.tag == 0) { // Registo
                                String id = model.createUser(data);
                                System.out.println();
                                System.out.println("Replying to: " + id);
                                tg.put(id, connection);
                                connection.send(frame.tag, String.valueOf(flag).getBytes());
                                connection.send(frame.tag, "Registo Concluído!!".getBytes());
                            }
                            else if (frame.tag == 1) { // Login
                                String[] tokens = data.split(" ");
                                if (model.userLogin(tokens[0], tokens[1])) {
                                    System.out.println("Replying to: " + data);
                                    tg.put(tokens[0], connection);
                                    connection.send(frame.tag, String.valueOf(flag).getBytes());
                                    connection.send(frame.tag, ("Login Concluído!!").getBytes());
                                }
                            }
                            else if (frame.tag == 2) { // Pedir Voo
                                String[] tokens = data.split(" ");
                                boolean r = model.searchFlight(tokens[0], tokens[1]);
                                if(r){
                                    connection.send(frame.tag, String.valueOf(flag).getBytes());
                                    connection.send(frame.tag, ("Voo Encontrado").getBytes());
                                }
                            }
                            else if (frame.tag == 3) { // Pedir Lista Voos
                                String allflights = model.allFlightsToString();
                                if(allflights != null){
                                    connection.send(frame.tag, String.valueOf(flag).getBytes());
                                    connection.send(frame.tag, allflights.getBytes());
                                }
                            }
                            else if (frame.tag == 4) { // Adicionar Voo
                                String[] tokens = data.split(" ");
                                boolean r = model.createFlight(tokens[0], tokens[1], tokens[2], tokens[3]);
                                if(r){
                                    connection.send(frame.tag, String.valueOf(flag).getBytes());
                                    connection.send(frame.tag, ("Novo voo adicionado!").getBytes());
                                }
                            }
                            else if(frame.tag == 5) { //Fazer uma reserva de voos em escala
                                String[] tokens = data.split(";"); //Porto-London-Tokyo;Data1-Data2
                                String[] dests = tokens[0].split("-");
                                String[] dates = tokens[1].split("-");
                                List<String> destinations = new ArrayList<>();
                                for(String dest : dests) destinations.add(dest);
                                String code = model.createTrip(destinations, dates[1], dates[2]);
                                if(code != null) {
                                    connection.send(frame.tag,String.valueOf(flag).getBytes());
                                    connection.send(frame.tag,("Reserva adicionada com o código: " + code).getBytes());
                                }
                            }
                        }
                        catch (IOException e){
                            flag = 1;
                            e.getMessage();
                        }
                    }
                }
                catch (IOException e){
                    e.getMessage();
                }
            };
            new Thread(worker).start();
        }
    }
}
