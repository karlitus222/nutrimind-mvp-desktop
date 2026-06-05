# Roteiro de Apresentacao - Nutrimind

## Objetivo da fala

Mostrar que o Nutrimind e um sistema academico de apoio a nutricao comportamental. A IA nao diagnostica e nao substitui o nutricionista; ela organiza sinais da consulta, gera alertas justificados, monta um relatorio inicial e sugere um plano para revisao humana.

## Ordem sugerida

1. Problema
   - Dizer que consultas de nutricao comportamental geram muitas informacoes subjetivas.
   - Explicar que o profissional precisa registrar falas, sinais de risco, historico e decisao clinica.

2. Solucao
   - Apresentar o Nutrimind como uma plataforma web com login, pacientes, consulta gravada e IA.
   - Reforcar que a IA e apoio a decisao.

3. Login
   - Acessar `https://nutrimind-two.vercel.app`.
   - Entrar com `demo@nutrimind.app` e `Nutrimind@2026`.

4. Paciente
   - Abrir a lista de pacientes.
   - Mostrar a paciente ficticia Ana Maria Souza.

5. Consulta com IA
   - Entrar em "Consulta com IA".
   - Marcar consentimento para audio.
   - Clicar em "Gravar consulta".
   - Falar um caso ficticio.
   - Parar a gravacao.
   - Clicar em "Encerrar consulta e analisar".

6. Resultado
   - Mostrar que a IA retorna resumo, alertas, relatorio e plano inicial.
   - Explicar que o alerta tem severidade e justificativa.
   - Mostrar que o nutricionista registra a revisao e aprova o plano.

7. Tecnologia
   - Frontend: React + Vite na Vercel.
   - Backend: Supabase Auth, PostgreSQL com RLS e Edge Function.
   - IA: Gemini API no modo de apresentacao gratuita; OpenAI fica como fallback.
   - Audio bruto nao e salvo no banco; o sistema salva transcricao, resumo, alertas, relatorio e plano.

## Fala de teste para gravar

Tenho tido dificuldade com comida nos ultimos meses. Eu fico ansiosa antes das refeicoes e, depois que como, as vezes sinto culpa. Tambem percebi que quando estou muito estressada acabo comendo muito rapido e depois me arrependo. Nao sei se isso e compulsao, mas sinto que perdi um pouco o controle da minha rotina alimentar.

## Fechamento

O Nutrimind transforma a consulta em informacao organizada, mas mantem a responsabilidade final com o nutricionista. Por isso, toda sugestao da IA fica em revisao antes de virar decisao clinica.
