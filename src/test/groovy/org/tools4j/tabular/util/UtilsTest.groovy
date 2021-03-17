package org.tools4j.tabular.util


import spock.lang.Specification

/**
 * User: ben
 * Date: 25/10/17
 * Time: 6:13 AM
 */
class UtilsTest extends Specification {
    def "testGetLastWordInText"() {
        given:
        assert Utils.getLastWordInText(text) == expectedResult

        where:
        text         | expectedResult
        'hi there'   | Optional.of('there')
        'there'      | Optional.of('there')
        't'          | Optional.of('t')
        'there '     | Optional.empty()
        'blah b'     | Optional.of('b')
    }

    def "testReplaceLastWordWith"() {
        given:
        assert Utils.replaceLastWordWith(text, replacement) == expectedResult

        where:
        text         | replacement | expectedResult
        'blah b'     | 'asdf'      | 'blah asdf'
        'blah boo'   | ''          | 'blah '
    }

    def "testReplaceLastWordWith_textEndsWithASpace"() {
        when:
        Utils.replaceLastWordWith('boo ', 'blah')

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'Text does not match, probably because it ends with a space, or is empty [boo ]'
    }

    def "testReplaceLastWordWith_textIsEmpty"() {
        when:
        Utils.replaceLastWordWith('', 'blah')

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'Text does not match, probably because it ends with a space, or is empty []'
    }
}
