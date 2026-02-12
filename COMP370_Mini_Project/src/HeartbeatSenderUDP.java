import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// HeartbeatSenderUDP sends heartbeat messages to monitoring server using UDP to indicate server is alive

public class HeartbeatSenderUDP implements Runnable {
    private final String serverId;  // ID of this server
    private final String monitorHost;  // monitor's hostname/IP
    private final int monitorPort;  // monitor's port number
    private final int intervalMs;  // time between heartbeats in milliseconds

    // Constructor initializes heartbeat configuration

    public HeartbeatSenderUDP(String serverId, String monitorHost, int monitorPort, int intervalMs) {
        this.serverId = serverId;  
        this.monitorHost = monitorHost; 
        this.monitorPort = monitorPort;   
        this.intervalMs = intervalMs;  
    }

    // runs in its own thread
    // continuously sends heartbeat messages at fixed intervals until thread interrupted

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
