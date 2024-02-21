package org.tools4j.tabular.config;

import java.io.File;
import java.util.Optional;

public class UserDirResolver implements DirResolver {
  @Override
    public Optional<File> resolve() {
      File userDirPlusTabular = new File(new File(System.getProperty("user.home")), "tabular");
      if(userDirPlusTabular.exists()) {
          return Optional.of(userDirPlusTabular.getAbsoluteFile());
      } else {
          return Optional.empty();
      }
    }
}
