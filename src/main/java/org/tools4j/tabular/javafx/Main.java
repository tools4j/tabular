package org.tools4j.tabular.javafx;

import com.airhacks.afterburner.injection.Injector;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tools4j.tabular.datasets.DataSetContext;
import org.tools4j.tabular.datasets.DataSetContextLoader;

public class Main extends Application {
    private final static Logger LOG = LoggerFactory.getLogger(Main.class);

    private final ExecutionService executionService;

    public Main() {
        this(new ExecutionServiceImpl());
    }

    public Main(final ExecutionService executionService) {
        this.executionService = executionService;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        if(System.getProperties().containsKey("logging.level")){
            setLoggingLevel(Level.toLevel(System.getProperty("logging.level")));
        }

        LOG.info("==================== Loading config and csv ====================");
        DataSetContext appContext = new DataSetContextLoader().load();

        LOG.info("==================== Starting GUI ====================");
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

    private void setLoggingLevel(Level level) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME); 
        loggerConfig.setLevel(level);
        ctx.updateLoggers();
    }
}
