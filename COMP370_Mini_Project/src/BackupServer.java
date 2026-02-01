
import java.io.PrintWriter;

// Backup servers stay on standby and do not
// handle requests unless promoted.

public class BackupServer extends ServerProcess {

    public BackupServer(String serverId) {
        super(serverId);
    }

    @Override
    protected void handleRequest(String request, PrintWriter out) {
        out.println("This is a backup server. Request not processed.");
    }

    // Called when the monitor promotes this server
    public void promoteToPrimary() {
        System.out.println("Backup server promoted to primary");
    }
}
