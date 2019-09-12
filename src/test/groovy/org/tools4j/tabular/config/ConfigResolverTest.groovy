package org.tools4j.tabular.config


import org.tools4j.tabular.properties.PropertiesRepo
import org.tools4j.tabular.service.MapBackedSysPropOrEnvVarResolver
import spock.lang.Ignore
import spock.lang.Specification

import java.util.stream.Collectors

import static org.spockframework.util.Assert.fail

class ConfigResolverTest extends Specification {
    private static final String BASE_TEST_DIR = "src/test/resources/config-resolver"
    private final static String FILE_WHICH_DOES_NOT_EXIST = "src/test/resources/blah-blah-blah";

    def setup() {
        assert !(new File(FILE_WHICH_DOES_NOT_EXIST)).exists()
    }

    def "test resolution - simple working dir"() {
        expect:
        String workingDir = "$BASE_TEST_DIR/1"
        ConfigReader config = new ConfigResolver(
                new DummyDirResolver(workingDir),
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST)).resolve();

        assertHasFiles(config.configPropertiesFiles, "$workingDir/$ConfigResolver.TABULAR_CONFIG_FILE_NAME_DEFAULT")
        assertHasFiles(config.localConfigPropertiesFiles, "$workingDir/$ConfigResolver.TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT")
        assertHasFiles(config.tableCsvFiles, "$workingDir/$ConfigResolver.TABULAR_TABLE_CSV_FILE_NAME_DEFAULT")
        config.close()
    }

    def "test resolution - user dir"() {
        expect:
        String userDir = "$BASE_TEST_DIR/1"
        ConfigReader config = new ConfigResolver(
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
                new DummyDirResolver(userDir)).resolve();

        assertHasFiles(config.configPropertiesFiles, "$userDir/$ConfigResolver.TABULAR_CONFIG_FILE_NAME_DEFAULT")
        assertHasFiles(config.localConfigPropertiesFiles, "$userDir/$ConfigResolver.TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT")
        assertHasFiles(config.tableCsvFiles, "$userDir/$ConfigResolver.TABULAR_TABLE_CSV_FILE_NAME_DEFAULT")
        config.close()
    }

    def "test resolution - config directory property"() {
        expect:
        String configDir = "$BASE_TEST_DIR/2"
        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_CONFIG_DIR_PROP, configDir)
        ConfigReader config = new ConfigResolver(
                propOrEnvVarResolver,
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST)).resolve();

        assertHasFiles(config.configPropertiesFiles, "$configDir/$ConfigResolver.TABULAR_CONFIG_FILE_NAME_DEFAULT")
        assertHasFiles(config.localConfigPropertiesFiles, "$configDir/$ConfigResolver.TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT")
        assertHasFiles(config.tableCsvFiles, "$configDir/$ConfigResolver.TABULAR_TABLE_CSV_FILE_NAME_DEFAULT")
        config.close()
    }

    def "test resolution - config paths properties"() {
        expect:
        File configFile = new File("$BASE_TEST_DIR/1/non-default-named-config.properties");
        File localConfigFile = new File("$BASE_TEST_DIR/1/config-local.properties");
        File tableCsvFile = new File("$BASE_TEST_DIR/2/table.csv");

        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_CONFIG_FILE_PATH_PROP, configFile.absolutePath)
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_LOCAL_CONFIG_FILE_PATH_PROP, localConfigFile.absolutePath)
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_TABLE_CSV_PATH_PROP, tableCsvFile.absolutePath)
        ConfigReader config = new ConfigResolver(
                propOrEnvVarResolver,
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST)).resolve();

        assertHasFiles(config.configPropertiesFiles, configFile.absolutePath)
        assertHasFiles(config.localConfigPropertiesFiles, localConfigFile.absolutePath)
        assertHasFiles(config.tableCsvFiles, tableCsvFile.absolutePath)
        config.close()
    }

    def "test resolution - urls"() {
        expect:
        File configFile1 = new File("$BASE_TEST_DIR/1/non-default-named-config.properties");
        File configLocalFile1 = new File("$BASE_TEST_DIR/1/config-local.properties");
        File configLocalFile2 = new File("$BASE_TEST_DIR/2/config-local.properties");
        File tableCsvFile1 = new File("$BASE_TEST_DIR/1/table.csv");
        File tableCsvFile2 = new File("$BASE_TEST_DIR/2/non-default-named-table.csv");

        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_CONFIG_FILE_URL_PROP, configFile1.toURI().toString())
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_LOCAL_CONFIG_FILE_URL_PROP, "${configLocalFile1.toURI().toString()},${configLocalFile2.toURI().toString()}")
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_TABLE_CSV_URL_PROP, "${tableCsvFile1.toURI().toString()},${tableCsvFile2.toURI().toString()}")

        ConfigReader config = new ConfigResolver(
                propOrEnvVarResolver,
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST)).resolve();

        assertHasFiles(config.configPropertiesFiles, configFile1.getAbsolutePath())
        assertHasFiles(config.localConfigPropertiesFiles, configLocalFile1.getAbsolutePath(), configLocalFile2.getAbsolutePath())
        assertHasFiles(config.tableCsvFiles, tableCsvFile1.getAbsolutePath(), tableCsvFile2.getAbsolutePath())
        config.close()
    }

    @Ignore
    def "test resolution - http url"() {
        expect:
        String configDir = "$BASE_TEST_DIR/2"
        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_CONFIG_DIR_PROP, configDir)
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_CONFIG_FILE_URL_PROP, "http://localhost/static/config.properties")
        ConfigReader config = new ConfigResolver(
                propOrEnvVarResolver,
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST)).resolve();

        PropertiesRepo properties = new PropertiesRepo(config.configPropertiesFiles)
        assert properties.get("config.http") == "value.http"
        config.close()
    }

    void assertHasFiles(List<Reader> actualFiles, String ... expectedPaths){
        List<String> expectedAbsolutePaths = Arrays.stream(expectedPaths).map{new File(it).absolutePath}.collect(Collectors.toList())
        assert actualFiles.size() == expectedAbsolutePaths.size(): "Actual files length does not match expected files length. Actual: $actualFiles, Expected: $expectedAbsolutePaths"
        List<String> actualFilesContent = actualFiles.stream().map{it.text}.collect(Collectors.toList())
        for(String expectedPath: expectedAbsolutePaths) {
            String expectedFileContent = new File(expectedPath).text
            if(!actualFilesContent.contains(expectedFileContent)){
                fail("Content of file [$expectedPath] not found");
            }
        }
    }
}
