# Diretrizes de Git e Fluxo de Trabalho

Para que os três membros (Back-end, Front-end e Infra) trabalhem em paralelo sem conflitos, vamos adotar um modelo simples baseado em **Feature Branching** e **Conventional Commits**.

## 1. Política de Branches

O projeto utilizará duas branches principais:
*   `main`: Contém apenas código funcional, testado e estável (pronto para produção).
*   `dev`: Branch de integração. Todo o desenvolvimento diário será unido nela antes de ir para a `main`.

Todo o desenvolvimento deve ocorrer em branches derivadas da `dev`.

**Padrão de nomenclatura das branches:**
*   **Back-end:** `back/nome-da-feature` (Ex: `back/criar-crud-tarefas`)
*   **Front-end:** `front/nome-da-feature` (Ex: `front/tela-kanban`)
*   **Infra:** `infra/nome-da-feature` (Ex: `infra/configurar-docker-compose`)
*   **Correções:** `bugfix/descrição-do-bug` (Ex: `bugfix/erro-ao-salvar-membro`)

**Fluxo de Integração:**
1. Crie sua branch a partir da `dev`: `git checkout -b front/nova-tela`
2. Faça seus commits localmente.
3. Abra um **Pull Request (PR)** para a branch `dev`.
4. Após revisão, faça o merge na `dev`.
5. Periodicamente, abriremos um PR da `dev` para a `main` para lançar uma nova versão estável.

---

## 2. Padrão de Commits (Conventional Commits)

Vamos usar prefixos nos commits para que o histórico fique legível e possamos automatizar (CI/CD) no futuro.

*   `feat: ` - Para novas funcionalidades. Ex: `feat: cria endpoint de adicionar dependência`
*   `fix: ` - Para correção de bugs. Ex: `fix: corrige validação de ciclo de dependências`
*   `infra: ` ou `chore:` - Para configurações, Docker, Maven, etc. Ex: `infra: adiciona dockerfile para o backend`
*   `test: ` - Para adição ou alteração de testes. Ex: `test: adiciona teste de transição de estado`
*   `docs: ` - Para documentação (como este README ou Wiki). Ex: `docs: atualiza diagrama de classes`

Desta forma, ao ler o histórico `git log`, fica muito claro em qual área o projeto avançou.
