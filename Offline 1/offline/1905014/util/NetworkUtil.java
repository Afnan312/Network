package util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class NetworkUtil {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean active;

    public NetworkUtil(String s, int port) throws IOException {
        this.socket = new Socket(s, port);
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        active=true;
    }

    public NetworkUtil(Socket s) throws IOException {
        this.socket = s;
        oos = new ObjectOutputStream(socket.getOutputStream());
        ois = new ObjectInputStream(socket.getInputStream());
        active=true;
    }

    public Object read() throws IOException, ClassNotFoundException {
        socket.setSoTimeout(0);
        return ois.readUnshared();
    }

    public Object readTimed() throws IOException, ClassNotFoundException, SocketException {
        socket.setSoTimeout(30000);
        return ois.readUnshared();
    }

    public void write(Object o) throws IOException {
        oos.writeUnshared(o);
    }

    public void closeConnection() throws IOException {
        ois.close();
        oos.close();
        active=false;
        throw new IOException("Server Disconnected");
    }

    public boolean isActive() {
        return active;
    }
}
