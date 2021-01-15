package pw.avvero.heartbeat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pw.avvero.heartbeat.api.HApplication;
import pw.avvero.heartbeat.api.HCall;
import pw.avvero.heartbeat.api.HComponent;
import pw.avvero.heartbeat.property.HeartBeatProperty;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;

@Slf4j
@Service
public class HeartbeatService {

    private HeartBeatProperty heartBeatProperty;
    private RestTemplate restTemplate;

    public HComponent root;

    public HeartbeatService(HeartBeatProperty heartBeatProperty, RestTemplate restTemplate) {
        this.heartBeatProperty = heartBeatProperty;
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        root = new HComponent();
        root.app = new HApplication();
        root.app.name = "heartbeat";
        root.app.components = Collections.synchronizedList(new ArrayList<>());
        heartBeatProperty.env.forEach((envName, envProperty) -> {
            HComponent env = new HComponent();
            root.app.components.add(env);
            env.app = new HApplication();
            env.app.name = envName;
            env.app.components = Collections.synchronizedList(new ArrayList<>());
            envProperty.service.forEach((serviceName, serviceProperty) -> {
                HComponent service = new HComponent();
                env.app.components.add(service);
                service.url = serviceProperty.info;
                service.calls = Collections.synchronizedList(new ArrayList<>());
            });
        });
    }

    public void rollCall() {
        root.app.components.stream()
                .flatMap(c -> c.app.components.stream())
                .peek(c -> log.debug("[Roll call] Service: {} ", c.url))
                .forEach(c -> {
                    CallTask task = new CallTask(restTemplate, c.url);
                    HCall hcall = task.handle();
                    c.getCalls().add(hcall);
                    if ("OK".equals((hcall.result))) {
                        if (c.app == null) c.app = new HApplication();
                        c.app.name = hcall.app.name;
                        c.app.version = hcall.app.version;
                        c.app.instanceId = hcall.app.instanceId;
                        c.error = null;
                    } else {
                        c.error = hcall.result;
                    }
                    c.lastUpdated = hcall.finished;
                });
    }

    @Scheduled(fixedRateString = "${app.heartbeat.rate.in.milliseconds}")
    public void scheduledHeartbeat() {

    }

}
