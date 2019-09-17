package org.tools4j.tabular.config

import org.apache.commons.io.FileUtils
import org.tools4j.tabular.service.MapBackedSysPropOrEnvVarResolver
import spock.lang.Specification

import java.nio.file.Files

import static org.tools4j.tabular.config.ConfigUrlDownloader.encodeUrlToUseAsFilename

class ConfigUrlDownloaderTest extends Specification {
    private static final String BASE_TEST_DIR = "src/test/resources/config-resolver"
    private final static String FILE_WHICH_DOES_NOT_EXIST = "src/test/resources/blah-blah-blah";

    def setup() {
        assert !(new File(FILE_WHICH_DOES_NOT_EXIST)).exists()
    }

    def "test download non-cached file"() {
        given:
        File tempUserDir = createTempDir()
        File file = new File("$BASE_TEST_DIR/1/non-default-named-config.properties");
        String fileUrl = file.toURI().toString()
        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();
        propOrEnvVarResolver.put(ConfigUrlDownloader.TABULAR_CACHE_URL_DOWNLOADS_PROP, "true")

        ConfigUrlDownloader urlDownloader = new ConfigUrlDownloader(
                new DummyDirResolver(tempUserDir.absolutePath),
                propOrEnvVarResolver)

        when:
        Reader downloadedFile = urlDownloader.downloadFile(fileUrl)

        then:
        assert downloadedFile.text == file.text
        FileUtils.forceDelete(tempUserDir)
    }

    def "test download non-existent file, caching turned off"() {
        given:
        File tempUserDir = createTempDir()
        File file = new File(FILE_WHICH_DOES_NOT_EXIST);
        String fileUrl = file.toURI().toString()
        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();
        propOrEnvVarResolver.put(ConfigUrlDownloader.TABULAR_CACHE_URL_DOWNLOADS_PROP, "false")

        ConfigUrlDownloader urlDownloader = new ConfigUrlDownloader(
                new DummyDirResolver(tempUserDir.absolutePath),
                propOrEnvVarResolver)

        when:
        urlDownloader.downloadFile(fileUrl)

        then:
        Exception e = thrown()
        assert e.message == "Error fetching from URL $fileUrl"
        FileUtils.forceDelete(tempUserDir)
    }

    def "test download cached file"() {
        File tempUserDir = createTempDir()
        File realFileToCopyToCache = new File("$BASE_TEST_DIR/1/non-default-named-config.properties");
        File nonExistentFile = new File("$tempUserDir/my.properties");
        String nonExistentFileUrl = nonExistentFile.toURI().toString()
        File cacheDir = new File(tempUserDir.absolutePath + "/cache")
        cacheDir.mkdirs()
        File cachedFile = new File("$cacheDir.absolutePath/${encodeUrlToUseAsFilename(nonExistentFileUrl)}.cached");
        FileUtils.copyFile(realFileToCopyToCache, cachedFile)

        MapBackedSysPropOrEnvVarResolver propOrEnvVarResolver = new MapBackedSysPropOrEnvVarResolver();
        propOrEnvVarResolver.put(ConfigUrlDownloader.TABULAR_CACHE_URL_DOWNLOADS_PROP, "true")
        ConfigUrlDownloader urlDownloader = new ConfigUrlDownloader(
                new DummyDirResolver(tempUserDir.absolutePath),
                propOrEnvVarResolver
        )

        when:
        Reader fileWhichWasTakenFromCache = urlDownloader.downloadFile(nonExistentFileUrl)

        then:
        assert fileWhichWasTakenFromCache.text == realFileToCopyToCache.text
        FileUtils.forceDelete(tempUserDir)
    }

    File createTempDir(){
        File tempUserDir = new File(Files.createTempDirectory("tabular").toString());
        tempUserDir.mkdirs();
        assert tempUserDir.exists()
        assert tempUserDir.isDirectory()
        return tempUserDir;
    }
}
