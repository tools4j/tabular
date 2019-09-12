package org.tools4j.tabular.service

import org.tools4j.tabular.properties.PropertiesFromString
import org.tools4j.tabular.properties.PropertiesRepo
import spock.lang.Specification

/**
 * User: ben
 * Date: 8/11/17
 * Time: 5:28 PM
 */
class GroovyExpressionPredicateTest extends Specification {
    private PropertiesRepo propertiesRepo
    private Row row;

    def setup(){
        propertiesRepo = new PropertiesRepo(new PropertiesFromString('''
            my.property.1=blahhostname
            my.property.2=myhostname
            my.property.3=${h}
            my.property.4=${my.property.3}
            my.command='${h}'.contains('host') && 64 > 32
        ''').load());

        row = new RowFromMap([h: "myhostname", e: "prod", d: "~/", l: "~/logs/"]);
    }

    def "test simple true"() {
        when:
        final GroovyExpressionPredicate predicate = new GroovyExpressionPredicate("true", propertiesRepo);

        then:
        assert predicate.test(row)
    }

    def "test simple false"() {
        when:
        final GroovyExpressionPredicate predicate = new GroovyExpressionPredicate("false", propertiesRepo);

        then:
        assert !predicate.test(row)
    }

    def "test equality"() {
        when:
        final GroovyExpressionPredicate predicate = new GroovyExpressionPredicate(
                '\'${e}\' == \'prod\'', propertiesRepo);
        then:
        assert predicate.test(row)
    }

    def "test inequality"() {
        when:
        final GroovyExpressionPredicate predicate = new GroovyExpressionPredicate(
                '\'${e}\' == \'uat\'', propertiesRepo);
        then:
        assert !predicate.test(row)
    }


    def "test using value from properties 1"() {
        when:
        final GroovyExpressionPredicate predicate = new GroovyExpressionPredicate(
                '\'${h}\' == \'${my.property.1}\'', propertiesRepo);
        then:
        assert !predicate.test(row)
    }

    def "test using value from properties 2"() {
        when:
        final GroovyExpressionPredicate predicate = new GroovyExpressionPredicate(
                '\'${h}\' == \'${my.property.2}\'', propertiesRepo);
        then:
        assert predicate.test(row)
    }

    def "test using value from properties 3"() {
        when:
        final GroovyExpressionPredicate predicate = new GroovyExpressionPredicate(
                '\'${h}\' == \'${my.property.3}\'', propertiesRepo);
        then:
        assert predicate.test(row)
    }

    def "test using value from properties 4"() {
        when:
        final GroovyExpressionPredicate predicate = new GroovyExpressionPredicate(
                '\'${h}\' == \'${my.property.4}\'', propertiesRepo);
        then:
        assert predicate.test(row)
    }

    def "test using command from properties"() {
        when:
        final GroovyExpressionPredicate predicate = new GroovyExpressionPredicate(
                propertiesRepo.get("my.command"), propertiesRepo);
        then:
        assert predicate.test(row)
    }
}
