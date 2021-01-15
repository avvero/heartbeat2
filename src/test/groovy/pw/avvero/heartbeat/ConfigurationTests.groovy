package pw.avvero.heartbeat

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import pw.avvero.heartbeat.property.HeartBeatProperty
import spock.lang.Specification

@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@TestPropertySource(properties = [
        "app.heartbeat.env[ci].service[service1].info=http://service1/info",
        "app.heartbeat.env[ci].service[service2].info=http://service2/info",
        "app.heartbeat.env[ci].service[service2].name=service2"
])
class ConfigurationTests extends Specification {

    @Autowired
    HeartBeatProperty heartBeatProperty

    def "Configuration properties are accessable"() {
        expect:
        heartBeatProperty.env["ci"].service["service1"].info == "http://service1/info"
        heartBeatProperty.env["ci"].service["service2"].info == "http://service2/info"
        heartBeatProperty.env["ci"].service["service2"].name == "service2"
    }

}
