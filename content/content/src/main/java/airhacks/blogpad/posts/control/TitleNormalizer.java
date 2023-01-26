package airhacks.blogpad.posts.control;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

public class TitleNormalizer {

    @Inject
    @ConfigProperty(name = "title.separator", defaultValue = "-")
    String titleSeparator;

    int codePointSeparator;

    @PostConstruct
    public void init() {
        this.codePointSeparator = this.titleSeparator.codePoints().findFirst().orElseThrow();
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
            return this.codePointSeparator;
        }
    }
}
