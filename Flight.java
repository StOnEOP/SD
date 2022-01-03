import java.util.concurrent.locks.ReentrantLock;

public class Flight {
    public String from;
    public String to;
    public int seats_taken;
    public int total_capacity;
    ReentrantLock l = new ReentrantLock();

    public Flight(String from, String to, int seats_taken, int total_capacity) {
        this.from = from;
        this.to = to;
        this.seats_taken = seats_taken;
        this.total_capacity = total_capacity;
    }

    public String getFrom() {
        try{
            l.lock();
            return from;
        }
        finally{
            l.unlock();
        }
    }

    public String getTo() {
        try{
            l.lock();
            return to;
        }
        finally{
            l.unlock();
        }
    }
}
