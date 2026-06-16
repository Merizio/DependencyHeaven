# Paraíso das Dependências - DependencyHeaven
Sistema de gerenciamento de tarefas com designação de dependências.

## Diagrama de Classes

![Diagrama de Classes Astah](DependencyHeaven.png)

## Ferramentas Escolhidas

1. **Controle de Versão**: Utilização geral do GitHub;
2. **Automação de Build**: Maven para Java;
3. **Testes**: Uso do JUnit5 para testes unitários;
4. **Issue Tracking & CI/CD**: Nativos do GitHub;
5. **Container**: Docker(?).

## Frameworks Reutilizados

### Back-End
O Back-End é feito em *Java*, com uso de *SpringBoot* para a aplicação e *SpringData JPA* para o Banco de Dados.

### Front-End
O Front-End é feito em Angular para a criação da interface para o usuário.

## Como Gerar a Documentação do Código

A documentação é gerada em JavaDoc, pela seguinte maneira:

```bash
mvn javadoc:javadoc
```

## Como Executar o Sistema

-EM PRODUÇÃO

## Membros
- Francisco Vassoler Merizio - 2024102652