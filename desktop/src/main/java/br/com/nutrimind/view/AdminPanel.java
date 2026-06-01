package br.com.nutrimind.view;

import br.com.nutrimind.controller.AppController;
import br.com.nutrimind.model.User;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.nio.file.Path;

public class AdminPanel extends JPanel {
    private final AppController controller;
    private final DefaultTableModel usersModel = new DefaultTableModel(new String[]{"ID", "Nome", "E-mail", "Perfil", "Ativo"}, 0);
    private final JTable usersTable = new JTable(usersModel);
    private final JLabel aiStatus = new JLabel();
    private final JTextField name = UiUtil.field(18);
    private final JTextField email = UiUtil.field(18);
    private final JPasswordField password = new JPasswordField(18);
    private final JTextField crn = UiUtil.field(12);
    private final JTextField specialty = UiUtil.field(18);

    public AdminPanel(AppController controller) {
        this.controller = controller;
        specialty.setText("Nutrição comportamental");
        password.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiUtil.BORDER),
                BorderFactory.createEmptyBorder(9, 10, 9, 10)));
        setLayout(new BorderLayout(14, 14));
        UiUtil.padded(this);

        JPanel header = new JPanel(new BorderLayout(4, 4));
        header.setOpaque(false);
        header.add(UiUtil.title("Administração"), BorderLayout.NORTH);
        header.add(UiUtil.subtitle("Gerencie usuários, configuração da IA e dados de apoio."), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        UiUtil.styleTable(usersTable);
        add(UiUtil.card(new JPanel(new BorderLayout()) {{
            add(UiUtil.scroll(usersTable), BorderLayout.CENTER);
        }}), BorderLayout.CENTER);
        add(form(), BorderLayout.EAST);
        reload();
    }

    private JPanel form() {
        JPanel form = UiUtil.card(new JPanel(new GridBagLayout()));
        JButton create = UiUtil.primaryButton("Cadastrar nutricionista");
        JButton deactivate = UiUtil.secondaryButton("Desativar usuário selecionado");
        JButton export = UiUtil.secondaryButton("Exportar dados JSON");
        form.add(aiStatus, UiUtil.gbc(0, 0));
        form.add(new JLabel("Nome"), UiUtil.gbc(0, 1));
        form.add(name, UiUtil.gbc(0, 2));
        form.add(new JLabel("E-mail"), UiUtil.gbc(0, 3));
        form.add(email, UiUtil.gbc(0, 4));
        form.add(new JLabel("Senha"), UiUtil.gbc(0, 5));
        form.add(password, UiUtil.gbc(0, 6));
        form.add(new JLabel("CRN"), UiUtil.gbc(0, 7));
        form.add(crn, UiUtil.gbc(0, 8));
        form.add(new JLabel("Especialidade"), UiUtil.gbc(0, 9));
        form.add(specialty, UiUtil.gbc(0, 10));
        form.add(create, UiUtil.gbc(0, 11));
        form.add(deactivate, UiUtil.gbc(0, 12));
        form.add(export, UiUtil.gbc(0, 13));
        create.addActionListener(event -> createNutritionist());
        deactivate.addActionListener(event -> deactivateSelectedUser());
        export.addActionListener(event -> exportJson());
        return form;
    }

    private void reload() {
        boolean configured = controller.adminController().isAiConfigured();
        aiStatus.setText(configured
                ? "IA OpenAI configurada: fluxo principal liberado"
                : "IA OpenAI não configurada: análise bloqueada");
        aiStatus.setForeground(configured ? UiUtil.GREEN : UiUtil.CORAL);
        usersModel.setRowCount(0);
        for (User user : controller.adminController().users()) {
            usersModel.addRow(new Object[]{user.getId(), user.getName(), user.getEmail(), user.getRole(), user.isActive()});
        }
    }

    private void createNutritionist() {
        try {
            controller.adminController().createNutritionist(name.getText(), email.getText(), password.getPassword(),
                    crn.getText(), specialty.getText());
            name.setText("");
            email.setText("");
            password.setText("");
            crn.setText("");
            reload();
        } catch (Exception e) {
            UiUtil.showError(this, e);
        }
    }

    private void exportJson() {
        try {
            Path file = controller.adminController().exportJson();
            javax.swing.JOptionPane.showMessageDialog(this, "Exportado para: " + file.toAbsolutePath());
        } catch (Exception e) {
            UiUtil.showError(this, e);
        }
    }

    private void deactivateSelectedUser() {
        int row = usersTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        try {
            long userId = Long.parseLong(usersModel.getValueAt(row, 0).toString());
            controller.adminController().deactivateUser(userId);
            reload();
        } catch (Exception e) {
            UiUtil.showError(this, e);
        }
    }
}
