package org.tools4j.tabular.properties

import spock.lang.Specification

class StringResolverTest extends Specification {
    private StringResolver stringResolver;
    
    def setup(){
        Map<String, String>[] otherResources = new Map<String, String>[2];
        
        Map<String,String> map = new HashMap<>();
        map.put('connection.host', 'haud0001');
        map.put('connection.username', 'me');
        map.put('conn', 'connection')
        map.put('prt', 'port')
        otherResources[0] = map;
        
        map = new HashMap<>();
        map.put('connection.port', '8080');
        map.put('connection.password', 'secret');
        otherResources[1] = map;
        
        stringResolver = new StringResolver(otherResources);
    }

    
    def "Resolve"(String unresolvedString, String expectedResolvedString) {
        given:
        assert stringResolver.resolve(unresolvedString)
        
        where:
        unresolvedString    | expectedResolvedString
        'hello'             | 'hello'
        '${${conn}.${prt}}' | '8080'
    }
}
