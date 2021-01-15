package pw.avvero.heartbeat;

import java.util.Map;

public class JPathFinder {

    private String[] path;

    public JPathFinder(String... path) {
        this.path = path;
    }

    public String search(Map map) {
        if (map == null) return null;
        if (map.isEmpty()) return null;

        Object value = map.get(path[0]);
        return value != null ? value.toString() : null;
    }

}
