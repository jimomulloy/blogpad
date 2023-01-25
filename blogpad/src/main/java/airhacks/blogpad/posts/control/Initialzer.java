package airhacks.blogpad.posts.control;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import airhacks.blogpad.posts.entity.Post;

@ApplicationScoped
public class Initialzer {

    @Inject
    PostsStore store;

    public void onStart(@Observes @Initialized(ApplicationScoped.class) Object pointless) {
        System.out.println(">>>---- InitializerOnStart.onStart() ");
        installFirstPost();
    }

    void installFirstPost() {
        store.createNew(createInitialPost());
    }

    Post createInitialPost() {
        return new Post("initial", "welcome to blogpad");

    }
}