package airhacks.blogpad.posts.control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import airhacks.blogpad.posts.entity.Post;

public class PostsStore {
 
    @Inject
    @ConfigProperty(name = "root.storage.dir")
    String storageDir;
    
    Path storageDirectoryPath;

    @PostConstruct
    public void init() {
        this.storageDirectoryPath = Path.of(this.storageDir);
    }

    public void save(Post post) {
        var fileName = this.normalize(post.title); 
        var stringified = serialise(post);
        try {
            write(fileName, stringified);
        } catch (IOException e) {
            throw new StorageException("Cannot Save Post " + fileName, e);
        }
    }

    public String serialise(Post post) {
        var jsonb = JsonbBuilder.create();
        return jsonb.toJson(post);
    }

    public Post read(String fileName) {
        String stringified;
        try {
            stringified = this.readString(fileName);
        } catch (IOException e) {
            throw new StorageException("Cannot Fetch Post " + fileName, e);
        }
        return deserialize(stringified);
    }

    Post deserialize(String stringified) {
        var jsonb = JsonbBuilder.create();
        return jsonb.fromJson(stringified, Post.class);
    }

    String normalize(String title) {
        return title.codePoints().map(this::replaceWithDigitOrletter).collect(
            StringBuffer::new,
            StringBuffer::appendCodePoint,
            StringBuffer::append
        ).toString();
    }

    int replaceWithDigitOrletter(int codePoint) {
        if (Character.isLetterOrDigit(codePoint)) {
            return codePoint;
        } else {
            return "-".codePoints().findFirst().orElseThrow();
        }
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
