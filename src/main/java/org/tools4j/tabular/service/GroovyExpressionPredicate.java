package org.tools4j.tabular.service;

import org.apache.log4j.Logger;
import org.tools4j.tabular.util.PropertiesRepo;
import org.tools4j.tabular.util.ResolvedString;

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
        final String groovyScriptWithResolvedVariables = new ResolvedString(groovyExpressionStr, propertiesRepo.asMap(), row).resolve();
        final Object evaluatedResult = new GroovyExpression(groovyScriptWithResolvedVariables, Boolean.class).resolveExpression();
        return (Boolean) evaluatedResult;
    }
}
