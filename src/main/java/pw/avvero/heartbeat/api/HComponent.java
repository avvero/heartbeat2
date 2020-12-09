package pw.avvero.heartbeat.api;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HComponent {

    private String url;
    private HApplication app;
    private LocalDateTime lastUpdated;
    private String error;

}
