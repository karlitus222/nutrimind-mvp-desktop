# Teste Final - Nutrimind

Data do teste: 2026-06-05

## Ambiente

- Producao: `https://nutrimind-two.vercel.app`
- Supabase project ref: `hcobzfxkkvezlopktlzf`
- Edge Function: `analyze-consultation`
- Provedor de IA ativo: Gemini
- Modelo de apresentacao: `gemini-2.5-flash-lite`

## Credenciais de apresentacao

- E-mail: `demo@nutrimind.app`
- Senha: `Nutrimind@2026`

## Validacoes executadas

- Login real pelo Supabase Auth.
- Paciente ficticio disponivel para apresentacao.
- Chamada da Edge Function com JWT obrigatorio.
- Analise real por Gemini.
- Persistencia de consulta no banco.
- Persistencia de alertas.
- Registro de decisao frente a alerta.
- Persistencia de relatorio em partes.
- Criacao e aprovacao de plano alimentar.
- Site publico respondendo HTTP 200.

## Caso de teste usado

Paciente relata ansiedade antes das refeicoes, culpa depois de comer e sensacao de perda de controle quando esta sob estresse.

## Resultado esperado

A IA deve apontar sinais relacionados a ansiedade alimentar, culpa apos refeicao ou necessidade de aprofundamento sobre perda de controle, sem fechar diagnostico. O relatorio deve reforcar que a decisao final e do nutricionista.

## Resultado observado

Fluxo concluido com sucesso. A funcao retornou analise estruturada, alertas e plano inicial. O alerta foi marcado como revisado e o plano foi aprovado para demonstracao.

- Consulta de teste: `9be8a44f-6a88-48e2-a691-8e10ee68b058`
- Provedor retornado: Gemini
- Modelo retornado: `gemini-2.5-flash-lite`
- Alertas retornados: 2
- Plano: aprovado

## Observacao de seguranca

Usar somente dados ficticios na apresentacao. O audio bruto nao e persistido no banco pelo fluxo principal.
