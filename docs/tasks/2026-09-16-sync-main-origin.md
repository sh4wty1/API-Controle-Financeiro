# Sincronizar branch local `main` com `origin/main`

**Por quê:** manter o repositório local atualizado antes de continuar o trabalho, trazendo o que já foi mesclado remotamente.

**O quê:** fast-forward de `2ac8230` para `e32a690`. Nenhum código foi escrito nesta sessão; a mudança veio inteiramente do remoto e inclui o novo pacote `categoria` (`Categoria`, `CategoriaRepository`, `CategoriaService`, `TipoCategoria`, `CategoriaRequest`, `CategoriaResponse`) e ajustes em `README.md` e `docs/ROADMAP.md`.

**Como:** `git pull` na branch `main`; fast-forward simples, sem merge nem rebase.

**Verificação:** pull concluído sem conflitos; working tree limpo, exceto a modificação pré-existente em `mvnw` (não relacionada a este pull).

**Pendências:** nenhuma.
