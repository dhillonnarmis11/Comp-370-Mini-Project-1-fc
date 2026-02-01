
// This runnable sends heartbeat messages at fixed intervals.
// Heartbeats are used by the monitor to check if a server is alive.

public class HeartbeatSender implements Runnable {

    private String serverId;
    private int intervalMs = 1000;

    public HeartbeatSender(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public void run() {
        while (true) {
            // In a full system, this would send a network message
            System.out.println("Heartbeat sent from server " + serverId);

            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
