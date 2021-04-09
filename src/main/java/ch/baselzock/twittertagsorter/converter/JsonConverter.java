package ch.baselzock.twittertagsorter.converter;

import ch.baselzock.twittertagsorter.Main;
import ch.baselzock.twittertagsorter.model.Tweet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonConverter implements Converter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Tweet convertToTweet(String text) {

        if (text == null) {
            return null;
        }

        try {
            return mapper.readValue(text, Tweet.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not convert json to tweet. Error: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String convertToString(Tweet tweet) {

        if (tweet == null) {
            return null;
        }

        try {
            return mapper.writeValueAsString(tweet);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not convert tweet to json. Error: {}", e.getMessage());
            return null;
        }
    }
}
