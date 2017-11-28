package org.tools4j.launcher.service;

/**
 * User: ben
 * Date: 2/11/17
 * Time: 4:54 PM
 */
public interface Pretty {
    String toPrettyString(String indent);

    default String toPrettyString(){
        return toPrettyString("    ");
    }
}
