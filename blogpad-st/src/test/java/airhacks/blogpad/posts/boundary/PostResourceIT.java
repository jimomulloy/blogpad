package airhacks.blogpad.posts.boundary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author airhacks.com
 */
public class PostResourceIT {

    private PostResourceClient client;

    @BeforeEach
    public void init() {
        URI uri = URI.create("http://localhost:9080/blogpad/resources/");
        this.client = RestClientBuilder.
                newBuilder().
                baseUri(uri).
                build(PostResourceClient.class);

    }

    @Test
    public void save() {
        String value = "remote_hello";
        JsonObject post = Json.createObjectBuilder().
            add("title", value).
            add("content", "First st").build();
        Response response = this.client.save(post);
        int status = response.getStatus();
        assertEquals(204, status);

        response = this.client.findPost(value);
        status = response.getStatus();
        assertEquals(200, status);
    }

    
    @Test
    public void savePostWithInvalidTitle() {
        String value = "/";
        JsonObject post = Json.createObjectBuilder().
            add("title", value).
            add("content", "First st").build();
        Response response = this.client.save(post);
    }
}