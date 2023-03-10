package org.tools4j.tabular.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class SysPropAndEnvVarResolverImpl implements SysPropAndEnvVarResolver {
    private final static Logger LOG = LoggerFactory.getLogger(SysPropAndEnvVarResolverImpl.class);

    @Override
    public Optional<String> resolve(String propOrEnvVarName) {
        if (System.getProperties().containsKey(propOrEnvVarName.toLowerCase())) {
            String value = System.getProperty(propOrEnvVarName.toLowerCase());
            LOG.info("Resolved system property [" + propOrEnvVarName.toLowerCase() + "] to [" + value + "]");
            return Optional.of(value);

        } else if (System.getProperties().containsKey(propOrEnvVarName.toUpperCase())) {
            String value = System.getProperty(propOrEnvVarName.toUpperCase());
            LOG.info("Resolved system property [" + propOrEnvVarName.toUpperCase() + "] to [" + value + "]");
            return Optional.of(value);

        } else if (System.getenv(propOrEnvVarName.toLowerCase()) != null) {
            String value = System.getenv(propOrEnvVarName.toLowerCase());
            LOG.info("Resolved env variable [" + propOrEnvVarName.toLowerCase() + "] to [" + value + "]");
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
