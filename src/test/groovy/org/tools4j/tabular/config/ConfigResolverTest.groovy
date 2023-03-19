package org.tools4j.tabular.config

import org.tools4j.tabular.datasets.DataSetResolver
import org.tools4j.tabular.properties.PropertiesRepo
import org.tools4j.tabular.util.FileResolver
import spock.lang.Specification

import java.nio.file.Files
import java.util.stream.Collectors

import static org.spockframework.util.Assert.fail

class ConfigResolverTest extends Specification {
    private static final String BASE_TEST_DIR = "src/test/resources/config-resolver"
    private static final String FILE_WHICH_DOES_NOT_EXIST = "src/test/resources/blah-blah-blah";

    def setup() {
        assert !(new File(FILE_WHICH_DOES_NOT_EXIST)).exists()
    }

    def "test resolution - simple working dir"() {
        given:
        PropertiesRepo propertiesRepo = new PropertiesRepo();
        String workingDir = "$BASE_TEST_DIR/1"

        when:
        FileResolver fileResolver = new FileResolver(
            propertiesRepo,
            new DummyDirResolver(workingDir),
            new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST)    
        )
        PropertiesRepo config = new ConfigResolverFromConfigFiles(fileResolver).resolve();

        then:
        assert config.get('1.config.properties') == 'fetched'
        assert config.get('1.config.local.properties') == 'fetched'
        assert config.get('2.config.properties') == null
        assert config.get('2.config.local.properties') == null
        
    }

    def "test resolution - user dir"() {
        given:
        PropertiesRepo propertiesRepo = new PropertiesRepo();
        String userDir = "$BASE_TEST_DIR/2"

        when:
        FileResolver fileResolver = new FileResolver(
            propertiesRepo,
            new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
            new DummyDirResolver(userDir)    
        )
        PropertiesRepo config = new ConfigResolverFromConfigFiles(fileResolver).resolve();

        then:
        assert config.get('1.config.properties') == null
        assert config.get('1.config.local.properties') == null
        assert config.get('2.config.properties') == 'fetched'
        assert config.get('2.config.local.properties') == 'fetched'
    }

    def "test resolution - config directory property"() {
        given:
        String configDir = "$BASE_TEST_DIR/2"
        PropertiesRepo propertiesRepo = new PropertiesRepo();
        propertiesRepo.put(FileResolver.TABULAR_CONFIG_DIR_PROP, configDir)

        when:
        FileResolver fileResolver = new FileResolver(
            propertiesRepo,
            new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
            new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST)    
        )
        PropertiesRepo config = new ConfigResolverFromConfigFiles(fileResolver).resolve();

        then:
        assert config.get('1.config.properties') == null
        assert config.get('1.local.config.properties') == null
        assert config.get('2.config.properties') == 'fetched'
        assert config.get('2.config.local.properties') == 'fetched'
    }

    def "test resolution - config paths properties"() {
        given:
        File configFile = new File("$BASE_TEST_DIR/1/non-default-named-config.properties");
        File localConfigFile = new File("$BASE_TEST_DIR/1/config-local.properties");

        PropertiesRepo propertiesRepo = new PropertiesRepo();
        propertiesRepo.put(ConfigResolverFromConfigFiles.TABULAR_CONFIG_FILE_PATH_PROP, configFile.absolutePath)
        propertiesRepo.put(ConfigResolverFromConfigFiles.TABULAR_LOCAL_CONFIG_FILE_PATH_PROP, localConfigFile.absolutePath)

        when:
        FileResolver fileResolver = new FileResolver(
            propertiesRepo,
            new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
            new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST)    
        )
        PropertiesRepo config = new ConfigResolverFromConfigFiles(fileResolver).resolve();

        then:
        assert config.get('1.config.properties') == null
        assert config.get('1.config.local.properties') == 'fetched'
        assert config.get('2.config.properties') == null
        assert config.get('2.config.local.properties') == null
        assert config.get('1.non.default.named.config.properties') == 'fetched'
        
    }
}
