package pw.avvero.heartbeat.api;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HApplication {

    public String name;
    public String version;
    public String instanceId;
    public List<HComponent> components;
    public LocalDateTime lastUpdated;

}
