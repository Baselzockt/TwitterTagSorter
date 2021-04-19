package converter;

import ch.baselzock.twittertagsorter.converter.Converter;
import ch.baselzock.twittertagsorter.converter.JsonConverter;
import ch.baselzock.twittertagsorter.model.Tweet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonConverterTest {
    private static final Converter converter = new JsonConverter();

    @Test
    void testJsonToTweet() {
        String expected = "To make room for more expression, we will now count all emojis as equal—including" +
                " those with gender and skin t… https://t.co/MkGjXf9aXm";
        String json = "{" +
                " \"created_at\": \"Wed Oct 10 20:19:24 +0000 2018\",\n" +
                " \"id\": 1050118621198921728,\n" +
                " \"id_str\": \"1050118621198921728\n\",\n" +
                " \"text\": \"" + expected + "\",\n" +
                " \"user\": {},  \n" +
                " \"entities\": {}\n" +
                "}";
        Tweet tweet = converter.convertToTweet(json);
        assertNotNull(tweet);
        assertEquals(expected, tweet.getText());
    }

    @Test
    void testTweetToJson() {

        String expected = "{\"text\":\"To make room for more expression, we will now count all emojis as equal—including" +
                " those with gender and skin t… https://t.co/MkGjXf9aXm\"," +
                "\"id\":1050118621198921728,\"user\":{" +
                "\"id\":0," +
                "\"name\":null," +
                "\"description\":null," +
                "\"verified\":false," +
                "\"protected\":false," +
                "\"created_at\":null},\"retweetCount\":0,\"created_at\":\"Wed Oct 10 20:19:24 +0000 2018\"}";

        String json = "{\"text\":\"To make room for more expression, we will now count all emojis as equal—including" +
                " those with gender and skin t… https://t.co/MkGjXf9aXm\"," +
                "\"id\":1050118621198921728," +
                "\"user\":{" +
                "\"id\":0," +
                "\"name\":null," +
                "\"description\n\":null," +
                "\"verified\":false," +
                "\"protected\":false," +
                "\"created_at\n\":null}," +
                "\"retweetCount\":0,\"created_at\":\"Wed Oct 10 20:19:24 +0000 2018\"},";
        Tweet tweet = converter.convertToTweet(json);
        assertNotNull(tweet);
        String actual = converter.convertToString(tweet);
        assertEquals(expected, actual);
    }

    @Test
    void testNull() {
        Tweet tweet = converter.convertToTweet(null);
        assertNull(tweet);
        String actual = converter.convertToString(null);
        assertNull(actual);
    }

}
