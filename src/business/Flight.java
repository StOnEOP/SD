package src.business;

import java.util.concurrent.locks.ReentrantLock;

/*
 *  Fligth:  -
 */

public class Flight {
    public String from;
    public String to;
    public int seats_taken;
    public int total_capacity;
    ReentrantLock l = new ReentrantLock();

    public Flight(String from, String to) {
        this.from = from;
        this.to = to;
        this.seats_taken = 0;
        this.total_capacity = 0;
    }

    public Flight(String from, String to, int seats_taken, int total_capacity) {
        this.from = from;
        this.to = to;
        this.seats_taken = seats_taken;
        this.total_capacity = total_capacity;
    }

    public Flight(Flight f) {
        this.from = f.getFrom();
        this.to = f.getTo();
        this.seats_taken = f.getSeatsTaken();
        this.total_capacity = f.getTotalCapacity();
    }

    // Getters
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

    // Auxiliaries
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.from + "\t");
        sb.append(this.to + "\t");
        sb.append(this.seats_taken + "\t");
        sb.append(this.total_capacity);
        return sb.toString();
    }

    public Flight clone() {
        return new Flight(this);
    }

    // Método: Verifica se o voo está com a capacidade máxima
    public boolean isFull() {
        return this.seats_taken == this.total_capacity;
    }

    // Método: Adiciona mais um lugar ocupado
    public void addSeat() {
        this.seats_taken++;
    }

    // Método: Remove um lugar ocupado
    public void removeSeat() {
        this.seats_taken--;
    }
}
