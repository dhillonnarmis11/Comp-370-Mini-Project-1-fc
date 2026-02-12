import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Timestamped logging to console + file
public class Log {
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static String logFile = null;

    public static void setLogFile(String path) {
        logFile = path;
        try {
            File f = new File(path);
            File parent = f.getParentFile();
            if (parent != null) parent.mkdirs();
        } catch (Exception ignored) {}
    }

    public static synchronized void info(String who, String msg) {
        String line = "[" + LocalDateTime.now().format(FMT) + "] [" + who + "] " + msg;
        System.out.println(line);

        if (logFile != null) {
            try (FileWriter fw = new FileWriter(logFile, true)) {
                fw.write(line + System.lineSeparator());
            } catch (IOException e) {
                System.out.println("LOG_WRITE_FAILED: " + e.getMessage());
            }
        }
    }
}
