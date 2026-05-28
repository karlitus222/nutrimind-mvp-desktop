package br.com.nutrimind;

import br.com.nutrimind.config.DatabaseInitializer;
import br.com.nutrimind.controller.AppController;
import br.com.nutrimind.view.MainFrame;
import br.com.nutrimind.view.UiUtil;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        new DatabaseInitializer().initialize();
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // O look and feel padrão continua funcionando.
            }
            UiUtil.applyGlobalTheme();
            new MainFrame(new AppController()).setVisible(true);
        });
    }
}
