import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// ConnectionHandler handles communication with single client
// runs in its own thread so multiple clients can connect at once

public class ConnectionHandler implements Runnable {

    private final Socket socket;
    private final ServerProcess server;

    // constructor sets up socket and server reference

    public ConnectionHandler(Socket socket, ServerProcess server) {
        this.socket = socket;
        this.server = server;
    }

    // runs when thread starts
    // reads client request, logs it, passes it to the server to handle

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
