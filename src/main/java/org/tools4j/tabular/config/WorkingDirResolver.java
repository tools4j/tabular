package org.tools4j.tabular.config;

import java.io.File;
import java.util.Optional;

public class WorkingDirResolver implements DirResolver {
  @Override
    public Optional<File> resolve() {
        return Optional.of(new File(".").getAbsoluteFile());
    }
}
