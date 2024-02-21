package org.tools4j.tabular;

public class CsvFileCreator {
    public static void main(String[] args) {
        String[] apps = new String[] {"http", "algo", "fix-gateway", "oms"};
        String[] envs = new String[] {"dev", "pre", "prod"};
        String[] dcs = new String[] {"prim", "dr"};

        int counter = 0;
        System.out.println("id,app,instance,host,env,dc");
        for (String env : envs) {
            for (String dc : dcs) {
                for (String app : apps) {
                    for(int i=1; i<3; i++) {
                        String str = ++counter + ",";
                        str += app + ",";
                        str += app + "_" + i + ",";
                        str += app + "-" + i + "-" + dc + "-" + env + ".m" + ",";
                        str += env + ",";
                        str += dc;
                        System.out.println(str);
                    }    
                }
            }
        }
    }
}
