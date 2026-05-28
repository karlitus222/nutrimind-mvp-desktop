package br.com.nutrimind.view;

import br.com.nutrimind.controller.AppController;
import br.com.nutrimind.model.User;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;
import java.awt.Dimension;

public class MainFrame extends JFrame {
    private final AppController controller;
    private final CardLayout cards = new CardLayout();

    public MainFrame(AppController controller) {
        super("Nutrimind");
        this.controller = controller;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1240, 800);
        setMinimumSize(new Dimension(1040, 680));
        setLocationRelativeTo(null);
        setLayout(cards);
        add(new LoginPanel(controller, this::showApp), "login");
        cards.show(getContentPane(), "login");
    }

    private void showApp(User user) {
        SwingUtilities.invokeLater(() -> {
            JTabbedPane tabs = new JTabbedPane();
            tabs.addTab("Dashboard", new DashboardPanel(controller));
            tabs.addTab("Pacientes", new PatientsPanel(controller));
            tabs.addTab("Consulta com IA", new ConsultationPanel(controller));
            tabs.addTab("Relatórios", new ReportsPanel(controller));
            tabs.addTab("Admin", new AdminPanel(controller));
            getContentPane().add(tabs, "app");
            cards.show(getContentPane(), "app");
            setTitle("Nutrimind - " + user.getName());
        });
    }
}
