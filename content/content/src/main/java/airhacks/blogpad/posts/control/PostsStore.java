package airhacks.blogpad.posts.control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.BadRequestException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricRegistry.Type;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import airhacks.blogpad.posts.entity.Post;

@ApplicationScoped
public class PostsStore {
 
    @Inject
    @ConfigProperty(name = "root.storage.dir")
    String storageDir;
    
    @Inject
    TitleNormalizer titleNormalizer;

    Path storageDirectoryPath;

    @Inject
    @ConfigProperty(name = "minimum.storage.space", defaultValue = "50")
    long storageThreshold;

    @Inject
    @RegistryType(type = Type.APPLICATION)
    MetricRegistry registry;

    @PostConstruct
    public void init() {
        this.storageDirectoryPath = Path.of(this.storageDir);
    }

    @Produces
    @Liveness
    public HealthCheck checkPostsDirectoryExists() {
        return () -> HealthCheckResponse.named("posts-directory-exists").state(Files.exists(this.storageDirectoryPath)).build();
    }
  
    @Produces
    @Liveness
    public HealthCheck checkEnoughSpace() {
        var size = this.getPostsStorageSpaceInMB();
        var enoughSpace = size >= this.storageThreshold;
        return () -> HealthCheckResponse.named("posts-directory-has-space").state(enoughSpace).build();
    }

    @Gauge(
       unit = "mb"
    )
    public long getPostsStorageSpaceInMB() {
        try {
            return Files.getFileStore(storageDirectoryPath).getUsableSpace() / 1024 / 1024;
        } catch (IOException e) {
            throw new StorageException("Cannot fetch size information from " + storageDirectoryPath);
        }
    }

    //Check Idempotent
    public Post createNew(Post post) {
        var fileName = this.titleNormalizer.normalize(post.title); 
        if (fileExists(fileName)) {
            System.out.println(">>filename exists: " + fileName);
            throw new BadRequestException("Post with name already exists: " + fileName + " use PUT for udpate");
        }
        post.setCreatedAt();
        var stringified = serialise(post);
        try {
            post.fileName = fileName;
            write(fileName, stringified);
            System.out.println(">>filename created: " + fileName);
            return post;
        } catch (IOException e) {
            throw new StorageException("Cannot Save Post " + fileName, e);
        }
    }

    boolean fileExists(String fileName) {
        Path fqn = this.storageDirectoryPath.resolve(fileName);
        System.out.println(">>filename fqdn: " + fqn);
        return Files.exists(fqn);
    }
    
    
    public void update(Post post) {
        var fileName = this.titleNormalizer.normalize(post.title); 
        if (!fileExists(fileName)) {
            throw new BadRequestException("Post with name does not exist: " + fileName + " use POST to create");
        }
        post.setCreatedAt();
        var stringified = serialise(post);
        try {
            post.fileName = fileName;
            write(fileName, stringified);
        } catch (IOException e) {
            throw new StorageException("Cannot Save Post " + fileName, e);
        }
    }

    public String serialise(Post post) {
        var jsonb = JsonbBuilder.create();
        return jsonb.toJson(post);
    }

    public Post read(String title) {
        var fileName = this.titleNormalizer.normalize(title); 
        if (!this.fileExists(fileName)) {
            this.increaseNotExistingPostCounter();
            return null;
        }
        String stringified;
        try {
            stringified = this.readString(fileName);
        } catch (IOException e) {
            throw new StorageException("Cannot Fetch Post " + fileName, e);
        }
        return deserialize(stringified);
    }

    void increaseNotExistingPostCounter() {
        this.registry.counter("fetch_post_with_ne_title").inc();
    }

    Post deserialize(String stringified) {
        var jsonb = JsonbBuilder.create();
        return jsonb.fromJson(stringified, Post.class);
    }

    String readString(String fileName) throws IOException {
        var path = this.storageDirectoryPath.resolve(fileName);
        return Files.readString(path);
    }

    void write (String fileName, String content) throws IOException {
        var path = this.storageDirectoryPath.resolve(fileName);
        Files.writeString(path, content);   
    }
}
