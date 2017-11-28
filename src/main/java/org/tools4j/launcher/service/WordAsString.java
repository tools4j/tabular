package org.tools4j.launcher.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

/**
 * User: ben
 * Date: 2/11/17
 * Time: 6:54 AM
 */
public class WordAsString {
    private final java.util.regex.Pattern PART_FINDER_REGEX = java.util.regex.Pattern.compile("([A-Z]|\\d+|^.)[^A-Z^\\n\\d]*");
    private final String string;

    public WordAsString(final String string) {
        this.string = string;
    }

    public Word getWord(){
        final List<Part> partsInWord = new ArrayList<>();

        for(final String split: string.split("[^\\w]+")){
            final Matcher matcher = PART_FINDER_REGEX.matcher(split);
            while(matcher.find()){
                partsInWord.add(new Part(matcher.group(0)));
            }
        }
        return new Word(string, partsInWord);
    }
}
