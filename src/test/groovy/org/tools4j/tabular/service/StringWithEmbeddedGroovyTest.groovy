package org.tools4j.tabular.service

import org.tools4j.tabular.service.commands.StringWithEmbeddedGroovy
import spock.lang.Specification

class StringWithEmbeddedGroovyTest extends Specification {
    def "Resolve simple"(String expression, String expectedResult) {
        when:
        StringWithEmbeddedGroovy str = new StringWithEmbeddedGroovy(expression)

        then:
        assert str.resolve() == expectedResult

        where:
        expression                                      | expectedResult
        'Hello {{(1+2).toString()}}!'                   | 'Hello 3!'
        '{{"BLAH".toLowerCase()}}-{{(1+2).toString()}}' | 'blah-3'
        '{{"BLAH".toLowerCase()}}{{(1+2).toString()}}'  | 'blah3'
        '{{"BLAH".toLowerCase()}}{{(1+2).toString()}}'  | 'blah3'
        'Date minus 1 day: {{java.time.LocalDate.of(2019, 7, 27).minusDays(1).toString()}}' | 'Date minus 1 day: 2019-07-26'
    }
}
