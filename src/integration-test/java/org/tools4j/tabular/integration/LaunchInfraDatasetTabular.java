package org.tools4j.tabular.integration;

import java.io.File;

public class LaunchInfraDatasetTabular {
    public static void main(String[] args) {
        File configDirFile = new File("src/integration-test/resources/table_infra");
        String configDir = configDirFile.getAbsolutePath();
        System.out.println("Using config dir of: " + configDir);
        System.setProperty("tabular_config_dir", configDir);
        org.tools4j.tabular.javafx.Main.main(new String[]{});
    }
}
