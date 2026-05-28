package br.com.nutrimind.view;

import br.com.nutrimind.controller.AppController;
import br.com.nutrimind.model.Alert;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private final AppController controller;
    private final JPanel statsPanel = new JPanel(new GridLayout(1, 4, 12, 12));
    private final DefaultTableModel alertModel = new DefaultTableModel(new String[]{"ID", "Risco", "Severidade", "Mensagem"}, 0);
    private final JTable alertsTable = new JTable(alertModel);

    public DashboardPanel(AppController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(14, 14));
        UiUtil.padded(this);

        JPanel header = new JPanel(new BorderLayout(4, 4));
        header.setOpaque(false);
        header.add(UiUtil.title("Painel do profissional"), BorderLayout.NORTH);
        header.add(UiUtil.subtitle("Resumo operacional, alertas e decisões clínicas pendentes."), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        statsPanel.setOpaque(false);
        add(statsPanel, BorderLayout.CENTER);

        UiUtil.styleTable(alertsTable);
        JPanel bottom = UiUtil.card(new JPanel(new BorderLayout(10, 10)));
        bottom.add(UiUtil.title("Alertas pendentes"), BorderLayout.NORTH);
        bottom.add(UiUtil.scroll(alertsTable), BorderLayout.CENTER);
        JButton decide = UiUtil.primaryButton("Registrar decisão do alerta");
        bottom.add(decide, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        decide.addActionListener(event -> decideSelected());
        reload();
    }

    private void reload() {
        statsPanel.removeAll();
        java.awt.Color[] colors = {UiUtil.TEAL, UiUtil.GREEN, UiUtil.CORAL, UiUtil.AMBER};
        int index = 0;
        for (Map.Entry<String, Integer> entry : controller.adminController().dashboardCounts().entrySet()) {
            statsPanel.add(UiUtil.metric(entry.getKey(), entry.getValue(), colors[index % colors.length]));
            index++;
        }
        alertModel.setRowCount(0);
        for (Alert alert : controller.adminController().openAlerts()) {
            alertModel.addRow(new Object[]{alert.getId(), alert.getRiskType(), alert.getSeverity(), alert.getMessage()});
        }
        UiUtil.refresh(this);
    }

    private void decideSelected() {
        int row = alertsTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        long id = Long.parseLong(alertModel.getValueAt(row, 0).toString());
        JComboBox<String> action = new JComboBox<>(new String[]{"MONITORAR", "INTERVIR", "ENCAMINHAR", "IGNORAR"});
        JTextArea notes = UiUtil.area(4);
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.add(action, BorderLayout.NORTH);
        panel.add(UiUtil.scroll(notes), BorderLayout.CENTER);
        int result = javax.swing.JOptionPane.showConfirmDialog(this, panel, "Decisão clínica", javax.swing.JOptionPane.OK_CANCEL_OPTION);
        if (result == javax.swing.JOptionPane.OK_OPTION) {
            controller.adminController().decideAlert(id, action.getSelectedItem().toString(), notes.getText());
            reload();
        }
    }
}
