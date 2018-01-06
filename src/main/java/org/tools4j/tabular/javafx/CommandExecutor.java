package org.tools4j.tabular.javafx;

import java.io.IOException;

/**
 * User: ben
 * Date: 29/12/17
 * Time: 6:33 AM
 */
public interface CommandExecutor {
    Process runCommand(String command) throws IOException;
}
