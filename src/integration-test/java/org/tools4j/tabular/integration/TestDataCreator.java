package org.tools4j.tabular.integration;

public class TestDataCreator {
    private static final String[] APPS = new String[]{"http_server", "proxy_server", "app_server", "kafka", "postgres_db"};
    private static final String[] REGIONS = new String[]{"emea", "apac", "amrs"};
    private static final String[] ENVS = new String[]{"dev", "qa", "uat", "perf", "beta", "prod"};
    private static final String[] DATA_CENTRES = new String[]{"dc1", "dc2", "dc3", "dc4", "dc5", "dc6"};
    private static final String[] INSTANCES = new String[]{"1", "2", "3", "4"};
        
    public static void main(String[] args) {
        createTestCsv();
    }

    private static void createPropertiesTestConfig() {
        for (int i = 0; i < 1000; i++) {
            System.out.println("app.commmands.command" + i + ".name=Command " + i);
            System.out.println("app.commmands.command" + i + ".predicate='${env}' != 'prod'");
            System.out.println("app.commmands.command" + i + ".command=/path/to/my/command ${env}");
            System.out.println("app.commmands.command" + i + ".description=Runs command " + i);
            System.out.println("");
        }
    }

    private static void createXmlTestConfig() {
        System.out.println("<commands>");
        for (int i = 0; i < 200; i++) {
            System.out.println("  <command " +
                    "id='command" + i + "' " +
                    "name='Command " + i + "' " +
                    "description='Runs command " + i + "' " +
                    "command_line='/path/to/my/command" + i + "${env}'>");
            System.out.println("    <condition>");        
            System.out.println("      <not>");        
            System.out.println("        <equals col_name='env' value='prod'/>");
            System.out.println("      </not>");        
            System.out.println("    </condition>");        
            System.out.println("  </command>");
        }
        System.out.println("</commands>");
    }
    
    private static void createTestCsv() {
        System.out.println("id,app,instance,host,region,env,dc");
        int i=0;
        for (String app : APPS) {
            for (String region : REGIONS) {
                for (String env : ENVS) {
                    for (String dataCentre : DATA_CENTRES) {
                        for (String instance : INSTANCES) {
                            System.out.print(i++ + ",");
                            System.out.print(app + ",");
                            System.out.print(app + "_" + instance + ",");
                            System.out.print(app + instance + "." + env + ".tabular.com,");
                            System.out.print(region + ",");
                            System.out.print(env + ",");
                            System.out.println(dataCentre);
                        }
                    }
                }
            }
        }
    }
}
