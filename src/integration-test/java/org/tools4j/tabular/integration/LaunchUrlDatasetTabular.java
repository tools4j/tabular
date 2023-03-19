package org.tools4j.tabular.integration;

import java.io.File;

public class LaunchUrlDatasetTabular {
    public static void main(String[] args) {
        File configDirFile = new File("src/integration-test/resources/table_which_fetches_table_from_url");
        String configDir = configDirFile.getAbsolutePath();
        System.out.println("Using config dir of: " + configDir);
        System.setProperty("tabular_config_dir", configDir);
        org.tools4j.tabular.javafx.Main.main(new String[]{});
    }
}
