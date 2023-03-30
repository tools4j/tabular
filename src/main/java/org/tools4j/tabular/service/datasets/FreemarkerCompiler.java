package org.tools4j.tabular.service.datasets;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.tools4j.tabular.properties.PropertiesRepo;

import java.io.IOException;
import java.io.StringReader;

public class FreemarkerCompiler implements ExpressionCompiler {
    private final Configuration configuration;
    private final PropertiesRepo propertiesRepo;
    
    public FreemarkerCompiler(PropertiesRepo propertiesRepo) {
        this.propertiesRepo = propertiesRepo;
        this.configuration = createConfiguration();
    }

    @Override
    public Expression compile(String expression) {
        try {
            Template t = new Template("col", new StringReader(expression), configuration);
            return new FreemarkerExpression(t, propertiesRepo);
        } catch (IOException e) {
            throw new IllegalStateException("Exception creating freemarker template from expression [" + expression + "]", e);
        }
    }

    private Configuration createConfiguration()  {
        // Create your Configuration instance, and specify if up to what FreeMarker
        // version (here 2.3.32) do you want to apply the fixes that are not 100%
        // backward-compatible. See the Configuration JavaDoc for details.
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);

        // From here we will set the settings recommended for new projects. These
        // aren't the defaults for backward compatibilty.

        // Set the preferred charset template files are stored in. UTF-8 is
        // a good choice in most applications:
        cfg.setDefaultEncoding("UTF-8");

        // Sets how errors will appear.
        // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
        cfg.setLogTemplateExceptions(false);

        // Wrap unchecked exceptions thrown during template processing into TemplateException-s:
        cfg.setWrapUncheckedExceptions(true);

        // Do not fall back to higher scopes when reading a null loop variable:
        cfg.setFallbackOnNullLoopVariable(false);
        return cfg;
    }
}
