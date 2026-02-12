import java.io.PrintWriter;

public class BackupServer extends ServerProcess {

    public BackupServer(String serverId) {
        super(serverId);
        this.isPrimary = false;
    }

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
