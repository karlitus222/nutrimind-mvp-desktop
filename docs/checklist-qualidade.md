# Checklist de Qualidade

- [x] Projeto desktop em Java 17.
- [x] Interface gráfica em Swing.
- [x] Estrutura MVC com pacotes separados.
- [x] Padrão DAO para persistência.
- [x] Singleton para conexão com banco.
- [x] SQLite com PK/FK.
- [x] CRUD de pacientes.
- [x] CRUD/cadastro administrativo de usuários nutricionistas.
- [x] Perfis separados para nutricionista e administrador.
- [x] Consultas relacionadas a pacientes e nutricionistas.
- [x] Consentimento de áudio/vídeo.
- [x] Captura de áudio em `.wav`.
- [x] Vínculo de vídeo à consulta.
- [x] Integração obrigatória com OpenAI.
- [x] Transcrição de áudio.
- [x] Análise estruturada por IA.
- [x] Alertas com severidade.
- [x] Registro de decisão frente a alertas.
- [x] Relatório final em três partes.
- [x] Plano alimentar em revisão.
- [x] Aprovação profissional do plano alimentar.
- [x] Dados de demonstração.
- [x] README com instruções.
- [x] DER lógico.
- [x] Diagrama de componentes.
- [x] Diagrama de implantação.
- [x] Scripts de compilação e execução.

## Checklist Web/Supabase

- [x] Aplicacao web publicada na Vercel.
- [x] Supabase Auth configurado para login real.
- [x] PostgreSQL Supabase com RLS ativo.
- [x] Edge Function `analyze-consultation` publicada com JWT obrigatorio.
- [x] Gemini API configurada como provedor de apresentacao.
- [x] OpenAI mantida como fallback pago.
- [x] Gravacao de audio pelo navegador com consentimento obrigatorio.
- [x] Audio bruto nao persistido no banco pelo fluxo principal.
- [x] Login de apresentacao criado.
- [x] Teste final documentado.
