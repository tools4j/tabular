package org.tools4j.tabular.service.commands;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.log4j.Logger;

/**
 * User: ben
 * Date: 8/11/17
 * Time: 5:14 PM
 */
public class GroovyExpression<T> {
    private final static Logger LOG = Logger.getLogger(GroovyExpression.class);
    private final String groovyExpression;
    private final Class<?> expectedReturnType;

    public GroovyExpression(final String groovyExpression, Class<T> expectedReturnType) {
        this.groovyExpression = groovyExpression;
        this.expectedReturnType = expectedReturnType;
    }

    public T resolveExpression() {
        final Binding sharedData = new Binding();
        final GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), sharedData);
        final Object evaluatedResult;
        try {
            evaluatedResult = shell.evaluate(groovyExpression);
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating script: '" + groovyExpression + "'", e);
        }
        if(!(expectedReturnType.isAssignableFrom(evaluatedResult.getClass()))){
            throw new RuntimeException("Expression does not evaluate to a " + expectedReturnType.getSimpleName() + ". Expression:'" + groovyExpression);
        }
        return (T) evaluatedResult;
    }
}
