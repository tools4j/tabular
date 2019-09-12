package org.tools4j.tabular.config

import org.tools4j.tabular.config.DirResolver;

class DummyDirResolver implements DirResolver {
    private final String path;

    DummyDirResolver(String path) {
        this.path = path;
    }

    @Override
    File resolve() {
        return new File((String) path);
    }
}
