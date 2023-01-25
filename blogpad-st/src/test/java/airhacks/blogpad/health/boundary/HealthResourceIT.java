package airhacks.blogpad.health.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HealthResourceIT {

    private HealthResourceClient client;

    @BeforeEach
    public void init() {
        URI uri = URI.create("http://localhost:9080/");
        this.client = RestClientBuilder.newBuilder().baseUri(uri).build(HealthResourceClient.class);

    }

    @Test
    public void liveness() {
        Response response = client.liveness();
        assertEquals(200, response.getStatus());
    }

    @Test
    public void readiness() {
        Response response = client.readiness();
        assertEquals(200, response.getStatus());
    }
}
