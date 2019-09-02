package org.tools4j.tabular.javafx

import spock.lang.Specification

class MainTest extends Specification {
    def "ResolveWorkingDir - default to working dir"() {
        expect:
        assert Main.resolveWorkingDir() == System.getProperty(Main.WORKING_DIR_SYS_PROP);
    }

    def "ResolveWorkingDir - system property defined, with valid path"() {
        given:
        //Find a directory that exists, that we can inject into system property
        File testDir = new File(this.getClass().getResource("/table_with_multiple_commands").toURI());
        assert testDir.exists()

        when:
        System.setProperty(Main.TABULAR_CONFIG_DIR_SYS_PROP, testDir.absolutePath)

        then:
        assert Main.resolveWorkingDir() == testDir.absolutePath;
    }

    def "ResolveWorkingDir - system property defined, with path that does not exist"() {
        given:
        //Find a directory that exists, that we can inject into system property
        File testDir = new File(new File(this.getClass().getResource("/table_with_multiple_commands").toURI()).canonicalPath + "/blah-de-blah");
        assert !testDir.exists()
        System.setProperty(Main.TABULAR_CONFIG_DIR_SYS_PROP, testDir.absolutePath)

        when:
        String exceptionMsg = null
        try {
            Main.resolveWorkingDir()
        } catch(Exception e){
            exceptionMsg = e.getMessage()
        }

        then:
        assert exceptionMsg == "Could not find config directory at [${testDir.absolutePath}] defined by system property tabular.config.dir"
    }
}
