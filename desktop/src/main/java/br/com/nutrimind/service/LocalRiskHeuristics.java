package br.com.nutrimind.service;

import br.com.nutrimind.model.RiskItem;
import br.com.nutrimind.model.Severity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocalRiskHeuristics {
    public List<RiskItem> validate(String transcriptAndNotes) {
        String text = transcriptAndNotes == null ? "" : transcriptAndNotes.toLowerCase(Locale.ROOT);
        List<RiskItem> risks = new ArrayList<>();
        if (containsAny(text, "ansiedade", "ansioso", "nervoso", "culpa")) {
            risks.add(new RiskItem("Ansiedade alimentar", Severity.MODERADO,
                    "Validação local encontrou termos ligados à ansiedade alimentar.",
                    "Aparecem palavras associadas a ansiedade, culpa ou nervosismo no relato."));
        }
        if (containsAny(text, "compulsão", "perco o controle", "não consigo parar", "exagero")) {
            risks.add(new RiskItem("Compulsão alimentar", Severity.GRAVE,
                    "Validação local encontrou possível compulsão alimentar.",
                    "O relato sugere perda de controle ou episódios de excesso alimentar."));
        }
        if (containsAny(text, "jejum prolongado", "dieta restritiva", "cortar carboidrato", "sem comer")) {
            risks.add(new RiskItem("Dieta extrema", Severity.MODERADO,
                    "Validação local encontrou possível restrição alimentar intensa.",
                    "O relato cita jejum, restrição ou retirada de grupos alimentares."));
        }
        if (containsAny(text, "pulo refeições", "pular almoço", "sem café da manhã", "não almoço")) {
            risks.add(new RiskItem("Pular refeições", Severity.LEVE,
                    "Validação local encontrou padrão de refeições omitidas.",
                    "O relato cita ausência de refeições durante a rotina."));
        }
        return risks;
    }

    private boolean containsAny(String value, String... terms) {
        for (String term : terms) {
            if (value.contains(term)) {
                return true;
            }
        }
        return false;
    }
}

