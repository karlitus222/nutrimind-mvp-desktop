# Fluxo de Trabalho no GitHub

## Regra principal

Ninguém trabalha direto na `main`. Cada pessoa cria uma branch própria, faz commits pequenos e abre Pull Request.

## Branches sugeridas

- `pessoa1-login-readme`
- `pessoa2-banco-der`
- `pessoa3-modelos-poo`
- `pessoa4-dao-crud`
- `pessoa5-interface-swing`
- `pessoa6-controllers-fluxo`
- `pessoa7-ia-testes-docs`

## Passo a passo

```powershell
git checkout main
git pull
git checkout -b pessoaX-nome-da-tarefa
```

Depois de editar:

```powershell
git add .
git commit -m "feat: descrever a tarefa feita"
git push origin pessoaX-nome-da-tarefa
```

No GitHub, abrir um Pull Request para a `main`.

## Organização por Issues

Crie uma issue para cada pessoa usando o template **Tarefa do MVP**. Cada issue deve listar:

- objetivo da pessoa;
- arquivos principais;
- critérios de conclusão;
- link do Pull Request.

## Evitar conflitos

- Não edite arquivo da área de outra pessoa sem avisar.
- Antes de começar no dia, sempre rode `git pull`.
- Faça commits pequenos.
- Não suba `.env`, banco local, `lib/`, `desktop/out/` ou arquivos gerados.
