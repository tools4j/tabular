package org.tools4j.tabular.commands;

import org.tools4j.tabular.properties.PropertiesRepo;
import org.tools4j.tabular.util.Constants;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;

public class CommandMetadataFromXml {
    private final PropertiesRepo propertiesRepo;
    private final InputStream inputStream;

    public CommandMetadataFromXml(PropertiesRepo propertiesRepo) {
        this(propertiesRepo, loadFile(propertiesRepo));
    }
    
    public CommandMetadataFromXml(PropertiesRepo propertiesRepo, String xml){
        this(propertiesRepo, new ByteArrayInputStream(xml.getBytes()));
    }

    private static InputStream loadFile(PropertiesRepo propertiesRepo) {
        String commandXmlFilePath = propertiesRepo.get(Constants.COMMAND_XML_FILE, null);
        if(commandXmlFilePath == null){
            throw new IllegalStateException("Property must be specified with path to xml file '" + Constants.COMMAND_XML_FILE + "'");
        }
        try {
            return new FileInputStream(new File(commandXmlFilePath));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("File not found exception [" + commandXmlFilePath + "]", e);
        }
    }

    public CommandMetadataFromXml(PropertiesRepo propertiesRepo, InputStream inputStream) {
        this.propertiesRepo = propertiesRepo;
        this.inputStream = inputStream;
    }

    public CommandMetadatas load(){
        if(inputStream == null) return new CommandMetadatas(Collections.emptyList());
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
