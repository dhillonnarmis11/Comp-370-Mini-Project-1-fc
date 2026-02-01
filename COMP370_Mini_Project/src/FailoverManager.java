
import java.util.*;

// This class decides which server becomes the new primary.
// A deterministic rule is used to keep things simple.

public class FailoverManager {

    // Chooses the server with the lowest ID
    public String selectNewPrimary(Set<String> aliveServers) {
        return Collections.min(aliveServers);
    }
}
