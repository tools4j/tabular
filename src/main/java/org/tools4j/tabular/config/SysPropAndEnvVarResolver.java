package org.tools4j.tabular.config;

import java.util.Optional;

public interface SysPropAndEnvVarResolver {
    Optional<String> resolve(String propOrEnvVarName);
}
