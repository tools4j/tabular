package org.tools4j.tabular.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

/**
 * User: ben
 * Date: 1/11/17
 * Time: 6:00 PM
 */
public class PartsFromWordsInString implements PartsSource {
    private final static java.util.regex.Pattern PART_FINDER_REGEX = java.util.regex.Pattern.compile("([A-Z]|\\d+|^.)[^A-Z^\\n\\d]*");
    private final String words;

    public PartsFromWordsInString(final String stringContainingWords) {
        this.words = stringContainingWords.trim();
    }

    @Override
    public Collection<Part> getParts(){
        final List<String> words = Arrays.asList(this.words.split("\\s+"));
        final List<Part> parts = new ArrayList<>();

        for(final String word: words){
            final Matcher matcher = PART_FINDER_REGEX.matcher(word);
            while(matcher.find()){
                parts.add(new Part(matcher.group(0)));
            }
        }
        return parts;
    }
}
