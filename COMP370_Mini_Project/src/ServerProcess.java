import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// abstract base class for all servers, primary and backup

public abstract class ServerProcess {

    protected String serverId;
    protected ServerSocket serverSocket;

    protected volatile boolean running = true;  // controls server loop
    protected volatile boolean isPrimary = false;  // indicates server role

    // thread pool to handle multiple client connections at once
    protected ExecutorService threadPool = Executors.newCachedThreadPool();

    public ServerProcess(String serverId) {
        this.serverId = serverId;
    }

    // updates server role
    public void setPrimary(boolean primary) {
        this.isPrimary = primary;
    }

    public boolean isPrimary() {
        return isPrimary;
    }
    // starts listening for client connections
    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        Log.info("SERVER-" + serverId, "Started on client port " + port + " role=" + (isPrimary ? "PRIMARY" : "BACKUP"));
        // accept client connections continuously
        while (running) {
            Socket socket = serverSocket.accept();
            threadPool.submit(new ConnectionHandler(socket, this));
        }
    }
    // stops the server and shuts down threads
    public void stop() throws IOException {
        running = false;
        Log.info("SERVER-" + serverId, "Stopping...");
        serverSocket.close();
        threadPool.shutdownNow();
    }
    // each server type must define how it handles requests
    protected abstract void handleRequest(String request, PrintWriter out);
}
