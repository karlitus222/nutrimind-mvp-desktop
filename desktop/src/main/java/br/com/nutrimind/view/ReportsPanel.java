package br.com.nutrimind.view;

import br.com.nutrimind.controller.AppController;
import br.com.nutrimind.model.Alert;
import br.com.nutrimind.model.Consultation;
import br.com.nutrimind.model.ConsultationReport;
import br.com.nutrimind.model.Patient;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

public class ReportsPanel extends JPanel {
    private final AppController controller;
    private final JComboBox<Patient> patients = new JComboBox<>();
    private final DefaultTableModel historyModel = new DefaultTableModel(new String[]{"ID", "Início", "Status"}, 0);
    private final JTable history = new JTable(historyModel);
    private final JTextArea reportArea = UiUtil.area(24);

    public ReportsPanel(AppController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(14, 14));
        UiUtil.padded(this);

        JPanel header = new JPanel(new BorderLayout(4, 4));
        header.setOpaque(false);
        header.add(UiUtil.title("Histórico e relatórios"), BorderLayout.NORTH);
        header.add(UiUtil.subtitle("Acompanhe consultas, alertas, decisões e relatórios gerados pela IA."), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        UiUtil.styleTable(history);
        JPanel left = UiUtil.card(new JPanel(new BorderLayout(10, 10)));
        left.add(patients, BorderLayout.NORTH);
        left.add(UiUtil.scroll(history), BorderLayout.CENTER);

        JPanel right = UiUtil.card(new JPanel(new BorderLayout(10, 10)));
        right.add(UiUtil.title("Relatório clínico"), BorderLayout.NORTH);
        right.add(UiUtil.scroll(reportArea), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setResizeWeight(0.34);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
        patients.addActionListener(event -> loadHistory());
        history.getSelectionModel().addListSelectionListener(event -> loadReport());
        reloadPatients();
    }

    private void reloadPatients() {
        patients.removeAllItems();
        for (Patient patient : controller.patientController().listAll()) {
            patients.addItem(patient);
        }
        loadHistory();
    }

    private void loadHistory() {
        historyModel.setRowCount(0);
        Patient patient = (Patient) patients.getSelectedItem();
        if (patient == null) {
            return;
        }
        List<Consultation> consultations = controller.consultationController().history(patient.getId());
        for (Consultation consultation : consultations) {
            historyModel.addRow(new Object[]{consultation.getId(), consultation.getStartedAt(), consultation.getStatus()});
        }
    }

    private void loadReport() {
        int row = history.getSelectedRow();
        if (row < 0) {
            return;
        }
        long consultationId = Long.parseLong(historyModel.getValueAt(row, 0).toString());
        StringBuilder text = new StringBuilder();
        ConsultationReport report = controller.consultationController().report(consultationId).orElse(null);
        if (report == null) {
            reportArea.setText("Relatório ainda não gerado.");
            return;
        }
        text.append("PARTE 1 - Identificação e achados da IA\n");
        text.append(report.getIdentificationSection()).append("\n\n");
        text.append("PARTE 2 - Relato e impressão clínica\n");
        text.append(report.getClinicalSection()).append("\n\n");
        text.append("PARTE 3 - Recomendações e plano\n");
        text.append(report.getRecommendationsSection()).append("\n\n");
        text.append("Limitações\n");
        text.append(report.getLimitationsSection()).append("\n\n");
        text.append("Alertas vinculados\n");
        for (Alert alert : controller.consultationController().alerts(consultationId)) {
            text.append("- ").append(alert.getSeverity()).append(" | ").append(alert.getRiskType())
                    .append(": ").append(alert.getMessage()).append(" [").append(alert.getStatus()).append("]\n");
        }
        reportArea.setText(text.toString());
    }
}
