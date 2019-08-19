package org.tools4j.tabular.service

import org.tools4j.tabular.util.PropertiesRepo
import org.tools4j.tabular.util.PropertiesRepoWithAdditionalTweakedContantStyleProperties
import spock.lang.Specification

/**
 * User: ben
 * Date: 29/01/2018
 * Time: 5:29 PM
 */
class PropertiesRepoWithAdditionalTweakedContantStylePropertiesTest extends Specification {
    def "Load"() {
        given:
        final Map<String, String> map = new HashMap<>();
        map.put("blah", "Blah1");
        map.put("blah.blah", "Blah2");
        map.put("hello there", "Blah3");
        map.put("hello_there", "Blah4");
        map.put("HELLO_THERE", "Blah5"); //will be repeated as 'hello.there'
        map.put("HELLO", "Blah6");       //will be repeated as 'hello'
        map.put("HI_THERE_MR", "Blah7"); //will be repeated as 'hello.there.mr'
        map.put("HI_THERE.MR", "Blah8"); //will NOT be repeated as contains a dot '.'
        map.put("_HI_THERE_MR", "Blah9"); //will NOT be repeated as starts with a '_'

        when:
        final Map<String, String> augmentedProperties = new PropertiesRepoWithAdditionalTweakedContantStyleProperties(new PropertiesRepo(map)).load().asMap();

        then:
        assert augmentedProperties.size() == 12
        assert augmentedProperties["blah"] == "Blah1"
        assert augmentedProperties["blah.blah"] == "Blah2"
        assert augmentedProperties["hello there"] == "Blah3"
        assert augmentedProperties["hello_there"] == "Blah4"
        assert augmentedProperties["HELLO_THERE"] == "Blah5"
        assert augmentedProperties["hello.there"] == "Blah5"
        assert augmentedProperties["HELLO"] == "Blah6"
        assert augmentedProperties["hello"] == "Blah6"
        assert augmentedProperties["HI_THERE_MR"] == "Blah7"
        assert augmentedProperties["hi.there.mr"] == "Blah7"
        assert augmentedProperties["HI_THERE.MR"] == "Blah8"
        assert augmentedProperties["_HI_THERE_MR"] == "Blah9"
    }
}
