package src.business;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/*
 *  User:   - Classe que guarda toda a informação de um utilizador
 *          - Possui todos os métodos necessários para alterar as suas variáveis
 */

public class User {
    private String name; // ID
    private String password; // Palavra passe
    private boolean isloggedin; // Guarda se utilizador está autenticado ou não
    private int special; // Se é administrador ou não
    private List<String> reservations; // Lista com todos os códigos de reservas
    ReentrantLock l = new ReentrantLock();

    public User(String name, String password, int special) {
        this.name = name;
        this.password = password;
        this.isloggedin = false;
        this.special = special;
        this.reservations = new ArrayList<>();
    }

    // Getters
    public String getName() {
        try {
            l.lock();
            return name;
        } finally {
            l.unlock();
        }
    }

    public String getPassword() {
        try {
            l.lock();
            return password;
        } finally {
            l.unlock();
        }
    }

    public int getSpecial() {
        try {
            l.lock();
            return special;
        } finally {
            l.unlock();
        }
    }

    public boolean getLoggedIn() {
        try {
            l.lock();
            return isloggedin;
        } finally {
            l.unlock();
        }
    }

    public List<String> getReservations() {
        try {
            l.lock();
            return reservations;
        } finally {
            l.unlock();
        }
    }

    // Setters
    public void setIsLoggedIn(boolean isloggedin) {
        try {
            l.lock();
            this.isloggedin = isloggedin;
        } finally {
            l.unlock();
        }
    }

    // Método: Adiciona uma reserva à lista de reservas
    public void addReservation(String code) {
        try {
            l.lock();
            this.reservations.add(code);
        } finally {
            l.unlock();
        }
    }

    // Método: Remove uma reserva da lista de reservas
    public void removeReservation(String code) {
        try {
            l.lock();
            this.reservations.remove(code);
        } finally {
            l.unlock();
        }
    }

    // Método: Verifica se a palavra passe dada está correta
    public boolean checkPassword(String password) {
        try {
            l.lock();
            return password.equals(this.password);
        } finally {
            l.unlock();
        }
    }
}
