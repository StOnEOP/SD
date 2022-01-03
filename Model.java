import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class Model {
    private Map<String, User> allUsers;
    private List<Flight> allFlights;
    private ReentrantLock lock = new ReentrantLock();

    public Model() {
        this.allUsers = new HashMap<>();
        this.allFlights = new ArrayList<>();
    }

    public boolean userLogin(String name, String password) {
        lock.lock();
        try {
            if (allUsers.containsKey(name)) {
                return allUsers.get(name).checkPassword(password);
            }
            return false;
        } 
        finally {
            lock.unlock();
        }
    }

    public User parseLine (String data) {
        int special = 0;
        String[] tokens = data.split(" ");
        if (tokens[0].substring(0, 3).equals("adm")){
            special = 1;
        }
        return new User(tokens[0], tokens[1], special);
    }

    public String createUser(String data) { // throws userjaexiste?
        User u = parseLine(data);
        lock.lock();
        try {
            boolean exists = allUsers.containsKey(u.getName());
            if(!exists) allUsers.put(u.getName(),u);
            return u.getName();
        }
        finally {
            lock.unlock();
        }
    }

    public User getUser(String nome) {
        lock.lock();
        try {
            return allUsers.get(nome);
        } 
        finally {
            lock.unlock();
        }

    }

    public boolean searchFlight(String from, String to) {
        for(int i = 0; i < this.allFlights.size(); i++){
            if(allFlights.get(i).getFrom().equals(from) && allFlights.get(i).getTo().equals(to))
                return true;
        }
        return false;
    }
}