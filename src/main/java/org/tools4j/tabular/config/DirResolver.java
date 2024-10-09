package org.tools4j.tabular.config;

import java.io.File;
import java.util.Optional;

public interface DirResolver {
    Optional<File> resolve();
}
