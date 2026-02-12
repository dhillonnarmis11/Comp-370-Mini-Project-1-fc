import java.io.PrintWriter;

public class PrimaryServer extends ServerProcess {

    public PrimaryServer(String serverId) {
        super(serverId);
        this.isPrimary = true;
    }

    @Override
    protected void handleRequest(String request, PrintWriter out) {
        Log.info("PRIMARY-" + serverId, "Processed request: " + request);
        out.println("Primary processed request: " + request);
    }
}
