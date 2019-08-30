package org.tools4j.tabular.javafx;

import com.airhacks.afterburner.injection.Injector;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.tools4j.tabular.service.DataSetContext;
import org.tools4j.tabular.service.DataSetContextFromDir;
import org.tools4j.tabular.util.PropertiesRepo;

import java.io.File;

public class Main extends Application {
    private final static Logger LOG = Logger.getLogger(Main.class);
    public static final String TABULAR_CONFIG_DIR_SYS_PROP = "tabular.config.dir";
    public static final String TABULAR_CONFIG_DIR_ENV_PROP = "TABULAR_CONFIG_DIR";
    public static final String WORKING_DIR_SYS_PROP = "user.dir";
    private final PropertiesRepo propertyOverrides;
    private final ExecutionService executionService;

    public Main() {
        this(new PropertiesRepo(), new ExecutionServiceImpl());
    }

    public Main(final PropertiesRepo propertyOverrides, final ExecutionService executionService) {
        this.propertyOverrides = propertyOverrides;
        this.executionService = executionService;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        if(System.getProperties().containsKey("logging.level")){
            LogManager.getRootLogger().setLevel(Level.toLevel(System.getProperty("logging.level")));
        }

        String configDir = resolveWorkingDir();
        LOG.info("Loading AppContext using dir [" + configDir + "]");
        final DataSetContext appContext = new DataSetContextFromDir(configDir, propertyOverrides).load();

        Injector.setModelOrService(DataSetContext.class, appContext);
        Injector.setModelOrService(Stage.class, primaryStage);
        Injector.setModelOrService(ExecutionService.class, executionService);

        if(primaryStage.getStyle() != StageStyle.TRANSPARENT) {
            primaryStage.initStyle(StageStyle.TRANSPARENT);
        }

        final LauncherView mainView = new LauncherView();
        final Scene scene = new Scene(mainView.getView());
        scene.getStylesheets().add("org/tools4j/launcher/javafx/launcher.css");
        scene.setFill(Color.TRANSPARENT);

        primaryStage.setTitle("launcher");
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                primaryStage.requestFocus();
            }
        });

        ResizeHelper.addResizeListener(primaryStage);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOG.info("Shutting down jkeymaster hotkey provider.");
            final Provider provider = Provider.getCurrentProvider(false);
            provider.reset();
            provider.stop();
            LOG.info("Completed shutdown of jkeymaster hotkey provider.");
        }));
    }

    public static String resolveWorkingDir() {
        if(System.getProperties().containsKey("tabular.config.dir")) {
            String dir = System.getProperty(TABULAR_CONFIG_DIR_SYS_PROP);
            LOG.info("Resolved config dir to [" + dir + "] from system property tabular.config.dir");
            if (!new File(dir).exists()) {
                throw new IllegalStateException("Could not find config directory at [" + dir + "] defined by system property " + TABULAR_CONFIG_DIR_SYS_PROP);
            }
            return dir;

        } else if(System.getenv(TABULAR_CONFIG_DIR_ENV_PROP) != null){
            String dir = System.getenv("TABULAR_CONFIG_DIR");
            LOG.info("Resolved config dir to [" + dir + "] from environment variable TABULAR_CONFIG_DIR");
            if(!new File(dir).exists()){
                throw new IllegalStateException("Could not find config directory at [" + dir + "] defined by system property " + TABULAR_CONFIG_DIR_ENV_PROP);
            }
            return dir;

        } else {
            String dir = System.getProperty(WORKING_DIR_SYS_PROP);
            LOG.info("Resolved config dir to [" + dir + "] using working directory system property user.dir");
            return dir;
        }
    }
}
