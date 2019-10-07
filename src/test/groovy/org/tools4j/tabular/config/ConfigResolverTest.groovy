package org.tools4j.tabular.config

import org.apache.commons.io.FileUtils
import org.tools4j.tabular.properties.PropertiesRepo
import org.tools4j.tabular.service.MapBackedSysPropOrEnvVarResolver
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.file.Files
import java.util.stream.Collectors

import static org.spockframework.util.Assert.fail
import static org.tools4j.tabular.config.ConfigUrlDownloader.encodeUrlToUseAsFilename

class ConfigResolverTest extends Specification {
    private static final String BASE_TEST_DIR = "src/test/resources/config-resolver"
    private final static String FILE_WHICH_DOES_NOT_EXIST = "src/test/resources/blah-blah-blah";

    def setup() {
        assert !(new File(FILE_WHICH_DOES_NOT_EXIST)).exists()
    }

    def "test resolution - simple working dir"() {
        given:
        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();
        String workingDir = "$BASE_TEST_DIR/1"

        when:
        ConfigReader config = new ConfigResolver(
                propOrEnvVarResolver,
                new DummyDirResolver(workingDir),
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST)).resolve();

        then:
        assertHasFiles(config.configPropertiesFiles, "$workingDir/$ConfigResolver.TABULAR_CONFIG_FILE_NAME_DEFAULT")
        assertHasFiles(config.localConfigPropertiesFiles, "$workingDir/$ConfigResolver.TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT")
        assertHasFiles(config.tableCsvFiles, "$workingDir/$ConfigResolver.TABULAR_TABLE_CSV_FILE_NAME_DEFAULT")
        config.close()
    }

    def "test resolution - user dir"() {
        given:
        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();
        String userDir = "$BASE_TEST_DIR/1"

        when:
        ConfigReader config = new ConfigResolver(
                propOrEnvVarResolver,
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
                new DummyDirResolver(userDir)).resolve();

        then:
        assertHasFiles(config.configPropertiesFiles, "$userDir/$ConfigResolver.TABULAR_CONFIG_FILE_NAME_DEFAULT")
        assertHasFiles(config.localConfigPropertiesFiles, "$userDir/$ConfigResolver.TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT")
        assertHasFiles(config.tableCsvFiles, "$userDir/$ConfigResolver.TABULAR_TABLE_CSV_FILE_NAME_DEFAULT")
        config.close()
    }

    def "test resolution - config directory property"() {
        given:
        String configDir = "$BASE_TEST_DIR/2"
        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_CONFIG_DIR_PROP, configDir)

        when:
        ConfigReader config = new ConfigResolver(
                propOrEnvVarResolver,
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST)).resolve();

        then:
        assertHasFiles(config.configPropertiesFiles, "$configDir/$ConfigResolver.TABULAR_CONFIG_FILE_NAME_DEFAULT")
        assertHasFiles(config.localConfigPropertiesFiles, "$configDir/$ConfigResolver.TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT")
        assertHasFiles(config.tableCsvFiles, "$configDir/$ConfigResolver.TABULAR_TABLE_CSV_FILE_NAME_DEFAULT")
        config.close()
    }

    def "test resolution - config paths properties"() {
        given:
        File configFile = new File("$BASE_TEST_DIR/1/non-default-named-config.properties");
        File localConfigFile = new File("$BASE_TEST_DIR/1/config-local.properties");
        File tableCsvFile = new File("$BASE_TEST_DIR/2/table.csv");

        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_CONFIG_FILE_PATH_PROP, configFile.absolutePath)
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_LOCAL_CONFIG_FILE_PATH_PROP, localConfigFile.absolutePath)
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_TABLE_CSV_PATH_PROP, tableCsvFile.absolutePath)

        when:
        ConfigReader config = new ConfigResolver(
                propOrEnvVarResolver,
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST)).resolve();

        then:
        assertHasFiles(config.configPropertiesFiles, configFile.absolutePath)
        assertHasFiles(config.localConfigPropertiesFiles, localConfigFile.absolutePath)
        assertHasFiles(config.tableCsvFiles, tableCsvFile.absolutePath)
        config.close()
    }

    def "test resolution - urls"() {
        given:
        File tempUserDir = createTempDir()
        File configFile1 = new File("$BASE_TEST_DIR/1/non-default-named-config.properties");
        File configLocalFile1 = new File("$BASE_TEST_DIR/1/config-local.properties");
        File configLocalFile2 = new File("$BASE_TEST_DIR/2/config-local.properties");
        File tableCsvFile1 = new File("$BASE_TEST_DIR/1/table.csv");
        File tableCsvFile2 = new File("$BASE_TEST_DIR/2/non-default-named-table.csv");

        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_CONFIG_FILE_URL_PROP, configFile1.toURI().toString())
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_LOCAL_CONFIG_FILE_URL_PROP, "${configLocalFile1.toURI().toString()},${configLocalFile2.toURI().toString()}")
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_TABLE_CSV_URL_PROP, "${tableCsvFile1.toURI().toString()},${tableCsvFile2.toURI().toString()}")
        propOrEnvVarResolver.put(ConfigUrlDownloader.TABULAR_CACHE_URL_DOWNLOADS_PROP, "true")

        when:
        ConfigReader config = new ConfigResolver(
                propOrEnvVarResolver,
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
                new DummyDirResolver(tempUserDir.absolutePath)).resolve();

        then:
        assertHasFiles(config.configPropertiesFiles, configFile1.getAbsolutePath())
        assertHasFiles(config.localConfigPropertiesFiles, configLocalFile1.getAbsolutePath(), configLocalFile2.getAbsolutePath())
        assertHasFiles(config.tableCsvFiles, tableCsvFile1.getAbsolutePath(), tableCsvFile2.getAbsolutePath())

        then:
        assertCachedFileExists(tempUserDir, configFile1);
        assertCachedFileExists(tempUserDir, configLocalFile1);
        assertCachedFileExists(tempUserDir, configLocalFile2);
        assertCachedFileExists(tempUserDir, tableCsvFile1);
        assertCachedFileExists(tempUserDir, tableCsvFile2);

        config.close()
        FileUtils.forceDelete(tempUserDir)
    }

    def "test resolution - using cached urls"() {
        given:
        File tempUserDir = createTempDir()

        //Real config files to copy to cache location
        File configFile1 = new File("$BASE_TEST_DIR/1/non-default-named-config.properties");
        File configLocalFile1 = new File("$BASE_TEST_DIR/1/config-local.properties");
        File tableCsvFile1 = new File("$BASE_TEST_DIR/1/table.csv");
        File tableCsvFile2 = new File("$BASE_TEST_DIR/2/non-default-named-table.csv");

        //Non-existent files to generate URLs
        File nonExistentConfigFile1 = new File("$tempUserDir/non-default-named-config.properties");
        File nonExistentConfigLocalFile1 = new File("$tempUserDir/config-local.properties");
        File nonExistentTableCsvFile1 = new File("$tempUserDir/table.csv");
        File nonExistentTableCsvFile2 = new File("$tempUserDir/non-default-named-table.csv");

        //Non-existent URLs to point config to
        String nonExistentConfigFile1Url = nonExistentConfigFile1.toURI().toString()
        String nonExistentConfigLocalFile1Url = nonExistentConfigLocalFile1.toURI().toString()
        String nonExistentTableCsvFile1Url = nonExistentTableCsvFile1.toURI().toString()
        String nonExistentTableCsvFile2Url = nonExistentTableCsvFile2.toURI().toString()

        //Cache files to create for config to find
        File cacheDir = new File(tempUserDir.absolutePath + "/cache")
        cacheDir.mkdirs()
        File configFile1Cached = new File("$cacheDir.absolutePath/${encodeUrlToUseAsFilename(nonExistentConfigFile1Url)}.cached");
        File configLocalFile1Cached = new File("$cacheDir.absolutePath/${encodeUrlToUseAsFilename(nonExistentConfigLocalFile1Url)}.cached");
        File tableCsvFile1Cached = new File("$cacheDir.absolutePath/${encodeUrlToUseAsFilename(nonExistentTableCsvFile1Url)}.cached");
        File tableCsvFile2Cached = new File("$cacheDir.absolutePath/${encodeUrlToUseAsFilename(nonExistentTableCsvFile2Url)}.cached");

        //Create cached files
        FileUtils.copyFile(configFile1, configFile1Cached)
        FileUtils.copyFile(configLocalFile1, configLocalFile1Cached)
        FileUtils.copyFile(tableCsvFile1, tableCsvFile1Cached)
        FileUtils.copyFile(tableCsvFile2, tableCsvFile2Cached)

        //Add non-existent URLs to config
        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_CONFIG_FILE_URL_PROP, nonExistentConfigFile1Url)
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_LOCAL_CONFIG_FILE_URL_PROP, nonExistentConfigLocalFile1Url)
        propOrEnvVarResolver.put(ConfigResolver.TABULAR_TABLE_CSV_URL_PROP, "$nonExistentTableCsvFile1Url,$nonExistentTableCsvFile2Url")
        propOrEnvVarResolver.put(ConfigUrlDownloader.TABULAR_CACHE_URL_DOWNLOADS_PROP, "true")

        when:
        ConfigReader config = new ConfigResolver(
                propOrEnvVarResolver,
                new DummyDirResolver(FILE_WHICH_DOES_NOT_EXIST),
                new DummyDirResolver(tempUserDir.absolutePath)).resolve();

        then:
        assertHasFiles(config.configPropertiesFiles, configFile1Cached.getAbsolutePath())
        assertHasFiles(config.localConfigPropertiesFiles, configLocalFile1Cached.getAbsolutePath())
        assertHasFiles(config.tableCsvFiles, tableCsvFile1Cached.getAbsolutePath(), tableCsvFile2Cached.getAbsolutePath())
        config.close()
        FileUtils.forceDelete(tempUserDir)
    }

    def assertCachedFileExists(File userDir, File file) {
        File expectedCachedFile = new File("${userDir.absolutePath}/cache/${ConfigResolver.encodeUrlForFilename(file.toURI().toString())}.cached");
        assert expectedCachedFile.exists()
        assert expectedCachedFile.isFile()
        assert expectedCachedFile.text == file.text
        return true
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

    def "test encodeUrlForFilename"(){
        expect:
        assert ConfigResolver.encodeUrlForFilename('http://mysite.com:8080/table.csv') == 'http_mysite_com_8080_table_csv'
    }

    File createTempDir(){
        File tempUserDir = new File(Files.createTempDirectory("tabular").toString());
        tempUserDir.mkdirs();
        assert tempUserDir.exists()
        assert tempUserDir.isDirectory()
        return tempUserDir;
    }

    void assertHasFiles(List<Reader> actualFiles, String ... expectedPaths){
        List<String> expectedAbsolutePaths = Arrays.stream(expectedPaths).map{new File(it).absolutePath}.collect(Collectors.toList())
        assert actualFiles.size() == expectedAbsolutePaths.size(): "Actual files length does not match expected files length. Actual: $actualFiles, Expected: $expectedAbsolutePaths"
        List<String> actualFilesContent = actualFiles.stream().map{it.text}.collect(Collectors.toList())
        for(String expectedPath: expectedAbsolutePaths) {
            String expectedFileContent = new File(expectedPath).text
            if(!actualFilesContent.contains(expectedFileContent)){
                fail("Content of expected file [$expectedPath] not found, actual file(s) content: " + actualFilesContent);
            }
        }
    }
}
