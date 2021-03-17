package org.tools4j.tabular.util;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static final Pattern PATTERN = Pattern.compile("(.*\\s)?([^\\s]+)$");

    public static Optional<String> getLastWordInText(String text) {
        Matcher matcher = PATTERN.matcher(text);
        if(matcher.matches()){
            return Optional.of(matcher.group(2));
        } else {
            return Optional.empty();
        }
    }

    public static String replaceLastWordWith(String text, String replacement) {
        Matcher matcher = PATTERN.matcher(text);
        if(!matcher.matches()){
            throw new IllegalArgumentException("Text does not match, probably because it ends with a space, or is empty [" + text + "]");
        }
        return matcher.replaceFirst("$1" + replacement);
    }
}
