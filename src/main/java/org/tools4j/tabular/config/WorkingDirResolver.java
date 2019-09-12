package org.tools4j.tabular.config;

import java.io.File;

public class WorkingDirResolver implements DirResolver {
    @Override
    public File resolve() {
        return new File(".");
    }
}
