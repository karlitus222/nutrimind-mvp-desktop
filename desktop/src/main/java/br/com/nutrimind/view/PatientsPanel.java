package br.com.nutrimind.view;

import br.com.nutrimind.controller.AppController;
import br.com.nutrimind.model.Patient;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.time.LocalDate;

public class PatientsPanel extends JPanel {
    private final AppController controller;
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Nome", "CPF", "Telefone", "Histórico"}, 0);
    private final JTable table = new JTable(model);
    private final JTextField name = UiUtil.field(22);
    private final JTextField cpf = UiUtil.field(14);
    private final JTextField birthDate = UiUtil.field(10);
    private final JTextField phone = UiUtil.field(14);
    private final JTextField email = UiUtil.field(22);
    private final JTextArea clinical = UiUtil.area(4);
    private final JTextArea eating = UiUtil.area(4);

    public PatientsPanel(AppController controller) {
        this.controller = controller;
        birthDate.setText("1990-01-01");
        setLayout(new BorderLayout(14, 14));
        UiUtil.padded(this);

        JPanel header = new JPanel(new BorderLayout(4, 4));
        header.setOpaque(false);
        header.add(UiUtil.title("Pacientes"), BorderLayout.NORTH);
        header.add(UiUtil.subtitle("Cadastro clínico, histórico alimentar e dados de contato."), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        UiUtil.styleTable(table);
        add(UiUtil.card(new JPanel(new BorderLayout()) {{
            add(UiUtil.scroll(table), BorderLayout.CENTER);
        }}), BorderLayout.CENTER);
        add(form(), BorderLayout.EAST);
        table.getSelectionModel().addListSelectionListener(event -> fillFromSelection());
        reload();
    }

    private JPanel form() {
        JPanel form = UiUtil.card(new JPanel(new GridBagLayout()));
        JButton newButton = UiUtil.secondaryButton("Novo");
        JButton save = UiUtil.primaryButton("Salvar paciente");
        JButton delete = UiUtil.secondaryButton("Desativar");
        form.add(new JLabel("Nome"), UiUtil.gbc(0, 0));
        form.add(name, UiUtil.gbc(0, 1));
        form.add(new JLabel("CPF"), UiUtil.gbc(0, 2));
        form.add(cpf, UiUtil.gbc(0, 3));
        form.add(new JLabel("Nascimento (AAAA-MM-DD)"), UiUtil.gbc(0, 4));
        form.add(birthDate, UiUtil.gbc(0, 5));
        form.add(new JLabel("Telefone"), UiUtil.gbc(0, 6));
        form.add(phone, UiUtil.gbc(0, 7));
        form.add(new JLabel("E-mail"), UiUtil.gbc(0, 8));
        form.add(email, UiUtil.gbc(0, 9));
        form.add(new JLabel("Notas clínicas"), UiUtil.gbc(0, 10));
        form.add(UiUtil.scroll(clinical), UiUtil.gbc(0, 11));
        form.add(new JLabel("Histórico alimentar"), UiUtil.gbc(0, 12));
        form.add(UiUtil.scroll(eating), UiUtil.gbc(0, 13));
        form.add(save, UiUtil.gbc(0, 14));
        form.add(newButton, UiUtil.gbc(0, 15));
        form.add(delete, UiUtil.gbc(0, 16));
        newButton.addActionListener(event -> clear());
        save.addActionListener(event -> save());
        delete.addActionListener(event -> delete());
        return form;
    }

    private void reload() {
        model.setRowCount(0);
        for (Patient patient : controller.patientController().listAll()) {
            model.addRow(new Object[]{patient.getId(), patient.getName(), patient.getCpf(), patient.getPhone(), patient.getEatingHistory()});
        }
    }

    private void save() {
        try {
            long selectedId = selectedId();
            Patient patient = new Patient(selectedId, controller.getCurrentUser().getId(), name.getText(), cpf.getText(),
                    LocalDate.parse(birthDate.getText()), phone.getText(), email.getText(), clinical.getText(),
                    eating.getText(), true, java.time.LocalDateTime.now());
            controller.patientController().save(patient);
            clear();
            reload();
        } catch (Exception e) {
            UiUtil.showError(this, e);
        }
    }

    private void delete() {
        long id = selectedId();
        if (id > 0) {
            controller.patientController().delete(id);
            clear();
            reload();
        }
    }

    private void fillFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        controller.patientController().listAll().stream()
                .filter(patient -> patient.getId() == selectedId())
                .findFirst()
                .ifPresent(patient -> {
                    name.setText(patient.getName());
                    cpf.setText(patient.getCpf());
                    birthDate.setText(patient.getBirthDate() == null ? "1990-01-01" : patient.getBirthDate().toString());
                    phone.setText(patient.getPhone());
                    email.setText(patient.getEmail());
                    clinical.setText(patient.getClinicalNotes());
                    eating.setText(patient.getEatingHistory());
                });
    }

    private long selectedId() {
        int row = table.getSelectedRow();
        return row < 0 ? 0 : Long.parseLong(model.getValueAt(row, 0).toString());
    }

    private void clear() {
        table.clearSelection();
        name.setText("");
        cpf.setText("");
        birthDate.setText("1990-01-01");
        phone.setText("");
        email.setText("");
        clinical.setText("");
        eating.setText("");
    }
}
