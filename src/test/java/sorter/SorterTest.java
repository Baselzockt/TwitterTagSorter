package sorter;

import ch.baselzock.twittertagsorter.sorter.Sorter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SorterTest {

    private static final Sorter sorter = new Sorter();

    @BeforeAll
    static void setupSorter() {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("test");
        tags.add("zmorge");
        tags.add("zmittag");
        tags.add("A good tag");
        tags.add("");
        sorter.setTags(tags);
    }

    @Test
    void testText() {
        String text = "test test ttest testetsttets hallo welt :D zmorge";
        List<String> tags = sorter.getMatchingTags(text);
        assertNotNull(tags);
        assertTrue(tags.contains("test"));
        assertTrue(tags.contains("zmorge"));
        assertFalse(tags.contains("zmittag"));
        assertFalse(tags.contains("A good tag"));
        assertFalse(tags.contains(""));
    }

    @Test
    void testNull() {
        Sorter nullSorter = new Sorter();
        List<String> tags = nullSorter.getMatchingTags(null);
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }

    @Test
    void testNoMatchingTags() {
        String text = "this text does not contain a tag but it is good and has an A. good A tag";
        List<String> tags = sorter.getMatchingTags(text);
        assertNotNull(tags);
        assertTrue(tags.isEmpty());
    }


}
