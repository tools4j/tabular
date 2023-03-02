package org.tools4j.tabular.service.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringWithEmbeddedGroovy {
    private static final Pattern VARIABLE_PATTERN = java.util.regex.Pattern.compile("(?<!\\\\)\\{\\{([^\\}\\}]+?)\\}\\}");
    private final String strWithEmbeddedGroovy;

    public StringWithEmbeddedGroovy(String strWithEmbeddedGroovy) {
        this.strWithEmbeddedGroovy = strWithEmbeddedGroovy;
    }

    public String resolve(){
        String str = strWithEmbeddedGroovy;
        while(true) {
            final Matcher matcher = VARIABLE_PATTERN.matcher(str);
            if(!matcher.find()) {
                return str;
            }
            final String unresolvedGroovyExpression = matcher.group(1);
            final GroovyExpression<String> groovyExpression = new GroovyExpression<>(unresolvedGroovyExpression, String.class);
            final String resolvedGroovyExpression = groovyExpression.resolveExpression();
            str = matcher.replaceFirst(resolvedGroovyExpression);
        }
    }
}
