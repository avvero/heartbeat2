package pw.avvero.heartbeat.dto;

import lombok.Data;

@Data
public class DtoApplicationInfo {

    private String name;
    private String instanceId;
    private String version;

}
