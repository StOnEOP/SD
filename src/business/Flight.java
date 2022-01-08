package src.business;

import java.time.LocalDate;
import java.util.concurrent.locks.ReentrantLock;

public class Flight {
    public String from;
    public String to;
    public int seats_taken;
    public int total_capacity;
    public LocalDate date;
    ReentrantLock l = new ReentrantLock();

    public Flight(String from, String to, int seats_taken, int total_capacity, LocalDate date) {
        this.from = from;
        this.to = to;
        this.seats_taken = seats_taken;
        this.total_capacity = total_capacity;
        this.date = date;
    }

    public String getFrom() {
        try {
            l.lock();
            return from;
        } finally {
            l.unlock();
        }
    }

    public String getTo() {
        try {
            l.lock();
            return to;
        } finally {
            l.unlock();
        }
    }

    public LocalDate getDate() {
        try {
            l.lock();
            return date;
        } finally {
            l.unlock();
        }
    }

    public int getSeatsTaken() {
        try {
            l.lock();
            return seats_taken;
        } finally {
            l.unlock();
        }
    }

    public int getTotalCapacity() {
        try {
            l.lock();
            return total_capacity;
        } finally {
            l.unlock();
        }
    }

    public boolean isFull() {
        return this.seats_taken == this.total_capacity;
    }

    public void addSeat() {
        this.seats_taken++;
    }

    public void removeSeat() {
        this.seats_taken--;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.from + "\t");
        sb.append(this.to + "\t");
        sb.append(this.seats_taken + "\t");
        sb.append(this.total_capacity);
        return sb.toString();
    }
}
