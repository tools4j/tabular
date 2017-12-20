package org.tools4j.tabular.javafx;

/**
 * User: ben
 * Date: 7/11/17
 * Time: 5:37 PM
 */
public class WorkingDir {
    public String get(){
        return System.getProperty("workingDir", System.getProperty("user.dir"));
    }
}
