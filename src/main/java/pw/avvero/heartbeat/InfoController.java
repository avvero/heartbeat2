package pw.avvero.heartbeat;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pw.avvero.heartbeat.api.HApplication;
import pw.avvero.heartbeat.api.HComponent;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
@RestController
@AllArgsConstructor
public class InfoController {

    @RequestMapping(value = "/info", method = GET, produces = APPLICATION_JSON_VALUE)
    public HComponent personalInformationVerification() {
        HComponent root = new HComponent();
        root.setApp(new HApplication());
        root.getApp().setName("heartbeat");
        return root;
    }

}
