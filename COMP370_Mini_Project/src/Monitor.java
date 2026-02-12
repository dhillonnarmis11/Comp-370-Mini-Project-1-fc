import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Monitor {

    private final int hbPort;
    private final long timeoutMs;

    private final Map<String, Long> lastHb = new ConcurrentHashMap<>();
    private final Map<String, String> hosts = new ConcurrentHashMap<>();
    private final Map<String, Integer> controlPorts = new ConcurrentHashMap<>();

    private volatile String primaryId = null;
    private final FailoverManager failoverManager = new FailoverManager();

    public Monitor(int hbPort, long timeoutMs) {
        this.hbPort = hbPort;
        this.timeoutMs = timeoutMs;
    }

    public void registerServer(String serverId, String host, int controlPort) {
        hosts.put(serverId, host);
        controlPorts.put(serverId, controlPort);
        Log.info("MONITOR", "Registered server " + serverId + " host=" + host + " ctrlPort=" + controlPort);
    }

    public void start() throws Exception {
        Log.info("MONITOR", "Starting monitor hbPort=" + hbPort + " timeoutMs=" + timeoutMs);

        Thread hbListener = new Thread(this::listenHeartbeats, "hb-listener");
        hbListener.start();

        Thread checker = new Thread(this::checkLoop, "fail-checker");
        checker.start();

        adminLoop();
    }

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
                        lastHb.put(id, System.currentTimeMillis());
                        Log.info("MONITOR", "Heartbeat received from " + id);

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

    private Set<String> getAliveSet() {
        long now = System.currentTimeMillis();
        Set<String> alive = new HashSet<>();

        for (String id : lastHb.keySet()) {
            long age = now - lastHb.getOrDefault(id, 0L);
            if (age <= timeoutMs) alive.add(id);
        }
        return alive;
    }

    private synchronized void promoteNewPrimary(Set<String> alive) {
        if (alive.isEmpty()) {
            Log.info("MONITOR", "No alive servers. Cannot promote.");
            primaryId = null;
            return;
        }

        String newPrimary = failoverManager.selectNewPrimary(alive);
        Log.info("MONITOR", "Selected new primary: " + newPrimary);

        sendPromote(newPrimary);

        primaryId = newPrimary;
        Log.info("MONITOR", "Primary updated to " + primaryId);
    }

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

    private void printStatus() {
        long now = System.currentTimeMillis();
        System.out.println("Primary=" + primaryId);
        for (String id : lastHb.keySet()) {
            long age = now - lastHb.getOrDefault(id, 0L);
            System.out.println("Server " + id + " lastHbAge=" + age + "ms");
        }
    }

    public static void main(String[] args) throws Exception {
        Log.setLogFile("logs/monitor.log");

        Monitor m = new Monitor(9999, 3000);
        m.registerServer("1", "localhost", 9101);
        m.registerServer("2", "localhost", 9102);
        m.registerServer("3", "localhost", 9103);

        m.start();
    }
}
