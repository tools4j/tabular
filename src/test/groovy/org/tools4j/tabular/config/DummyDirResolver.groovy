package org.tools4j.tabular.config

class DummyDirResolver implements DirResolver {
    private final String path;

    DummyDirResolver(String path) {
        this.path = path;
    }

    @Override
    Optional<File> resolve() {
        return Optional.of(new File((String) path));
    }
}
