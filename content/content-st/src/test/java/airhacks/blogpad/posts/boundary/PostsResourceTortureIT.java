package airhacks.blogpad.posts.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import airhacks.blogpad.metrics.boundary.MetricsResourceClient;

public class PostsResourceTortureIT {

    private PostResourceClient client;
    private String title;
    private ExecutorService threadPool;
    private MetricsResourceClient metricsClient;

    @BeforeEach
    public void init() {
        URI uri = URI.create("http://localhost:9080/blogpad/resources/");
        this.client = RestClientBuilder.newBuilder().baseUri(uri).build(PostResourceClient.class);
        this.title = "torture" + System.currentTimeMillis();
        JsonObject post = Json.createObjectBuilder().add("title", title).add("content", "for torture").build();
        Response response = this.client.createNew(post);
        int status = response.getStatus();
        assertEquals(201, status);

        this.threadPool = Executors.newFixedThreadPool(20);

        this.initMetricsEndPoint();
    }

    void initMetricsEndPoint() {
        this.metricsClient = RestClientBuilder.newBuilder().baseUri(URI.create("http://localhost:9080/"))
                .build(MetricsResourceClient.class);
    }

    @Test
    public void startTorture() {
        assumeTrue(System.getProperty("torture", null) != null);
        List<CompletableFuture<Void>> tasks = Stream
                .generate(this::runScenario).limit(500)
                .collect(Collectors.toList());
        tasks.forEach(CompletableFuture::join);
        verifyPerformance();
    }

    CompletableFuture<Void> runScenario() {
        return CompletableFuture.runAsync(this::findPost, this.threadPool).thenRunAsync(this::findNonExistingPost,
                this.threadPool);
    }

    void findPost() {
        Response response = this.client.findPost(title);
        JsonObject post = response.readEntity(JsonObject.class);
        assertNotNull(post);
    }

    void findNonExistingPost() {
        try {
            this.client.findPost("not-existing" + System.currentTimeMillis());
            fail("Should not exist");
        } catch (WebApplicationException ex) {

        }
    }

    void verifyPerformance() {
        JsonObject findOpResult = this.metricsClient.applicationMetrics()
                .getJsonObject("airhacks.blogpad.posts.boundary.PostsResource.find");
        double oneMinRate = findOpResult.getJsonNumber("oneMinRate").doubleValue();
        System.out.println("-----------oneMinRate " + oneMinRate);
        assertTrue(oneMinRate > 10);
    }
}
