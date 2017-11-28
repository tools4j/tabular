package org.tools4j.launcher.service

import spock.lang.Specification

/**
 * User: ben
 * Date: 30/10/17
 * Time: 7:04 AM
 */
class ResolvedDataSetTest extends Specification {
    def "test resolved dataset"(){
        when:
        final String configDir = "src/test/resources/test3"
        final SharedConfig sharedConfig = new SharedConfigFromDir(configDir).load();
        final DataSetContext dataSetContext = new DataSetContextFromDir(configDir, 'blah', sharedConfig.asPropertiesRepo()).load();

        then:
        println dataSetContext.toPrettyString();

    }
}
