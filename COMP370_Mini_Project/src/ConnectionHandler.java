import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

    private final Socket socket;
    private final ServerProcess server;

    public ConnectionHandler(Socket socket, ServerProcess server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String request = in.readLine();
            Log.info("SERVER-" + server.serverId, "Received request: " + request);
            server.handleRequest(request, out);
        } catch (IOException e) {
            Log.info("SERVER-" + server.serverId, "Connection error: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
