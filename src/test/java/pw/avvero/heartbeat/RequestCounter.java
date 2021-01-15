package pw.avvero.heartbeat;

import lombok.Getter;
import org.awaitility.Awaitility;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.test.web.client.RequestMatcher;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class RequestCounter implements RequestMatcher {

    private AtomicInteger number = new AtomicInteger(0);

    @Override
    public void match(ClientHttpRequest request) throws AssertionError {
        number.incrementAndGet();
    }

    public int getHits() {
        return number.get();
    }

    public void await(int expectedNumber, long timeoutSeconds) {
        Awaitility.await()
                .atMost(timeoutSeconds, TimeUnit.SECONDS)
                .pollInterval(100, TimeUnit.MILLISECONDS)
                .until(() -> expectedNumber != number.get());
    }
}
