package src.business;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/*
 *  Model:  -
 */

public class Model {
    LocalDate currentDay = LocalDate.now();
    private Map<String, User> allUsers;
    private List<Flight> allFlights;
    private Map<LocalDate, List<Flight>> allDatedFlights;
    private Map<String, List<Flight>> allTrips; // String: código de reserva, List: voos de uma viagem
    private ReentrantLock lock = new ReentrantLock();

    public Model() {
        this.allUsers = new HashMap<>();
        this.allFlights = new ArrayList<>();
        this.allDatedFlights = new HashMap<>();
        this.allTrips = new HashMap<>();

        this.allFlights.add(new Flight("Porto", "Lisboa"));
        this.allFlights.add(new Flight("Porto", "Barcelona"));
        this.allFlights.add(new Flight("Lisboa", "Nova Iorque"));
        this.allFlights.add(new Flight("Nova Iorque", "Toronto"));
        this.allFlights.add(new Flight("Barcelona", "Amesterdão"));
        this.allFlights.add(new Flight("Lisboa", "Paris"));

        this.allDatedFlights.put(LocalDate.of(2022, 01, 5), cloneList(this.allFlights));
        this.allDatedFlights.put(LocalDate.of(2022, 01, 6), cloneList(this.allFlights));
        this.allDatedFlights.put(LocalDate.of(2022, 01, 7), cloneList(this.allFlights));
        this.allDatedFlights.put(LocalDate.of(2022, 01, 10), cloneList(this.allFlights));
        this.allDatedFlights.put(LocalDate.of(2022, 02, 5), cloneList(this.allFlights));
    }

    // Método:
    public boolean userLogin(String name, String password) {
        lock.lock();
        try {
            if (allUsers.containsKey(name)) {
                return allUsers.get(name).checkPassword(password);
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    // Método:
    public User parseLine(String data) {
        int special = 0;
        String[] tokens = data.split(" ");
        if (tokens[0].substring(0, 3).equals("adm")) {
            special = 1;
        }
        return new User(tokens[0], tokens[1], special);
    }

    // Método:
    public String createUser(String data) { // throws userjaexiste?
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

    // Método:
    public User getUser(String nome) {
        lock.lock();
        try {
            return allUsers.get(nome);
        } finally {
            lock.unlock();
        }

    }

    // Método:
    public boolean searchFlight(String from, String to, String data1, String data2) {
        // ver se data é valida e nao esta encerrado
        for (int i = 0; i < this.allFlights.size(); i++) {
            if (allFlights.get(i).getFrom().equals(from) && allFlights.get(i).getTo().equals(to))
                return true;
        }
        return false;
    }

    // Método:
    public LocalDate searchAvailableFlightBetweenDates(String from, String to, LocalDate start, LocalDate end) {
        for (LocalDate dstart = start; dstart.isBefore(end) || dstart.isEqual(end); dstart.plusDays(1)) {
            List<Flight> flights = this.allDatedFlights.get(dstart);
            for (int i = 0; i < flights.size(); i++) {
                if (flights.get(i).getFrom().equals(from) && flights.get(i).getTo().equals(to)
                        && !flights.get(i).isFull())
                    return dstart;
            }
        }
        return null;
    }

    // Método:
    public int getFlightIndex(String from, String to, LocalDate data) {
        List<Flight> flights = this.allDatedFlights.get(data);
        for (int i = 0; i < flights.size(); i++) {
            if (flights.get(i).getFrom().equals(from) && flights.get(i).getTo().equals(to)) {
                try {
                    lock.lock();
                    return i;
                } finally {
                    lock.unlock();
                }
            }
        }
        return -1;
    }

    // Método:
    public String allFlightsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("From     To      Occupied Seats   Total capacity\n");
        for (int i = 0; i < this.allFlights.size(); i++) {
            sb.append(allFlights.get(i).toString());
        }
        return sb.toString();
    }

    // Método:
    public boolean createFlight(String from, String to, String seats) {
        int seats_i = Integer.parseInt(seats);
        return this.allFlights.add(new Flight(from, to, 0, seats_i));
    }

    // Método:
    public String createTrip(String username, List<String> destinations, String start, String end) {
        List<Flight> res = new ArrayList<>();
        String code = null;
        List<LocalDate> days = isTripPossible(destinations, LocalDate.parse(start), LocalDate.parse(end));
        if (days.size() == destinations.size()) {
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
    }

    // Método:
    public void addIfAbsentFlight(LocalDate start, LocalDate end) {
        for (LocalDate dstart = start; dstart.isBefore(end) || dstart.isEqual(end); dstart.plusDays(1)) {
            List<Flight> f = new ArrayList<>();
            for (int i = 0; i < this.allFlights.size(); i++)
                f.add(allFlights.get(i).clone());
            allDatedFlights.putIfAbsent(dstart, f);
        }
    }

    // Método:
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

    // Método:
    public boolean cancelTrip(String username, String code) {
        // Dado um codigo de viagem vamos ver se a viagem pode ser cancelada comparando a data da mesma (DONE)
        // Apos ter a data, se for possivel cancelar vamos guardar todos os voos para diminuir os lugares ocupados (DONE)
        // Apos isso removemos a key com o codigo do mapa
        List<Flight> lf = this.allTrips.get(code);
        int flights = 0;
        Boolean ispossible = false;
        LocalDate data = null;

        for (Map.Entry<LocalDate, List<Flight>> entry : this.allDatedFlights.entrySet()) {
            if (entry.getKey().isAfter(this.currentDay.minusDays(1)) && lf != null)
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

        if (ispossible && allUsers.containsKey(code)) {
            for (Flight f : lf) {
                f.removeSeat();
            }
            allUsers.get(username).removeReservation(code);
            allTrips.remove(code);
            return true;
        }
        return false;
    }

    // Método
    public boolean endingDay() {
        this.currentDay.plusDays(1);
        return true;
    }

    // Método:
    public List<Flight> cloneList(List<Flight> flight) {
        List<Flight> f = new ArrayList<>();
        for (int i = 0; i < flight.size(); i++)
            f.add(flight.get(i).clone());
        return f;
    }

    // Método:
    public Model loadData(String file) throws IOException, ClassNotFoundException {
        FileInputStream f = new FileInputStream(file);
        ObjectInputStream o = new ObjectInputStream(f);
        Model m = (Model) o.readObject();
        o.close();
        return m;
    }

    // Método:
    public void saveData(String file, Model model) throws IOException {
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(model);
        o.flush(); // para ter a certeza que todos os dados foram gravados
        o.close();
    }
}