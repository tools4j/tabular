package org.tools4j.launcher.service;

/**
 * User: ben
 * Date: 2/11/17
 * Time: 6:31 AM
 */
public abstract class Term implements Comparable<Term> {
    private final String term;

    protected Term(final String term) {
        this.term = term;
    }

    public boolean isContainedIn(final String str) {
        return str.contains(term);
    }

    public int positionIn(final String str) {
        return str.indexOf(term);
    }

    public String getTerm(){
        return term;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Term)) return false;

        final Term term1 = (Term) o;

        return term != null ? term.equals(term1.term) : term1.term == null;
    }

    @Override
    public int hashCode() {
        return term != null ? term.hashCode() : 0;
    }

    @Override
    public int compareTo(final Term o) {
        return term.toLowerCase().compareTo(o.term.toLowerCase());
    }

    @Override
    public String toString() {
        return term;
    }

    public int length() {
        return term.length();
    }
}
