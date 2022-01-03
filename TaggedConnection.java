import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable{
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;
    private ReentrantLock rl;
    private ReentrantLock wl;


    public static class Frame {
        public final int tag;
        public final byte[] data;   
        public Frame(int tag, byte[] data) {this.tag = tag; this.data = data;}
    }

    public TaggedConnection(Socket socket) throws IOException {
        this.s = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.rl = new ReentrantLock();
        this.wl = new ReentrantLock();
    }

    public void send(Frame frame) throws IOException {
        send(frame.tag,frame.data);
    }

    public void send(int tag, byte[] data) throws IOException {
        try {
            wl.lock();
            out.writeInt(data.length);
            out.writeInt(tag);
            out.write(data);
            out.flush();
        }
        finally {
            wl.unlock();
        }
    }

    public Frame receive() throws IOException {
        try {
            rl.lock();
            byte[] data = new byte[in.readInt()];
            int tag = in.readInt();
            in.readFully(data);
            return new Frame(tag,data);
        }
        finally {
            rl.unlock();
        }
    }

    public void close() throws IOException {
        this.s.close();
    }
}