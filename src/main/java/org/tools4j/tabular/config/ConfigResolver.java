package org.tools4j.tabular.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.tabular.util.CachingFileDownloaderImpl;
import org.tools4j.tabular.util.FileDownloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ConfigResolver {
    private final static Logger LOG = LoggerFactory.getLogger(ConfigResolver.class);

    //File urls
    public static final String TABULAR_CONFIG_FILE_URL_PROP = "tabular_config_file_url";
    public static final String TABULAR_LOCAL_CONFIG_FILE_URL_PROP = "tabular_local_config_file_url";
    public static final String TABULAR_TABLE_CSV_URL_PROP = "tabular_csv_url";

    //File paths
    public static final String TABULAR_CONFIG_FILE_PATH_PROP = "tabular_config_file_path";
    public static final String TABULAR_LOCAL_CONFIG_FILE_PATH_PROP = "tabular_local_config_file_path";
    public static final String TABULAR_TABLE_CSV_PATH_PROP = "tabular_csv_path";

    //Config path prop
    public static final String TABULAR_CONFIG_DIR_PROP = "tabular_config_dir";

    //Defaults
    public static final String TABULAR_CONFIG_FILE_NAME_DEFAULT = "config.properties";
    public static final String TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT = "config-local.properties";
    public static final String TABULAR_TABLE_CSV_FILE_NAME_DEFAULT = "table.csv";

    private final SysPropAndEnvVarResolver sysPropAndEnvVarResolver;
    private final DirResolver workingDirResolver;
    private final DirResolver userDirResolver;
    private final FileDownloader fileDownloader;

    public ConfigResolver(){
        this(new SysPropAndEnvVarResolverImpl(), new WorkingDirResolver(), new UserDirResolver());
    }

    public ConfigResolver(DirResolver workingDirResolver, DirResolver userDirResolver){
        this(new SysPropAndEnvVarResolverImpl(), workingDirResolver, userDirResolver);
    }

    public ConfigResolver(
            SysPropAndEnvVarResolver sysPropAndEnvVarResolver,
            DirResolver workingDirResolver,
            DirResolver userDirResolver) {
        this.sysPropAndEnvVarResolver = sysPropAndEnvVarResolver;
        this.workingDirResolver = workingDirResolver;
        this.userDirResolver = userDirResolver;
        this.fileDownloader = new CachingFileDownloaderImpl(userDirResolver, sysPropAndEnvVarResolver);
    }

    public ConfigReader resolve(){
        List<Reader> tableCsvFiles = resolveFiles(TABULAR_TABLE_CSV_URL_PROP, TABULAR_TABLE_CSV_PATH_PROP, TABULAR_TABLE_CSV_FILE_NAME_DEFAULT);
        if(tableCsvFiles.isEmpty()){
            throw new IllegalArgumentException("Could not resolve table csv file.");
        }
        List<Reader> configFiles = resolveFiles(TABULAR_CONFIG_FILE_URL_PROP, TABULAR_CONFIG_FILE_PATH_PROP, TABULAR_CONFIG_FILE_NAME_DEFAULT);
        if(configFiles.isEmpty()){
            throw new IllegalArgumentException("Could not find config file.");
        }
        List<Reader> localConfigFiles = resolveFiles(TABULAR_LOCAL_CONFIG_FILE_URL_PROP, TABULAR_LOCAL_CONFIG_FILE_PATH_PROP, TABULAR_LOCAL_CONFIG_FILE_NAME_DEFAULT);
        return new ConfigReaderImpl(tableCsvFiles, configFiles, localConfigFiles);
    }

    private List<Reader> resolveFiles(String urlProp, String pathProp, String fileNameDefault) {
        List<Reader> filesAtUrlsProp = resolveFilesUsingUrlsProperty(urlProp);
        if (!filesAtUrlsProp.isEmpty()) return filesAtUrlsProp;

        Optional<Reader> fileAtPathProp = resolveFileUsingPathProperty(pathProp);
        if (fileAtPathProp.isPresent()) return Collections.singletonList(fileAtPathProp.get());

        Optional<Reader> fileInConfigDir = resolveFileInConfigDir(fileNameDefault);
        if (fileInConfigDir.isPresent()) return Collections.singletonList(fileInConfigDir.get());

        Optional<Reader> fileInUserDir = resolveFileInUserDir(fileNameDefault);
        if (fileInUserDir.isPresent()) return Collections.singletonList(fileInUserDir.get());

        Optional<Reader> fileInWorkingDir = resolveFileInWorkingDir(fileNameDefault);
        if (fileInWorkingDir.isPresent()) return Collections.singletonList(fileInWorkingDir.get());

        return Collections.emptyList();
    }

    private Optional<Reader> resolveFileUsingPathProperty(String pathProp) {
        Optional<File> file = getFileAtSysPropOrEnvVarPath(pathProp);
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
        Optional<String> dirPath = sysPropAndEnvVarResolver.resolve(ConfigResolver.TABULAR_CONFIG_DIR_PROP);
        if(!dirPath.isPresent()) {
            return Optional.empty();
        }

        File dir = new File(dirPath.get());
        if(!doesDirExist(dir)) {
            throw new IllegalArgumentException("Dir specified by property [" + ConfigResolver.TABULAR_CONFIG_DIR_PROP + "] value [" + dir.getAbsolutePath() + "] does not exist, or is not a directory.");
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
        Optional<File> file = resolveFile(userDirResolver.resolve(), fileName);
        file.ifPresent(f -> LOG.info("Found file in user dir: " + f.getAbsolutePath()));
        return toReader(file);
    }

    private Optional<Reader> resolveFileInWorkingDir(String fileName) {
        Optional<File> file = resolveFile(workingDirResolver.resolve(), fileName);
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

    private Optional<File> getFileAtSysPropOrEnvVarPath(String propertyName){
        Optional<String> path = sysPropAndEnvVarResolver.resolve(propertyName);
        if(!path.isPresent()){
            return Optional.empty();
        }
        File file = new File(path.get());
        ensureFileExists(file);
        LOG.info("Found file at system or env variable path: " + file.getAbsolutePath());
        return Optional.of(file);
    }

    private List<Reader> resolveFilesUsingUrlsProperty(String propertyName){
        Optional<String> urls = sysPropAndEnvVarResolver.resolve(propertyName);
        if(!urls.isPresent()){
            return Collections.emptyList();
        }
        List<Reader> files = new ArrayList<>();
        for (String urlStr : urls.get().split(",")) {
            files.add(fileDownloader.downloadFile(urlStr));
        }
        return files;
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

    public static String encodeUrlForFilename(String url){
        return url.replaceAll("[^\\w]+", "_");
    }
}
