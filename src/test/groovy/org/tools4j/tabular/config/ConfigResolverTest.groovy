package org.tools4j.tabular.config


import org.tools4j.tabular.properties.PropertiesRepo
import org.tools4j.tabular.util.TabularDirAndFileResolver
import spock.lang.Specification

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
        TabularDirAndFileResolver fileResolver = new TabularDirAndFileResolver(
            propertiesRepo,
            new DummyDirResolver(workingDir),
            new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),   
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
        TabularDirAndFileResolver fileResolver = new TabularDirAndFileResolver(
            propertiesRepo,
            new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
            new DummyDirResolver(userDir),
            new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST))
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
        propertiesRepo.put(TabularProperties.CONFIG_DIR, configDir)

        when:
        TabularDirAndFileResolver fileResolver = new TabularDirAndFileResolver(
            propertiesRepo,
            new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
            new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
            new ConfigDirResolver(propertiesRepo))
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
        propertiesRepo.put(TabularProperties.CONFIG_FILE_PATH, configFile.absolutePath)
        propertiesRepo.put(TabularProperties.LOCAL_CONFIG_FILE_PATH, localConfigFile.absolutePath)

        when:
        TabularDirAndFileResolver fileResolver = new TabularDirAndFileResolver(
            propertiesRepo,
            new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
            new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
            new ConfigDirResolver(propertiesRepo))
        PropertiesRepo config = new ConfigResolverFromConfigFiles(fileResolver).resolve();

        then:
        assert config.get('1.config.properties') == null
        assert config.get('1.config.local.properties') == 'fetched'
        assert config.get('2.config.properties') == null
        assert config.get('2.config.local.properties') == null
        assert config.get('1.non.default.named.config.properties') == 'fetched'
    }
}
