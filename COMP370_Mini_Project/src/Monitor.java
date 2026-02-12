import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// monitor keeps track of server heartbeats and handles automatic failover
public class Monitor {

    private final int hbPort;
    private final long timeoutMs;

    // stores last heartbeat time for each server
    private final Map<String, Long> lastHb = new ConcurrentHashMap<>();
    // stores host and control port info for each server
    private final Map<String, String> hosts = new ConcurrentHashMap<>();
    private final Map<String, Integer> controlPorts = new ConcurrentHashMap<>();

    private volatile String primaryId = null; // current primary server
    private final FailoverManager failoverManager = new FailoverManager();

    public Monitor(int hbPort, long timeoutMs) {
        this.hbPort = hbPort;
        this.timeoutMs = timeoutMs;
    }

    // registers a server so monitor knows how to contact it
    public void registerServer(String serverId, String host, int controlPort) {
        hosts.put(serverId, host);
        controlPorts.put(serverId, controlPort);
        Log.info("MONITOR", "Registered server " + serverId + " host=" + host + " ctrlPort=" + controlPort);
    }

    // starts heartbeat listener, failure checker, and admin console
    public void start() throws Exception {
        Log.info("MONITOR", "Starting monitor hbPort=" + hbPort + " timeoutMs=" + timeoutMs);

        Thread hbListener = new Thread(this::listenHeartbeats, "hb-listener");
        hbListener.start();

        Thread checker = new Thread(this::checkLoop, "fail-checker");
        checker.start();

        adminLoop(); // runs in main thread
    }

    // listens for UDP heartbeat messages from servers
    private void listenHeartbeats() {
        try (DatagramSocket sock = new DatagramSocket(hbPort)) {
            byte[] buf = new byte[256];

            while (true) {
                DatagramPacket p = new DatagramPacket(buf, buf.length);
                sock.receive(p);

                String msg = new String(p.getData(), 0, p.getLength()).trim();
                if (msg.startsWith("HB|")) {
                    String[] parts = msg.split("\\|");
                    if (parts.length >= 2) {
                        String id = parts[1];

                        // update last heartbeat time
                        lastHb.put(id, System.currentTimeMillis());
                        Log.info("MONITOR", "Heartbeat received from " + id);

                        // first server to send heartbeat becomes initial primary
                        if (primaryId == null) {
                            primaryId = id;
                            Log.info("MONITOR", "Initial primary set to " + primaryId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.info("MONITOR", "Heartbeat listener stopped: " + e.getMessage());
        }
    }

    // checks if primary has timed out
    private void checkLoop() {
        while (true) {
            try {
                Thread.sleep(300);

                Set<String> alive = getAliveSet();
                if (primaryId != null && !alive.contains(primaryId)) {
                    Log.info("MONITOR", "PRIMARY TIMEOUT: " + primaryId);
                    promoteNewPrimary(alive);
                }

            } catch (InterruptedException e) {
                return;
            }
        }
    }

    // returns set of servers that have not timed out
    private Set<String> getAliveSet() {
        long now = System.currentTimeMillis();
        Set<String> alive = new HashSet<>();

        for (String id : lastHb.keySet()) {
            long age = now - lastHb.getOrDefault(id, 0L);
            if (age <= timeoutMs) alive.add(id);
        }
        return alive;
    }

    // chooses and promotes a new primary server
    private synchronized void promoteNewPrimary(Set<String> alive) {
        if (alive.isEmpty()) {
            Log.info("MONITOR", "No alive servers. Cannot promote.");
            primaryId = null;
            return;
        }

        String newPrimary = failoverManager.selectNewPrimary(alive);
        Log.info("MONITOR", "Selected new primary: " + newPrimary);

        sendPromote(newPrimary); // tell server to become primary

        primaryId = newPrimary;
        Log.info("MONITOR", "Primary updated to " + primaryId);
    }

    // sends PROMOTE command to selected server
    private void sendPromote(String serverId) {
        try {
            String host = hosts.get(serverId);
            Integer portObj = controlPorts.get(serverId);

            if (host == null || portObj == null) {
                Log.info("MONITOR", "No registry info for " + serverId + " (cannot PROMOTE)");
                return;
            }

            int port = portObj;
            try (Socket s = new Socket(host, port);
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
                out.println("PROMOTE");
            }

            Log.info("MONITOR", "Sent PROMOTE to " + serverId);
        } catch (Exception e) {
            Log.info("MONITOR", "PROMOTE failed for " + serverId + ": " + e.getMessage());
        }
    }

    // simple admin console to check status or trigger manual failover
    private void adminLoop() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("monitor> ");
            String cmd = sc.nextLine().trim();

            if ("status".equalsIgnoreCase(cmd)) {
                printStatus();
            } else if ("primary".equalsIgnoreCase(cmd)) {
                System.out.println("Primary: " + primaryId);
            } else if ("failover".equalsIgnoreCase(cmd)) {
                Log.info("MONITOR", "Manual failover requested");
                promoteNewPrimary(getAliveSet());
            } else if ("exit".equalsIgnoreCase(cmd)) {
                Log.info("MONITOR", "Exiting monitor");
                return;
            } else {
                System.out.println("Commands: status | primary | failover | exit");
            }
        }
    }

    // prints current primary and heartbeat ages
    private void printStatus() {
        long now = System.currentTimeMillis();
        System.out.println("Primary=" + primaryId);
        for (String id : lastHb.keySet()) {
            long age = now - lastHb.getOrDefault(id, 0L);
            System.out.println("Server " + id + " lastHbAge=" + age + "ms");
        }
    }

    // main method to start monitor and register servers
    public static void main(String[] args) throws Exception {
        Log.setLogFile("logs/monitor.log");

        Monitor m = new Monitor(9999, 3000);
        m.registerServer("1", "localhost", 9101);
        m.registerServer("2", "localhost", 9102);
        m.registerServer("3", "localhost", 9103);

        m.start();
    }
}
