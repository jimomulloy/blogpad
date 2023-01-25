package airhacks.blogpad.posts.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import airhacks.blogpad.posts.entity.Post;

public class PostStoreTest {

    PostsStore cut;

    @BeforeEach
    public void init() {
        this.cut = new PostsStore();
        this.cut.storageDir = "target";
        this.cut.init();
        System.out.println("!!Create cut");
    }

    @Test
    public void serialize() {
        String stringified = this.cut.serialise(new Post("hello", "world"));
        assertNotNull(stringified);
        System.out.println("-> " + stringified);
    } 

    @Test
    public void writeString() throws IOException {
        String path = "nextPost";
        String content = "hello";
        this.cut.write(path, content);
        String actual = this.cut.readString(path);
        assertEquals(content, actual);
    }
    
    @Test 
    public void savePostThenRead() throws IOException {
        String title = "first";
        String content = "hey duke";
        Post expected = new Post(title, content);
        System.out.println("!!Create wit cut: " + this.cut.getClass());
        this.cut.createNew(expected);
        Post actual = this.cut.read(title);
        assertEquals(expected.title, actual.title);
        assertEquals(expected.content, actual.content);
    }
}
