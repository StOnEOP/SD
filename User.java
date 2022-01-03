import java.util.concurrent.locks.ReentrantLock;

public class User {
    private String name;
    private String password;
    private boolean isloggedin;
    private int special;
    ReentrantLock l = new ReentrantLock();

    public User(String name, String password, int special) {
        this.name = name;
        this.password = password;
        this.isloggedin = false;
        this.special = special;
    }

    public String getName() {
        try{
            l.lock();
            return name;
        }
        finally{
            l.unlock();
        }
    }

    public String getPassword() {
        try{
            l.lock();
            return password;
        }
        finally{
            l.unlock();
        }
    }

    public void setIsLoggedIn(boolean isloggedin) {
        try{
            l.lock();
            this.isloggedin = isloggedin;
        }
        finally{
            l.unlock();
        }
    }

    public int getSpecial() {
        try{
            l.lock();
            return special;
        }
        finally{
            l.unlock();
        }
    }

    public boolean getLoggedIn() {
        try{
            l.lock();
            return isloggedin;
        }
        finally{
            l.unlock();
        }
    }


    public boolean checkPassword(String password) {
        try{
            l.lock();
            return password.equals(this.password);
        }
        finally{
            l.unlock();
        }
    }

}