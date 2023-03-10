package airhacks.blogpad.posts.boundary;

import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("posts")
public interface PostResourceClient {

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    Response update(JsonObject post);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    Response createNew(JsonObject post);

    @GET
    @Path("{title}")
    @Produces(MediaType.APPLICATION_JSON)
    Response findPost(@PathParam("title") String title);
    
}