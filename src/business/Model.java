package src.business;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/*
 *  Model:  - Classe que guarda todas as variáveis necessárias para a execução de todo o programa
 *          - Possui todos os métodos necessários para a execução correta das funcionalidades
 */

public class Model {
    LocalDate currentDay = LocalDate.of(2022, 01, 01); // Dia atual
    private Map<String, User> allUsers; // Mapa que guarda todos os utilizadores. Key: ID , Value: Utilizador
    private List<Flight> allFlights; // Lista que guarda todas os voos presentes no sistema
    private Map<LocalDate, List<Flight>> allDatedFlights; // Mapa que guarda todos os voos por dia. Key: Dia , Value: Voos
    private Map<String, List<Flight>> allTrips; // Mapa que guarda todas as reservas de voos. Key: Código da reserva , Value: Voo
    private ReentrantLock lock = new ReentrantLock();

    public Model() {
        this.allUsers = new HashMap<>();
        this.allFlights = new ArrayList<>();
        this.allDatedFlights = new HashMap<>();
        this.allTrips = new HashMap<>();

        this.allUsers.put("admin", new User("admin", "123", 1));

        this.allFlights.add(new Flight("Porto", "Lisboa"));
        this.allFlights.add(new Flight("Porto", "Barcelona"));
        this.allFlights.add(new Flight("Lisboa", "Nova Iorque"));
        this.allFlights.add(new Flight("Nova Iorque", "Toronto"));
        this.allFlights.add(new Flight("Barcelona", "Amesterdão"));
        this.allFlights.add(new Flight("Lisboa", "Paris"));
        this.allFlights.add(new Flight("Lisboa", "Braga", 0, 1));

        this.allDatedFlights.put(LocalDate.of(2022, 01, 5), cloneList(this.allFlights));
        this.allDatedFlights.put(LocalDate.of(2022, 01, 6), cloneList(this.allFlights));
        this.allDatedFlights.put(LocalDate.of(2022, 01, 7), cloneList(this.allFlights));
        this.allDatedFlights.put(LocalDate.of(2022, 01, 10), cloneList(this.allFlights));
        this.allDatedFlights.put(LocalDate.of(2022, 02, 5), cloneList(this.allFlights));
        this.allDatedFlights.put(LocalDate.of(2022, 01, 14), cloneList(this.allFlights));
    }

    // Método: Getter do currentDay
    public LocalDate getCurrentDay() {
        return this.currentDay;
    }

