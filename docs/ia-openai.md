# IA OpenAI

O fluxo principal usa OpenAI como provedor obrigatório de IA:

1. O áudio `.wav` da consulta é enviado para o endpoint de transcrição.
2. A transcrição, notas clínicas, observações visuais e histórico do paciente são enviados para a Responses API.
3. A resposta é solicitada em JSON estruturado com:
   - resumo da consulta;
   - recomendações iniciais;
   - sugestão de plano alimentar;
   - lista de riscos;
   - severidade;
   - mensagem de alerta;
   - justificativa.

Variáveis:

```powershell
$env:OPENAI_API_KEY="sua-chave"
$env:OPENAI_TRANSCRIPTION_MODEL="gpt-4o-mini-transcribe"
$env:OPENAI_ANALYSIS_MODEL="gpt-5-mini"
```

Regras do sistema:

- Sem `OPENAI_API_KEY`, a análise é bloqueada.
- Regras locais existem apenas como validação auxiliar.
- A IA não fecha diagnóstico.
- O plano alimentar só fica aprovado depois da revisão do nutricionista.
- A chave de API nunca deve ser salva no Git.

Referências oficiais:

- https://platform.openai.com/docs/api-reference/responses
- https://platform.openai.com/docs/guides/speech-to-text
- https://platform.openai.com/docs/guides/structured-outputs
- https://platform.openai.com/docs/models
