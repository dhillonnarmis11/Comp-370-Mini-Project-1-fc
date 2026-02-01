
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

// This abstract class contains logic shared by ALL servers.
// It handles networking, threading, and basic lifecycle control.
// PrimaryServer and BackupServer extend this class.

public abstract class ServerProcess {

    protected String serverId;
    protected ServerSocket serverSocket;

    // volatile ensures all threads see updates to this variable
    protected volatile boolean running = true;

    // Thread pool lets the server handle multiple connections
    protected ExecutorService threadPool =
            Executors.newCachedThreadPool();

    public ServerProcess(String serverId) {
        this.serverId = serverId;
    }

    // Starts the server and listens for connections
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server " + serverId + " started on port " + port);

        // Main server loop
        while (running) {
            Socket socket = serverSocket.accept();
            threadPool.submit(new ConnectionHandler(socket, this));
        }
    }

    // Stops the server safely
    public void stop() throws IOException {
        running = false;
        serverSocket.close();
        threadPool.shutdownNow();
    }

    // Each server type handles requests differently
    protected abstract void handleRequest(String request, PrintWriter out);
}
