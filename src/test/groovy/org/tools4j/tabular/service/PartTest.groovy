package org.tools4j.tabular.service

import spock.lang.Specification

/**
 * User: ben
 * Date: 3/11/17
 * Time: 6:59 AM
 */
class PartTest extends Specification {
    def "GetCombinations"() {
        when:
        final Part part = new Part("Hithere");

        then:
        assert part.getCombinations() == new PartsFromStrings("H", "Hi", "Hit", "Hith", "Hithe", "Hither", "Hithere").getParts()
    }

    def "GetCombinations 1 part"() {
        when:
        final Part part = new Part("H");

        then:
        assert part.getCombinations() == new PartsFromStrings("H").getParts()
    }

    def "GetCombinations 0 parts"() {
        when:
        final Part part = new Part("");

        then:
        assert part.getCombinations() == Collections.emptyList()
    }
}
