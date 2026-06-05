# IA no Nutrimind

O fluxo principal usa IA como apoio ao nutricionista, nao como substituto da decisao clinica.

## Desktop Java

No desktop, a IA e chamada diretamente pelos servicos Java:

- `GeminiClient`
- `GeminiTranscriptionService`
- `GeminiRiskAnalysisService`
- `OpenAiClient`
- `OpenAiTranscriptionService`
- `OpenAiRiskAnalysisService`

O `AppController` escolhe o provedor automaticamente:

1. Se `GEMINI_API_KEY` existir, usa Gemini.
2. Se nao houver Gemini e `OPENAI_API_KEY` existir, usa OpenAI.
3. Se nenhuma chave existir, o sistema abre, mas bloqueia transcricao, analise e relatorio inteligente.

## Configuracao Recomendada

Para apresentacao gratuita:

```text
GEMINI_API_KEY=sua-chave
GEMINI_ANALYSIS_MODEL=gemini-2.5-flash-lite
GEMINI_TRANSCRIPTION_MODEL=gemini-2.5-flash-lite
```

Fallback pago:

```text
OPENAI_API_KEY=sua-chave
OPENAI_TRANSCRIPTION_MODEL=gpt-4o-mini-transcribe
OPENAI_ANALYSIS_MODEL=gpt-5-mini
```

Nunca salve chaves em arquivos do projeto e nunca envie chaves para o GitHub.

## Fluxo de Dados no Desktop

1. O nutricionista marca consentimento de audio.
2. O desktop grava audio em `.wav` ou recebe transcricao manual.
3. O servico de transcricao envia o audio para Gemini/OpenAI quando houver chave configurada.
4. O servico de analise envia transcricao, notas clinicas, observacoes e historico do paciente.
5. A IA retorna JSON estruturado com resumo, riscos, justificativas, recomendacoes e sugestao inicial de plano.
6. O sistema salva analise, alertas, relatorio e plano alimentar no SQLite.
7. O nutricionista revisa, decide e aprova o que sera utilizado.

## Regras Clinicas no Prompt

- A IA nao fecha diagnostico.
- A IA nao deve inventar habitos, sintomas ou riscos.
- Comportamentos especificos so podem ser afirmados se estiverem explicitamente no historico, notas, transcricao ou observacoes.
- Se a fala for vaga, por exemplo "tenho problema com comida", a IA deve pedir aprofundamento, nao concluir compulsao, restricao, culpa ou pular refeicoes.
- Se o achado vier do historico cadastrado, isso deve aparecer na justificativa.
- O plano alimentar sempre fica em revisao ate aprovacao do nutricionista.

## Web Mantido

A versao web usa a Edge Function `analyze-consultation` no Supabase com a mesma ideia de provedor:

1. Gemini como principal.
2. OpenAI como fallback.
3. Bloqueio da analise real sem chave.

Esse web app continua como demonstrativo publicado, mas a entrega principal fica no desktop Java.

## Referencias

- https://ai.google.dev/gemini-api/docs/api-key
- https://ai.google.dev/gemini-api/docs/pricing
- https://platform.openai.com/docs/api-reference/responses
- https://platform.openai.com/docs/guides/speech-to-text
- https://platform.openai.com/docs/guides/structured-outputs
