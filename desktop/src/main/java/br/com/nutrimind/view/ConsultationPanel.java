package br.com.nutrimind.view;

import br.com.nutrimind.controller.AppController;
import br.com.nutrimind.model.Alert;
import br.com.nutrimind.model.Patient;
import br.com.nutrimind.service.AudioRecorderService;
import br.com.nutrimind.service.ConsultationResult;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.nio.file.Path;
import java.util.List;

public class ConsultationPanel extends JPanel {
    private final AppController controller;
    private final JComboBox<Patient> patients = new JComboBox<>();
    private final JCheckBox consentAudio = new JCheckBox("Paciente autorizou áudio", true);
    private final JCheckBox consentVideo = new JCheckBox("Paciente autorizou vídeo", true);
    private final JTextArea clinical = UiUtil.area(5);
    private final JTextArea transcript = UiUtil.area(6);
    private final JTextArea visual = UiUtil.area(4);
    private final JTextArea output = UiUtil.area(18);
    private final JLabel audioLabel = UiUtil.subtitle("Áudio: nenhum arquivo gravado");
    private Path audioPath;
    private Integer audioDuration;
    private Path videoPath;

    public ConsultationPanel(AppController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(14, 14));
        UiUtil.padded(this);

        JPanel header = new JPanel(new BorderLayout(4, 4));
        header.setOpaque(false);
        header.add(UiUtil.title("Consulta com IA"), BorderLayout.NORTH);
        header.add(UiUtil.subtitle("Registre o atendimento, transcreva áudio e gere alertas clínicos estruturados."), BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form(), UiUtil.card(new JPanel(new BorderLayout()) {{
            add(UiUtil.title("Resultado da análise"), BorderLayout.NORTH);
            add(UiUtil.scroll(output), BorderLayout.CENTER);
        }}));
        split.setResizeWeight(0.52);
        split.setBorder(null);
        add(split, BorderLayout.CENTER);
        reloadPatients();
    }

    private JPanel form() {
        JPanel form = UiUtil.card(new JPanel(new GridBagLayout()));
        JButton startAudio = UiUtil.secondaryButton("Iniciar áudio");
        JButton stopAudio = UiUtil.secondaryButton("Parar áudio");
        JButton chooseVideo = UiUtil.secondaryButton("Vincular vídeo");
        JButton analyze = UiUtil.primaryButton("Encerrar consulta e analisar com IA");

        form.add(new JLabel("Paciente"), UiUtil.gbc(0, 0));
        form.add(patients, UiUtil.gbc(0, 1));
        form.add(consentAudio, UiUtil.gbc(0, 2));
        form.add(consentVideo, UiUtil.gbc(0, 3));
        form.add(audioLabel, UiUtil.gbc(0, 4));
        form.add(startAudio, UiUtil.gbc(0, 5));
        form.add(stopAudio, UiUtil.gbc(0, 6));
        form.add(chooseVideo, UiUtil.gbc(0, 7));
        form.add(new JLabel("Notas clínicas e alimentares"), UiUtil.gbc(0, 8));
        form.add(UiUtil.scroll(clinical), UiUtil.gbc(0, 9));
        form.add(new JLabel("Transcrição manual ou complemento"), UiUtil.gbc(0, 10));
        form.add(UiUtil.scroll(transcript), UiUtil.gbc(0, 11));
        form.add(new JLabel("Observações visuais/comportamentais"), UiUtil.gbc(0, 12));
        form.add(UiUtil.scroll(visual), UiUtil.gbc(0, 13));
        form.add(analyze, UiUtil.gbc(0, 14));

        startAudio.addActionListener(event -> startAudio());
        stopAudio.addActionListener(event -> stopAudio());
        chooseVideo.addActionListener(event -> chooseVideo());
        analyze.addActionListener(event -> analyze());
        return form;
    }

    private void reloadPatients() {
        patients.removeAllItems();
        List<Patient> list = controller.patientController().listAll();
        for (Patient patient : list) {
            patients.addItem(patient);
        }
    }

    private void startAudio() {
        try {
            audioPath = controller.consultationController().startAudioRecording();
            audioLabel.setText("Áudio gravando: " + audioPath.getFileName());
        } catch (Exception e) {
            UiUtil.showError(this, e);
        }
    }

    private void stopAudio() {
        try {
            AudioRecorderService.RecordedAudio audio = controller.consultationController().stopAudioRecording();
            audioPath = audio.file();
            audioDuration = audio.durationSeconds();
            audioLabel.setText("Áudio pronto: " + audioPath.getFileName() + " (" + audioDuration + "s)");
        } catch (Exception e) {
            UiUtil.showError(this, e);
        }
    }

    private void chooseVideo() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            videoPath = chooser.getSelectedFile().toPath();
            output.append("\nVídeo vinculado: " + videoPath + "\n");
        }
    }

    private void analyze() {
        Patient patient = (Patient) patients.getSelectedItem();
        if (patient == null) {
            return;
        }
        output.setText("Chamando IA da OpenAI. Aguarde...\n");
        UiUtil.background(this, () -> controller.consultationController().run(patient, controller.getCurrentUser(),
                consentAudio.isSelected(), consentVideo.isSelected(), clinical.getText(), transcript.getText(),
                visual.getText(), audioPath, audioDuration, videoPath), this::showResult);
    }

    private void showResult(ConsultationResult result) {
        StringBuilder text = new StringBuilder();
        text.append("Consulta analisada com IA\n");
        text.append("Modelo: ").append(result.getAnalysis().getModel()).append("\n\n");
        text.append("Resumo:\n").append(result.getAnalysis().getSummary()).append("\n\n");
        text.append("Alertas:\n");
        for (Alert alert : result.getAlerts()) {
            text.append("- ").append(alert.getSeverity()).append(" | ").append(alert.getRiskType())
                    .append(": ").append(alert.getMessage()).append("\n");
        }
        text.append("\nRelatório - Parte 1:\n").append(result.getReport().getIdentificationSection()).append("\n\n");
        text.append("Parte 2:\n").append(result.getReport().getClinicalSection()).append("\n\n");
        text.append("Parte 3:\n").append(result.getReport().getRecommendationsSection()).append("\n\n");
        text.append("Limitações:\n").append(result.getReport().getLimitationsSection()).append("\n\n");
        text.append("Plano alimentar em revisão:\n").append(result.getMealPlan().getDescription());
        output.setText(text.toString());
    }
}
