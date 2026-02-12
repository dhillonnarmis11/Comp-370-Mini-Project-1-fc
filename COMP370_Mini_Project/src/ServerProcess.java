import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ServerProcess {

    protected String serverId;
    protected ServerSocket serverSocket;

    protected volatile boolean running = true;
    protected volatile boolean isPrimary = false;

    protected ExecutorService threadPool = Executors.newCachedThreadPool();

    public ServerProcess(String serverId) {
        this.serverId = serverId;
    }

    public void setPrimary(boolean primary) {
        this.isPrimary = primary;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        Log.info("SERVER-" + serverId, "Started on client port " + port + " role=" + (isPrimary ? "PRIMARY" : "BACKUP"));

        while (running) {
            Socket socket = serverSocket.accept();
            threadPool.submit(new ConnectionHandler(socket, this));
        }
    }

    public void stop() throws IOException {
        running = false;
        Log.info("SERVER-" + serverId, "Stopping...");
        serverSocket.close();
        threadPool.shutdownNow();
    }

    protected abstract void handleRequest(String request, PrintWriter out);
}
