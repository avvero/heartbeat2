package pw.avvero.heartbeat

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

import static org.hamcrest.Matchers.*
import static org.springframework.http.MediaType.APPLICATION_JSON
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@TestPropertySource(properties = [
        "app.heartbeat.env[ci].service[service1].info=http://172.0.0.1:1001/info"
])
class HeartbeatTests extends Specification {

    @Autowired
    MockMvc mockMvc
    @Autowired
    RestTemplate restTemplate
    @Shared
    MockRestServiceServer restServiceServer

    def setup() {
        restServiceServer = MockRestServiceServer.bindTo(restTemplate)
                .ignoreExpectOrder(true)
                .build()
    }

    def "Heartbeat for one service is provided"() {
        when:
        restServiceServer
                .expect(requestTo("http://172.0.0.1:1001/info"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""{"name":"service1", "instanceId": 1, "version": "1.0.0"}""", APPLICATION_JSON))
        then:
        mockMvc.perform(get("/info")
                .contentType(APPLICATION_JSON_VALUE)
                .content()
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.app.name', is("heartbeat")))
                .andExpect(jsonPath('$.app.name.components[0].app.name', is("ci")))
                .andExpect(jsonPath('$.app.name.components[0].app.name.components[0].url', is("http://172.0.0.1:1001/info")))
                .andExpect(jsonPath('$.app.name.components[0].app.name.components[0].app', is("service1")))
                .andExpect(jsonPath('$.app.name.components[0].app.name.components[0].app.instanceId', is("1")))
                .andExpect(jsonPath('$.app.name.components[0].app.name.components[0].app.version', is("1.0.0")))
                .andExpect(jsonPath('$.app.name.components[0].app.name.components[0].lastUpdated', notNullValue()))
                .andExpect(jsonPath('$.app.name.components[0].app.name.components[0].error', nullValue()))
    }

}
