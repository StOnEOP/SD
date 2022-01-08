package src.business;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Model {
    private Map<String, User> allUsers;
    private List<Flight> allFlights;
    private List<LocalDate> blockedDates;
    private Map<String, List<Flight>> allTrips; // String: código de reserva, List: voos de uma viagem
    private ReentrantLock lock = new ReentrantLock();

    public Model() {
        this.allUsers = new HashMap<>();
        this.allFlights = new ArrayList<>();
        this.blockedDates = new ArrayList<LocalDate>();
    }

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

    public User parseLine(String data) {
        int special = 0;
        String[] tokens = data.split(" ");
        if (tokens[0].substring(0, 3).equals("adm")) {
            special = 1;
        }
        return new User(tokens[0], tokens[1], special);
    }

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

    public User getUser(String nome) {
        lock.lock();
        try {
            return allUsers.get(nome);
        } finally {
            lock.unlock();
        }

    }

    public boolean searchFlight(String from, String to, String data1, String data2) {
        // ver se data é valida e nao esta encerrado
        for (int i = 0; i < this.allFlights.size(); i++) {
            if (allFlights.get(i).getFrom().equals(from) && allFlights.get(i).getTo().equals(to))
                return true;
        }
        return false;
    }

    public LocalDate searchAvailableFlightBetweenDates(String from, String to, LocalDate start, LocalDate end) {
        for (int i = 0; i < this.allFlights.size(); i++) {
            if (allFlights.get(i).getFrom().equals(from) && allFlights.get(i).getTo().equals(to)
                    && (allFlights.get(i).getDate().isEqual(start) || allFlights.get(i).getDate().isEqual(end) ||
                            (allFlights.get(i).getDate().isAfter(start) && allFlights.get(i).getDate().isBefore(end)))
                    && allFlights.get(i).isFull())
                return allFlights.get(i).getDate();
        }
        return null;
    }

    public int getFlightIndex(String from, String to) {
        for (int i = 0; i < this.allFlights.size(); i++) {
            if (allFlights.get(i).getFrom().equals(from) && allFlights.get(i).getTo().equals(to)) {
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

    public String allFlightsToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("From     To      Ocupied Seats   Total capacity\n");
        for (int i = 0; i < this.allFlights.size(); i++) {
            sb.append(allFlights.get(i).toString());
        }
        return sb.toString();
    }

    public boolean createFlight(String from, String to, String seats, String str_date) {
        int seats_i = Integer.parseInt(seats);
        LocalDate date = LocalDate.parse(str_date);
        return this.allFlights.add(new Flight(from, to, 0, seats_i, date));
    }

    public String createTrip(String username, List<String> destinations, String start, String end) {
        List<Flight> res = new ArrayList<>();
        String code = null;
        if (isTripPossible(destinations, LocalDate.parse(start), LocalDate.parse(end))) {
            Random rnd = new Random();
            int number;
            for (int i = 0; i < destinations.size() - 1; i++) {
                String from = destinations.get(i);
                String to = destinations.get(i + 1);
                int index = getFlightIndex(from, to);
                Flight f = allFlights.get(index);
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

    public boolean isTripPossible(List<String> destinations, LocalDate start, LocalDate end) {
        LocalDate day = start;
        for (int i = 0; i < destinations.size() - 1; i++) {
            String from = destinations.get(i);
            String to = destinations.get(i + 1);
            day = searchAvailableFlightBetweenDates(from, to, day, end);
            if (day == null)
                return false;
        }
        return true;
    }

    public boolean cancelTrip(String username, String code) {
        if (!allUsers.containsKey(code))
            return false;
        allUsers.get(username).removeReservation(code);
        for (Flight f : allTrips.get(code)) {
            f.removeSeat();
        }
        allTrips.remove(code);
        return true;
    }

    public boolean addNewBlockedDate(LocalDate date) {
        return this.blockedDates.add(date);
    }
}