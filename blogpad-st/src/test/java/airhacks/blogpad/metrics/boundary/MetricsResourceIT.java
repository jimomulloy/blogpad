package airhacks.blogpad.metrics.boundary;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import airhacks.blogpad.posts.boundary.PostResourceIT;

/**
 *
 * @author airhacks.com
 */
public class MetricsResourceIT {

    private MetricsResourceClient client;

    @BeforeAll
    public static void initMetricsWithBusinessCall() {
        var test = new PostResourceIT();
        test.init();
        test.createNew();
    }

    @BeforeEach
    public void init() {
        URI uri = URI.create("http://localhost:9080/");
        this.client = RestClientBuilder.
                newBuilder().
                baseUri(uri).
                build(MetricsResourceClient.class);

    }

    //Smoke tests
    @Test
    public void metrics() {
        var metrics = this.client.metrics();
        assertNotNull(metrics);
        assertFalse(metrics.isEmpty());
    }

    @Test
    public void applicationMetrics() {
        var applicationMetrics = this.client.applicationMetrics();
        assertNotNull(applicationMetrics);
        assertFalse(applicationMetrics.isEmpty());
        int saveInvocationCounter = applicationMetrics.getJsonNumber("airhacks.blogpad.posts.boundary.PostsResource.createNew").intValue();
        assertTrue(saveInvocationCounter > 0);
    }
    

}