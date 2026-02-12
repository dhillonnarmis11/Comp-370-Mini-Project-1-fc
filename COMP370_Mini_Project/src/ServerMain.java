// Entry point for starting a server (primary or backup)

public class ServerMain {
    
    public static void main(String[] args) throws Exception {
        // make sure correct arguments are provided
        if (args.length < 4) {
            System.out.println("Usage: ServerMain <id> <clientPort> <controlPort> <role>");
            return;
        }
        
        // read command-line arguments
        String id = args[0];
        int clientPort = Integer.parseInt(args[1]);
        int controlPort = Integer.parseInt(args[2]);
        String role = args[3];

        // set log file specific to this server
        Log.setLogFile("logs/server-" + id + ".log");

        // create either a PrimaryServer or BackupServer based on role
        ServerProcess server;
        if ("primary".equalsIgnoreCase(role)) {
            server = new PrimaryServer(id);
        } else {
            server = new BackupServer(id);
        }

        // start control listener for PROMOTE commands
        new Thread(new ControlListener(server, controlPort)).start();
        // start heartbeat sender to notify monitor
        new Thread(new HeartbeatSenderUDP(id, "localhost", 9999, 1000)).start();
        // start accepting client requests
        server.start(clientPort);
    }
}
