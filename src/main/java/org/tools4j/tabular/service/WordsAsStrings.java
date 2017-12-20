package org.tools4j.tabular.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * User: ben
 * Date: 2/11/17
 * Time: 6:54 AM
 */
public class WordsAsStrings {
    private final Collection<String> wordStrings;

    public WordsAsStrings(final Collection<String> wordStrings) {
        this.wordStrings = wordStrings;
    }

    public WordsAsStrings(final String[] wordStrings) {
        this(Arrays.asList(wordStrings));
    }

    public List<Word> getWords(){
        final List<Word> words = new ArrayList<>();

        for(final String word: wordStrings){
            words.add(new WordAsString(word.trim()).getWord());
        }
        return words;
    }
}
