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
import org.tools4j.tabular.config.ConfigResolver;
import org.tools4j.tabular.service.DataSetContext;
import org.tools4j.tabular.service.DataSetContextFromConfig;
import org.tools4j.tabular.config.ConfigReader;

public class Main extends Application {
    private final static Logger LOG = Logger.getLogger(Main.class);

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
    public void start(Stage primaryStage) throws Exception {
        if(System.getProperties().containsKey("logging.level")){
            LogManager.getRootLogger().setLevel(Level.toLevel(System.getProperty("logging.level")));
        }

        final DataSetContext appContext;
        try(ConfigReader config = new ConfigResolver().resolve()) {
            appContext = new DataSetContextFromConfig(config).load();
        }

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
}
