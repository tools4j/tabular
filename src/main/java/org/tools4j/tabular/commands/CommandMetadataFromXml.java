package org.tools4j.tabular.commands;

import static org.tools4j.tabular.config.TabularProperties.COMMAND_XML_PATH;
import static org.tools4j.tabular.config.TabularProperties.COMMAND_XML_URL;

import java.io.FileReader;
import java.io.Reader;
import java.util.Optional;
import lombok.SneakyThrows;
import org.apache.tools.ant.util.ReaderInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.tabular.config.TabularConstants;
import org.tools4j.tabular.properties.PropertiesRepo;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import org.tools4j.tabular.util.FileResolver;

public class CommandMetadataFromXml {
    private final static Logger LOG = LoggerFactory.getLogger(CommandMetadata.class);
    private final PropertiesRepo propertiesRepo;
    private final InputStream inputStream;

    public CommandMetadataFromXml(PropertiesRepo propertiesRepo, FileResolver fileResolver) {
        this(propertiesRepo, getAsInputStream(propertiesRepo, fileResolver));
    }

    private static InputStream getAsInputStream(PropertiesRepo propertiesRepo, FileResolver fileResolver) {
        Optional<Reader> optReader = resolveCommandsFile(propertiesRepo, fileResolver);
        return optReader.map(ReaderInputStream::new).orElse(null);
    }

    public CommandMetadataFromXml(PropertiesRepo propertiesRepo, String xml){
        this(propertiesRepo, new ByteArrayInputStream(xml.getBytes()));
    }

    public CommandMetadataFromXml(PropertiesRepo propertiesRepo, InputStream inputStream) {
        this.propertiesRepo = propertiesRepo;
        this.inputStream = inputStream;
    }

    @SneakyThrows
    private static Optional<Reader> resolveCommandsFile(PropertiesRepo propertiesRepo, FileResolver fileResolver) {
        if(propertiesRepo.hasKey(COMMAND_XML_PATH)){
            File xmlFile = new File(propertiesRepo.get(COMMAND_XML_PATH));
            if(xmlFile.exists() && xmlFile.isFile()){
                LOG.info("Resolved file at path: [" + xmlFile.getAbsolutePath() + "]");
                return Optional.of(new FileReader(xmlFile));
            } else {
                return Optional.empty();
            }
        } else {
            return fileResolver.resolveFile(
                COMMAND_XML_URL,
                COMMAND_XML_PATH,
                TabularConstants.TABULAR_COMMANDS_FILE_NAME_DEFAULT);
        }
    }

    public CommandMetadatas load(){
        if(inputStream == null) return CommandMetadatas.EMPTY;
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            CommandXmlHandler commandXmlHandler = new CommandXmlHandler(propertiesRepo);
            saxParser.parse(inputStream, commandXmlHandler);
            return commandXmlHandler.getCommands();
        } catch (Exception e) {
            throw new IllegalStateException("Error parsing xml from input stream [" + inputStream + "]", e);
        }
    }
}
