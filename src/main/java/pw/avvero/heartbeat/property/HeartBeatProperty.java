package pw.avvero.heartbeat.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "app.heartbeat")
public class HeartBeatProperty {

    public Map<String, EnvProperty> env;

}
