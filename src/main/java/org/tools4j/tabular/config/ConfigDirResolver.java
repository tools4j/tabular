package org.tools4j.tabular.config;

import static org.tools4j.tabular.config.TabularProperties.CONFIG_DIR;
import static org.tools4j.tabular.util.TabularDirAndFileResolver.doesDirExist;

import java.io.File;
import java.util.Optional;
import org.tools4j.tabular.properties.PropertiesRepo;

public class ConfigDirResolver implements DirResolver {
  private final PropertiesRepo propertiesRepo;

  public ConfigDirResolver(PropertiesRepo propertiesRepo) {
    this.propertiesRepo = propertiesRepo;
  }
  
  @Override
  public Optional<File> resolve() {
    if (!propertiesRepo.hasKey(CONFIG_DIR)) {
      return Optional.empty();
    }
    String dirPath = propertiesRepo.get(CONFIG_DIR);

    File dir = new File(dirPath);
    if (!doesDirExist(dir)) {
      throw new IllegalArgumentException(
          "Dir specified by property [" + CONFIG_DIR + "] value [" +
              dir.getAbsolutePath() + "] does not exist, or is not a directory.");
    }

    return Optional.of(dir.getAbsoluteFile());
  }
}
