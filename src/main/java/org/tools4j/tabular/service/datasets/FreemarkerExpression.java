package org.tools4j.tabular.service.datasets;

import freemarker.template.Template;
import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.service.Row;

import java.io.StringWriter;
import java.util.Map;

public class FreemarkerExpression implements Expression {
    private final Template template;
    private final PropertiesRepo propertiesRepo;
    
    public FreemarkerExpression(Template template, PropertiesRepo propertiesRepo) {
        this.template = template;
        this.propertiesRepo = propertiesRepo;
    }

    @Override
    public String resolve(Row row) {
        StringWriter sw = new StringWriter();
        Map<String, String> allProperties = new PropertiesRepo(propertiesRepo).asMap();
        allProperties.putAll(row);
        try {
            template.process(allProperties, sw);
            return sw.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Exception processing template [" + template + "]", e);    
        }
    }
}
