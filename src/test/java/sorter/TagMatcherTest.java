package sorter;

import ch.baselzock.twittertagsorter.tagmatcher.TagMatcher;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TagMatcherTest {

    private static final TagMatcher TAG_MATCHER = new TagMatcher();


    @Test
    void testText() {
        String text = "test #test # ttest testetsttets hallo welt :D #zmorge";
        List<String> tags = TAG_MATCHER.getMatchingTags(text);
        assertNotNull(tags);
        assertTrue(tags.contains("#test"));
        assertTrue(tags.contains("#zmorge"));
        assertFalse(tags.contains("#zmittag"));
        assertFalse(tags.contains("#A good tag"));
        assertFalse(tags.contains(""));
        assertFalse(tags.contains("#"));
    }

    @Test
    void testNull() {
        TagMatcher nullTagMatcher = new TagMatcher();
        List<String> tags = nullTagMatcher.getMatchingTags(null);
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }

    @Test
    void testNoMatchingTags() {
        String text = "this text does # ## not contain a tag but it is good and has an A. good A tag";
        List<String> tags = TAG_MATCHER.getMatchingTags(text);
        assertNotNull(tags);
        assertFalse(tags.isEmpty());
        assertTrue(tags.contains("noTagZone"));
    }


}
