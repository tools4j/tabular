package org.tools4j.tabular.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * User: ben
 * Date: 29/12/17
 * Time: 6:48 AM
 */
public class StackTrace {
    private final Throwable t;

    public StackTrace(final Throwable t) {
        this.t = t;
    }

    @Override
    public String toString(){
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }
}
