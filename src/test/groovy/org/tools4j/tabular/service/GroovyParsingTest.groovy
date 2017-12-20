package org.tools4j.tabular.service

import spock.lang.Specification

/**
 * User: ben
 * Date: 3/11/17
 * Time: 6:59 AM
 */
class GroovyParsingTest extends Specification {
    def "Test Simple Parsing"() {
        given:
        final Binding sharedData = new Binding()
        final GroovyShell shell = new GroovyShell(sharedData)
        sharedData.setProperty("e", "prod")
        assert shell.evaluate("['prod', 'demo'].contains(e)")

        sharedData.setProperty("e", "uat")
        assert !shell.evaluate("['prod', 'demo'].contains(e)")
    }
}
