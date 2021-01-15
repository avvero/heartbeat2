package pw.avvero.heartbeat

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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
import static org.springframework.test.web.client.ExpectedCount.times
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
        "app.heartbeat.enable=false",
        "app.heartbeat.env[ci].service[service1].info=http://service1/info"
])
class SingleServiceHeartbeatTests extends Specification {

    @Autowired
    MockMvc mockMvc
    @Autowired
    RestTemplate restTemplate
    @Shared
    MockRestServiceServer restServiceServer
    @Value('${app.heartbeat.per-service-capacity}')
    int perServiceCapacity

    def setup() {
        restServiceServer = MockRestServiceServer.bindTo(restTemplate)
                .ignoreExpectOrder(true)
                .build()
    }

    def "Heartbeats for one service are provided if service is accessible"() {
        when: "First time service is unavailable"
        restServiceServer
                .expect(requestTo("http://service1/info"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(new SocketTimeoutExceptionResponse())
        then: "One call entry with error"
        mockMvc.perform(get("/rollCall")
                .contentType(APPLICATION_JSON_VALUE)
                .content()
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.app.name', is("heartbeat")))
                .andExpect(jsonPath('$.app.components[0].app.name', is("ci")))
                .andExpect(jsonPath('$.app.components[0].app.components[0].url', is("http://service1/info")))
                .andExpect(jsonPath('$.app.components[0].app.components[0].app', nullValue()))
                .andExpect(jsonPath('$.app.components[0].app.components[0].lastUpdated', notNullValue()))
                .andExpect(jsonPath('$.app.components[0].app.components[0].error', is("Error; nested exception is java.net.SocketTimeoutException: Connection reset")))
//                .andExpect(jsonPath('$.app.components[0].app.components[0].calls', arrayWithSize(1)))
                .andExpect(jsonPath('$.app.components[0].app.components[0].calls[0].app', nullValue()))
                .andExpect(jsonPath('$.app.components[0].app.components[0].calls[0].started', notNullValue()))
                .andExpect(jsonPath('$.app.components[0].app.components[0].calls[0].finished', notNullValue()))
                .andExpect(jsonPath('$.app.components[0].app.components[0].calls[0].result', is("Error; nested exception is java.net.SocketTimeoutException: Connection reset")))
        and:
        restServiceServer.verify()
        when: "Second time service is available"
        restServiceServer.reset()
        restServiceServer
                .expect(requestTo("http://service1/info"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("""{"name":"service1", "instanceId": 1, "version": "1.0.0"}""", APPLICATION_JSON))
        then:
        mockMvc.perform(get("/rollCall")
                .contentType(APPLICATION_JSON_VALUE)
                .content()
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.app.name', is("heartbeat")))
                .andExpect(jsonPath('$.app.components[0].app.name', is("ci")))
                .andExpect(jsonPath('$.app.components[0].app.components[0].url', is("http://service1/info")))
                .andExpect(jsonPath('$.app.components[0].app.components[0].app.name', is("service1")))
                .andExpect(jsonPath('$.app.components[0].app.components[0].app.instanceId', is("1")))
                .andExpect(jsonPath('$.app.components[0].app.components[0].app.version', is("1.0.0")))
                .andExpect(jsonPath('$.app.components[0].app.components[0].lastUpdated', notNullValue()))
                .andExpect(jsonPath('$.app.components[0].app.components[0].error', nullValue()))
//                .andExpect(jsonPath('$.app.components[0].app.components[0].calls', arrayWithSize(2)))
                .andExpect(jsonPath('$.app.components[0].app.components[0].calls[1].app.name', is("service1")))
                .andExpect(jsonPath('$.app.components[0].app.components[0].calls[1].app.instanceId', is("1")))
                .andExpect(jsonPath('$.app.components[0].app.components[0].calls[1].app.version', is("1.0.0")))
                .andExpect(jsonPath('$.app.components[0].app.components[0].calls[1].started', notNullValue()))
                .andExpect(jsonPath('$.app.components[0].app.components[0].calls[1].finished', notNullValue()))
                .andExpect(jsonPath('$.app.components[0].app.components[0].calls[1].result', is("OK")))
        and:
        restServiceServer.verify()
        cleanup:
        restServiceServer.reset()
    }

    def "Only #perServiceCapacity heartbeats for one service would be provided"() {
        when:
        restServiceServer
                .expect(times(perServiceCapacity), requestTo("http://service1/info"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(new SocketTimeoutExceptionResponse())
        then: "make per-service-capacity-times requests"
        perServiceCapacity.times {
            mockMvc.perform(get("/rollCall")
                    .contentType(APPLICATION_JSON_VALUE)
                    .content()
                    .accept(APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
        }
        then: "make one more request"
        mockMvc.perform(get("/rollCall")
                .contentType(APPLICATION_JSON_VALUE)
                .content()
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath('$.app.components[0].app.components[0].url', is("http://service1/info")))
                .andExpect(jsonPath('$.app.components[0].app.components[0].calls', arrayWithSize(perServiceCapacity)))
        and:
        restServiceServer.verify()
        cleanup:
        restServiceServer.reset()
    }

}
