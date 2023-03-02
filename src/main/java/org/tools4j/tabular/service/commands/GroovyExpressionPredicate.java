package org.tools4j.tabular.service.commands;

import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.properties.StringResolver;
import org.tools4j.tabular.service.Row;

import java.util.function.Predicate;

/**
 * User: ben
 * Date: 8/11/17
 * Time: 5:14 PM
 */
public class GroovyExpressionPredicate implements Predicate<Row> {
    private final String groovyExpressionStr;
    private final PropertiesRepo propertiesRepo;

    public GroovyExpressionPredicate(final String groovyExpressionStr, final PropertiesRepo propertiesRepo) {
        this.propertiesRepo = propertiesRepo;
        this.groovyExpressionStr = groovyExpressionStr;
    }

    @Override
    public boolean test(Row row) {
        //Test for simple true, or empty string
        if(groovyExpressionStr.trim().equalsIgnoreCase("true") || groovyExpressionStr.trim().length() == 0){
            return true;
        }
        final String groovyScriptWithResolvedVariables = new StringResolver(propertiesRepo.asMap(), row).resolve(groovyExpressionStr);
        return new GroovyExpression<>(groovyScriptWithResolvedVariables, Boolean.class).resolveExpression();
    }

    @Override
    public String toString() {
        return "GroovyExpressionPredicate{" +
                "groovyExpressionStr='" + groovyExpressionStr + '\'' +
                '}';
    }
}
