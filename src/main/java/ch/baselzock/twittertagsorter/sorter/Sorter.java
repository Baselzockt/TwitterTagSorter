package ch.baselzock.twittertagsorter.sorter;

import java.util.ArrayList;
import java.util.List;

public class Sorter {
    private List<String> tags;

    public Sorter() {
        this.tags = new ArrayList<>();
    }

    public List<String> getMatchingTags(String text) {
        ArrayList<String> tags = new ArrayList<>();
        if (text == null) {
            return tags;
        }

        this.tags.forEach(tag -> {
            if (!tag.isEmpty() && text.contains(tag)) {
                tags.add(tag);
            }
        });
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
