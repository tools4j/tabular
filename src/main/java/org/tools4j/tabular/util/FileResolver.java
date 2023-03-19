package org.tools4j.tabular.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.tabular.config.DirResolver;
import org.tools4j.tabular.properties.PropertiesRepo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Optional;

public class FileResolver {
    //Config path prop
    public static final String TABULAR_CONFIG_DIR_PROP = "tabular_config_dir";
    
    private final static Logger LOG = LoggerFactory.getLogger(FileResolver.class);
    private final PropertiesRepo propertiesRepo;
    private final DirResolver workingDirResolver;
    private final DirResolver userDirResolver;
    private final FileDownloader fileDownloader;

    public FileResolver(
            PropertiesRepo propertiesRepo,
            DirResolver workingDirResolver,
            DirResolver userDirResolver) {
        this.propertiesRepo = propertiesRepo;
        this.workingDirResolver = workingDirResolver;
        this.userDirResolver = userDirResolver;
        this.fileDownloader = new CachingFileDownloaderImpl(userDirResolver, this.propertiesRepo);
    }

    public Optional<Reader> resolveFile(String urlProp, String pathProp, String fileNameDefault) {
        Optional<Reader> fileAtUrlProp = resolveFileUsingUrlProperty(urlProp);
        if (fileAtUrlProp.isPresent()) return fileAtUrlProp;

        Optional<Reader> fileAtPathProp = resolveFileUsingPathProperty(pathProp);
        if (fileAtPathProp.isPresent()) return fileAtPathProp;

        Optional<Reader> fileInConfigDir = resolveFileInConfigDir(fileNameDefault);
        if (fileInConfigDir.isPresent()) return fileInConfigDir;

        Optional<Reader> fileInUserDir = resolveFileInUserDir(fileNameDefault);
        if (fileInUserDir.isPresent()) return fileInUserDir;

        Optional<Reader> fileInWorkingDir = resolveFileInWorkingDir(fileNameDefault);
        if (fileInWorkingDir.isPresent()) return fileInWorkingDir;

        return Optional.empty();
    }

    private Optional<Reader> resolveFileUsingPathProperty(String pathProp) {
        Optional<File> file = getFileAtProperty(pathProp);
        if (file.isPresent()) {
            LOG.info("Found file using pathProp [" + pathProp + "] at " + file.get().getAbsolutePath());
            try {
                return Optional.of(new FileReader(file.get()));
            } catch (FileNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        return Optional.empty();
    }

    private Optional<Reader> resolveFileInConfigDir(String fileName) {
        String dirPath = propertiesRepo.get(FileResolver.TABULAR_CONFIG_DIR_PROP);
        if(dirPath == null) {
            return Optional.empty();
        }

        File dir = new File(dirPath);
        if(!doesDirExist(dir)) {
            throw new IllegalArgumentException("Dir specified by property [" + FileResolver.TABULAR_CONFIG_DIR_PROP + "] value [" + dir.getAbsolutePath() + "] does not exist, or is not a directory.");
        }

        File file = new File(dir.getAbsolutePath() + "/" + fileName);
        if(!doesFileExist(file)){
            return Optional.empty();
        }

        LOG.info("Found file in config dir: " + file.getAbsolutePath());
        try {
            return Optional.of(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private Optional<Reader> resolveFileInUserDir(String fileName) {
        File userDir = userDirResolver.resolve();
        LOG.info("Looking for file in user dir [" + userDir + "] named [" + fileName + "]");
        Optional<File> file = resolveFile(userDir, fileName);
        file.ifPresent(f -> LOG.info("Found file in user dir: " + f.getAbsolutePath()));
        return toReader(file);
    }

    private Optional<Reader> resolveFileInWorkingDir(String fileName) {
        File workingDir = workingDirResolver.resolve();
        LOG.info("Looking for file in working dir [" + workingDir + "] named [" + fileName + "]");
        Optional<File> file = resolveFile(workingDir, fileName);
        file.ifPresent(f -> LOG.info("Found file in working dir: " + f.getAbsolutePath()));
        return toReader(file);
    }

    private Optional<File> resolveFile(File directory, String fileName) {
        File file = new File(directory + "/" + fileName);
        if(doesFileExist(file)){
            return Optional.of(file);
        }
        return Optional.empty();
    }

    private Optional<File> getFileAtProperty(String propertyName){
        LOG.info("Looking for file at property [" + propertyName + "]");
        String path = propertiesRepo.get(propertyName);
        if(path == null){
            return Optional.empty();
        }
        File file = new File(path);
        ensureFileExists(file);
        LOG.info("Found file at property: " + file.getAbsolutePath());
        return Optional.of(file);
    }

    private Optional<Reader> resolveFileUsingUrlProperty(String propertyName){
        LOG.info("Looking to download file at property [" + propertyName + "]");
        String url = propertiesRepo.get(propertyName);
        if(url == null){
            return Optional.empty();
        }
        return Optional.of(fileDownloader.downloadFile(url));
    }

    private void ensureFileExists(File file) {
        if (!file.exists()) {
            throw new IllegalStateException("Could not find directory at [" + file.getAbsolutePath() + "]");
        }
        if (!file.isFile()) {
            throw new IllegalStateException("Not a file [" + file.getAbsolutePath() + "]");
        }
    }

    private boolean doesFileExist(File file) {
        return file.exists() && file.isFile();
    }

    private boolean doesDirExist(File dir) {
        return dir.exists() && dir.isDirectory();
    }

    private Optional<Reader> toReader(Optional<File> file){
        try {
            if(file.isPresent()) {
                return Optional.of(new FileReader(file.get()));
            } else {
                return Optional.empty();
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
