package org.tools4j.tabular.javafx;

import java.io.IOException;

/**
 * User: ben
 * Date: 29/12/17
 * Time: 6:34 AM
 */
public class DefaultCommandExecutor implements CommandExecutor {
    @Override
    public Process runCommand(final String command) throws IOException {
        return Runtime.getRuntime().exec(command);
    }
}
