package org.tools4j.launcher.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: ben
 * Date: 1/11/17
 * Time: 5:40 PM
 */
public class Part extends Term {

    public Part(final String part) {
        super(part);
    }

    public boolean isContainedIn(final String str){
        return str.contains(getTerm());
    }

    public Collection<Part> getCombinations(){
        final List<Part> combinations = new ArrayList<>();
        for(int i=0; i<getTerm().length(); i++){
            combinations.add(new Part(getTerm().substring(0, i+1)));
        }
        return combinations;
    }

    public Part toLowerCase() {
        return new Part(getTerm().toLowerCase());
    }
}
