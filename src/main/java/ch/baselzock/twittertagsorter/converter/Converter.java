package ch.baselzock.twittertagsorter.converter;

import ch.baselzock.twittertagsorter.model.Tweet;

public interface Converter {

    public Tweet convertToTweet(String text);

    public String convertToString(Tweet tweet);

}
