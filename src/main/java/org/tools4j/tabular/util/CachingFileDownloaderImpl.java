package org.tools4j.tabular.util;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.tools4j.tabular.config.DirResolver;
import org.tools4j.tabular.config.SysPropAndEnvVarResolver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class CachingFileDownloaderImpl implements FileDownloader {
    private final static Logger LOG = Logger.getLogger(CachingFileDownloaderImpl.class);

    //Config
    public static final String TABULAR_CACHE_URL_DOWNLOADS_PROP = "tabular_cache_url_downloads";

    private final SysPropAndEnvVarResolver sysPropAndEnvVarResolver;
    private final DirResolver userDirResolver;

    public CachingFileDownloaderImpl(DirResolver userDirResolver, SysPropAndEnvVarResolver sysPropAndEnvVarResolver) {
        this.userDirResolver = userDirResolver;
        this.sysPropAndEnvVarResolver = sysPropAndEnvVarResolver;
    }

    @Override
    public Reader downloadFile(String urlStr) {
        urlStr = urlStr.trim();
        try {
            URL url = new URL(urlStr);
            File tmpFile = downloadToTmpFile(url);
            LOG.info("Found file at url: " + url);
            if(cacheDownloads()){
                File cachedFile = cacheFile(url, tmpFile);
                return new FileReader(cachedFile);
            } else {
                return new FileReaderWhichDeletesFileOnClose(tmpFile);
            }
        } catch (Exception e) {
            if(cacheDownloads()){
                LOG.info("Could not find file at [" + urlStr + "], looking for cached file");
                Reader cacheFile = lookForCachedFile(urlStr);
                if (cacheFile != null) return cacheFile;
            }
            throw new IllegalStateException("Error fetching from URL " + urlStr, e);
        }
    }

    private Reader lookForCachedFile(String urlStr) {
        try {
            URL url = new URL(urlStr);
            File cacheFile = new File(resolveCachedFileName(url));
            LOG.info("Looking for cached file at [" + cacheFile + "]");
            if(cacheFile.exists()) {
                LOG.info("Found cached file [" + cacheFile + "]");
                return new FileReader(cacheFile);
            }
        } catch (FileNotFoundException | MalformedURLException ex) {
            throw new IllegalStateException("Error fetching cached file", ex);
        }
        return null;
    }

    private File cacheFile(URL url, File file) throws IOException {
        File cacheFile = new File(resolveCachedFileName(url));
        cacheFile.getParentFile().mkdirs();
        if(cacheFile.exists()) {
            FileUtils.forceDelete(cacheFile);
        }
        FileUtils.moveFile(file, cacheFile);
        LOG.info("Cached downloaded file at: " + cacheFile);
        return cacheFile;
    }

    private boolean cacheDownloads() {
        return Boolean.parseBoolean(sysPropAndEnvVarResolver.resolve(TABULAR_CACHE_URL_DOWNLOADS_PROP).orElse("false"));
    }

    private File downloadToTmpFile(URL url) {
        try {
            File downloadedFile = new File(resolveDownloadedTmpFileName(url));
            if(downloadedFile.exists()){
                FileUtils.forceDelete(downloadedFile);
            }
            LOG.info("Downloading file from url [" + url + "] to [" + downloadedFile.getAbsolutePath() + "]");
            FileUtils.copyURLToFile(url, downloadedFile);
            return downloadedFile;
        } catch (Exception e){
            throw new IllegalStateException("Could not download file at url [" + url.toString() + "]", e);
        }
    }

    private String resolveCachedFileName(URL url) {
        try {
            File userDir = userDirResolver.resolve();
            return userDir.getAbsolutePath() + "/cache/" + encodeUrlToUseAsFilename(url.toURI().toString()) + ".cached";
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String resolveDownloadedTmpFileName(URL url) {
        try {
            File userDir = userDirResolver.resolve();
            return userDir.getAbsolutePath() + "/cache/" + encodeUrlToUseAsFilename(url.toURI().toString()) + ".tmp";
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String encodeUrlToUseAsFilename(String url){
        return url.replaceAll("[^\\w]+", "_");
    }

    private static class FileReaderWhichDeletesFileOnClose extends FileReader {
        private final File file;

        public FileReaderWhichDeletesFileOnClose(File file) throws FileNotFoundException {
            super(file);
            this.file = file;
        }

        @Override
        public void close() throws IOException {
            super.close();
            if(file.exists()){
                FileUtils.forceDelete(file);
            }
        }
    }
}
