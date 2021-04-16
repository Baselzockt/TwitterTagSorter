package ch.baselzock.twittertagsorter.sorter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sorter {

    private final Pattern tagPattern = Pattern.compile("#[^\s#]+");

    public List<String> getMatchingTags(String text) {
        ArrayList<String> tags = new ArrayList<>();
        if (text == null) {
            return tags;
        }
        Matcher tagMatcher = tagPattern.matcher(text);

        while (tagMatcher.find()) {
            tags.add(tagMatcher.group());
        }

        if (tags.isEmpty()) {
            tags.add("noTagZone");
        }

        return tags;
    }

}
