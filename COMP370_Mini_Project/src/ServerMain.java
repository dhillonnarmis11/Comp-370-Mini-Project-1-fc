public class ServerMain {

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Usage: ServerMain <id> <clientPort> <controlPort> <role>");
            return;
        }

        String id = args[0];
        int clientPort = Integer.parseInt(args[1]);
        int controlPort = Integer.parseInt(args[2]);
        String role = args[3];

        Log.setLogFile("logs/server-" + id + ".log");

        ServerProcess server;
        if ("primary".equalsIgnoreCase(role)) {
            server = new PrimaryServer(id);
        } else {
            server = new BackupServer(id);
        }

        new Thread(new ControlListener(server, controlPort)).start();
        new Thread(new HeartbeatSenderUDP(id, "localhost", 9999, 1000)).start();

        server.start(clientPort);
    }
}
