# Checklist de Qualidade

## Desktop Java - Entrega Principal

- [x] Projeto desktop em Java 17.
- [x] Interface grafica em Swing.
- [x] Estrutura MVC com pacotes separados.
- [x] Padrao DAO para persistencia.
- [x] Singleton para conexao com banco.
- [x] SQLite com PK/FK.
- [x] CRUD de pacientes.
- [x] CRUD/cadastro administrativo de usuarios nutricionistas.
- [x] Perfis separados para nutricionista e administrador.
- [x] Consultas relacionadas a pacientes e nutricionistas.
- [x] Consentimento de audio/video.
- [x] Captura de audio em `.wav`.
- [x] Vinculo de video a consulta.
- [x] IA obrigatoria no fluxo de analise.
- [x] Gemini API como provedor principal para apresentacao gratuita.
- [x] OpenAI API como fallback configuravel.
- [x] Transcricao de audio.
- [x] Analise estruturada por IA.
- [x] Prompt com regra para nao inventar fatos, habitos ou sintomas.
- [x] Alertas com severidade.
- [x] Registro de decisao frente a alertas.
- [x] Relatorio final com identificacao, achados, recomendacoes e limitacoes.
- [x] Plano alimentar em revisao.
- [x] Aprovacao profissional do plano alimentar.
- [x] Dados de demonstracao.
- [x] README com instrucoes.
- [x] DER logico.
- [x] Diagrama de componentes.
- [x] Diagrama de implantacao.
- [x] Scripts de compilacao e execucao.

## Web - Apoio de Demonstracao

- [x] Aplicacao web publicada na Vercel.
- [x] Supabase Auth configurado para login real.
- [x] PostgreSQL Supabase com RLS ativo.
- [x] Edge Function `analyze-consultation` publicada.
- [x] Gemini API configurada como provedor de apresentacao.
- [x] OpenAI mantida como fallback pago.
- [x] Gravacao de audio pelo navegador com consentimento obrigatorio.
- [x] Audio bruto nao persistido no banco pelo fluxo principal.
- [x] Login de apresentacao criado.
- [x] Teste final documentado.
