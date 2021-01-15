package pw.avvero.heartbeat.property;

import lombok.Data;

import java.util.Map;

@Data
public class EnvProperty {

    public Map<String, ServiceProperty> service;

}
