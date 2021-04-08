package ch.baselzock.twittertagsorter.converter;

import ch.baselzock.twittertagsorter.model.Tweet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter implements Converter {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Tweet convertToTweet(String text) {

        if (text == null) {
            return null;
        }

        try {
            return mapper.readValue(text, Tweet.class);
        } catch (JsonProcessingException e) {
            //todo logging
            e.printStackTrace();
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
            //todo logging
            e.printStackTrace();
            return null;
        }
    }
}
