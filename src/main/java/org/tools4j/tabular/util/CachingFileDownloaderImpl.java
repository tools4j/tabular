package org.tools4j.tabular.util;

import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.tabular.config.DirResolver;
import org.tools4j.tabular.config.TabularProperties;
import org.tools4j.tabular.properties.PropertiesRepo;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CachingFileDownloaderImpl implements FileDownloader {
    private final static Logger LOG = LoggerFactory.getLogger(CachingFileDownloaderImpl.class);
    
    private static final MessageDigest hasher;
    private final PropertiesRepo propertiesRepo;
    private final DirResolver userDirResolver;

    static {
        try {
            hasher = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Cannot find MD5 hashing algorithm", e);
        }   
    }
    
    public CachingFileDownloaderImpl(DirResolver userDirResolver, PropertiesRepo propertiesRepo) {
        this.userDirResolver = userDirResolver;
        this.propertiesRepo = propertiesRepo;
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
                LOG.info("No file found at [" + urlStr + "], looking for cached file", e);
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
        return Boolean.parseBoolean(propertiesRepo.get(TabularProperties.CACHE_URL_DOWNLOADS, "false"));
    }

    private File downloadToTmpFile(URL url) {
        try {
            File downloadedFile = new File(resolveDownloadedTmpFileName(url));
            if(downloadedFile.exists()){
                FileUtils.forceDelete(downloadedFile);
            }
            LOG.info("Downloading file from url [" + url + "] to [" + downloadedFile.getAbsolutePath() + "]");
            long startTime = System.currentTimeMillis();
            FileUtils.copyURLToFile(url, downloadedFile);
            long endTime = System.currentTimeMillis();
            LOG.info("Finished download, took " + (endTime - startTime) + "ms");
            return downloadedFile;
        } catch (Exception e){
            throw new IllegalStateException("Could not download file at url [" + url.toString() + "]", e);
        }
    }

    private String resolveCachedFileName(URL url) {
        try {
            File userDir = resolveUserDir();
            return userDir.getAbsolutePath() + "/cache/" + encodeUrlToUseAsFilename(url.toURI().toString()) + ".cached";
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private String resolveDownloadedTmpFileName(URL url) {
        try {
            File userDir = resolveUserDir();
            return userDir.getAbsolutePath() + "/cache/" + encodeUrlToUseAsFilename(url.toURI().toString()) + ".tmp";
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private File resolveUserDir() {
        Optional<File> userDirOpt = userDirResolver.resolve();
        userDirOpt.orElseThrow(() -> new IllegalStateException("userDir could not be resolved"));
        File userDir = userDirOpt.get();
        return userDir;
    }

    public static String encodeUrlToUseAsFilename(String url){
        try {
            byte[] bytesOfMessage = url.getBytes("UTF-8");
            byte[] digest = hasher.digest(bytesOfMessage);
            return DatatypeConverter.printHexBinary(digest).toUpperCase();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
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
