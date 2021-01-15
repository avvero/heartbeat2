package pw.avvero.heartbeat

import org.springframework.http.client.ClientHttpRequest
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.web.client.ResponseCreator
import org.springframework.web.client.ResourceAccessException

class SocketTimeoutExceptionResponse implements ResponseCreator {
    @Override
    ClientHttpResponse createResponse(ClientHttpRequest request) throws IOException {
        throw new ResourceAccessException("Error", new SocketTimeoutException("Connection reset"))
    }
}
