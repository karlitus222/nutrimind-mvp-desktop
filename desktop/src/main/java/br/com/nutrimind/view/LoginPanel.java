package br.com.nutrimind.view;

import br.com.nutrimind.controller.AppController;
import br.com.nutrimind.model.User;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.util.function.Consumer;

public class LoginPanel extends JPanel {
    public LoginPanel(AppController controller, Consumer<User> onLogin) {
        setLayout(new BorderLayout());
        UiUtil.padded(this);
        JPanel form = UiUtil.card(new JPanel(new GridBagLayout()));
        JTextField email = UiUtil.field(24);
        email.setText("nutri@nutrimind.com");
        JPasswordField password = new JPasswordField("123456", 24);
        password.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiUtil.BORDER),
                BorderFactory.createEmptyBorder(9, 10, 9, 10)));
        JButton login = UiUtil.primaryButton("Entrar");

        form.add(UiUtil.title("Nutrimind"), UiUtil.gbc(0, 0));
        form.add(UiUtil.subtitle("IA aplicada à nutrição comportamental"), UiUtil.gbc(0, 1));
        form.add(new JLabel("E-mail"), UiUtil.gbc(0, 2));
        form.add(email, UiUtil.gbc(0, 3));
        form.add(new JLabel("Senha"), UiUtil.gbc(0, 4));
        form.add(password, UiUtil.gbc(0, 5));
        form.add(login, UiUtil.gbc(0, 6));
        JLabel demo = new JLabel("Demo: nutri@nutrimind.com / 123456 ou admin@nutrimind.com / admin123");
        demo.setForeground(UiUtil.MUTED);
        form.add(demo, UiUtil.gbc(0, 7));
        add(form, BorderLayout.CENTER);

        login.addActionListener(event -> {
            try {
                User user = controller.authService().login(email.getText(), password.getPassword());
                controller.setCurrentUser(user);
                onLogin.accept(user);
            } catch (Exception e) {
                UiUtil.showError(this, e);
            }
        });
    }
}
