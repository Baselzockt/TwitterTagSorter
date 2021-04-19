package ch.baselzock.twittertagsorter.tagmatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagMatcher {

    private final Pattern tagPattern = Pattern.compile("#[^\s#]+");
    private final String noTagDestination;

    public TagMatcher() {
        noTagDestination = "noTagZone";
    }

    public TagMatcher(String nn) {
        noTagDestination = nn;
    }

    public List<String> getAllTags(String text) {
        ArrayList<String> tags = new ArrayList<>();
        if (text == null) {
            return tags;
        }
        Matcher tagMatcher = tagPattern.matcher(text);

        while (tagMatcher.find()) {
            tags.add(tagMatcher.group());
        }

        if (tags.isEmpty()) {
            tags.add(noTagDestination);
        }

        return tags;
    }

}
