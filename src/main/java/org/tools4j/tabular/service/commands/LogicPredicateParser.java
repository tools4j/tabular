package org.tools4j.tabular.service.commands;

import org.tools4j.tabular.service.Row;
import org.tools4j.tabular.service.commands.LogicPredicate.LogicParent;
import org.xml.sax.Attributes;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class LogicPredicateParser {
    private LogicPredicate rootPredicate = null;   
    private final LinkedList<LogicPredicate> predicateStack;
    private final Map<String, BiFunction<String, Attributes, LogicPredicate>> registeredLogicTypes;
    
    public LogicPredicateParser() {
        predicateStack = new LinkedList<>();
        registeredLogicTypes = new LinkedHashMap<>();
        
        //LogicPredicates
        registeredLogicTypes.put("equals", (qName, attr) -> new LogicPredicate.Equals(qName, attr));
        registeredLogicTypes.put("matches", (qName, attr) -> new LogicPredicate.Matches(qName, attr));
        registeredLogicTypes.put("greater_than_or_equal_to", (qName, attr) -> new LogicPredicate.GreaterThanOrEqualToValue(qName, attr));
        registeredLogicTypes.put("less_than_or_equal_to", (qName, attr) -> new LogicPredicate.LessThanOrEqualToValue(qName, attr));
        registeredLogicTypes.put("greater_than", (qName, attr) -> new LogicPredicate.GreaterThanValue(qName, attr));
        registeredLogicTypes.put("less_than", (qName, attr) -> new LogicPredicate.LessThanValue(qName, attr));
        
        //LogicPredicateParents
        registeredLogicTypes.put("and", (qName, attr) -> new LogicPredicate.And());
        registeredLogicTypes.put("or", (qName, attr) -> new LogicPredicate.Or());
        registeredLogicTypes.put("not", (qName, attr) -> new LogicPredicate.Not());
    }

    public void handleStartElement(String name, Attributes attr) {
        if(!registeredLogicTypes.containsKey(name)){
            throw new IllegalStateException("Unknown logic tag [" + name + "]");
        } else {
            LogicPredicate logicPredicate = registeredLogicTypes.get(name).apply(name, attr);
            if(rootPredicate == null){
                rootPredicate = logicPredicate;
            }
            if(!predicateStack.isEmpty()){
                if(predicateStack.peekFirst() instanceof LogicParent){
                    ((LogicParent) predicateStack.peekFirst()).addChild(logicPredicate);
                } else {
                    throw new IllegalArgumentException("Need to end the last non-parent predicate [" + predicateStack.peekFirst() + "] " +
                            "before starting a new predicate [" + logicPredicate + "]");
                }
            }
            predicateStack.push(logicPredicate);
        }
    }

    public void handleEndElement(String name) {
        if(!registeredLogicTypes.containsKey(name)){
            throw new IllegalStateException("Unknown logic tag [" + name + "]");
        } else {
            predicateStack.pop();
        }
    }

    public Predicate<Row> get() {
        return rootPredicate;
    }

    public void clear() {
        predicateStack.clear();
        rootPredicate = null;
    }
}
