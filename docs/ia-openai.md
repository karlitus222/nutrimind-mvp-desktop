# IA no Nutrimind

O fluxo principal usa IA como apoio ao nutricionista, nao como substituto da decisao clinica.

## Provedor de apresentacao

Para a apresentacao academica, o provedor principal e Gemini API no free tier:

```text
GEMINI_API_KEY=sua-chave
GEMINI_ANALYSIS_MODEL=gemini-2.5-flash-lite
GEMINI_TRANSCRIPTION_MODEL=gemini-2.5-flash-lite
```

A chave fica cadastrada somente como segredo da Edge Function no Supabase. Ela nao deve ser colocada no frontend nem salva no Git.

## Fallback pago

OpenAI permanece como fallback caso a equipe queira usar API paga no futuro:

```text
OPENAI_API_KEY=sua-chave
OPENAI_TRANSCRIPTION_MODEL=gpt-4o-mini-transcribe
OPENAI_ANALYSIS_MODEL=gpt-5.4-mini
```

## Fluxo de dados

1. O nutricionista marca consentimento para audio.
2. O navegador grava ou recebe upload de audio.
3. O audio e enviado para a Edge Function apenas para transcricao/analise.
4. A IA recebe transcricao, notas clinicas, observacoes e historico do paciente.
5. A IA retorna JSON estruturado com resumo, riscos, justificativas, recomendacoes, relatorio e plano inicial.
6. O sistema salva transcricao, resumo, alertas, relatorio e plano.
7. O audio bruto nao e persistido no banco pelo fluxo principal.

## Regras clinicas

- A IA nao fecha diagnostico.
- A IA nao deve inventar habitos, sintomas ou riscos.
- Se a fala for vaga, a resposta deve pedir aprofundamento em vez de concluir padroes especificos.
- Se o achado vier do historico cadastrado, isso deve aparecer claramente na justificativa.
- O plano alimentar so fica aprovado depois da revisao do nutricionista.

## Referencias oficiais

- https://ai.google.dev/gemini-api/docs/api-key
- https://ai.google.dev/gemini-api/docs/pricing
- https://ai.google.dev/gemini-api/docs/rate-limits
- https://platform.openai.com/docs/api-reference/responses
- https://platform.openai.com/docs/guides/speech-to-text
- https://platform.openai.com/docs/guides/structured-outputs
