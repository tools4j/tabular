package org.tools4j.launcher.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: ben
 * Date: 2/11/17
 * Time: 6:54 AM
 */
public class WordsAsString {
    private final String string;

    public WordsAsString(final String string) {
        this.string = string;
    }

    public List<Word> getWords(){
        final List<String> wordsInQuery = Arrays.asList(string.trim().split("\\s+"));
        return new WordsAsStrings(wordsInQuery).getWords();
    }

    public List<Part> getAllParts(){
        return getWords().stream().flatMap((word) -> word.getParts().stream()).collect(Collectors.toList());
    }
}
