package org.tools4j.tabular.config;

import org.apache.log4j.Logger;

import java.util.Optional;

public class SysPropAndEnvVarResolverImpl implements SysPropAndEnvVarResolver {
    private final static Logger LOG = Logger.getLogger(SysPropAndEnvVarResolverImpl.class);

    @Override
    public Optional<String> resolve(String propOrEnvVarName) {
        if (System.getProperties().containsKey(propOrEnvVarName)) {
            String value = System.getProperty(propOrEnvVarName);
            LOG.info("Resolved system property [" + propOrEnvVarName + "] to [" + value + "]");
            return Optional.of(value);

        } else if (System.getenv(propOrEnvVarName.toUpperCase()) != null) {
            String value = System.getenv(propOrEnvVarName.toUpperCase());
            LOG.info("Resolved env variable [" + propOrEnvVarName.toUpperCase() + "] to [" + value + "]");
            return Optional.of(value);
        } else {
            return Optional.empty();
        }
    }
}
