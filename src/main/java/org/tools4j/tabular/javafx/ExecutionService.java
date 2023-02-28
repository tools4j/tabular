package org.tools4j.tabular.javafx;

import javafx.scene.control.TextArea;
import org.tools4j.tabular.service.PostExecutionBehaviour;
import org.tools4j.tabular.service.commands.Command;

/**
 * User: ben
 * Date: 21/11/17
 * Time: 5:52 PM
 */
public interface ExecutionService {
    ExecutingCommand exec(final Command command, final TextArea outputConsole, final PostExecutionBehaviour postExecutionBehaviour);
    void destroy();
}
