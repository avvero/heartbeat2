package pw.avvero.heartbeats;

import lombok.Data;

import java.util.Map;

@Data
public class EnvProperty {

    private Map<String, ServiceProperty> service;

}
