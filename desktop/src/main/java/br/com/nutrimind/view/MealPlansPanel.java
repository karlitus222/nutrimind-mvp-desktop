package br.com.nutrimind.view;

import br.com.nutrimind.controller.AppController;
import br.com.nutrimind.model.MealPlan;
import br.com.nutrimind.model.Patient;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;

public class MealPlansPanel extends JPanel {
    private final AppController controller;
    private final JComboBox<Patient> patients = new JComboBox<>();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Objetivo", "Status", "Início", "Fim", "Aprovado por"}, 0);
    private final JTable table = new JTable(model);

    public MealPlansPanel(AppController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(14, 14));
        UiUtil.padded(this);

        JPanel header = new JPanel(new BorderLayout(4, 4));
        header.setOpaque(false);
        header.add(UiUtil.title("Planos alimentares"), BorderLayout.NORTH);
        header.add(UiUtil.subtitle("Revise as sugestões iniciais da IA antes de aprovar qualquer plano."), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        UiUtil.styleTable(table);
        JPanel content = UiUtil.card(new JPanel(new BorderLayout(10, 10)));
        content.add(patients, BorderLayout.NORTH);
        content.add(UiUtil.scroll(table), BorderLayout.CENTER);
        JButton approve = UiUtil.primaryButton("Aprovar plano selecionado");
        content.add(approve, BorderLayout.SOUTH);
        add(content, BorderLayout.CENTER);

        patients.addActionListener(event -> reloadPlans());
        approve.addActionListener(event -> approveSelected());
        reloadPatients();
    }

    private void reloadPatients() {
        patients.removeAllItems();
        for (Patient patient : controller.patientController().listAll()) {
            patients.addItem(patient);
        }
        reloadPlans();
    }

    private void reloadPlans() {
        model.setRowCount(0);
        Patient patient = (Patient) patients.getSelectedItem();
        if (patient == null) {
            return;
        }
        for (MealPlan plan : controller.mealPlanController().listForPatient(patient.getId())) {
            model.addRow(new Object[]{plan.getId(), plan.getObjective(), plan.getStatus(), plan.getStartDate(),
                    plan.getEndDate(), plan.getApprovedBy()});
        }
    }

    private void approveSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        long planId = Long.parseLong(model.getValueAt(row, 0).toString());
        controller.mealPlanController().approve(planId, controller.getCurrentUser().getId());
        reloadPlans();
    }
}
