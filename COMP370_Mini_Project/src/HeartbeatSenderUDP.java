import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HeartbeatSenderUDP implements Runnable {
    private final String serverId;
    private final String monitorHost;
    private final int monitorPort;
    private final int intervalMs;

    public HeartbeatSenderUDP(String serverId, String monitorHost, int monitorPort, int intervalMs) {
        this.serverId = serverId;
        this.monitorHost = monitorHost;
        this.monitorPort = monitorPort;
        this.intervalMs = intervalMs;
    }

    @Override
    public void run() {
        try (DatagramSocket sock = new DatagramSocket()) {
            InetAddress addr = InetAddress.getByName(monitorHost);

            while (!Thread.currentThread().isInterrupted()) {
                String msg = "HB|" + serverId;
                byte[] data = msg.getBytes();

                DatagramPacket p = new DatagramPacket(data, data.length, addr, monitorPort);
                sock.send(p);

                Log.info("SERVER-" + serverId, "Heartbeat sent");
                Thread.sleep(intervalMs);
            }
        } catch (Exception e) {
            Log.info("SERVER-" + serverId, "Heartbeat sender stopped: " + e.getMessage());
        }
    }
}
