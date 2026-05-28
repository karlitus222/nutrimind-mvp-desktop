# Fluxo de Trabalho no GitHub Desktop

## Regra principal

Ninguém trabalha direto na `main`. Cada pessoa usa a própria branch, faz alterações no editor, salva pelo GitHub Desktop e abre um Pull Request.

Este guia é para trabalhar pela interface gráfica.

## Branches do grupo

- `pessoa1-login-readme`
- `pessoa2-banco-der`
- `pessoa3-modelos-poo`
- `pessoa4-dao-crud`
- `pessoa5-interface-swing`
- `pessoa6-controllers-fluxo`
- `pessoa7-ia-testes-docs`

## Abrir a pasta do projeto

1. Abra o GitHub Desktop.
2. Selecione o repositório `nutrimind-mvp-desktop`.
3. Clique em **Repository** no menu superior.
4. Clique em **Show in Explorer** para abrir a pasta no Windows.
5. Para editar o código, clique em **Repository > Open in Visual Studio Code** ou abra a pasta pelo editor que o grupo estiver usando.

## Escolher sua branch

1. No topo do GitHub Desktop, clique em **Current Branch**.
2. Escolha a branch da sua pessoa.
3. Clique em **Fetch origin**.
4. Se aparecer **Pull origin**, clique para baixar a versão mais recente.

## Editar sua parte

1. Abra a pasta no VS Code, IntelliJ, Eclipse ou outro editor.
2. Mexa apenas nos arquivos combinados para sua pessoa.
3. Salve os arquivos normalmente no editor.
4. Volte para o GitHub Desktop para ver a lista de arquivos alterados.

## Salvar e enviar alterações

1. No GitHub Desktop, confira os arquivos alterados.
2. Escreva uma mensagem curta no campo **Summary**, por exemplo `feat: ajustar tela de login`.
3. Clique em **Commit to pessoaX...**.
4. Clique em **Push origin** para enviar sua branch ao GitHub.

## Abrir Pull Request

1. Depois do push, clique em **Create Pull Request** no GitHub Desktop.
2. O navegador vai abrir o GitHub.
3. Confira se a comparação está da sua branch para a `main`.
4. Escreva um resumo do que foi feito.
5. Clique em **Create Pull Request**.
6. Depois que outra pessoa revisar, o grupo pode juntar o Pull Request na `main`.

## Organização por Issues

Cada pessoa deve usar a issue com seu número. A issue deve ter:

- objetivo da pessoa;
- arquivos principais;
- critérios de conclusão;
- link do Pull Request.

## Evitar conflitos

- Não edite arquivo da área de outra pessoa sem avisar.
- Antes de começar no dia, clique em **Fetch origin** e depois em **Pull origin** se aparecer.
- Faça commits pequenos.
- Não envie `.env`, banco local, `lib/`, `desktop/out/` ou arquivos gerados.
- Se aparecer conflito no GitHub Desktop, pare e chame o grupo antes de apagar código.
