
import java.io.PrintWriter;

// The primary server is the ONLY server that
// processes client requests.

public class PrimaryServer extends ServerProcess {

    public PrimaryServer(String serverId) {
        super(serverId);
    }

    @Override
    protected void handleRequest(String request, PrintWriter out) {
        // Simple placeholder logic
        out.println("Primary processed request: " + request);
    }
}
