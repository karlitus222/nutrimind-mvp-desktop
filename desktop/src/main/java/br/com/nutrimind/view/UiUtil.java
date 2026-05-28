package br.com.nutrimind.view;

import br.com.nutrimind.exception.AppException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public final class UiUtil {
    public static final Color TEAL = new Color(0, 128, 112);
    public static final Color TEAL_DARK = new Color(0, 83, 77);
    public static final Color GREEN = new Color(46, 125, 50);
    public static final Color CORAL = new Color(224, 90, 84);
    public static final Color AMBER = new Color(242, 172, 54);
    public static final Color GRAPHITE = new Color(31, 45, 51);
    public static final Color MUTED = new Color(93, 111, 118);
    public static final Color BORDER = new Color(214, 226, 223);
    public static final Color SURFACE = new Color(247, 251, 250);
    public static final Color PANEL = Color.WHITE;
    public static final Font BASE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private UiUtil() {
    }

    public static void applyGlobalTheme() {
        UIManager.put("Panel.background", SURFACE);
        UIManager.put("OptionPane.background", SURFACE);
        UIManager.put("TabbedPane.background", SURFACE);
        UIManager.put("TabbedPane.foreground", GRAPHITE);
        UIManager.put("TabbedPane.selected", PANEL);
        UIManager.put("Table.font", BASE_FONT);
        UIManager.put("TableHeader.font", BOLD_FONT);
        UIManager.put("Label.font", BASE_FONT);
        UIManager.put("Button.font", BOLD_FONT);
        UIManager.put("TextField.font", BASE_FONT);
        UIManager.put("TextArea.font", BASE_FONT);
        UIManager.put("ComboBox.font", BASE_FONT);
        UIManager.put("CheckBox.font", BASE_FONT);
    }

    public static JLabel title(String value) {
        JLabel label = new JLabel(value);
        label.setFont(new Font("Segoe UI", Font.BOLD, 28));
        label.setForeground(GRAPHITE);
        return label;
    }

    public static JLabel subtitle(String value) {
        JLabel label = new JLabel(value);
        label.setFont(BASE_FONT);
        label.setForeground(MUTED);
        return label;
    }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(TEAL);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(buttonBorder(TEAL));
        button.setOpaque(true);
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(232, 244, 241));
        button.setForeground(TEAL_DARK);
        button.setFocusPainted(false);
        button.setBorder(buttonBorder(new Color(232, 244, 241)));
        button.setOpaque(true);
        return button;
    }

    public static JTextArea area(int rows) {
        JTextArea area = new JTextArea(rows, 30);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setForeground(GRAPHITE);
        area.setBackground(new Color(251, 253, 252));
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return area;
    }

    public static JTextField field(int columns) {
        JTextField field = new JTextField(columns);
        field.setForeground(GRAPHITE);
        field.setBackground(new Color(251, 253, 252));
        field.setBorder(fieldBorder());
        return field;
    }

    public static JPanel padded(JPanel panel) {
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));
        return panel;
    }

    public static JPanel card(JPanel panel) {
        panel.setBackground(PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        return panel;
    }

    public static JLabel metric(String label, int value, Color accent) {
        JLabel metric = new JLabel("<html><span style='font-size:11px;color:#5d6f76'>" + label +
                "</span><br><b style='font-size:28px;color:#1f2d33'>" + value + "</b></html>");
        metric.setOpaque(true);
        metric.setBackground(tint(accent));
        metric.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(
                        Math.max(0, accent.getRed() - 10),
                        Math.max(0, accent.getGreen() - 10),
                        Math.max(0, accent.getBlue() - 10), 60)),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        return metric;
    }

    public static JScrollPane scroll(Component component) {
        JScrollPane pane = new JScrollPane(component);
        pane.setBorder(BorderFactory.createLineBorder(BORDER));
        pane.getViewport().setBackground(PANEL);
        return pane;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));
        table.setSelectionBackground(new Color(218, 240, 236));
        table.setSelectionForeground(GRAPHITE);
        table.getTableHeader().setBackground(new Color(239, 246, 244));
        table.getTableHeader().setForeground(MUTED);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(BORDER));
    }

    public static GridBagConstraints gbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        return gbc;
    }

    public static void showError(Component parent, Throwable error) {
        Throwable current = error;
        while (current.getCause() != null && !(current instanceof AppException)) {
            current = current.getCause();
        }
        JOptionPane.showMessageDialog(parent, current.getMessage(), "Nutrimind", JOptionPane.ERROR_MESSAGE);
    }

    public static <T> void background(Component parent, Callable<T> work, Consumer<T> onDone) {
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return work.call();
            }

            @Override
            protected void done() {
                try {
                    onDone.accept(get());
                } catch (Exception e) {
                    showError(parent, e);
                }
            }
        }.execute();
    }

    public static void refresh(JComponent component) {
        component.revalidate();
        component.repaint();
    }

    private static Border buttonBorder(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker()),
                BorderFactory.createEmptyBorder(10, 14, 10, 14));
    }

    private static Border fieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(9, 10, 9, 10));
    }

    private static Color tint(Color color) {
        int red = Math.min(255, color.getRed() + 205);
        int green = Math.min(255, color.getGreen() + 205);
        int blue = Math.min(255, color.getBlue() + 205);
        return new Color(red, green, blue);
    }
}
