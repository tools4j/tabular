package org.tools4j.tabular.service.commands

import org.tools4j.tabular.properties.PropertiesRepo
import org.tools4j.tabular.service.Row
import org.tools4j.tabular.service.RowFromMap
import org.tools4j.tabular.util.Constants
import spock.lang.Specification

import java.util.function.Predicate

class CommandMetadataFromXmlTest extends Specification {
    def "Load"() {
        given:
        PropertiesRepo propertiesRepo = new PropertiesRepo();
        propertiesRepo.put(Constants.COMMAND_XML_FILE, "src/test/resources/table_with_xml_config/commands.xml")
        CommandMetadataFromXml commandMetadataFromXml = new CommandMetadataFromXml(propertiesRepo);
        CommandMetadatas commands = commandMetadataFromXml.load();
        
        when:
        def commandsStr = commands.toPrettyString("    ")
        
        then:
        assert commandsStr == '''
        commandMetadatas{
            CommandMetadata{id='list_files', name='list files', predicate=GroovyExpressionPredicate{groovyExpressionStr='true'}, command='ls', description='list files in current dir'}
            CommandMetadata{id='hello_world', name='hello world', predicate=Or{children=[Equals{colName='Item', colValue='Skirt'}, Not{child=Matches{colName='Item', regex='.*?Jacket'}}, And{children=[GreaterThanValue{colName='price', value=18.0}, LessThanValue{colName='price', value=21.0}]}]}, command='echo Hello World!', description='prints out hello world'}
            CommandMetadata{id='cheap_stuff', name='cheap stuff', predicate=Not{child=GreaterThanOrEqualToValue{colName='price', value=6.0}}, command='echo Cheap Stuff!', description='prints out Cheap Stuff!'}
            CommandMetadata{id='kinda_cheap_stuff', name='kinda_cheap_stuff', predicate=And{children=[GreaterThanOrEqualToValue{colName='price', value=6.0}, LessThanOrEqualToValue{colName='price', value=15.0}]}, command='echo Kinda cheap stuff!', description='prints out Kinda cheap stuff!'}
        }'''.replaceFirst('\n','').stripIndent()  
    }

    def "Load from xml string"() {
        given:
        PropertiesRepo propertiesRepo = new PropertiesRepo();
        String xml = '''
            <commands>
                <command id="list_files" name="list files" description="list files in current dir" command_line="ls"
                         condition_type="groovy" groovy_expression="true"/>
                <command id="hello_world" name="hello world" description="prints out hello world"
                         command_line="echo 'Hello World!'">
                    <condition>
                        <or>
                            <equals col_name="Item" value="Skirt"/>
                            <not>
                                <matches col_name="Item" regex=".*?Jacket"/>
                            </not>
                            <and>
                                <greater_than col_name="price" value="18"/>
                                <less_than col_name="price" value="21"/>
                            </and>
                        </or>
                    </condition>
                </command>
            </commands>'''.replaceFirst('\n','').stripIndent();
        
        CommandMetadataFromXml commandMetadataFromXml = new CommandMetadataFromXml(propertiesRepo, xml);
        
        when:
        CommandMetadatas commands = commandMetadataFromXml.load();
        
        then:
        assert commands.size() == 2
        assert commands.get('list_files') != null
        CommandMetadata command1 = commands.get('list_files')
        assert command1.getId() == 'list_files'
        assert command1.getName() == 'list files'
        assert command1.getCommand() == 'ls'
        assert command1.getDescription() == 'list files in current dir'
        Predicate<Row> rootPredicate1 = command1.getPredicate()
        assert rootPredicate1 instanceof GroovyExpressionPredicate
        
        then:
        CommandMetadata command2 = commands.get('hello_world')
        assert command2.getId() == 'hello_world'
        assert command2.getName() == 'hello world'
        assert command2.getCommand() == 'echo \'Hello World!\''
        assert command2.getDescription() == 'prints out hello world'
        Predicate<Row> rootPredicate2 = command2.getPredicate()
        assert rootPredicate2 instanceof LogicPredicate.Or
        
        LogicPredicate.Equals equalsPredicate = (LogicPredicate.Equals) rootPredicate2.getChild(0)
        assert equalsPredicate.test(new RowFromMap(["Item": "Skirt"]))
        
        '''<or>
            <equals col_name="Item" value="Skirt"/>
            <not>
                <matches col_name="Item" regex=".*?Jacket"/>
            </not>
            <and>
                <greater_than col_name="price" value="18"/>
                <less_than col_name="price" value="21"/>
            </and>
        </or>'''
    }
}
