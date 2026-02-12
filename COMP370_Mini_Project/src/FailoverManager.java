import java.util.Collections;
import java.util.Set;

// Deterministic rule: choose the lowest ID to avoid split-brain
public class FailoverManager {
    public String selectNewPrimary(Set<String> aliveServers) {
        return Collections.min(aliveServers);
    }
}
