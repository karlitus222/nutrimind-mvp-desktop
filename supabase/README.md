# Supabase do Nutrimind

Projeto publicado:

- nome: `nutrimind`
- project ref: `hcobzfxkkvezlopktlzf`
- regiao: `sa-east-1`

Para reproduzir a configuracao:

1. Crie um projeto Supabase exclusivo para o Nutrimind.
2. Aplique `migrations/20260601180000_nutrimind_schema.sql`.
3. Aplique `migrations/20260601233000_harden_rls.sql` somente em bancos criados antes da revisao de seguranca.
4. Publique a Edge Function `analyze-consultation` com validacao JWT habilitada.
5. No gerenciamento de segredos das Edge Functions, cadastre `GEMINI_API_KEY` para apresentacao gratuita.
6. Opcionalmente cadastre `OPENAI_API_KEY` como fallback pago.
7. Opcionalmente defina `GEMINI_ANALYSIS_MODEL` e `GEMINI_TRANSCRIPTION_MODEL`; o padrao e `gemini-2.0-flash`.
8. Opcionalmente defina `OPENAI_ANALYSIS_MODEL`; o padrao e `gpt-5.4-mini`.
9. Opcionalmente defina `OPENAI_TRANSCRIPTION_MODEL`; o padrao e `gpt-4o-mini-transcribe`.
10. Configure no frontend apenas `VITE_SUPABASE_URL` e `VITE_SUPABASE_PUBLISHABLE_KEY`.

O projeto publicado ja recebeu as duas migrations, possui sete tabelas com RLS ativo e executa a funcao com JWT obrigatorio. O unico passo externo pendente e cadastrar uma chave Gemini valida como segredo de apresentacao.

A Edge Function prioriza Gemini com JSON estruturado. Caso `GEMINI_API_KEY` nao exista, ela tenta OpenAI com `gpt-4o-mini-transcribe` para audio e Responses API com JSON Schema estrito.
