
import java.io.*;
import java.net.*;

// Each ConnectionHandler runs in its own thread.
// This prevents one slow client from blocking others.

public class ConnectionHandler implements Runnable {

    private Socket socket;
    private ServerProcess server;

    public ConnectionHandler(Socket socket, ServerProcess server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true)
        ) {
            String request = in.readLine();
            server.handleRequest(request, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
