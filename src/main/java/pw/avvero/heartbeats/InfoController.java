package pw.avvero.heartbeats;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Slf4j
@RestController
@AllArgsConstructor
public class InfoController {

    @RequestMapping(value = "/info", method = GET, produces = APPLICATION_JSON_VALUE)
    public String personalInformationVerification() {
        return "{}";
    }

}
