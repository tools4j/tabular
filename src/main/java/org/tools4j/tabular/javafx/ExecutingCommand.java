package org.tools4j.tabular.javafx;

/**
 * User: ben
 * Date: 29/12/17
 * Time: 7:14 AM
 */
public interface ExecutingCommand {
    void init();
    boolean isFinished();
    void stop();
}
