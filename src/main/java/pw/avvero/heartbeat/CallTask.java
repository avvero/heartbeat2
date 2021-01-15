package pw.avvero.heartbeat;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pw.avvero.heartbeat.api.HApplication;
import pw.avvero.heartbeat.api.HCall;

import java.util.Date;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class CallTask {

    private RestTemplate restTemplate;
    private String url;

    public HCall handle() {
        log.debug("Service call {}", url);
        HCall hcall = new HCall();
        hcall.started = new Date();
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            hcall.result = response.getStatusCode().name();
            if ("OK".equals((hcall.result))) {
                Map responseBody = response.getBody();
                hcall.app = new HApplication();
                hcall.app.name = new JPathFinder("name").search(responseBody);
                hcall.app.version = new JPathFinder("version").search(responseBody);
                hcall.app.instanceId = new JPathFinder("instanceId").search(responseBody);
            }
        } catch (Throwable t) {
            hcall.result = t.getLocalizedMessage();
        }
        hcall.finished = new Date();
        return hcall;
    }
}
