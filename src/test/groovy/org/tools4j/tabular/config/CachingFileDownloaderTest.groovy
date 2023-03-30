package org.tools4j.tabular.config

import org.apache.commons.io.FileUtils
import org.tools4j.tabular.properties.PropertiesRepo
import org.tools4j.tabular.util.CachingFileDownloaderImpl
import org.tools4j.tabular.util.FileDownloader
import spock.lang.Specification

import java.nio.file.Files

import static org.tools4j.tabular.util.CachingFileDownloaderImpl.encodeUrlToUseAsFilename

class CachingFileDownloaderTest extends Specification {
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
        PropertiesRepo propertiesRepo = new PropertiesRepo();
        propertiesRepo.put(TabularProperties.CACHE_URL_DOWNLOADS, "true")

        FileDownloader fileDownloader = new CachingFileDownloaderImpl(
                new DummyDirResolver(tempUserDir.absolutePath),
                propertiesRepo)

        when:
        Reader downloadedFile = fileDownloader.downloadFile(fileUrl)

        then:
        assert downloadedFile.text == file.text
        FileUtils.forceDelete(tempUserDir)
    }

    def "test download non-existent file, caching turned off"() {
        given:
        File tempUserDir = createTempDir()
        File file = new File(FILE_WHICH_DOES_NOT_EXIST);
        String fileUrl = file.toURI().toString()
        PropertiesRepo propertiesRepo = new PropertiesRepo();
        propertiesRepo.put(TabularProperties.CACHE_URL_DOWNLOADS, "false")

        FileDownloader fileDownloader = new CachingFileDownloaderImpl(
                new DummyDirResolver(tempUserDir.absolutePath),
                propertiesRepo)

        when:
        fileDownloader.downloadFile(fileUrl)

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

        PropertiesRepo propertiesRepo = new PropertiesRepo();
        propertiesRepo.put(TabularProperties.CACHE_URL_DOWNLOADS, "true")
        FileDownloader fileDownloader = new CachingFileDownloaderImpl(
                new DummyDirResolver(tempUserDir.absolutePath),
                propertiesRepo
        )

        when:
        Reader fileWhichWasTakenFromCache = fileDownloader.downloadFile(nonExistentFileUrl)

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
