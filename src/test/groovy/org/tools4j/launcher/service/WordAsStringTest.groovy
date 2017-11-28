package org.tools4j.launcher.service

import spock.lang.Specification

/**
 * User: ben
 * Date: 3/11/17
 * Time: 5:44 PM
 */
class WordAsStringTest extends Specification {
    def "GetWord"() {
        when:
        final WordAsString wordAsString = new WordAsString(str);

        then:
        assert wordAsString.getWord().getParts() == new PartsFromStrings(parts).getParts()

        where:
        str                     | parts
        "hi"                    | ["hi"]
        "HiThere"               | ["Hi", "There"]
        "HiThereMr"             | ["Hi", "There", "Mr"]
        "HiThere009"            | ["Hi", "There", "009"]
        "Hi.there"              | ["Hi", "there"]
        "Hi.there.MrBozo"       | ["Hi", "there", "Mr", "Bozo"]
    }
}
