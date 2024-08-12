package org.tools4j.tabular.util;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.tabular.config.DirResolver;
import org.tools4j.tabular.config.TabularProperties;
import org.tools4j.tabular.properties.PropertiesRepo;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Optional;

public class TabularDirAndFileResolver {
    //Config path prop
    private final static Logger LOG = LoggerFactory.getLogger(TabularDirAndFileResolver.class);
    private final PropertiesRepo propertiesRepo;
    private final DirResolver workingDirResolver;
    private final DirResolver userDirResolver;
    private final FileDownloader fileDownloader;
    private final DirResolver configDirResolver;

    public TabularDirAndFileResolver(
            PropertiesRepo propertiesRepo,
            DirResolver workingDirResolver,
            DirResolver userDirResolver,
            DirResolver configDirResolver) {
        this.propertiesRepo = propertiesRepo;
        this.workingDirResolver = workingDirResolver;
        this.userDirResolver = userDirResolver;
        this.fileDownloader = new CachingFileDownloaderImpl(userDirResolver, this.propertiesRepo);
        this.configDirResolver = configDirResolver;
    }

    /**
     * This method looks for a given file in a number of different locations.  Once a file is found, it is immediately
     * returned.<br/>
     * This method provides a standard way to resolve files in the tabular system.  Used for both configuration files and data files.<br/>
     * This method tries to provide the right balance between flexibility and being able to fallback to sensible defaults.<br/>
     * Files are looked for in this order:
     * <ul>
     *     <li>At the URL specified by the property urlProp.</li>
     *     <li>At the location specified by fileNamePathProp.</li>
     *     <li>In the folder specified by property {@value TabularProperties#CONFIG_DIR}, named fileNameDefault</li>
     *     <li>In the users home folder under a subfolder named 'tabular'. (i.e. ~/tabular/${fileNameDefault}</li>
     *     <li>In the current working directory named fileNameDefault</li>
     * </ul>
     */
    public Optional<Reader> resolveFile(String urlProp, String fileNamePathProp, String fileNameDefault) {
        Optional<Reader> fileAtUrlProp = resolveFileUsingUrlProperty(urlProp);
        if (fileAtUrlProp.isPresent()) return fileAtUrlProp;

        Optional<Reader> fileAtPathProp = resolveFileUsingPathProperty(fileNamePathProp);
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
        String path = propertiesRepo.get(pathProp);
        LOG.info("Looking for file at property  [" + pathProp + ":" + path + "]");
        if(path == null){
            return Optional.empty();
        } else {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                LOG.info("Found file at property [" + pathProp + "]: " + file.getAbsolutePath());
                return toReader(file);
            } else {
                LOG.error("No file found at property [" + pathProp + "] " +
                    "with value [" + path + "] resolving to absolute path [" + file.getAbsolutePath() + "]");
                return Optional.empty();
            }
        }
    }

    private Optional<Reader> resolveFileInConfigDir(String fileName) {
        Optional<File> configDirOpt = configDirResolver.resolve();
        if(configDirOpt.isEmpty()){
            return Optional.empty();
        }
        File configDir = configDirOpt.get();
        Optional<File> file = resolveFile(configDir, fileName);
        LOG.info("Looking for file in config dir [" + configDir.getAbsolutePath() + "/" + fileName + "]");
        if(file.isPresent()){
            LOG.info("Found file in config dir: " + file.get().getAbsolutePath());
            return Optional.of(toReader(file).get());
        } else {
            return Optional.empty();
        }
    }

    private Optional<Reader> resolveFileInUserDir(String fileName) {
        Optional<File> userDirOpt = userDirResolver.resolve();
        if(userDirOpt.isEmpty()){
            return Optional.empty();
        }
        File userDir = userDirOpt.get();
        Optional<File> file = resolveFile(userDir, fileName);
        LOG.info("Looking for file in user dir [" + userDir.getAbsolutePath() + "/" + fileName + "]");
        if(file.isPresent()){
            LOG.info("Found file in user dir [" + file.get().getAbsolutePath() + "]");
            return toReader(file);
        } else {
            return Optional.empty();
        }
    }
    
    private Optional<Reader> resolveFileInWorkingDir(String fileName) {
        Optional<File> workingDirDirOpt = workingDirResolver.resolve();
        if(workingDirDirOpt.isEmpty()){
            return Optional.empty();
        }
        File workingDir = workingDirDirOpt.get();
        Optional<File> file = resolveFile(workingDir, fileName);
        LOG.info("Looking for file in working dir [" + workingDir.getAbsolutePath() + "/" + fileName + "]");
        if(file.isPresent()){
            LOG.info("Found file in working dir [" + file.get().getAbsolutePath() + "]");
            return toReader(file);
        } else {
            return Optional.empty();
        }
    }

    private Optional<File> resolveFile(File directory, String fileName) {
        File file = new File(directory + "/" + fileName);
        if(doesFileExist(file)){
            return Optional.of(file);
        }
        return Optional.empty();
    }

    private Optional<Reader> resolveFileUsingUrlProperty(String propertyName){
        String url = propertiesRepo.get(propertyName);
        LOG.info("Looking for file at URL given by property [" + propertyName + ":" + url + "]");
        if(url == null){
            return Optional.empty();
        }
        return Optional.of(fileDownloader.downloadFile(url));
    }

    private boolean doesFileExist(File file) {
        return file.exists() && file.isFile();
    }

    public static boolean doesDirExist(File dir) {
        return dir.exists() && dir.isDirectory();
    }

    private Optional<Reader> toReader(Optional<File> file){
        if(file.isPresent()) {
            return toReader(file.get());
        } else {
            return Optional.empty();
        }
    }

    @SneakyThrows
    private static Optional<Reader> toReader(File file) {
        if(file.exists() && file.isFile()){
            return Optional.of(new FileReader(file));
        } else {
            return Optional.empty();
        }
    }
}
