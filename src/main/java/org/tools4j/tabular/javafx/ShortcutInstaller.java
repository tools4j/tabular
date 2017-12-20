package org.tools4j.tabular.javafx;

import com.tulskiy.keymaster.common.HotKeyListener;
import com.tulskiy.keymaster.common.Provider;
import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.tools4j.tabular.util.PropertiesRepo;

import javax.swing.*;

/**
 * User: ben
 * Date: 10/05/2016
 * Time: 6:53 AM
 */
public class ShortcutInstaller {
    private final static Logger LOG = Logger.getLogger(ShortcutInstaller.class);
    private final Stage stage;
    private final Control componentToFocus;
    private final String[] hotKeyCombinations;

    public ShortcutInstaller(final Stage stage, final Control componentToFocus, final PropertiesRepo propertiesRepo) {
        this(stage, componentToFocus, propertiesRepo.get("hotkey.combinations.show").split(","));
    }

    public ShortcutInstaller(final Stage stage, final Control componentToFocus, final String[] hotKeyCombinations) {
        this.stage = stage;
        this.componentToFocus = componentToFocus;
        this.hotKeyCombinations = hotKeyCombinations;
    }

    public void install() {
        Provider provider = Provider.getCurrentProvider(false);
        final HotKeyListener hotKeyListener = hotKey -> Platform.runLater(() -> {
            stage.setIconified(false);
            stage.requestFocus();
            if (componentToFocus != null) {
                componentToFocus.requestFocus();
            }
        });

        for (String hotkeyCombination: hotKeyCombinations) {
            try {
                LOG.info("Registering hotkey " + hotkeyCombination + " for window restore.");
                provider.register(KeyStroke.getKeyStroke(hotkeyCombination), hotKeyListener);
            } catch (Throwable t) {
                LOG.error("Unable to assign hotkey: " + hotkeyCombination + " " + t.getMessage(), t);
            }
        }
    }
}
