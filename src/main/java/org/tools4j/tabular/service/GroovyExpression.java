package org.tools4j.tabular.service;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.log4j.Logger;
import org.tools4j.tabular.util.PropertiesRepo;
import org.tools4j.tabular.util.ResolvedString;

/**
 * User: ben
 * Date: 8/11/17
 * Time: 5:14 PM
 */
public class GroovyExpression {
    private final static Logger LOG = Logger.getLogger(GroovyExpression.class);
    private final String groovyExpression;
    private final PropertiesRepo propertiesRepo;
    private final Class<?> expectedReturnType;

    public GroovyExpression(final String groovyExpression, final PropertiesRepo propertiesRepo, Class<?> expectedReturnType) {
        this.propertiesRepo = propertiesRepo;
        this.groovyExpression = groovyExpression;
        this.expectedReturnType = expectedReturnType;
    }

    public Object resolveExpression(Row row) {
        final Binding sharedData = new Binding();
        final String groovyScriptWithResolvedVariables = new ResolvedString(groovyExpression, propertiesRepo.asMap(), row).resolve();

        final GroovyShell shell = new GroovyShell(sharedData);
        final Object evaluatedResult;
        try {
            evaluatedResult = shell.evaluate(groovyScriptWithResolvedVariables);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating script: '" + groovyScriptWithResolvedVariables + "'");
        }
        if(!(expectedReturnType.isAssignableFrom(evaluatedResult.getClass()))){
            throw new RuntimeException("Expression does not evaluate to a " + expectedReturnType.getSimpleName() + ". Expression:'" + groovyScriptWithResolvedVariables + "' for Row:" + row);
        }
        return evaluatedResult;
    }
}