    // Método: Autentica o Cliente
    public boolean userLogin(String name, String password) {
        lock.lock();
        try {
            if (allUsers.containsKey(name)) {
                return !allUsers.get(name).getLoggedIn() && allUsers.get(name).checkPassword(password);
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    // Método: Muda o estado do Cliente para LoggedIn
    public void setLoggedIn(String name) {
        lock.lock();
        try {
            this.allUsers.get(name).setIsLoggedIn(true);
        } finally {
            lock.unlock();
        }
    }

    // Método: Getter do estatuto do Cliente
    public int getSpecial(String name) {
        lock.lock();
        try {
            return this.allUsers.get(name).getSpecial();
        } finally {
            lock.unlock();
        }
    }

    // Método: Parse da informação de um Cliente
    public User parseLine(String data) {
        int special = 0;
        String[] tokens = data.split(" ");
        if (tokens[0].substring(0, 3).equals("adm")) {
            special = 1;
        }
        return new User(tokens[0], tokens[1], special);
    }

    // Método: Registo de um Cliente
    public String createUser(String data) {
        User u = parseLine(data);
        lock.lock();
        try {
            boolean exists = allUsers.containsKey(u.getName());
            if (!exists) {
                allUsers.put(u.getName(), u);
                return u.getName();
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    // Método: Getter de um User
    public User getUser(String nome) {
        lock.lock();
        try {
            return allUsers.get(nome);
        } finally {
            lock.unlock();
        }

    }

    // Método: Verifica se existe um voo com a partida e destino indicados no intervalo de tempo dado
    public LocalDate searchAvailableFlightBetweenDates(String from, String to, LocalDate start, LocalDate end) {
        for (LocalDate dstart = start; dstart.isBefore(end) || dstart.isEqual(end); dstart = dstart.plusDays(1)) {
            List<Flight> flights = this.allDatedFlights.get(dstart);
            for (int i = 0; i < flights.size(); i++) {
                if (flights.get(i).getFrom().equals(from) && flights.get(i).getTo().equals(to)
                        && !flights.get(i).isFull())
                    return dstart;
            }
        }
        return null;
    }

    // Método: Retorna o índice onde se encontra um determinado voo
    public int getFlightIndex(String from, String to, LocalDate data) {
        List<Flight> flights = this.allDatedFlights.get(data);
        for (int i = 0; i < flights.size(); i++) {
            if (flights.get(i).getFrom().equals(from) && flights.get(i).getTo().equals(to)) {
                return i;
            }
        }
        return -1;
    }

    // Método: Constrói a lista de todos os voos presentes no sistema
    public String allFlightsToString() {
        try {
            lock.lock();
            StringBuilder sb = new StringBuilder();
            sb.append("** From\t\t** To\n");
            for (int i = 0; i < this.allFlights.size(); i++) {
                sb.append(allFlights.get(i).toString() + "\n");
            }
            return sb.toString();
        } finally {
            lock.unlock();
        }
    }

    // Método: Verifica se existe o voo com a partida e destino dadas na lista de voos
    public boolean containsFlight(String from, String to) {
        for (Flight f : this.allFlights)
            if (f.getFrom().equals(from) && f.getTo().equals(to))
                return true;
        return false;
    }

    // Método: Administrador adiciona uma nova rota
    public boolean createFlight(String from, String to, String seats) {
        try {
            int seats_i = Integer.parseInt(seats);
            Flight f = new Flight(from, to, 0, seats_i);
            try {
                lock.lock();
                if (!containsFlight(from, to)) {
                    this.allFlights.add(f);
                    for (Map.Entry<LocalDate, List<Flight>> entry : this.allDatedFlights.entrySet()) {
                        entry.getValue().add(f);
                    }
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Método: Reserva uma viagem
    public String createTrip(String username, List<String> destinations, String start, String end) {
        String code = null;
        try {
            LocalDate dstart = LocalDate.parse(start);
            LocalDate dend = LocalDate.parse(end);
            try {
                lock.lock();
                if (dend.isBefore(this.currentDay))
                    return code; // Caso tente fazer reserva antes do dia atual ERRO
                if (dstart.isBefore(this.currentDay))
                    dstart = this.currentDay;
                List<Flight> res = new ArrayList<>();
                List<LocalDate> days = isTripPossible(destinations, dstart, dend);
                if (days.size() == destinations.size() - 1) {
                    Random rnd = new Random();
                    int number;
                    for (int i = 0; i < destinations.size() - 1; i++) {
                        String from = destinations.get(i);
                        String to = destinations.get(i + 1);
                        int index = getFlightIndex(from, to, days.get(i));
                        Flight f = allDatedFlights.get(days.get(i)).get(index);
                        res.add(f);
                        f.addSeat();
                    }
                    do {
                        number = rnd.nextInt(999999);
                        code = Integer.toString(number);
                    } while (this.allTrips.containsKey(code));
                    allTrips.put(code, res);
                    allUsers.get(username).addReservation(code);
                }
                return code;
            } finally {
                lock.unlock();
            }
        } catch (DateTimeParseException e) {
            return code;
        }
    }

    // Método: Adiciona as rotas existentes numa nova data caso esta não exista
    public void addIfAbsentFlight(LocalDate start, LocalDate end) {
        for (LocalDate dstart = start; dstart.isBefore(end) || dstart.isEqual(end); dstart = dstart.plusDays(1)) {
            List<Flight> f = new ArrayList<>();
            for (int i = 0; i < this.allFlights.size(); i++)
                f.add(allFlights.get(i).clone());
            allDatedFlights.putIfAbsent(dstart, f);
        }
    }

    // Método: Verifica se existe alguma viagem dentro do intervalo de tempo dado
    public List<LocalDate> isTripPossible(List<String> destinations, LocalDate start, LocalDate end) {
        LocalDate day = start;
        List<LocalDate> days = new ArrayList<>();
        for (int i = 0; i < destinations.size() - 1; i++) {
            String from = destinations.get(i);
            String to = destinations.get(i + 1);
            addIfAbsentFlight(start, end);
            day = searchAvailableFlightBetweenDates(from, to, day, end);
            if (day == null)
                break;
            days.add(day);
        }
        return days;
    }

    // Método: Cancelamento de uma reserva
    public boolean cancelTrip(String username, String code) {
        Boolean ispossible = false;
        LocalDate data = null;
        try {
            lock.lock();
            List<Flight> lf = this.allTrips.get(code);
            for (Map.Entry<LocalDate, List<Flight>> entry : this.allDatedFlights.entrySet()) {
                if ((entry.getKey().isAfter(this.currentDay) || entry.getKey().isEqual(this.currentDay)) && lf != null)
                    for (int i = 0; i < lf.size() && !ispossible; i++)
                        if (entry.getValue().contains(lf.get(i))) {
                            if (data == null || entry.getKey().isBefore(data)) {
                                data = entry.getKey();
                                ispossible = true;
                            }
                        }
                if (ispossible)
                    break;
            }

            if (ispossible && allUsers.containsKey(username)) {
                for (Flight f : lf) {
                    f.removeSeat();
                }
                allUsers.get(username).removeReservation(code);
                allTrips.remove(code);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }

    }

    // Método: Encerramento do dia
    public boolean endingDay() {
        try {
            lock.lock();
            this.currentDay = this.currentDay.plusDays(1);
            return true;
        } finally {
            lock.unlock();
        }
    }

    // Método: Faz logout do Cliente
    public boolean logout(String name) {
        lock.lock();
        try {
            this.allUsers.get(name).setIsLoggedIn(false);
            return true;
        } finally {
            lock.unlock();
        }
    }

    // Método: Clone da Lista de voos
    public List<Flight> cloneList(List<Flight> flight) {
        try {
            lock.lock();
            List<Flight> f = new ArrayList<>();
            for (int i = 0; i < flight.size(); i++)
                f.add(flight.get(i).clone());
            return f;
        } finally {
            lock.unlock();
        }
    }
}