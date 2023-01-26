package airhacks.blogpad.posts.control;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import airhacks.blogpad.posts.entity.Post;

@ApplicationScoped
public class Initialzer {

    static final String TITLE = "initial";

    @Inject
    PostsStore store;

    public void onStart(@Observes @Initialized(ApplicationScoped.class) Object pointless) {
        System.out.println(">>>---- InitializerOnStart.onStart() ");
        installFirstPost();
    }

    void installFirstPost() {
        if (this.postExists()) {
            return;
        }
        store.createNew(createInitialPost());
    }

    Post createInitialPost() {
        return new Post(TITLE, "welcome to blogpad");

    }

    Post fetchPost() {
        return this.store.read(TITLE);
    }

    boolean postExists() {
        var post = fetchPost();
        if (post == null) {
            return false;
        }
        return TITLE.equalsIgnoreCase(post.title);
    }

    @Produces
    @Liveness
    public HealthCheck initialExists() {
        return () -> HealthCheckResponse.named("initial-post-exists").state(this.postExists()).build();
    }    
        
}