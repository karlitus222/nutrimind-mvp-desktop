# Fluxo de Trabalho no GitHub Desktop

## Regra Principal

Ninguem trabalha direto na `main`. Cada pessoa usa a propria branch, faz alteracoes no editor, salva pelo GitHub Desktop e abre um Pull Request.

O sistema principal do trabalho e o **desktop Java + Swing + SQLite**. A versao web fica mantida como apoio de demonstracao.

## Branches do Grupo

- `pessoa1-login-readme`
- `pessoa2-banco-der`
- `pessoa3-modelos-poo`
- `pessoa4-dao-crud`
- `pessoa5-interface-swing`
- `pessoa6-controllers-fluxo`
- `pessoa7-ia-testes-docs`

## Ordem Recomendada

1. Pessoa 2: banco de dados, SQL, DER e inicializacao.
2. Pessoa 3: models e pilares de POO.
3. Pessoa 1: login, aplicacao principal e README.
4. Pessoa 4: DAO, Singleton e CRUDs.
5. Pessoa 6: controllers e fluxo de consulta.
6. Pessoa 5: telas Swing e usabilidade.
7. Pessoa 7: IA, testes e documentacao.
8. Todos: revisao final, correcao de erros e ensaio.

## Abrir a Pasta do Projeto

1. Abra o GitHub Desktop.
2. Selecione o repositorio `nutrimind-mvp-desktop`.
3. Clique em **Repository** no menu superior.
4. Clique em **Show in Explorer** para abrir a pasta no Windows.
5. Para editar o codigo, clique em **Repository > Open in Visual Studio Code** ou abra a pasta pelo editor escolhido.

## Escolher Sua Branch

1. No topo do GitHub Desktop, clique em **Current Branch**.
2. Escolha a branch da sua pessoa.
3. Clique em **Fetch origin**.
4. Se aparecer **Pull origin**, clique para baixar a versao mais recente.

## Editar Sua Parte

1. Abra a pasta no editor.
2. Mexa apenas nos arquivos combinados para sua pessoa.
3. Salve os arquivos normalmente.
4. Volte para o GitHub Desktop para ver a lista de arquivos alterados.

## Salvar e Enviar Alteracoes

1. No GitHub Desktop, confira os arquivos alterados.
2. Escreva uma mensagem curta no campo **Summary**, por exemplo `feat: ajustar tela de login`.
3. Clique em **Commit to pessoaX...**.
4. Clique em **Push origin** para enviar sua branch ao GitHub.

## Abrir Pull Request

1. Depois do push, clique em **Create Pull Request** no GitHub Desktop.
2. O navegador vai abrir o GitHub.
3. Confira se a comparacao esta da sua branch para a `main`.
4. Escreva um resumo do que foi feito.
5. Marque a area da pessoa no template.
6. Clique em **Create Pull Request**.

## PR Final de Integracao

O PR final deve reunir a versao integrada do desktop e a documentacao final. PRs individuais dos integrantes podem continuar existindo como evidencia de participacao.

## Evitar Conflitos

- Nao edite arquivo da area de outra pessoa sem avisar.
- Antes de comecar no dia, clique em **Fetch origin** e depois em **Pull origin** se aparecer.
- Faca commits pequenos.
- Nao envie `.env`, chave de API, banco local, `lib/`, `desktop/out/` ou arquivos gerados.
- Se aparecer conflito no GitHub Desktop, pare e chame o grupo antes de apagar codigo.
