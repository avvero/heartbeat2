package pw.avvero.heartbeat;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pw.avvero.heartbeat.api.HComponent;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
@RestController
@AllArgsConstructor
public class Controller {

    private HeartbeatService heartbeatService;

    @RequestMapping(value = "/info", method = GET, produces = APPLICATION_JSON_VALUE)
    public HComponent info() {
        return heartbeatService.root;
    }

    @RequestMapping(value = "/rollCall", method = GET, produces = APPLICATION_JSON_VALUE)
    public HComponent rollCall() {
        heartbeatService.rollCall();
        return heartbeatService.root;
    }

}
