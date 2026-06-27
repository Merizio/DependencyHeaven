# Regras de Negócio - Dependency Heaven

Este documento detalha o comportamento esperado para o gerenciamento de tarefas, estados, membros e dependências do sistema.

## 1. Entidades do Domínio

*   **Usuário (`Usuario`)**: Representa uma conta no sistema com credenciais (`nome`, `email`, `senha`). É quem gerencia os templates.
*   **Template (`Template`)**: Um espaço de trabalho ("tela em branco") onde as tarefas são criadas.
    *   Toda tarefa pertence a exatamente um `Template` (não existem tarefas soltas).
    *   O `Template` pode ser salvo e copiado/duplicado.
*   **Membro (`Membro`)**: Representa uma pessoa designada para trabalhar em tarefas. 
    *   Possui apenas `nome`. Não possui login ou conta no sistema.
*   **Tarefa (`Tarefa`)**: Representa a unidade de trabalho com `titulo`, `descricao`, `estado` e associações.

---

## 2. Estados das Tarefas (`Estado`)

A tarefa pode assumir quatro estados representados pela enumeração:

1.  **`BLOQUEADO`**: A tarefa possui dependências (pré-requisitos) pendentes que ainda não foram finalizadas.
2.  **`PENDENTE`**: A tarefa não possui dependências impedindo seu início (ou todas já foram finalizadas), mas o trabalho ainda não começou.
3.  **`EM_ANDAMENTO`**: A tarefa está sendo executada por pelo menos um membro.
4.  **`FINALIZADO`**: A tarefa foi concluída com sucesso.

---

## 3. Regras de Transição e Validação

### Associação de Membros
*   Uma tarefa pode ser criada sem nenhum membro associado.
*   **Restrição**: Uma tarefa **só pode** transitar para o estado `EM_ANDAMENTO` se possuir **pelo menos um membro** associado.

### Gerenciamento de Dependências e Estados
*   **Inicialização**:
    *   Ao criar uma tarefa, se ela possuir alguma dependência cujo estado atual não seja `FINALIZADO`, ela deve ser criada com o estado `BLOQUEADO`.
    *   Caso contrário, ela é criada como `PENDENTE`.
*   **Conclusão de Dependência**:
    *   Quando uma tarefa `X` transita para `FINALIZADO`, o sistema deve buscar todas as tarefas que dependem diretamente de `X`.
    *   Para cada uma dessas tarefas dependentes, o sistema verifica se ainda resta alguma outra dependência não finalizada. Caso não reste nenhuma, o estado dessa tarefa muda automaticamente de `BLOQUEADO` para `PENDENTE`.
*   **Reabertura de Tarefa**:
    *   Se uma tarefa `X` que estava `FINALIZADO` for reaberta (mudar para qualquer outro estado), todas as tarefas que dependem de `X` devem voltar automaticamente para o estado `BLOQUEADO`.
*   **Validação de Grafo (DAG)**:
    *   O backend deve validar qualquer tentativa de adicionar uma dependência entre tarefas para garantir que **não existam ciclos** (garantia de Grafo Acíclico Dirigido - DAG). Exemplo: se `A` depende de `B`, `B` não pode depender de `A` (nem direta nem indiretamente).
    *   Qualquer tentativa de criar dependência cíclica deve ser impedida pelo backend com retorno de erro apropriado.
    *   Se uma nova dependência válida não-finalizada for adicionada a uma tarefa em estado `PENDENTE` ou `EM_ANDAMENTO`, o estado da tarefa afetada deve mudar automaticamente para `BLOQUEADO`.
