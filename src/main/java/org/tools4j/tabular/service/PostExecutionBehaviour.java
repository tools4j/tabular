package org.tools4j.tabular.service;

import java.util.function.Function;

/**
 * User: ben
 * Date: 27/11/17
 * Time: 6:00 PM
 */
public class PostExecutionBehaviour {
    public final Runnable onFinish;
    public final Runnable onFinishWithError;
    public final Runnable onRunning;

    public PostExecutionBehaviour(final Runnable onRunning, final Runnable onFinish, final Runnable onFinishWithError) {
        this.onRunning = onRunning;
        this.onFinish = onFinish;
        this.onFinishWithError = onFinishWithError;
    }
}
