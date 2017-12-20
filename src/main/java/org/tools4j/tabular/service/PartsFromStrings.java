package org.tools4j.tabular.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * User: ben
 * Date: 1/11/17
 * Time: 6:00 PM
 */
public class PartsFromStrings implements PartsSource {
    private final Collection<String> strings;

    public PartsFromStrings(final Collection<String> strings) {
        this.strings = strings;
    }

    public PartsFromStrings(final String ... strings) {
        this.strings = Arrays.asList(strings);
    }

    @Override
    public Collection<Part> getParts(){
        return strings.stream().map((str) -> new Part(str)).collect(Collectors.toList());
    }
}
