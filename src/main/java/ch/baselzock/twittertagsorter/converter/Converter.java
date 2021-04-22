package ch.baselzock.twittertagsorter.converter;

import ch.baselzock.twittertagsorter.exceptions.MarshalException;
import ch.baselzock.twittertagsorter.exceptions.UnmarshalException;
import ch.baselzock.twittertagsorter.model.Tweet;

public interface Converter {

    public Tweet convertToTweet(String text) throws UnmarshalException;

    public String convertToString(Tweet tweet) throws MarshalException;

}
