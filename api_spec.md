# Especificação da API REST - Dependency Heaven

Abaixo estão as principais rotas da nossa API para orientar tanto o desenvolvimento do **Back-end** quanto a integração no **Front-end**.

## 1. Templates

Os templates são os espaços de trabalho que contêm as tarefas.

### `POST /api/templates`
Cria um novo template vazio.
**Request Payload:**
```json
{
  "nome": "Projeto de Migração"
}
```
**Response (201 Created):**
```json
{
  "id": 1,
  "nome": "Projeto de Migração"
}
```

### `GET /api/templates/{id}`
Retorna um template e a lista completa de suas tarefas (útil para montar a tela principal).
**Response (200 OK):**
```json
{
  "id": 1,
  "nome": "Projeto de Migração",
  "tarefas": [
    {
      "id": 10,
      "titulo": "Configurar Banco",
      "estado": "PENDENTE",
      "membros": [],
      "dependenciasIds": []
    }
  ]
}
```

---

## 2. Tarefas

### `POST /api/templates/{templateId}/tarefas`
Cria uma nova tarefa dentro de um template.
**Request Payload:**
```json
{
  "titulo": "Configurar Banco de Dados",
  "descricao": "Instalar e configurar o PostgreSQL",
  "membros": [
    {"nome": "Davi"}
  ]
}
```
**Response (201 Created):**
*Nota: O backend deve avaliar as dependências (que na criação inicial vêm vazias, então o estado inicial será `PENDENTE`)*.

### `PUT /api/tarefas/{id}`
Atualiza os dados básicos de uma tarefa e a lista de membros.
**Request Payload:**
```json
{
  "titulo": "Configurar BD (Atualizado)",
  "descricao": "Instalar PostgreSQL 15",
  "membros": [
    {"nome": "Davi"},
    {"nome": "Paula"}
  ]
}
```

### `PUT /api/tarefas/{id}/estado`
Muda manualmente o estado de uma tarefa (ex: de PENDENTE para EM_ANDAMENTO).
*Nota: O backend deve validar se a transição é permitida (ex: ir para EM_ANDAMENTO exige ter membros e não estar BLOQUEADO).*
**Request Payload:**
```json
{
  "estado": "EM_ANDAMENTO"
}
```

---

## 3. Dependências

Como a gestão de dependências exige validações de ciclo (DAG), temos rotas específicas.

### `POST /api/tarefas/{id}/dependencias`
Adiciona uma nova tarefa como pré-requisito (dependência).
**Request Payload:**
```json
{
  "dependenciaId": 12
}
```
**Response (200 OK):**
Retorna a tarefa atualizada (seu estado pode ter mudado para `BLOQUEADO` caso a tarefa 12 não esteja concluída).

### `DELETE /api/tarefas/{id}/dependencias/{dependenciaId}`
Remove uma dependência. O backend reavalia se a tarefa pode voltar a ficar `PENDENTE`.
