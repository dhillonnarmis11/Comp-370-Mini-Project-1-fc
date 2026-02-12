import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

// ControlListener listens on separate control port to receive admin commands

public class ControlListener implements Runnable {
    private final ServerProcess server;
    private final int controlPort; // port used for control commands

    // Constructor initializes server reference and control port

    public ControlListener(ServerProcess server, int controlPort) {
        this.server = server; 
        this.controlPort = controlPort;
    }

    // runs in its own thread, waits for control connections and processes commands

    @Override
    public void run() {
        try (ServerSocket ss = new ServerSocket(controlPort)) {
            Log.info("SERVER-" + server.serverId, "Control listener on port " + controlPort);

            // keep listening while the server is running

            while (server.running) {
                Socket s = ss.accept();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                    String cmd = in.readLine();
                    Log.info("SERVER-" + server.serverId, "Control cmd: " + cmd);

                    if ("PROMOTE".equalsIgnoreCase(cmd)) {
                        server.setPrimary(true);
                        Log.info("SERVER-" + server.serverId, "PROMOTED to PRIMARY");
                    }
                } finally {
                    s.close(); // Close control connection
                }
            }
        } catch (Exception e) {
            Log.info("SERVER-" + server.serverId, "Control listener stopped: " + e.getMessage());
        }
    }
}
