package pw.avvero.heartbeat.api;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HApplication {

    private String name;
    private List<HComponent> components;
    private LocalDateTime lastUpdated;

}
