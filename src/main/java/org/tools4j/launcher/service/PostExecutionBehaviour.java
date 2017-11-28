package org.tools4j.launcher.service;

import java.util.function.Function;

/**
 * User: ben
 * Date: 27/11/17
 * Time: 6:00 PM
 */
public class PostExecutionBehaviour {
    public final Function<Void, Void> onFinish;
    public final Function<Void, Void> onFinishWithError;
    public final Function<Void, Void> onRunning;

    public PostExecutionBehaviour(final Function<Void, Void> onRunning, final Function<Void, Void> onFinish, final Function<Void, Void> onFinishWithError) {
        this.onRunning = onRunning;
        this.onFinish = onFinish;
        this.onFinishWithError = onFinishWithError;
    }
}
