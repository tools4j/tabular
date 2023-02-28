package org.tools4j.tabular.integration;

import java.io.File;

public class LaunchLargeDatasetTabular {
    public static void main(String[] args) {
        File configDirFile = new File("src/integration-test/resources/large_dataset_test");
        String configDir = configDirFile.getAbsolutePath();
        System.out.println("Using config dir of: " + configDir);
        System.setProperty("tabular_config_dir", configDir);
        org.tools4j.tabular.javafx.Main.main(new String[]{});
    }
}
