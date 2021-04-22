package ch.baselzock.twittertagsorter.converter;

import ch.baselzock.twittertagsorter.Main;
import ch.baselzock.twittertagsorter.exceptions.JsonMarshalException;
import ch.baselzock.twittertagsorter.exceptions.JsonUnmarshalException;
import ch.baselzock.twittertagsorter.model.Tweet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonConverter implements Converter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Tweet convertToTweet(@NotNull String json) throws JsonUnmarshalException {
        try {
            return mapper.readValue(json, Tweet.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not convert json to tweet. Error: {}", e.getMessage());
            throw new JsonUnmarshalException(e);
        }
    }

    @Override
    public String convertToString(@NotNull Tweet tweet) throws JsonMarshalException {
        try {
            return mapper.writeValueAsString(tweet);
        } catch (JsonProcessingException e) {
            LOGGER.error("Could not convert tweet to json. Error: {}", e.getMessage());
            throw new JsonMarshalException(e);
        }
    }
}
