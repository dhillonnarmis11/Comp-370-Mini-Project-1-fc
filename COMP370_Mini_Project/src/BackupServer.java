import java.io.PrintWriter; // for sending response to client

public class BackupServer extends ServerProcess {

    // constructor initializes backup server with ID, ensures starts as non-primary server

    public BackupServer(String serverId) {
        super(serverId);
        this.isPrimary = false;
    }

    // handles incoming client requests, processes them only if this server is primary

    @Override
    protected void handleRequest(String request, PrintWriter out) {
        if (isPrimary) {
            Log.info("PRIMARY-" + serverId, "Processed request after promotion: " + request);
            out.println("Primary processed request: " + request);
        } else {
            Log.info("BACKUP-" + serverId, "Rejected request (backup): " + request);
            out.println("This is a backup server. Request not processed.");
        }
    }
}
