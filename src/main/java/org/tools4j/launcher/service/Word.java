package org.tools4j.launcher.service;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ben
 * Date: 2/11/17
 * Time: 6:16 AM
 */
public class Word extends Term {
    private final List<Part> parts;

    public Word(final String asString, final List<Part> parts) {
        super(asString);
        this.parts = parts;
    }

    public boolean allPartsAreContainedIn(final String str) {
        for(final Part part: parts){
            if(!part.isContainedIn(str)){
                return false;
            }
        }
        return true;
    }

    public List<Part> getParts() {
        return parts;
    }
}
