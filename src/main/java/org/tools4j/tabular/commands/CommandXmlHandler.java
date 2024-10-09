package org.tools4j.tabular.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.tools4j.tabular.datasets.Row;
import org.tools4j.tabular.properties.PropertiesRepo;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CommandXmlHandler extends DefaultHandler {
    private static final Predicate<Row> ALWAYS = row -> true;
    private final PropertiesRepo propertiesRepo;
    private final LogicPredicateParser logicPredicateParser;
    private List<CommandMetadata> commands = new ArrayList<>();
    private CommandMetadata.Builder commandMetadataBuilder = new CommandMetadata.Builder();
    private boolean insideConditionElement = false;
    private String conditionType;

    public CommandXmlHandler(PropertiesRepo propertiesRepo) {
        this.propertiesRepo = propertiesRepo;
        logicPredicateParser = new LogicPredicateParser();
    }

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
        switch (qName) {
            case "command":
                commandMetadataBuilder.withId(getMandatoryStringAttr(qName, attr, "id"));
                commandMetadataBuilder.withName(getMandatoryStringAttr(qName, attr, "name"));
                commandMetadataBuilder.withCommand(getMandatoryStringAttr(qName, attr, "command_line"));
                commandMetadataBuilder.withDescription(getMandatoryStringAttr(qName, attr, "description"));
                if(attr.getValue("condition_type") == null){
                    commandMetadataBuilder.withPredicate(ALWAYS);
                } else {
                    conditionType = attr.getValue("condition_type");
                    if (conditionType.equalsIgnoreCase("groovy")) {
                        commandMetadataBuilder.withPredicate(new GroovyExpressionPredicate(getMandatoryStringAttr(qName, attr, "groovy_expression"), propertiesRepo));
                    } else if (conditionType.equalsIgnoreCase("logic")) {
                        //no-op, we will wait for 'condition' element
                    } else {
                        throw new IllegalArgumentException("Illegal condition_type value [" + conditionType + "]");
                    }
                }
                break;
            case "condition":
                insideConditionElement = true;
                break;
            default:
                if(insideConditionElement){
                    logicPredicateParser.handleStartElement(qName, attr);
                }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case "command":
                commands.add(commandMetadataBuilder.build());
                commandMetadataBuilder = new CommandMetadata.Builder();
                conditionType = null;
                break;
            case "condition":
                insideConditionElement = false;
                commandMetadataBuilder.withPredicate(logicPredicateParser.get());
                logicPredicateParser.clear();
                break;
            default:
                if(insideConditionElement){
                    logicPredicateParser.handleEndElement(qName);
                }    
        }
    }

    public static String getMandatoryStringAttr(String qName, Attributes attr, String attributeName) {
        String attributeValue = attr.getValue(attributeName);
        if(attributeValue == null){
            throw new IllegalStateException("Could not find expected attribute [" + attributeName + "] in element with name [" + qName + "], and attributes: " + toString(attr));
        } 
        return attributeValue;
    }

    public static double getMandatoryDoubleAttr(String qName, Attributes attr, String attributeName) {
        String attrAsString = getMandatoryStringAttr(qName, attr, attributeName);
        try {
            return Double.parseDouble(attrAsString);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Could not convert attribute [" + attributeName + "] with value [" + attrAsString  + "] in element with name [" + qName + "], and attributes: " + toString(attr));
        }
    }

    private static String toString(Attributes attr) {
        StringBuilder sb = new StringBuilder("[");
        for(int i=0; i<attr.getLength(); i++){
            sb.append(attr.getQName(i)).append("=\"").append(attr.getValue(i)).append("\"");
            if(i<(attr.getLength()-1)){
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public CommandMetadatas getCommands() {
        return new CommandMetadatas(commands);
    }
}