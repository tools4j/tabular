package org.tools4j.tabular.config;

import java.io.File;

public class UserDirResolver implements DirResolver {
    @Override
    public File resolve() {
        return new File(System.getProperty("user.dir") + "/.tabular");
    }
}
