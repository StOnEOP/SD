package src.business;

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
    LocalDate currentDay = LocalDate.of(2022, 01, 01);
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
        this.allDatedFlights.put(LocalDate.of(2022, 01, 14),cloneList(this.allFlights));
    }

    public LocalDate getCurrentDay(){
        return this.currentDay;
    }

    // Método:
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

    // Método:
    public void setLoggedIn(String name){
        lock.lock();
        try{
            this.allUsers.get(name).setIsLoggedIn(true);
        } finally{
            lock.unlock();
        }
    }

    // Método
    public int getSpecial(String name){
        lock.lock();
        try{
            return this.allUsers.get(name).getSpecial();
        } finally{
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
        try {
            lock.lock();
            for (int i = 0; i < this.allFlights.size(); i++) {
                if (allFlights.get(i).getFrom().equals(from) && allFlights.get(i).getTo().equals(to))
                    return true;
            }
            return false;
        } finally {
            lock.unlock();
        }    
    }

    // Método:
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

    // Método:
    public boolean containsFlight(String from, String to) {
        for(Flight f : this.allFlights)
            if (f.getFrom().equals(from) && f.getTo().equals(to))
                return true;
        return false;        
    }

    // Método:
    public boolean createFlight(String from, String to, String seats) {
        int seats_i = Integer.parseInt(seats);
        Flight f = new Flight(from, to, 0, seats_i);
        try {
            lock.lock();
            if(!containsFlight(from,to)) { 
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
    }

    // Método:
    public String createTrip(String username, List<String> destinations, String start, String end) {
        String code = null;
        LocalDate dstart = LocalDate.parse(start);
        LocalDate dend = LocalDate.parse(end);
        try {
            lock.lock();
            if (dend.isBefore(this.currentDay)) return code; //Caso tente fazer reserva antes do dia atual ERRO
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
    }

    // Método:
    public void addIfAbsentFlight(LocalDate start, LocalDate end) {
        for (LocalDate dstart = start; dstart.isBefore(end) || dstart.isEqual(end); dstart = dstart.plusDays(1)) {
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

            if (ispossible && allUsers.containsKey(username)) { //Meti username pq não entendi bem este containsKey... dantes tinha containsKey(code)
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

    // Método
    public boolean endingDay() {
        try {
            lock.lock();
            this.currentDay = this.currentDay.plusDays(1);
            return true;
        } finally {
            lock.unlock();
        }
    }

    // Método:
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